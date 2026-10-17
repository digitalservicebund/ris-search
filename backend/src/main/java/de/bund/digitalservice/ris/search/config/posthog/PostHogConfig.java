package de.bund.digitalservice.ris.search.config.posthog;

import com.posthog.server.PostHog;
import com.posthog.server.PostHogInterface;
import de.bund.digitalservice.ris.search.client.posthog.NoOpPostHogClient;
import de.bund.digitalservice.ris.search.client.posthog.PostHogClient;
import de.bund.digitalservice.ris.search.client.posthog.PostHogServerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration class for setting up the PostHog clients used to send api metrics and user
 * feedback. PostHog is only available in specific environments, all others use a {@link
 * NoOpPostHogClient}.
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
  @Profile({"prototype"})
  public PostHogInterface postHogInterface(PosthogProperties properties) {
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
   * Creates the {@link PostHogClient} sending events to PostHog.
   *
   * @param postHog the configured PostHog server SDK instance
   * @return a {@link PostHogServerClient}
   */
  @Bean
  @Profile({"prototype"})
  public PostHogClient postHogClient(PostHogInterface postHog) {
    return new PostHogServerClient(postHog);
  }

  /**
   * Fallback {@link PostHogClient} for environments without PostHog.
   *
   * @return a {@link NoOpPostHogClient}
   */
  @Bean
  @Profile({"!prototype"})
  public PostHogClient noOpPostHogClient() {
    return new NoOpPostHogClient();
  }
}
