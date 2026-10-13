package de.bund.digitalservice.ris.search.client.posthog;

/** {@link PostHogClient} that discards all events. Used when PostHog is not configured. */
public class NoOpPostHogClient implements PostHogClient {

  @Override
  public void submitFeedback(String userId, String url, String text, String surveyId) {
    // PostHog is not configured, nothing to submit
  }

  @Override
  public void submitApiEvent(String uri, String method, String userAgent) {
    // PostHog is not configured, nothing to submit
  }
}
