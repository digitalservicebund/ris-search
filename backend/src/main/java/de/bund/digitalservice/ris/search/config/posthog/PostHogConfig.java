package de.bund.digitalservice.ris.search.config.posthog;

import com.posthog.server.PostHog;
import com.posthog.server.PostHogInterface;
import de.bund.digitalservice.ris.search.client.posthog.PostHogClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration class for setting up the PostHog analytics client.
 *
 * <p>This class is responsible for defining the PostHogClient bean, which is used to interact with
 * the PostHog analytics service. The configuration is environment-specific and activated only for
 * specific profiles.
 */
@Configuration
public class PostHogConfig {

  /** PostHog EU cloud. Hardcoded so it can't be forgotten or misconfigured. */
  private static final String POSTHOG_HOST = "https://eu.i.posthog.com";

  /**
   * Creates and configures a PostHogClient bean for use in specified environments. Initializes a
   * PostHog instance using the provided PosthogProperties.
   *
   * @param properties the configuration properties containing the API key required to set up the
   *     PostHog instance
   * @return a configured PostH
   */
  @Bean
  @Profile("prototype")
  public PostHogClient postHog(PosthogProperties properties) {
    var config =
        com.posthog.server.PostHogConfig.builder(properties.getApiKey())
            .host(POSTHOG_HOST)
            .debug(properties.isDebug())
            .build();
    PostHogInterface posthogInterface = PostHog.with(config);
    return new PostHogClient(posthogInterface);
  }
}
