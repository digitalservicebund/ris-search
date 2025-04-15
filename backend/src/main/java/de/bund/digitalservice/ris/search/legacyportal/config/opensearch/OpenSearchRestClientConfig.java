package de.bund.digitalservice.ris.search.legacyportal.config.opensearch;

import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.OpenSearchRestTemplate;
import org.opensearch.data.client.orhlc.RestClients;
import org.opensearch.data.client.orhlc.RestClients.RestClientConfigurationCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(
    basePackages = "de.bund.digitalservice.ris.search.legacyportal.repository")
@ComponentScan(basePackages = {"de.bund.digitalservice.ris.search.legacyportal.service"})
public class OpenSearchRestClientConfig extends AbstractOpenSearchConfiguration {

  private LegacyConfigurations configurationsOpensearch;

  @Autowired
  public OpenSearchRestClientConfig(LegacyConfigurations configurationsOpensearch) {
    this.configurationsOpensearch = configurationsOpensearch;
  }

  @Override
  public RestHighLevelClient opensearchClient() {
    final var keepAliveCallback =
        RestClientConfigurationCallback.from(
            clientConfigurer ->
                clientConfigurer.setDefaultIOReactorConfig(
                    IOReactorConfig.custom().setSoKeepAlive(true).build()));

    final ClientConfiguration clientConfiguration =
        ClientConfiguration.builder()
            .connectedTo(
                this.configurationsOpensearch.getHost()
                    + ":"
                    + this.configurationsOpensearch.getPort())
            .withClientConfigurer(keepAliveCallback)
            .build();

    return RestClients.create(clientConfiguration).rest();
  }

  @Override
  public ElasticsearchOperations elasticsearchOperations(
      ElasticsearchConverter elasticsearchConverter, RestHighLevelClient elasticsearchClient) {
    return new OpenSearchRestTemplate(opensearchClient(), elasticsearchConverter) {
      @Override
      public <T> T execute(OpenSearchRestTemplate.ClientCallback<T> callback) {
        try {
          return super.execute(callback);
        } catch (DataAccessResourceFailureException e) {
          return super.execute(callback);
        }
      }
    };
  }
}
