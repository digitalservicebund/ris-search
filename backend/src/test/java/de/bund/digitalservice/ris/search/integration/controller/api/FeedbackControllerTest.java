package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.config.ApiConfig;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class FeedbackControllerTest extends ContainersIntegrationBase {
  @Autowired private MockMvc mockMvc;
  @MockitoBean private PostHogService postHogService;

  MultiValueMap<String, String> testParams =
      new LinkedMultiValueMap<>() {
        {
          add("text", "test feedback");
          add("url", "http://example.com");
          add("user_id", "test-distinct-id");
        }
      };

  @Test
  void feedbackCanBeSentSuccessfullyToPostHog() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.FEEDBACK)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .params(testParams))
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
    MultiValueMap<String, String> invalidParams = testParams;
    invalidParams.remove("user_id");
    mockMvc
        .perform(
            get(ApiConfig.Paths.FEEDBACK)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .params(invalidParams))
        .andExpect(status().isUnprocessableContent())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.errors.*.code").value("information_missing"))
        .andExpect(jsonPath("$.errors.*.parameter").value("user_id"))
        .andExpect(
            jsonPath("$.errors.*.message")
                .value(
                    "Required request parameter 'user_id' for method parameter type String is not present"));
  }

  MultiValueMap<String, String> testParams() {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("text", "test feedback");
    params.add("url", "http://example.com");
    params.add("user_id", "test-distinct-id");
    return params;
  }

  @Test
  void feedbackIsIgnoredWhenHoneypotIsFilled() throws Exception {
    MultiValueMap<String, String> botParams = testParams();
    botParams.add("name", "I am a bot");

    mockMvc
        .perform(
            get(ApiConfig.Paths.FEEDBACK)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .params(botParams))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Feedback sent successfully"));

    verify(postHogService, never()).sendFeedback(anyString(), anyString(), anyString());
  }

  @Test
  void feedbackIsProcessedWhenHoneypotIsEmpty() throws Exception {
    MultiValueMap<String, String> humanParams = testParams();
    humanParams.add("name", "");

    mockMvc
        .perform(
            get(ApiConfig.Paths.FEEDBACK)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .params(humanParams))
        .andExpect(status().isOk());

    verify(postHogService, times(1))
        .sendFeedback("test-distinct-id", "http://example.com", "test feedback");
  }
}
