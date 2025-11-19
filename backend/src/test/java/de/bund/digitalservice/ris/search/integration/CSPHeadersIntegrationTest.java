package de.bund.digitalservice.ris.search.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
public class CSPHeadersIntegrationTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  private final String CSP_HEADER_NAME = "Content-Security-Policy";
  private final String CSP_HEADER_VALUE =
      "default-src 'none'; script-src 'none'; style-src 'unsafe-inline'; img-src data:; connect-src 'none'; font-src 'none'; frame-src 'none'; object-src 'none'; media-src 'none';";

  @Test
  void shouldExposeEndpointsWithCSP() throws Exception {
    mockMvc
        .perform(get("/v1/legislation"))
        .andExpect(header().string(CSP_HEADER_NAME, CSP_HEADER_VALUE));
    mockMvc
        .perform(get("/v1/case-law"))
        .andExpect(header().string(CSP_HEADER_NAME, CSP_HEADER_VALUE));
    mockMvc
        .perform(get("/v1/literature"))
        .andExpect(header().string(CSP_HEADER_NAME, CSP_HEADER_VALUE));
    mockMvc
        .perform(get("/v1/literature/XXLU000000001.akn.xml"))
        .andExpect(header().string(CSP_HEADER_NAME, CSP_HEADER_VALUE));
    mockMvc
        .perform(get("/v1/literature/XXLU000000001.html"))
        .andExpect(header().string(CSP_HEADER_NAME, CSP_HEADER_VALUE));
    mockMvc
        .perform(get("/v1/statistics"))
        .andExpect(header().string(CSP_HEADER_NAME, CSP_HEADER_VALUE));
  }
}
