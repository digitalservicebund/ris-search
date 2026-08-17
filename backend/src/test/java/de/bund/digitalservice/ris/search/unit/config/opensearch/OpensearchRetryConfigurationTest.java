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

  @Test
  void itReturnsTheResultOfASuccessfulOperation() {
    assertThat(OpensearchRetryConfiguration.executeWithRetries(retryTemplate, () -> "result"))
        .isEqualTo("result");
  }

  @Test
  void itRetriesFailingOperations() {
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
  void itRethrowsTheFailureInsteadOfTheRetryException() {
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
}
