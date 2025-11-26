package de.bund.digitalservice.ris.search.config.posthog;

import com.posthog.java.PostHog;
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

  /**
   * Creates and configures a PostHogClient bean for use in specified environments. Initializes a
   * PostHog instance using the provided PosthogProperties.
   *
   * @param properties the configuration properties containing the API key and host information
   *     required to set up the PostHog instance
   * @return a configured PostH
   */
  @Bean
  @Profile({"test", "prototype"})
  public PostHogClient postHog(PosthogProperties properties) {
    var postHog = new PostHog.Builder(properties.getApiKey()).host(properties.getHost()).build();
    return new PostHogClient(postHog);
  }
}
