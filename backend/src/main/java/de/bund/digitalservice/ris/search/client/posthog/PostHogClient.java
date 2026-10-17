package de.bund.digitalservice.ris.search.client.posthog;

/** Client for interacting with PostHog analytics service. */
public interface PostHogClient {

  /**
   * Submits user feedback to PostHog.
   *
   * @param userId the ID of the user submitting the feedback
   * @param url the current URL where the feedback was submitted
   * @param text the feedback text provided by the user
   * @param surveyId the ID of the survey associated with the feedback
   */
  void submitFeedback(String userId, String url, String text, String surveyId);

  /**
   * Submits an api event to PostHog.
   *
   * @param uri path of the api call made
   * @param method Http Method made
   * @param userAgent userAgent
   */
  void submitApiEvent(String uri, String method, String userAgent);
}
