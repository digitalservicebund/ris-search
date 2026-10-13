package de.bund.digitalservice.ris.search.client.posthog;

import com.posthog.server.PostHogCaptureOptions;
import com.posthog.server.PostHogInterface;
import java.util.HashMap;
import java.util.Map;

/** {@link PostHogClient} that sends events through the PostHog server SDK. */
public class PostHogServerClient implements PostHogClient {

  private final PostHogInterface postHog;

  /**
   * Client for interacting with PostHog analytics service.
   *
   * @param postHog the PostHog instance used for sending events
   */
  public PostHogServerClient(PostHogInterface postHog) {
    this.postHog = postHog;
  }

  @Override
  public void submitFeedback(String userId, String url, String text, String surveyId) {
    Map<String, Object> surveyFeedback = new HashMap<>();
    surveyFeedback.put("$survey_id", surveyId);
    surveyFeedback.put("$survey_response", text);
    surveyFeedback.put("$current_url", url);
    postHog.capture(
        userId, "survey sent", PostHogCaptureOptions.builder().properties(surveyFeedback).build());
  }

  @Override
  public void submitApiEvent(String uri, String method, String userAgent) {
    Map<String, Object> properties = new HashMap<>();
    properties.put("path", uri);
    properties.put("method", method);
    properties.put("user_agent", userAgent);

    PostHogCaptureOptions opts = PostHogCaptureOptions.builder().properties(properties).build();
    postHog.capture(null, "api_call_performed", opts);
  }
}
