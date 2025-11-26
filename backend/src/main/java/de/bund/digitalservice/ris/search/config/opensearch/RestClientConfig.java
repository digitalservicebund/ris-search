package de.bund.digitalservice.ris.search.config.opensearch;

import org.apache.hc.core5.reactor.IOReactorConfig;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.OpenSearchRestTemplate;
import org.opensearch.data.client.orhlc.RestClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/** Class to configure the REST client which connects to opensearch in production without ssl. */
@Configuration
@Profile({"staging", "uat", "production", "prototype"})
@EnableElasticsearchRepositories(
    basePackages = "de.bund.digitalservice.ris.search.repository.opensearch")
public class RestClientConfig extends AbstractOpenSearchConfiguration {

  /**
   * No-argument constructor provided for frameworks/proxies.
   *
   * <p>Delegates to the main constructor with null parameters to avoid duplicate final-field
   * assignment.
   */
  public RestClientConfig() {
    this(null, null);
  }

  private final Configurations configurationsOpensearch;
  private final OpensearchSchemaSetup schemaSetup;

  /**
   * Constructs a RestClientConfig instance to configure and initialize the OpenSearch REST client.
   *
   * @param configurationsOpensearch the configuration properties for connecting to OpenSearch,
   *     including host, port, and other connection-related settings.
   * @param schemaSetup the object responsible for setting up and updating the OpenSearch schema.
   */
  @Autowired
  public RestClientConfig(
      Configurations configurationsOpensearch, OpensearchSchemaSetup schemaSetup) {
    this.configurationsOpensearch = configurationsOpensearch;
    this.schemaSetup = schemaSetup;
  }

  @Override
  public RestHighLevelClient opensearchClient() {
    final var keepAliveCallback =
        RestClients.RestClientConfigurationCallback.from(
            clientConfigurer ->
                clientConfigurer.setIOReactorConfig(
                    IOReactorConfig.custom().setSoKeepAlive(true).build()));

    final ClientConfiguration clientConfiguration =
        ClientConfiguration.builder()
            .connectedTo(
                this.configurationsOpensearch.getHost()
                    + ":"
                    + this.configurationsOpensearch.getPort())
            .withClientConfigurer(keepAliveCallback)
            .build();

    RestHighLevelClient restHighLevelClient =
        RestClients.create( // NOSONAR java:S2095 closed by spring @Bean(destroyMethod = "close")
                clientConfiguration)
            .rest();
    schemaSetup.updateOpensearchSchema(restHighLevelClient);
    return restHighLevelClient;
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
