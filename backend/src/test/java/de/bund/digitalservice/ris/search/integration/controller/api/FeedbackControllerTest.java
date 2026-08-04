package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.controller.api.FeedbackController.FeedbackRequest;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.service.PostHogService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class FeedbackControllerTest extends ContainersIntegrationBase {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private PostHogService postHogService;

  private static final String TEXT = "test feedback";
  private static final String URL = "http://example.com";
  private static final String USER_ID = "test-distinct-id";

  @Test
  void feedbackCanBeSentSuccessfullyToPostHog() throws Exception {
    var body = new FeedbackRequest(TEXT, URL, USER_ID, null);

    mockMvc
        .perform(
            post(ApiConfig.Paths.FEEDBACK)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "message": "Feedback sent successfully"
                    }
                    """));
  }

  @Test
  void throwsValidationErrorIfAParameterIsMissing() throws Exception {
    var body = new FeedbackRequest(TEXT, URL, null, null);

    mockMvc
        .perform(
            post(ApiConfig.Paths.FEEDBACK)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isUnprocessableContent())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.errors.*.code").value("invalid_parameter_value"))
        .andExpect(jsonPath("$.errors.*.parameter").value("userId"))
        .andExpect(jsonPath("$.errors.*.message").value("must not be null"));
  }

  @Test
  void feedbackIsIgnoredWhenHoneypotIsFilled() throws Exception {
    var body = new FeedbackRequest(TEXT, URL, USER_ID, "I am a bot");

    mockMvc
        .perform(
            post(ApiConfig.Paths.FEEDBACK)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Feedback sent successfully"));

    verify(postHogService, never()).sendFeedback(anyString(), anyString(), anyString());
  }

  @Test
  void feedbackIsProcessedWhenHoneypotIsEmpty() throws Exception {
    var body = new FeedbackRequest(TEXT, URL, USER_ID, "");

    mockMvc
        .perform(
            post(ApiConfig.Paths.FEEDBACK)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk());

    verify(postHogService, times(1)).sendFeedback(USER_ID, URL, TEXT);
  }
}
