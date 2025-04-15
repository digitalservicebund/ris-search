package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.service.IndexAliasService;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opensearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.opensearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.GetIndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class IndexAliasServiceTest extends ContainersIntegrationBase {
  @Autowired private IndexAliasService indexAliasService;
  @Autowired private RestHighLevelClient restHighLevelClient;

  @BeforeEach
  void setUpSearchControllerApiTest() throws IOException {

    Assertions.assertTrue(openSearchContainer.isRunning());
    super.recreateIndex();
  }

  @Test
  void testCreatesAliases() throws IOException {
    final GetAliasesRequest request = new GetAliasesRequest().aliases("documents");
    assertThat(restHighLevelClient.indices().existsAlias(request, RequestOptions.DEFAULT))
        .isFalse();

    indexAliasService.setIndexAlias();

    Set<String> aliasTargets =
        restHighLevelClient
            .indices()
            .getAlias(request, RequestOptions.DEFAULT)
            .getAliases()
            .keySet();
    assertThat(aliasTargets).containsExactly("norms", "caselaws");
  }

  @Test
  @DisplayName("Writes to an alias should go to the correct index")
  void testWritesToAlias() throws IOException {
    if (!restHighLevelClient
        .indices()
        .exists(new GetIndexRequest("norms_2"), RequestOptions.DEFAULT)) {
      restHighLevelClient
          .indices()
          .create(new CreateIndexRequest("norms_2"), RequestOptions.DEFAULT);
    }
    IndicesAliasesRequest request = new IndicesAliasesRequest();
    request.addAliasAction(
        new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
            .index("norms_2")
            .alias("norms")
            .writeIndex(true));
    request.addAliasAction(
        new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.REMOVE_INDEX)
            .index("norms"));
    restHighLevelClient.indices().updateAliases(request, RequestOptions.DEFAULT);

    indexAliasService.setIndexAlias();

    // assert that writes still work and go to the correct index
    IndexResponse response =
        restHighLevelClient.index(
            new IndexRequest().index("norms").source(Map.of("name", "test_norm")),
            RequestOptions.DEFAULT);
    assertThat(response.getShardId().getIndexName()).isEqualTo("norms_2");
  }
}
