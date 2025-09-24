package de.bund.digitalservice.ris.search.client.posthog;

/**
 * Dummy class that implements NOPs for posthog operations. This is to be used in environments which
 * don't support posthog feedback submission.
 */
public class PostHogClientDummy implements PostHogClient {

  @Override
  public void submitFeedback(String feedback, String url, String userId, String surveyId) {
    /**/
  }
}
