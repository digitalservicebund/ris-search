package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.repository.opensearch.AdministrativeDirectiveRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.LiteratureRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class StatisticsControllerTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;
  @Autowired private CaseLawRepository caseLawRepository;
  @Autowired private LiteratureRepository literatureRepository;
  @Autowired private NormsRepository normsRepository;
  @Autowired private AdministrativeDirectiveRepository administrativeDirectiveRepository;

  @BeforeEach
  void setUpSearchControllerApiTest() {
    resetRepositories();
  }

  @Test
  @DisplayName("Should return correct count Values")
  void endpointShouldReturnCorrectCount() throws Exception {
    long caseLawCount = caseLawRepository.count();
    long literatureCount = literatureRepository.count();
    long normsCount = normsRepository.count();
    long administrativeDirectiveCount = administrativeDirectiveRepository.count();

    mockMvc
        .perform(get(ApiConfig.Paths.STATISTICS).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.legislation.count").value(normsCount))
        .andExpect(jsonPath("$.case-law.count").value(caseLawCount))
        .andExpect(jsonPath("$.literature.count").value(literatureCount))
        .andExpect(
            jsonPath("$.administrative-directive.count").value(administrativeDirectiveCount));
  }
}
