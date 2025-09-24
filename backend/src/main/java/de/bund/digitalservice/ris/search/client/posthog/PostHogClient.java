package de.bund.digitalservice.ris.search.client.posthog;

public interface PostHogClient {
  void submitFeedback(String userId, String url, String text, String surveyId);
}
