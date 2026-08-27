package de.bund.digitalservice.ris.search.client.posthog;

import com.posthog.java.PostHog;
import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

/**
 * Client for interacting with PostHog analytics service.
 *
 * @param postHog the PostHog instance used for sending events
 */
public record PostHogClient(PostHog postHog) {

  /**
   * Submits user feedback to PostHog.
   *
   * @param userId the ID of the user submitting the feedback
   * @param url the current URL where the feedback was submitted
   * @param text the feedback text provided by the user
   * @param surveyId the ID of the survey associated with the feedback
   */
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
