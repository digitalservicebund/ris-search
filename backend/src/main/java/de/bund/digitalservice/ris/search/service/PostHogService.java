package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.client.posthog.FeedbackClient;
import de.bund.digitalservice.ris.search.config.posthog.PosthogProperties;
import org.springframework.stereotype.Service;

/** Service class for sending feedback to PostHog. */
@Service
public class PostHogService {

  private final FeedbackClient postHogClient;
  private final PosthogProperties posthogProperties;

  /**
   * Constructs a PostHogService with the specified PostHog client and properties.
   *
   * @param postHogClient The PostHog client used to send feedback.
   * @param posthogProperties The PostHog configuration properties.
   */
  public PostHogService(FeedbackClient postHogClient, PosthogProperties posthogProperties) {
    this.postHogClient = postHogClient;
    this.posthogProperties = posthogProperties;
  }

  /**
   * Sends the feedback to PostHog survey endpoint.
   *
   * @param userId The user identifier for PostHog.
   * @param url The URL associated with the feedback.
   * @param text The text of the feedback to be sent.
   */
  public void sendFeedback(String userId, String url, String text) {
    postHogClient.submitFeedback(userId, url, text, posthogProperties.getFeedbackSurveyId());
  }
}
