package de.bund.digitalservice.ris.search.config.posthog;

import com.posthog.java.PostHog;
import de.bund.digitalservice.ris.search.client.posthog.PostHogClient;
import de.bund.digitalservice.ris.search.client.posthog.PostHogClientDummy;
import de.bund.digitalservice.ris.search.client.posthog.PostHogClientImpl;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class PostHogConfig {
  @Bean
  @Profile({"prototype"})
  public PostHogClient postHog(PosthogProperties properties) {
    var postHog = new PostHog.Builder(properties.getApiKey()).host(properties.getHost()).build();
    return new PostHogClientImpl(postHog);
  }

  @Bean
  @Profile({"!prototype"})
  public PostHogClient postHogDummy() {
    return new PostHogClientDummy();
  }

  @Bean
  public DisposableBean posthogShutdownHook(PostHogClient postHogClient) {
    return postHogClient::shutdown;
  }
}
