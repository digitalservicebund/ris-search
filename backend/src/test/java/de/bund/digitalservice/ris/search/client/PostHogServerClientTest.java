package de.bund.digitalservice.ris.search.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;

import com.posthog.server.PostHogCaptureOptions;
import com.posthog.server.PostHogInterface;
import de.bund.digitalservice.ris.search.client.posthog.PostHogServerClient;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostHogServerClientTest {

  @Mock private PostHogInterface postHog;

  @Test
  void callsPostHogWithValidParameters() {
    var postHogClient = new PostHogServerClient(postHog);

    String text = "Test feedback";
    String url = "http://user-url.test";
    String userId = "test_id";
    String surveyId = "test_survey_id";

    postHogClient.submitFeedback(userId, url, text, surveyId);

    var options = ArgumentCaptor.forClass(PostHogCaptureOptions.class);
    verify(postHog).capture(eq(userId), eq("survey sent"), options.capture());
    assertThat(options.getValue().getProperties())
        .isEqualTo(Map.of("$survey_id", surveyId, "$survey_response", text, "$current_url", url));
  }

  @Test
  void submitsApiEventWithoutDistinctId() {
    var postHogClient = new PostHogServerClient(postHog);

    postHogClient.submitApiEvent("/v1/legislation", "GET", "test-agent");

    var options = ArgumentCaptor.forClass(PostHogCaptureOptions.class);
    verify(postHog).capture(isNull(), eq("api_call_performed"), options.capture());
    assertThat(options.getValue().getProperties())
        .isEqualTo(Map.of("path", "/v1/legislation", "method", "GET", "user_agent", "test-agent"));
  }
}
