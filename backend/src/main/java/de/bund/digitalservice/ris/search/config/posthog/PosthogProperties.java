package de.bund.digitalservice.ris.search.config.posthog;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "posthog")
@Getter
@Setter
public class PosthogProperties {
  private String apiKey;
  private String host;
  private String feedbackSurveyId;
}
