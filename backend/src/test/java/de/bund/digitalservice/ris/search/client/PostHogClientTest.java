package de.bund.digitalservice.ris.search.client;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.posthog.server.PostHog;
import de.bund.digitalservice.ris.search.client.posthog.PostHogClient;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostHogClientTest {

  @Mock private PostHog postHog;

  @Test
  void callsPostHogWithValidParameters() {
    var postHogClient = new PostHogClient(postHog, 1.0);

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
    var postHogClient = new PostHogClient(postHog, 1.0);

    postHogClient.submitApiEvent("/v1/legislation/§ BGB%41", "test-agent");

    Map<String, Object> expectedData = new HashMap<>();
    expectedData.put("$current_url", "/v1/legislation/§ BGB%41");
    expectedData.put("user_agent", "test-agent");
    expectedData.put("sample_rate", 1.0);
    expectedData.put("$process_person_profile", false);

    verify(postHog)
        .capture(
            isNull(),
            eq("api_call_performed"),
            argThat(options -> expectedData.equals(options.getProperties())));
  }

  @Test
  void submitApiEventSkipsEventWithSampleRateZero() {
    var postHogClient = new PostHogClient(postHog, 0.0);

    postHogClient.submitApiEvent("/v1/case-law/KORE123", "test-agent");

    verifyNoInteractions(postHog);
  }

  @ParameterizedTest
  @ValueSource(doubles = {-0.1, 1.1})
  void rejectsInvalidSampleRate(double sampleRate) {
    assertThrows(IllegalArgumentException.class, () -> new PostHogClient(postHog, sampleRate));
  }

  @Test
  void shutdownCallPosthogShutdown() {
    var postHogClient = new PostHogClient(postHog, 1.0);

    postHogClient.shutdown();
    verify(postHog).close();
  }
}
