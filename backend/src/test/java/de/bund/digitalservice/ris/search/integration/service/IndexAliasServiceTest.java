package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class IndexAliasServiceTest extends ContainersIntegrationBase {
  @Autowired private RestHighLevelClient restHighLevelClient;

  @Test
  @DisplayName("Writes to an alias should go to the correct index")
  void testWritesToAlias() throws IOException {
    // assert that writes still work and go to the correct index
    IndexResponse response =
        restHighLevelClient.index(
            new IndexRequest().index("norms").source(Map.of("name", "test_norm")),
            RequestOptions.DEFAULT);
    assertThat(response.getShardId().getIndexName()).startsWith("norms_");
  }
}
