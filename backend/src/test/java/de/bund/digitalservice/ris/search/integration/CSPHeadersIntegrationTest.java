package de.bund.digitalservice.ris.search.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CSPHeadersIntegrationTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  private final String cspHeaderName = "Content-Security-Policy";
  private final String cspHeaderValue =
      "default-src 'none'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src data: 'self'; connect-src 'self'; font-src 'self'; frame-src 'none'; object-src 'none'; media-src 'none';";

  @Test
  void shouldExposeEndpointsWithCSP() throws Exception {
    mockMvc
        .perform(get("/v1/legislation"))
        .andExpect(header().string(cspHeaderName, cspHeaderValue));
    mockMvc.perform(get("/v1/case-law")).andExpect(header().string(cspHeaderName, cspHeaderValue));
    mockMvc
        .perform(get("/v1/literature"))
        .andExpect(header().string(cspHeaderName, cspHeaderValue));
    mockMvc
        .perform(get("/v1/literature/XXLU000000001.akn.xml"))
        .andExpect(header().string(cspHeaderName, cspHeaderValue));
    mockMvc
        .perform(get("/v1/literature/XXLU000000001.html"))
        .andExpect(header().string(cspHeaderName, cspHeaderValue));
    mockMvc
        .perform(get("/v1/statistics"))
        .andExpect(header().string(cspHeaderName, cspHeaderValue));
  }
}
