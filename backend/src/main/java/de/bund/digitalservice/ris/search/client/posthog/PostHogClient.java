package de.bund.digitalservice.ris.search.client.posthog;

import com.posthog.server.PostHogCaptureOptions;
import com.posthog.server.PostHogInterface;
import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Client for interacting with PostHog analytics service.
 *
 * @param postHog the PostHog instance used for sending events
 * @param apiCallsSampleRate fraction of api events (between 0 and 1) that are sent to PostHog
 */
public record PostHogClient(PostHogInterface postHog, double apiCallsSampleRate) {

  /**
   * Validates the sample rate.
   *
   * @param postHog the PostHog instance used for sending events
   * @param apiCallsSampleRate fraction of api events (between 0 and 1) that are sent to PostHog
   */
  public PostHogClient {
    if (apiCallsSampleRate < 0 || apiCallsSampleRate > 1) {
      throw new IllegalArgumentException(
          "apiCallsSampleRate must be between 0 and 1, but was " + apiCallsSampleRate);
    }
  }

  /**
   * Submits user feedback to PostHog.
   *
   * @param url the current URL where the feedback was submitted
   * @param text the feedback text provided by the user
   * @param surveyId the ID of the survey associated with the feedback
   */
  public void submitFeedback(String url, String text, String surveyId) {
    Map<String, Object> surveyFeedback = new HashMap<>();
    surveyFeedback.put("$survey_id", surveyId);
    surveyFeedback.put("$survey_response", text);
    surveyFeedback.put("$current_url", url);
    surveyFeedback.put("$process_person_profile", false);

    postHog.capture(
        null, "survey sent", PostHogCaptureOptions.builder().properties(surveyFeedback).build());
  }

  /**
   * Submits an api event to PostHog, sampled with {@link #apiCallsSampleRate}. Each submitted event
   * carries the sample rate, so it represents {@code 1 / sample_rate} api calls.
   *
   * @param path decoded path of the api call made
   * @param userAgent userAgent
   */
  public void submitApiEvent(String path, String userAgent) {
    if (ThreadLocalRandom.current().nextDouble() >= apiCallsSampleRate) {
      return;
    }

    Map<String, Object> properties = new HashMap<>();
    properties.put("$current_url", path);
    properties.put("user_agent", userAgent);
    properties.put("sample_rate", apiCallsSampleRate);
    // do not create a person profile in posthog for api calls
    properties.put("$process_person_profile", false);

    // a personless UUID is generated on null distinctId
    postHog.capture(
        null, "api_call_performed", PostHogCaptureOptions.builder().properties(properties).build());
  }

  @PreDestroy
  public void shutdown() {
    postHog.close();
  }
}
