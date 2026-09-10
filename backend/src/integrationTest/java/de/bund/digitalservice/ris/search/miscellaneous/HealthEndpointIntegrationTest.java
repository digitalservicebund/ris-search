package de.bund.digitalservice.ris.search.miscellaneous;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class HealthEndpointIntegrationTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldExposeHealthEndpoint() throws Exception {
    mockMvc.perform(get("/actuator/health")).andDo(print()).andExpect(status().isOk());
  }
}
