package de.bund.digitalservice.ris.search.config.posthog;

import com.posthog.server.PostHog;
import com.posthog.server.PostHogInterface;
import de.bund.digitalservice.ris.search.client.posthog.PostHogClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration class for setting up the PostHog clients used to send api metrics and user
 * feedback.
 */
@Configuration
public class PostHogConfig {

  /**
   * Registering the PostHogInterface bean Spring calls {@code close()} on shutdown, which flushes
   * any queued events.
   *
   * @param properties the configuration properties {@link PosthogProperties} containing the API key
   *     and host information required to set up the PostHog instance
   * @return the configured {@link com.posthog.server.PostHogInterface}
   */
  @Bean(destroyMethod = "close")
  public PostHogInterface posthogClient(PosthogProperties properties) {
    var builder =
        com.posthog.server.PostHogConfig.builder(properties.getApiKey())
            .host(properties.getHost())
            .debug(properties.isDebug());
    if (properties.getFlushAt() != null) {
      builder.flushAt(properties.getFlushAt());
    }
    if (properties.getFlushIntervalSeconds() != null) {
      builder.flushIntervalSeconds(properties.getFlushIntervalSeconds());
    }
    return PostHog.with(builder.build());
  }

  /**
   * Creates a PostHogClient bean for sending user feedback in specified environments.
   *
   * @param postHog the shared PostHog server client
   * @return a configured PostHogClient
   */
  @Bean
  @Profile({"test", "prototype"})
  public PostHogClient postHog(PostHogInterface postHog) {
    return new PostHogClient(postHog);
  }
}
