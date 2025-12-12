package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ratelimiting.DefaultRateLimitInterceptor;
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
class DefaultRateLimitTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  @TestBean private DefaultRateLimitInterceptor testInterceptor;

  static DefaultRateLimitInterceptor testInterceptor() {
    return new DefaultRateLimitInterceptor(2, 10);
  }

  @Test
  void legislationEndpointsGetsRateLimited() throws Exception {
    // first 2 calls will go through
    for (int i = 0; i < 2; i++) {
      mockMvc
          .perform(
              get(ApiConfig.Paths.LEGISLATION).with(csrf()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
    }
    // third one is being rate limited
    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION).with(csrf()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isTooManyRequests());
  }
}
