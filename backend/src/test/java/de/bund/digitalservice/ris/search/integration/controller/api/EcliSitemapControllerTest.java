package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class EcliSitemapControllerTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/2025/02/ABC/anotherfile.xml",
        "/ABC/02/01/anotherfile.xml",
        "/2025/ABC/01/anotherfile.xml"
      })
  void itValidatesIncomingRequests(String path) throws Exception {
    mockMvc.perform(get(ApiConfig.Paths.ECLICRAWLER + path)).andExpect(status().isBadRequest());
  }
}
