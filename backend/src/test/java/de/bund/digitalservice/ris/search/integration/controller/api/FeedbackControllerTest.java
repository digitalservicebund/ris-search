package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class FeedbackControllerTest extends ContainersIntegrationBase {
  @Autowired private MockMvc mockMvc;

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
        .andExpect(status().isUnprocessableEntity())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.errors.*.code").value("information_missing"))
        .andExpect(jsonPath("$.errors.*.parameter").value("user_id"))
        .andExpect(
            jsonPath("$.errors.*.message")
                .value(
                    "Required request parameter 'user_id' for method parameter type String is not present"));
  }
}
