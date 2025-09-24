package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
@WithJwt("jwtTokens/ValidAccessToken.json")
class EcliSitemapControllerTest {

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
