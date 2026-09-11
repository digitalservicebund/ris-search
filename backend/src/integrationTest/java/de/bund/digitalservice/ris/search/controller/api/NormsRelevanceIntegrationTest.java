package de.bund.digitalservice.ris.search.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import de.bund.digitalservice.ris.SharedTestConstants;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class NormsRelevanceIntegrationTest extends ContainersIntegrationBase {

  @Autowired private IndexNormsService indexNormsService;
  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("Should return most relevant expression for a day")
  void shouldReturnMostRelevantExpressionForADay() throws Exception {
    addNormXmlFiles(NormsTestData.s102WorkExpressions);
    indexNormsService.reindexAll(SharedTestConstants.TIMESTAMP_2024_01_01_AS_STRING);

    // A very old date should return the oldest expression
    DocumentContext json =
        JsonPath.parse(
            mockMvc
                .perform(
                    get(ApiConfig.Paths.LEGISLATION
                            + "?eli="
                            + NormsTestData.S_102_WORK_ELI
                            + "&mostRelevantOn=1900-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString());
    assertThat(json.read("$.member.length()", Integer.class)).isEqualTo(1);
    assertThat(json.read("$.member[0].item.legislationIdentifier", String.class))
        .isEqualTo("eli/bund/bgbl-1/1991/s102/1991-01-01/1/deu");

    // A date where 1 expression was in force return that expression
    json =
        JsonPath.parse(
            mockMvc
                .perform(
                    get(ApiConfig.Paths.LEGISLATION
                            + "?eli="
                            + NormsTestData.S_102_WORK_ELI
                            + "&mostRelevantOn=1991-06-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString());
    assertThat(json.read("$.member.length()", Integer.class)).isEqualTo(1);
    assertThat(json.read("$.member[0].item.legislationIdentifier", String.class))
        .isEqualTo("eli/bund/bgbl-1/1991/s102/1991-01-01/1/deu");

    // A date where no expressions were in force should return the next to be in force
    json =
        JsonPath.parse(
            mockMvc
                .perform(
                    get(ApiConfig.Paths.LEGISLATION
                            + "?eli="
                            + NormsTestData.S_102_WORK_ELI
                            + "&mostRelevantOn=1996-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString());
    assertThat(json.read("$.member.length()", Integer.class)).isEqualTo(1);
    assertThat(json.read("$.member[0].item.legislationIdentifier", String.class))
        .isEqualTo("eli/bund/bgbl-1/1991/s102/2020-01-01/1/deu");

    // A date far in the future will return the last expression (ausserkraft undefined)
    json =
        JsonPath.parse(
            mockMvc
                .perform(
                    get(ApiConfig.Paths.LEGISLATION
                            + "?eli="
                            + NormsTestData.S_102_WORK_ELI
                            + "&mostRelevantOn=5000-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString());
    assertThat(json.read("$.member.length()", Integer.class)).isEqualTo(1);
    assertThat(json.read("$.member[0].item.legislationIdentifier", String.class))
        .isEqualTo("eli/bund/bgbl-1/1991/s102/2050-01-01/1/deu");
  }

  @Test
  @DisplayName("Should allow filtering norms by abbreviation and most relevantOn date")
  void shouldReturnNormsFilteringByAbbreviationAndMostRelevantOn() throws Exception {
    // Note a Norm with the same abbreviation but different date is
    // already added via the ContainerIntegrationBase class
    LocalDate date = LocalDate.of(2025, Month.NOVEMBER, 3);
    normsRepository.save(
        Norm.builder()
            .id("eli/2024/teg/4/exp")
            .abbreviation("TeG")
            .officialTitle("This is it")
            .entryIntoForceDate(date)
            .expiryDate(date)
            .build());

    final String uri = ApiConfig.Paths.LEGISLATION + "?abbreviation=Teg&mostRelevantOn=2025-11-03";
    mockMvc
        .perform(get(uri).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.member", hasSize(1)),
            jsonPath("$.member[0]['item'].abbreviation", is("TeG")),
            jsonPath("$.member[0]['item'].name", is("This is it")));
  }
}
