package de.bund.digitalservice.ris.search.config.opensearch;

import io.sentry.Sentry;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.ssl.SSLContextBuilder;
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

/** Class to configure the REST client which connects to opensearch in non-local environment */
@Configuration
@Profile({"staging"})
@EnableElasticsearchRepositories(
    basePackages = "de.bund.digitalservice.ris.search.repository.opensearch")
public class RestClientConfigStaging extends AbstractOpenSearchConfiguration {

  private static final long OPENSEARCH_TIMEOUT = 30000;

  private Configurations configurations;

  @Autowired
  public RestClientConfigStaging(Configurations configurationsOpensearch) {
    this.configurations = configurationsOpensearch;
  }

  @Override
  public RestHighLevelClient opensearchClient() {
    final var keepAliveCallback =
        RestClients.RestClientConfigurationCallback.from(
            clientConfigurer ->
                clientConfigurer.setDefaultIOReactorConfig(
                    IOReactorConfig.custom().setSoKeepAlive(true).build()));

    SSLContext sslContext = buildSSLContext();

    ClientConfiguration config =
        ClientConfiguration.builder()
            .connectedTo(String.format("%s:%s", configurations.getHost(), configurations.getPort()))
            .usingSsl(sslContext, NoopHostnameVerifier.INSTANCE)
            .withClientConfigurer(keepAliveCallback)
            .withConnectTimeout(OPENSEARCH_TIMEOUT)
            .withSocketTimeout(OPENSEARCH_TIMEOUT)
            .withBasicAuth(configurations.getUsername(), configurations.getPassword())
            .build();
    return RestClients.create( // NOSONAR java:S2095 closed by spring @Bean(destroyMethod = "close")
            config)
        .rest();
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
          Sentry.captureException(e);
          return super.execute(callback);
        }
      }
    };
  }

  private SSLContext buildSSLContext() {
    try {
      SSLContextBuilder contextBuilder =
          new SSLContextBuilder().loadTrustMaterial(null, new TrustAllStrategy());
      return contextBuilder.build();
    } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException ex) {
      throw new IllegalArgumentException("Failed to initialize SSL Context instance", ex);
    }
  }
}
