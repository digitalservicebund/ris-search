package de.bund.digitalservice.ris.search.config.opensearch;

import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;

@Configuration
public class OpensearchRetryConfiguration {

  private static final Logger logger = LogManager.getLogger(OpensearchRetryConfiguration.class);

  @Bean
  public RetryTemplate openSearchRetryTemplate() {
    RetryPolicy retryPolicy =
        RetryPolicy.builder()
            .maxRetries(2) // Total 3 attempts
            .delay(Duration.ofSeconds(1)) // Wait 1 second on the first retry
            .multiplier(2.0) // Wait twice as long with each retry
            .predicate(
                throwable -> {
                  logger.warn(
                      "OpenSearch failure. Error: {}. Will attempt retry...",
                      throwable.getMessage());
                  return true;
                })
            .build();

    return new RetryTemplate(retryPolicy);
  }
}
