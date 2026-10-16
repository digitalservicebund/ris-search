package de.bund.digitalservice.ris.testmocks;

import de.bund.digitalservice.ris.search.client.posthog.PostHogClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/** This client is a mock so we don't actually call posthog while running tests */
@TestConfiguration
public class PostHogTestConfig {

  @Bean
  public PostHogClient postHog() {
    return Mockito.mock(PostHogClient.class);
  }
}
