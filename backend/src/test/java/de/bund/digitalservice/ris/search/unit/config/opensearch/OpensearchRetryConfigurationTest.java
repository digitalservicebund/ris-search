package de.bund.digitalservice.ris.search.unit.config.opensearch;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.config.opensearch.OpensearchRetryConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;

class OpensearchRetryConfigurationTest {

  private final RetryPolicy configuredPolicy =
      new OpensearchRetryConfiguration().openSearchRetryTemplate().getRetryPolicy();

  @Test
  void itDoesNotRetryRequestsOpensearchRejectedAsClientErrors() {
    var parseFailure =
        new UncategorizedElasticsearchException("all shards failed", 400, "response body", null);

    assertThat(configuredPolicy.shouldRetry(parseFailure)).isFalse();
  }

  @Test
  void itRetriesServerSideAndConnectionFailures() {
    var serverFailure =
        new UncategorizedElasticsearchException("all shards failed", 503, "response body", null);
    var connectionFailure = new UncategorizedElasticsearchException("connection closed by peer");

    assertThat(configuredPolicy.shouldRetry(serverFailure)).isTrue();
    assertThat(configuredPolicy.shouldRetry(connectionFailure)).isTrue();
  }
}
