package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.oneOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import de.bund.digitalservice.ris.TestJsonUtils;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.CaseLawTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.LiteratureTestData;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.repository.opensearch.AdministrativeDirectiveRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.LiteratureRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.commons.collections4.IteratorUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class AllDocumentsSearchControllerAPITest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;
  @Autowired private CaseLawRepository caseLawRepository;
  @Autowired private LiteratureRepository literatureRepository;
  @Autowired private AdministrativeDirectiveRepository administrativeDirectiveRepository;

  @Test
  @DisplayName("Should return correct result for term search, with textMatches")
  void shouldReturnCorrectResultForFilterSearch() throws Exception {
    String query = "?searchTerm=Test";

    mockMvc
        .perform(get(ApiConfig.Paths.DOCUMENT + query).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.member", hasSize(5)),
            jsonPath(
                "$.member[*].textMatches[*].name",
                everyItem(
                    either(
                            oneOf(
                                "name",
                                "headnote",
                                "otherHeadnote",
                                "caseFacts",
                                "headline",
                                "outline",
                                "longText",
                                "otherLongText",
                                "dissentingOpinion",
                                "decisionGrounds",
                                "fileNumbers",
                                "shortReport"))
                        .or(matchesRegex("§ \\d .+")))));
  }

  @Test
  @DisplayName("Should correctly highlight case law file numbers")
  void shouldReturnTextMatchForFileNumber() throws Exception {
    String fileNumber = CaseLawTestData.allDocuments.getFirst().fileNumbers().getFirst();
    String query = "?searchTerm=" + fileNumber;

    mockMvc
        .perform(get(ApiConfig.Paths.DOCUMENT + query).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath(
                "$.member[0].textMatches[?(@.name == 'fileNumbers')].text",
                contains("<mark>IX</mark> <mark>ZR</mark> <mark>100</mark>/<mark>10</mark>")));
  }

  @Test
  @DisplayName("Should return correct result for search with date range filter")
  void shouldReturnCorrectResultForSearchWithDateRangeFilter() throws Exception {
    final String query = "?dateFrom=2025-02-02&dateTo=2025-11-01";
    mockMvc
        .perform(get(ApiConfig.Paths.DOCUMENT + query).contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(2)))
        .andExpect(jsonPath("$.member[0]['item'].abbreviation", Matchers.is("TeG")))
        .andExpect(jsonPath("$.member[1]['item'].documentNumber", Matchers.is("BFRE000157359")));
  }

  @Test
  @DisplayName("Should return correct result for search with dateBefore filter")
  void shouldReturnCorrectResultForSearchWithDateLtFilter() throws Exception {
    final String url = ApiConfig.Paths.DOCUMENT + "?searchTerm=Test&dateTo=2025-11-01";
    mockMvc
        .perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(3)))
        .andExpect(jsonPath("$.member[0]['item'].documentNumber", Matchers.is("KSNR0000")))
        .andExpect(jsonPath("$.member[1]['item'].documentNumber", Matchers.is("BFRE000087655")))
        .andExpect(jsonPath("$.member[2]['item'].abbreviation", Matchers.is("TeG")));
  }

  static Stream<Arguments> testArgsSameResultsAsOtherControllers() {
    return Stream.of(
        Arguments.of(
            ApiConfig.Paths.DOCUMENT
                + "?searchTerm="
                + CaseLawTestData.matchAllTerm
                + "&documentKind=R",
            ApiConfig.Paths.CASELAW + "?searchTerm=" + CaseLawTestData.matchAllTerm),
        Arguments.of(
            ApiConfig.Paths.DOCUMENT + "?searchTerm=Gesetz&documentKind=N",
            ApiConfig.Paths.LEGISLATION + "?searchTerm=Gesetz"),
        Arguments.of(
            ApiConfig.Paths.DOCUMENT
                + "?searchTerm="
                + LiteratureTestData.matchAllTerm
                + "&documentKind=L",
            ApiConfig.Paths.LITERATURE + "?searchTerm=" + LiteratureTestData.matchAllTerm));
  }

  @ParameterizedTest
  @MethodSource("testArgsSameResultsAsOtherControllers")
  @DisplayName("Returns the same contents as Norms, CaseLaw and Literature search controllers")
  void testConsistencyWithOtherSearchControllers(String firstPath, String secondPath)
      throws Exception {
    var firstResponse =
        mockMvc.perform(get(firstPath).contentType(MediaType.APPLICATION_JSON)).andReturn();

    var secondResponse =
        mockMvc.perform(get(secondPath).contentType(MediaType.APPLICATION_JSON)).andReturn();
    var firstMap =
        new ObjectMapper().readValue(firstResponse.getResponse().getContentAsString(), Map.class);
    var secondMap =
        new ObjectMapper().readValue(secondResponse.getResponse().getContentAsString(), Map.class);

    Assertions.assertEquals(firstMap.get("member"), secondMap.get("member"));
  }

  @ParameterizedTest
  @CsvSource({
    "documentNumber, 10, $.member[*].item.documentNumber",
    "-documentNumber, 10, $.member[*].item.documentNumber",
    "courtName, 6, $.member[*].item.courtName",
    "-courtName, 6, $.member[*].item.courtName"
  })
  @DisplayName("Should return correct ordering")
  void shouldReturnCorrectOrdering(String sortParam, int expectedCount, String jsonPattern)
      throws Exception {
    String url = ApiConfig.Paths.DOCUMENT + String.format("?sort=%s", sortParam);

    DocumentContext json =
        JsonPath.parse(
            mockMvc
                .perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString());
    assertThat(json.read("$.member.length()", Integer.class)).isEqualTo(expectedCount);
    String actual = String.join(";", json.read(jsonPattern, List.class));

    List<CaseLawDocumentationUnit> allCaseLaw =
        IteratorUtils.toList(caseLawRepository.findAll().iterator());
    List<Literature> allLiterature =
        IteratorUtils.toList(literatureRepository.findAll().iterator());
    List<AdministrativeDirective> allDirectives =
        IteratorUtils.toList(administrativeDirectiveRepository.findAll().iterator());

    List<String> expected = new ArrayList<>();
    if (sortParam.endsWith("documentNumber")) {
      expected.addAll(allCaseLaw.stream().map(CaseLawDocumentationUnit::documentNumber).toList());
      expected.addAll(allLiterature.stream().map(Literature::documentNumber).toList());
      expected.addAll(allDirectives.stream().map(AdministrativeDirective::documentNumber).toList());
    } else if (sortParam.endsWith("courtName")) {
      expected.addAll(allCaseLaw.stream().map(CaseLawDocumentationUnit::courtKeyword).toList());
    }
    Collections.sort(expected);
    if (sortParam.startsWith("-")) {
      expected = expected.reversed();
    }
    assertThat(actual).isEqualTo(String.join(";", expected));
  }

  @ParameterizedTest
  @CsvSource({"date, 13", "-date, 13", "'', 13"})
  @DisplayName("Should return correct date ordering")
  void shouldReturnCorrectDateOrdering(String sortParam, int expectedCount) throws Exception {
    String url = ApiConfig.Paths.DOCUMENT + String.format("?sort=%s", sortParam);

    var result =
        TestJsonUtils.parseJsonResult(
            mockMvc
                .perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString());
    List<String> actualDates = TestJsonUtils.getDates(result);
    assertThat(result.totalItems()).isEqualTo(expectedCount);

    List<String> expected = getAllRepositoryEntityDates();
    Collections.sort(expected);
    if (sortParam.startsWith("-")) {
      expected = expected.reversed();
    }

    assertThat(actualDates).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("Should return an error when the search has invalid sort parameter")
  void invalidSortParameter() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.DOCUMENT + "?sort=invalidsortparameter")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            content()
                .json(
                    """
                                                        {
                                                          "errors": [
                                                            {
                                                              "code": "invalid_parameter_value",
                                                              "message":"must match \\"^-?(|default|date|DATUM|courtName|documentNumber|temporalCoverageFrom|legislationIdentifier)$\\"",
                                                              "parameter": "sort"
                                                            }
                                                          ]
                                                        }
                                                    """));
  }

  private static Stream<Arguments> specialCharactersArguments() {
    return Stream.of(
        Arguments.of("§ 4 TBestG", "BFRE000107055"), Arguments.of("1.000 €", "BFRE000087655"));
  }

  @ParameterizedTest
  @MethodSource("specialCharactersArguments")
  @DisplayName("Should return correct result for special characters")
  void testSpecialCharacters(String searchTerm, String expectedDocumentNumber) throws Exception {
    String query = "?searchTerm=" + searchTerm;

    mockMvc
        .perform(get(ApiConfig.Paths.DOCUMENT + query).contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath("$.member[0].item.documentNumber", Matchers.is(expectedDocumentNumber)));
  }

  @Test
  @DisplayName("should not break when stop words are included in search")
  void testForStopWords() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.DOCUMENT
                    + "?searchTerm=Leitsatz mit ein paar Wörtern und Ergänzungen")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.member", hasSize(1)),
            jsonPath("$.member[0].item.documentNumber", Matchers.is("BFRE000107055")));
  }

  @Test
  @DisplayName("should throw an error when pagesize exceeds 300")
  void testForExceededPageSize() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.DOCUMENT
                    + "?searchTerm=Leitsatz mit ein paar Wörtern und Ergänzungen&size=301&pageIndex=0")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().is(422),
            jsonPath("$.errors[0].code", Matchers.is("invalid_parameter_value")),
            jsonPath("$.errors[0].parameter", Matchers.is("size")),
            jsonPath("$.errors[0].message", Matchers.is("size must not exceed 300")));
  }

  @Test
  @DisplayName("should throw an error when pagesize is 0")
  void testForZeroPageSize() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.DOCUMENT
                    + "?searchTerm=Leitsatz mit ein paar Wörtern und Ergänzungen&size=0&pageIndex=0")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().is(422),
            jsonPath("$.errors[0].code", Matchers.is("invalid_parameter_value")),
            jsonPath("$.errors[0].parameter", Matchers.is("size")),
            jsonPath("$.errors[0].message", Matchers.is("size must be at least 1")));
  }
}
