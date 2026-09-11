package de.bund.digitalservice.ris.search.controller.api;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class NormsSortIntegrationTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  @Test
  void itSortsByTemporalCoverageFrom() throws Exception {

    normsRepository.deleteAll();

    var normTestOne =
        Norm.builder()
            .id("n1")
            .officialTitle("title1")
            .entryIntoForceDate(LocalDate.of(2026, Month.JANUARY, 1))
            .build();

    var normTestTwo =
        Norm.builder()
            .id("id2")
            .officialTitle("title2")
            .entryIntoForceDate(LocalDate.of(2025, Month.JANUARY, 1))
            .build();

    normsRepository.saveAll(List.of(normTestOne, normTestTwo));

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION + String.format("?sort=%s", "temporalCoverageFrom"))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member[*].item.name", is(List.of("title2", "title1"))));

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION + String.format("?sort=%s", "-temporalCoverageFrom"))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member[*].item.name", is(List.of("title1", "title2"))));
  }
}
