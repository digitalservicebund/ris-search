package de.bund.digitalservice.ris.search.unit.client;

import static org.mockito.Mockito.verify;

import com.posthog.java.PostHog;
import de.bund.digitalservice.ris.search.client.posthog.PostHogClientImpl;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostHogClientImplTest {

  @Mock private PostHog postHog;

  @Test
  void callsPostHogWithValidParameters() {
    var postHogClient = new PostHogClientImpl(postHog);

    String text = "Test feedback";
    String url = "http://user-url.test";
    String userId = "test_id";
    String surveyId = "test_survey_id";

    postHogClient.submitFeedback(userId, url, text, surveyId);

    Map<String, Object> expectedData = new HashMap<>();
    expectedData.put("$survey_id", surveyId);
    expectedData.put("$survey_response", text);
    expectedData.put("$current_url", url);

    verify(postHog).capture(userId, "survey sent", expectedData);
  }
}
