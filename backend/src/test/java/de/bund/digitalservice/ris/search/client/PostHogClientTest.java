package de.bund.digitalservice.ris.search.client;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;

import com.posthog.server.PostHog;
import de.bund.digitalservice.ris.search.client.posthog.PostHogClient;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostHogClientTest {

  @Mock private PostHog postHog;

  @Test
  void callsPostHogWithValidParameters() {
    var postHogClient = new PostHogClient(postHog);

    String text = "Test feedback";
    String url = "http://user-url.test";
    String surveyId = "test_survey_id";

    postHogClient.submitFeedback(url, text, surveyId);

    Map<String, Object> expectedData = new HashMap<>();
    expectedData.put("$survey_id", surveyId);
    expectedData.put("$survey_response", text);
    expectedData.put("$current_url", url);
    expectedData.put("$process_person_profile", false);

    verify(postHog)
        .capture(
            isNull(),
            eq("survey sent"),
            argThat(options -> expectedData.equals(options.getProperties())));
  }

  @Test
  void submitApiEventPassesPathUnchanged() {
    var postHogClient = new PostHogClient(postHog);

    postHogClient.submitApiEvent("/v1/legislation/§ BGB%41", "test-agent");

    Map<String, Object> expectedData = new HashMap<>();
    expectedData.put("$current_url", "/v1/legislation/§ BGB%41");
    expectedData.put("user_agent", "test-agent");
    expectedData.put("$process_person_profile", false);

    verify(postHog)
        .capture(
            isNull(),
            eq("api_call_performed"),
            argThat(options -> expectedData.equals(options.getProperties())));
  }

  @Test
  void shutdownCallPosthogShutdown() {
    var postHogClient = new PostHogClient(postHog);

    postHogClient.shutdown();
    verify(postHog).close();
  }
}
