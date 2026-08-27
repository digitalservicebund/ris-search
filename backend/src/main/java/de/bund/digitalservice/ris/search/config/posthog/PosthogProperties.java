package de.bund.digitalservice.ris.search.config.posthog;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for integration with the PostHog analytics service.
 *
 * <p>This class holds the configurable properties required for setting up the PostHog integration.
 * The properties are populated from the external configuration source, typically application.yml or
 * application.properties, using the prefix "posthog".
 *
 * <p>Properties included: - apiKey: The API key used to authenticate with the PostHog service. -
 * host: The hostname or base URL of the PostHog service. - feedbackSurveyId: The identifier for the
 * feedback survey configured in PostHog.
 *
 * <p>These properties are utilized by various components, such as `PostHogConfig` and
 * `PostHogService`, to initialize PostHog clients and submit analytics data or user feedback to the
 * configured PostHog service.
 */
@Component
@ConfigurationProperties(prefix = "posthog")
@Getter
@Setter
public class PosthogProperties {
  private String apiKey;
  private String host;
  private String feedbackSurveyId;
}
