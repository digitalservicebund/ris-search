package de.bund.digitalservice.ris.search.unit.service;

import static org.mockito.Mockito.verify;

import com.posthog.java.PostHog;
import de.bund.digitalservice.ris.search.service.PostHogService;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostHogServiceTest {
  @Test
  void testSendFeedback_WithValidParameters() {
    String surveyId = "validSurvey";
    String text = "Test feedback";
    String url = "http://user-url.test";
    String userId = "test_id";
    String phKey = "validKey";
    String phHost = "http://example.com";

    PostHogService service = new PostHogService();
    try (MockedConstruction<PostHog> mocked = Mockito.mockConstruction(PostHog.class)) {
      service.sendFeedback(userId, url, text, phKey, phHost, surveyId);
      PostHog postHogInstance = mocked.constructed().getFirst();
      Map<String, Object> expectedData = new HashMap<>();
      expectedData.put("$survey_id", surveyId);
      expectedData.put("$survey_response", text);
      expectedData.put("$current_url", url);
      verify(postHogInstance).capture(userId, "survey sent", expectedData);
      verify(postHogInstance).shutdown();
    }
  }
}
