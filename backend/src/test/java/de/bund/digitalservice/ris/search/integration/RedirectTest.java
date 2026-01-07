package de.bund.digitalservice.ris.search.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class RedirectTest extends ContainersIntegrationBase {

  private static final String SLASH = "/";
  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("In case the API path has trailing slash, it should redirect without traling slash")
  void shouldRedirectedWithTrailingSlash() throws Exception {
    mockMvc
        .perform(get(ApiConfig.Paths.CASELAW + SLASH))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ApiConfig.Paths.CASELAW));
  }

  @Test
  @DisplayName(
      "In case the API path has trailing slash and with query parameters, it should redirect without traling slash and with the query parameters")
  void shouldRedirectedWithTrailingSlashAndQueryParameters() throws Exception {
    String queryParametersMock = "?q=test&test=anotherTest";
    mockMvc
        .perform(get(ApiConfig.Paths.CASELAW + SLASH + queryParametersMock))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ApiConfig.Paths.CASELAW + queryParametersMock));
  }
}
