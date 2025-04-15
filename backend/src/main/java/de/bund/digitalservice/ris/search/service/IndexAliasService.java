package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.config.opensearch.Configurations;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.opensearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.opensearch.client.IndicesClient;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.core.rest.RestStatus;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/** Service class for setting index aliases. */
@Service
public class IndexAliasService {

  private final RestHighLevelClient restHighLevelClient;

  private final Configurations configurations;

  private static final Logger logger = LogManager.getLogger(IndexAliasService.class);

  public IndexAliasService(RestHighLevelClient restHighLevelClient, Configurations configurations) {
    this.restHighLevelClient = restHighLevelClient;
    this.configurations = configurations;
  }

  /**
   * Creates aliases from "documents" to the default norms and caselaw indices, if those aren't
   * aliases themselves. This is done to enable search across all document kinds.
   */
  @EventListener(value = ApplicationReadyEvent.class)
  public void setIndexAlias() {
    IndicesAliasesRequest request = new IndicesAliasesRequest();

    final String normsIndexName = configurations.getNormsIndexName();

    try {

      if (isNotAlias(normsIndexName)) {
        request.addAliasAction(
            new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                .index(normsIndexName)
                .alias(configurations.getDocumentsAliasName()));
      }

      final String caseLawsIndexName = configurations.getCaseLawsIndexName();
      if (isNotAlias(caseLawsIndexName)) {
        request.addAliasAction(
            new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                .index(caseLawsIndexName)
                .alias(configurations.getDocumentsAliasName()));
      }
      if (!request.getAliasActions().isEmpty()) {
        restHighLevelClient.indices().updateAliases(request, RequestOptions.DEFAULT);
      }

    } catch (IOException e) {
      logger.error("Error while updating index aliases", e);
    }
  }

  /**
   * Checks whether @param aliasName is an alias to another index, which means that no alias should
   * be created with this name as target.
   */
  public boolean isNotAlias(String aliasName) throws IOException {
    IndicesClient indicesClient = restHighLevelClient.indices();
    var aliasInfo =
        indicesClient.getAlias(new GetAliasesRequest().aliases(aliasName), RequestOptions.DEFAULT);
    final boolean exists = aliasInfo.status() == RestStatus.OK;
    if (exists) {
      var targetIndices = aliasInfo.getAliases().keySet();
      logger.info("Found alias {} → {}", aliasName, targetIndices);
    }
    return !exists;
  }
}
