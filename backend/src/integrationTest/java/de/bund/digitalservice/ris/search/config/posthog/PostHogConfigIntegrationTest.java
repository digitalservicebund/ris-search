package de.bund.digitalservice.ris.search.config.posthog;

import static org.assertj.core.api.Assertions.assertThat;

import com.posthog.server.PostHogInterface;
import de.bund.digitalservice.ris.search.client.posthog.NoOpPostHogClient;
import de.bund.digitalservice.ris.search.client.posthog.PostHogClient;
import de.bund.digitalservice.ris.search.client.posthog.PostHogServerClient;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * Verifies which {@link PostHogClient} is registered per profile. Only the PostHog configuration is
 * loaded, since the full application context requires the {@code test} profile.
 */
class PostHogConfigIntegrationTest {

  @EnableConfigurationProperties(PosthogProperties.class)
  static class PropertiesConfig {}

  @Nested
  @SpringBootTest(classes = {PropertiesConfig.class, PostHogConfig.class})
  @ActiveProfiles("dev")
  class WithoutPostHogProfile {

    @Autowired private ApplicationContext context;

    @Test
    void registersNoOpClient() {
      assertThat(context.getBean(PostHogClient.class)).isInstanceOf(NoOpPostHogClient.class);
      assertThat(context.getBeanNamesForType(PostHogInterface.class)).isEmpty();
    }
  }

  @Nested
  @SpringBootTest(classes = {PropertiesConfig.class, PostHogConfig.class})
  @ActiveProfiles("test")
  class WithPostHogProfile {

    @Autowired private ApplicationContext context;

    @Test
    void registersServerClient() {
      assertThat(context.getBean(PostHogClient.class)).isInstanceOf(PostHogServerClient.class);
      assertThat(context.getBeanNamesForType(PostHogInterface.class)).hasSize(1);
    }
  }
}
