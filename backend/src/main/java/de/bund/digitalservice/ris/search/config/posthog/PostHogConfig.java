package de.bund.digitalservice.ris.search.config.posthog;

import com.posthog.java.PostHog;
import de.bund.digitalservice.ris.search.client.posthog.PostHogClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class PostHogConfig {
  @Bean
  @Profile({"test", "prototype"})
  public PostHogClient postHog(PosthogProperties properties) {
    var postHog = new PostHog.Builder(properties.getApiKey()).host(properties.getHost()).build();
    return new PostHogClient(postHog);
  }
}
