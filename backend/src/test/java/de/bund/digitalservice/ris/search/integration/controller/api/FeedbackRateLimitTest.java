package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ratelimiting.FeedbackRateLimitInterceptor;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
@WithJwt("jwtTokens/ValidAccessToken.json")
public class FeedbackRateLimitTest {

  @Autowired private MockMvc mockMvc;

  @TestBean private FeedbackRateLimitInterceptor myFakeService;

  static FeedbackRateLimitInterceptor myFakeService() {
    return new FeedbackRateLimitInterceptor(2, 10);
  }

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
    // first 2 calls will go through
    for (int i = 0; i < 2; i++) {
      mockMvc
          .perform(
              get(ApiConfig.Paths.FEEDBACK)
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .params(testParams))
          .andExpect(status().isOk());
    }
    // third one is being rate limited
    mockMvc
        .perform(
            get(ApiConfig.Paths.FEEDBACK)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .params(testParams))
        .andExpect(status().isTooManyRequests());
  }
}
