package de.bund.digitalservice.ris.search.unit.service;

import static org.mockito.Mockito.verify;

import de.bund.digitalservice.ris.search.client.posthog.PostHogClient;
import de.bund.digitalservice.ris.search.config.posthog.PosthogProperties;
import de.bund.digitalservice.ris.search.service.PostHogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostHogServiceTest {

  @Mock private PostHogClient postHogClient;

  @Test
  void testSendFeedbackWithValidParameters() {
    var posthogConfig = new PosthogProperties();
    posthogConfig.setFeedbackSurveyId("validSurvey");

    String text = "Test feedback";
    String url = "http://user-url.test";
    String userId = "test_id";

    PostHogService service = new PostHogService(postHogClient, posthogConfig);
    service.sendFeedback(userId, url, text);

    verify(postHogClient).submitFeedback(userId, url, text, posthogConfig.getFeedbackSurveyId());
  }
}
