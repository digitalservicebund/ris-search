package de.bund.digitalservice.ris.search.miscellaneous;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ApiKeyIntegrationTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  @Test
  void testValidApiKey_ShouldPass() throws Exception {
    mockMvc
        .perform(get("/v1/legislation").header("X-Api-Key", "prefix_test"))
        .andExpect(status().isOk());
  }

  @Test
  @Disabled("Pending An addition of a Profile for testing Production API")
  void testInvalidApiKey_ShouldBeUnauthorized() throws Exception {
    // Test with missing API key
    mockMvc.perform(get("/your-endpoint")).andExpect(status().isUnauthorized());

    // Test with incorrect API key
    mockMvc
        .perform(get("/your-endpoint").header("X-Api-Key", "prefix_something_else"))
        .andExpect(status().isUnauthorized());
    // Test with no prefix
    mockMvc
        .perform(get("/your-endpoint").header("X-Api-Key", "test"))
        .andExpect(status().isUnauthorized());
    // Test with other prefix
    mockMvc
        .perform(get("/your-endpoint").header("X-Api-Key", "other_prefix_test"))
        .andExpect(status().isUnauthorized());
  }
}
