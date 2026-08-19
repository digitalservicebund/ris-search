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
                    logger.warn(
                        "OpenSearch rejected the request as a client error. Error: {}. Not"
                            + " retrying.",
                        throwable.getMessage());
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
   * Runs the operation through the given retry template.
   *
   * <p>This method unwraps the {@link RetryException} that Spring throws in two cases:
   *
   * <ul>
   *   <li>the operation uses all its retries
   *   <li>the {@link RetryPolicy} does not retry the failure
   * </ul>
   *
   * <p>Without this step, the caller sees only a generic retry error. The caller cannot tell why
   * the operation failed. For example, the caller cannot tell that OpenSearch rejected the query
   * because it could not parse the query. This method rethrows the original failure. As a result,
   * callers handle the same exceptions as when the operation runs without retries.
   *
   * @param <T> the type the operation returns
   * @param retryTemplate the retry template that controls the retry behavior
   * @param operation the operation to run
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
}
