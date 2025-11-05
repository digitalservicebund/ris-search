package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.CaseLawTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.LiteratureTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.LiteratureRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
@WithJwt("jwtTokens/ValidAccessToken.json")
class StatisticsControllerTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;
  @Autowired private CaseLawRepository caseLawRepository;
  @Autowired private LiteratureRepository literatureRepository;
  @Autowired private NormsRepository normsRepository;

  @BeforeEach
  void setUpSearchControllerApiTest() {
    clearRepositoryData();
    normsRepository.saveAll(NormsTestData.allDocuments);
    caseLawRepository.saveAll(CaseLawTestData.allDocuments);
    literatureRepository.saveAll(LiteratureTestData.allDocuments);
  }

  @Test
  @DisplayName("Should return correct count Values")
  void endpointShouldReturnCorrectCount() throws Exception {
    long caseLawCount = caseLawRepository.count();
    long literatureCount = literatureRepository.count();
    long normsCount = normsRepository.count();

    mockMvc
        .perform(get(ApiConfig.Paths.STATISTICS).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.legislation.count").value(normsCount))
        .andExpect(jsonPath("$.case-law.count").value(caseLawCount))
        .andExpect(jsonPath("$.literature.count").value(literatureCount));
  }

  @Test
  @DisplayName("Should return cached count Values")
  void endpointShouldReturnCachedCount() throws Exception {
    long caseLawCount = caseLawRepository.count();
    long literatureCount = literatureRepository.count();
    long normsCount = normsRepository.count();

    mockMvc
        .perform(get(ApiConfig.Paths.STATISTICS).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.legislation.count").value(normsCount))
        .andExpect(jsonPath("$.case-law.count").value(caseLawCount))
        .andExpect(jsonPath("$.literature.count").value(literatureCount));

    normsRepository.deleteAll();
    literatureRepository.deleteAll();
    caseLawRepository.deleteAll();

    assertEquals(0L, caseLawRepository.count());
    assertEquals(0L, literatureRepository.count());
    assertEquals(0L, normsRepository.count());
  }
}
