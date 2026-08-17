package de.bund.digitalservice.ris.search.config.opensearch;

import java.time.Duration;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.core.retry.Retryable;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.http.HttpStatusCode;

/** RetryConfiguration fo configure behaviour of opensearch query retries */
@Configuration
public class OpensearchRetryConfiguration {

  private static final Logger logger = LogManager.getLogger(OpensearchRetryConfiguration.class);

  /**
   * RetryTemplate Bean to control opensearch execution retry behaviour
   *
   * @return RetryTemplate
   */
  @Bean
  public RetryTemplate openSearchRetryTemplate() {
    RetryPolicy retryPolicy =
        RetryPolicy.builder()
            .maxRetries(2) // Total 3 attempts
            .delay(Duration.ofSeconds(1)) // Wait 1 second on the first retry
            .multiplier(2.0) // Wait twice as long with each retry
            .predicate(
                throwable -> {
                  if (isClientError(throwable)) {
                    return false;
                  }
                  logger.warn(
                      "OpenSearch failure. Error: {}. Will attempt retry...",
                      throwable.getMessage());
                  return true;
                })
            .build();

    return new RetryTemplate(retryPolicy);
  }

  /**
   * Whether the failure is Opensearch rejecting the request itself, e.g. because it can't parse the
   * query. Retrying can't turn a client error into a success, it would only delay the response the
   * caller is going to get anyway.
   *
   * @param throwable the failure to check
   * @return whether the request was rejected with a 4xx status
   */
  private static boolean isClientError(Throwable throwable) {
    if (!(throwable instanceof UncategorizedElasticsearchException exception)) {
      return false;
    }

    Integer statusCode = exception.getStatusCode();
    return statusCode != null && HttpStatusCode.valueOf(statusCode).is4xxClientError();
  }

  /**
   * Runs an operation through the given retry template, unwrapping the {@link RetryException} that
   * Spring throws once the retries are exhausted.
   *
   * <p>Without unwrapping, callers only see a generic retry error and can no longer tell what made
   * the operation fail, e.g. that OpenSearch rejected the query as unparseable. Rethrowing the
   * original failure keeps the exceptions callers handle the same as if the operation hadn't been
   * retried at all.
   *
   * @param <T> the type the operation returns
   * @param retryTemplate the retry template controlling the retry behaviour
   * @param operation the operation to execute
   * @return the result of the operation
   */
  @SneakyThrows
  public static <T> T executeWithRetries(RetryTemplate retryTemplate, Retryable<T> operation) {
    try {
      return retryTemplate.execute(operation);
    } catch (RetryException e) {
      throw e.getCause();
    }
  }
}
