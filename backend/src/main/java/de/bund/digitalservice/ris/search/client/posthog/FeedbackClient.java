package de.bund.digitalservice.ris.search.client.posthog;

/** Client to store user feedback */
public interface FeedbackClient {

  /**
   * Submits user feedback to the client.
   *
   * @param userId the ID of the user submitting the feedback
   * @param url the current URL where the feedback was submitted
   * @param text the feedback text provided by the user
   * @param surveyId the ID of the survey associated with the feedback
   */
  void submitFeedback(String userId, String url, String text, String surveyId);
}
