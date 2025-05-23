package de.bund.digitalservice.ris.search.service;

import com.posthog.java.PostHog;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

/** Service class for sending feedback to PostHog. */
@Service
public class PostHogService {
  /**
   * Sends the feedback to PostHog survey endpoint.
   *
   * @param userId The user identifier for PostHog.
   * @param url The URL associated with the feedback.
   * @param text The text of the feedback to be sent.
   * @param phHost The PostHog host.
   * @param phKey The PostHog API key.
   * @param phSurveyId The PostHog survey ID.
   */
  public void sendFeedback(
      String userId, String url, String text, String phHost, String phKey, String phSurveyId) {
    PostHog postHog = new PostHog.Builder(phKey).host(phHost).build();
    Map<String, Object> surveyFeedback = new HashMap<>();
    surveyFeedback.put("$survey_id", phSurveyId);
    surveyFeedback.put("$survey_response", text);
    surveyFeedback.put("$current_url", url);
    postHog.capture(userId, "survey sent", surveyFeedback);
    postHog.shutdown();
  }
}
