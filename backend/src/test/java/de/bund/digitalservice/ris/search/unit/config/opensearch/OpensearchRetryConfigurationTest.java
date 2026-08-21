package de.bund.digitalservice.ris.search.unit.config.opensearch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import de.bund.digitalservice.ris.search.config.opensearch.OpensearchRetryConfiguration;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;

class OpensearchRetryConfigurationTest {

  /** Retries without a delay, so the tests don't have to wait for the configured backoff */
  private final RetryTemplate retryTemplate =
      new RetryTemplate(
          RetryPolicy.builder()
              .maxRetries(2)
              .delay(Duration.ofMillis(1))
              .predicate(t -> true)
              .build());

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

  @Test
  void itReturnsTheResultOfASuccessfulOperation() {
    assertThat(OpensearchRetryConfiguration.executeWithRetries(retryTemplate, () -> "result"))
        .isEqualTo("result");
  }

  @Test
  void itRetriesFailingOperationsUntilTheConfiguredMaximumIsReached() {
    AtomicInteger attempts = new AtomicInteger();

    assertThatExceptionOfType(UncategorizedElasticsearchException.class)
        .isThrownBy(
            () ->
                OpensearchRetryConfiguration.executeWithRetries(
                    retryTemplate,
                    () -> {
                      attempts.incrementAndGet();
                      throw new UncategorizedElasticsearchException("all shards failed");
                    }));

    assertThat(attempts).hasValue(3);
  }

  @Test
  void itRethrowsTheOriginalFailureInsteadOfTheRetryExceptionOnceRetriesAreExhausted() {
    var failure = new UncategorizedElasticsearchException("all shards failed");

    assertThatExceptionOfType(UncategorizedElasticsearchException.class)
        .isThrownBy(
            () ->
                OpensearchRetryConfiguration.executeWithRetries(
                    retryTemplate,
                    () -> {
                      throw failure;
                    }))
        .isSameAs(failure);
  }

  @Test
  void itDoesNotRetryClientErrorsAndRethrowsTheOriginalFailure() {
    var configuredRetryTemplate = new OpensearchRetryConfiguration().openSearchRetryTemplate();
    AtomicInteger attempts = new AtomicInteger();
    var clientFailure =
        new UncategorizedElasticsearchException("all shards failed", 400, "response body", null);

    assertThatExceptionOfType(UncategorizedElasticsearchException.class)
        .isThrownBy(
            () ->
                OpensearchRetryConfiguration.executeWithRetries(
                    configuredRetryTemplate,
                    () -> {
                      attempts.incrementAndGet();
                      throw clientFailure;
                    }))
        .isSameAs(clientFailure);

    assertThat(attempts).hasValue(1);
  }
}
