package de.bund.digitalservice.ris.search.config.opensearch;

import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.RestClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/** Class to configure the REST client which connects to opensearch in local environment */
@Configuration
@Profile("test")
@EnableElasticsearchRepositories(
    basePackages = "de.bund.digitalservice.ris.search.repository.opensearch")
public class RestClientConfigDev extends AbstractOpenSearchConfiguration {

  private Configurations configurations;

  @Autowired
  public RestClientConfigDev(Configurations configurationsOpensearch) {
    this.configurations = configurationsOpensearch;
  }

  @Override
  public RestHighLevelClient opensearchClient() {
    ClientConfiguration config =
        ClientConfiguration.builder()
            .connectedTo(String.format("%s:%s", configurations.getHost(), configurations.getPort()))
            .build();
    return RestClients.create( // NOSONAR java:S2095 closed by spring @Bean(destroyMethod = "close")
            config)
        .rest();
  }
}
