package de.bund.digitalservice.ris.search.miscellaneous;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TrailingSlashFilterIntegrationTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  @ParameterizedTest
  @CsvSource({
    "/v1/documents/, /v1/documents",
    "/v1/documents/?searchTerm=test, /v1/documents?searchTerm=test"
  })
  void itRedirectsToPathsWithoutTrailingSlashes(String path, String redirect) throws Exception {

    mockMvc
        .perform(get(path).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(redirect));
  }
}
