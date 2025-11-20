package de.bund.digitalservice.ris.search.config.opensearch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.opensearch.client.Request;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.client.indices.GetIndexResponse;
import org.opensearch.cluster.metadata.AliasMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class OpensearchSchemaSetup {

  private static final Logger logger = LogManager.getLogger(OpensearchSchemaSetup.class);
  private final Configurations configurations;

  @Autowired
  public OpensearchSchemaSetup(Configurations configurations) {
    this.configurations = configurations;
  }

  public void updateOpensearchSchema(RestHighLevelClient restHighLevelClient) {
    try {
      upsertTemplates(restHighLevelClient);
      GetIndexResponse indexResponse =
          restHighLevelClient.indices().get(new GetIndexRequest("*"), RequestOptions.DEFAULT);
      upsertOneIndexAndItsAliases(
          restHighLevelClient, indexResponse, configurations.getCaseLawsIndexName());
      upsertOneIndexAndItsAliases(
          restHighLevelClient, indexResponse, configurations.getLiteratureIndexName());
      upsertOneIndexAndItsAliases(
          restHighLevelClient, indexResponse, configurations.getNormsIndexName());
      upsertOneIndexAndItsAliases(
          restHighLevelClient, indexResponse, configurations.getAdministrativeDirectiveIndexName());

    } catch (IOException e) {
      throw new IllegalStateException(
          "unexpected IOException during startup while updating OpenSearch schema", e);
    }
  }

  private void upsertTemplates(RestHighLevelClient restHighLevelClient) throws IOException {
    putJsonToOpenSearch(
        restHighLevelClient,
        "/_component_template/german_analyzer_template",
        "/openSearch/german_analyzer_template.json");
    putJsonToOpenSearch(
        restHighLevelClient,
        "/_index_template/case_law_index_template",
        "/openSearch/case_law_index_template.json");
    putJsonToOpenSearch(
        restHighLevelClient,
        "/_index_template/literature_index_template",
        "/openSearch/literature_index_template.json");
    putJsonToOpenSearch(
        restHighLevelClient,
        "/_index_template/norms_index_template",
        "/openSearch/norms_index_template.json");
    putJsonToOpenSearch(
        restHighLevelClient,
        "/_index_template/administrative_directive_index_template",
        "/openSearch/administrative_directive_index_template.json");
  }

  private void putJsonToOpenSearch(RestHighLevelClient client, String endpoint, String jsonPath)
      throws IOException {
    ClassPathResource resource = new ClassPathResource(jsonPath);
    if (!resource.exists()) {
      logger.error(
          "Error during startup. Problem loading opensearch configuration file: {}", jsonPath);
      return;
    }
    String jsonContent = resource.getContentAsString(StandardCharsets.UTF_8);
    Request request = new Request("PUT", endpoint);
    request.setJsonEntity(jsonContent);
    client.getLowLevelClient().performRequest(request);
  }

  private void upsertOneIndexAndItsAliases(
      RestHighLevelClient restHighLevelClient, GetIndexResponse indexState, String aliasName)
      throws IOException {

    String latestIndex =
        Arrays.stream(indexState.getIndices())
            .filter(e -> e.startsWith(aliasName))
            .max(Comparator.naturalOrder())
            .orElse(null);
    if (latestIndex == null) {
      latestIndex = aliasName + "_" + LocalDate.now();
      logger.info("Creating index {}", latestIndex);

      boolean acknowledged =
          restHighLevelClient
              .indices()
              .create(new CreateIndexRequest(latestIndex), RequestOptions.DEFAULT)
              .isAcknowledged();
      if (!acknowledged) {
        logger.error(
            "Error during startup. No {} index exist and attempting to create one failed.",
            aliasName);
      }
    }

    List<String> lastIndexAliases =
        Optional.ofNullable(indexState.getAliases().get(latestIndex)).stream()
            .flatMap(List::stream)
            .map(AliasMetadata::alias)
            .toList();

    if (!lastIndexAliases.contains(aliasName)) {
      createAlias(restHighLevelClient, latestIndex, aliasName);
    }
    String documentsAliasName = configurations.getDocumentsAliasName();
    if (!lastIndexAliases.contains(documentsAliasName)) {
      createAlias(restHighLevelClient, latestIndex, documentsAliasName);
    }
  }

  private void createAlias(
      RestHighLevelClient restHighLevelClient, String indexName, String aliasName)
      throws IOException {
    logger.info("Creating alias '{}' for index '{}'", aliasName, indexName);
    IndicesAliasesRequest aliasRequest = new IndicesAliasesRequest();
    aliasRequest.addAliasAction(
        new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
            .index(indexName)
            .alias(aliasName));

    restHighLevelClient.indices().updateAliases(aliasRequest, RequestOptions.DEFAULT);
  }
}
