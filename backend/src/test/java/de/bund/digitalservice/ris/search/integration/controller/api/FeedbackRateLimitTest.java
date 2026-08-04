package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ratelimiting.FeedbackRateLimitInterceptor;
import de.bund.digitalservice.ris.search.controller.api.FeedbackController.FeedbackRequest;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class FeedbackRateLimitTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @TestBean private FeedbackRateLimitInterceptor testInterceptor;

  static FeedbackRateLimitInterceptor testInterceptor() {
    return new FeedbackRateLimitInterceptor(2, 10);
  }

  @Test
  void feedbackGetsRateLimited() throws Exception {
    var body = new FeedbackRequest("test feedback", "http://example.com", "test-distinct-id", null);
    var requestBody = objectMapper.writeValueAsString(body);

    // first 2 calls will go through
    for (int i = 0; i < 2; i++) {
      mockMvc
          .perform(
              post(ApiConfig.Paths.FEEDBACK)
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody))
          .andExpect(status().isOk());
    }
    // third one is being rate limited
    mockMvc
        .perform(
            post(ApiConfig.Paths.FEEDBACK)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isTooManyRequests());
  }
}
