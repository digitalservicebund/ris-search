package de.bund.digitalservice.ris.search.client.posthog;

import com.posthog.java.PostHog;
import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

public record PostHogClient(PostHog postHog) {

  public void submitFeedback(String userId, String url, String text, String surveyId) {
    Map<String, Object> surveyFeedback = new HashMap<>();
    surveyFeedback.put("$survey_id", surveyId);
    surveyFeedback.put("$survey_response", text);
    surveyFeedback.put("$current_url", url);
    postHog.capture(userId, "survey sent", surveyFeedback);
  }

  @PreDestroy
  public void shutdown() {
    postHog.shutdown();
  }
}
