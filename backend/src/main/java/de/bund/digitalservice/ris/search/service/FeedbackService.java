package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.client.posthog.PostHogClient;
import de.bund.digitalservice.ris.search.config.posthog.PosthogProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/** Service class for sending feedback to PostHog. */
@Profile({"test", "prototype"})
@Service
public class FeedbackService {

  private final PostHogClient postHogClient;
  private final PosthogProperties posthogProperties;

  /**
   * Constructs a PostHogService with the specified PostHog client and properties.
   *
   * @param postHogClient The PostHog client used to send feedback.
   * @param posthogProperties The PostHog configuration properties.
   */
  public FeedbackService(PostHogClient postHogClient, PosthogProperties posthogProperties) {
    this.postHogClient = postHogClient;
    this.posthogProperties = posthogProperties;
  }

  /**
   * Sends the feedback to PostHog survey endpoint.
   *
   * @param url The URL associated with the feedback.
   * @param text The text of the feedback to be sent.
   */
  public void sendFeedback(String url, String text) {
    postHogClient.submitFeedback(url, text, posthogProperties.getFeedbackSurveyId());
  }
}
