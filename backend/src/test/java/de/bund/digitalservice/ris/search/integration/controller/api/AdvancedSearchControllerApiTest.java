package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.CaseLawTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.SharedTestConstants;
import de.bund.digitalservice.ris.search.integration.controller.api.values.SortingTestArguments;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
@WithJwt("jwtTokens/ValidAccessToken.json")
class AdvancedSearchControllerApiTest extends ContainersIntegrationBase {

  @Autowired private CaseLawRepository caseLawRepository;
  @Autowired private NormsRepository normsRepository;
  @Autowired private MockMvc mockMvc;

  @BeforeEach
  void setUpSearchControllerApiTest() {
    clearData();
    caseLawRepository.saveAll(CaseLawTestData.allDocuments);
    normsRepository.saveAll(NormsTestData.allDocuments);
  }

  static String buildAdvancedQuery(String key, String value) {
    return "?query=%s:%s".formatted(key, value);
  }

  private static Stream<Arguments> caseLawAdvancedSearchParams() {
    var arguments = new ArrayList<Arguments>();
    for (var alias : new String[] {"document_type", "DOKUMENTTYP", "TYP"}) {
      arguments.add(Arguments.of(alias, "Urteil", CaseLawTestData.URTEIL_COUNT));
    }
    arguments.add(Arguments.of("location", "Berlin", 2));

    for (var alias : new String[] {"dissenting_opinion", "ABWMEIN"}) {
      arguments.add(Arguments.of(alias, "abweichende", 1));
    }

    arguments.add(
        Arguments.of(
            "decision_date",
            "[2024-01-01 TO 2024-12-31]",
            3)); // DATUM and DAT aliases are tested separately

    for (var alias : new String[] {"ecli", "ECLI"}) {
      arguments.add(Arguments.of(alias, "\"ECLI:DE:FGHH:1972:0630.III10.72.0\"", 1));
    }

    for (var alias : new String[] {"grounds", "GRUENDE", "GR"}) {
      arguments.add(Arguments.of(alias, "folgende", 1));
    }

    for (var alias : new String[] {"decision_grounds", "ENTSCHEIDUNGSGRUENDE", "EGR"}) {
      arguments.add(Arguments.of(alias, "Entscheidungsgründe", 1));
    }

    for (var alias : new String[] {"guiding_principle", "LEITSATZ", "LS"}) {
      arguments.add(Arguments.of(alias, "Leitsatz", CaseLawTestData.WITH_LEITSATZ_COUNT));
    }

    for (var alias : new String[] {"headnote", "ORIENTIERUNGSSATZ", "OSATZ"}) {
      arguments.add(Arguments.of(alias, "Orientierungssatz", 1));
    }

    for (var alias : new String[] {"other_long_text", "SLANGTEXT", "STEXT"}) {
      arguments.add(Arguments.of(alias, "Sonstiger", 1));
    }

    for (var alias : new String[] {"other_headnote", "SORIENTIERUNGSSATZ", "SOSATZ"}) {
      arguments.add(Arguments.of(alias, "Sonstiger Orientierungssatz", 2));
    }

    for (var alias : new String[] {"case_facts", "TATBESTAND", "TB"}) {
      arguments.add(Arguments.of(alias, "Tatbestand", 2));
    }

    int documentsWithTenorCount =
        (int)
            CaseLawTestData.allDocuments.stream()
                .filter(d -> Objects.equals(d.tenor(), "Tenor"))
                .count();
    Assertions.assertTrue(documentsWithTenorCount > 0);
    for (var alias : new String[] {"tenor", "TENOR"}) {
      arguments.add(Arguments.of(alias, "Tenor", documentsWithTenorCount));
    }

    for (var alias : new String[] {"headline", "TITELZEILE", "TTZE"}) {
      arguments.add(Arguments.of(alias, "Titelzeile", 1));
    }

    for (var alias : new String[] {"outline", "GLIEDERUNG", "GD"}) {
      arguments.add(Arguments.of(alias, "outlineTest", 1));
    }

    for (var alias : new String[] {"judicial_body", "SPRUCHKOERPER", "SK"}) {
      arguments.add(Arguments.of(alias, "judicialbodyTest", 1));
    }

    for (var alias : new String[] {"court_keyword", "GER", "GERICHT"}) {
      arguments.add(Arguments.of(alias, "LG Saarbrücken", 1));
    }

    for (var alias : new String[] {"keywords", "SLW", "SCHLAGWOERTER"}) {
      arguments.add(Arguments.of(alias, "keywordsTest", 1));
    }

    for (var alias : new String[] {"court_type", "GERICHTSTYP", "GERTYP"}) {
      arguments.add(Arguments.of(alias, "LG", 1));
    }

    for (var alias : new String[] {"location", "GERICHTSORT", "GERORT"}) {
      arguments.add(Arguments.of(alias, "Hamburg", 1));
    }

    for (var alias : new String[] {"file_numbers", "AKTENZEICHEN", "AZ"}) {
      arguments.add(Arguments.of(alias, "\"IX ZR 100/10\"", 1));
    }

    for (var alias : new String[] {"decision_name", "ENTSCHEIDUNGSNAME", "ENAME"}) {
      arguments.add(Arguments.of(alias, "decisionNames", 1));
    }

    for (var alias : new String[] {"dissenting_opinion", "ABWMEIN"}) {
      arguments.add(Arguments.of(alias, "eine abweichende Meinung", 1));
    }

    for (var alias : new String[] {"deviating_document_number", "ABWNR"}) {
      arguments.add(Arguments.of(alias, "deviatingDocumentNumbers", 1));
    }

    for (var alias : new String[] {"publication_status", "STATUS"}) {
      arguments.add(Arguments.of(alias, "PUBLISHED", CaseLawTestData.PUBLISHED_COUNT));
    }

    for (var alias : new String[] {"error", "FEHLER"}) {
      arguments.add(Arguments.of(alias, "true", 1));
    }

    arguments.add(Arguments.of("documentation_office", "DS", 1));

    for (var alias : new String[] {"procedures", "VORGANG", "VG"}) {
      arguments.add(Arguments.of(alias, "proceduresTest", 1));
    }
    for (var alias : new String[] {"legal_effect", "RECHTSKRAFT", "RK"}) {
      arguments.add(Arguments.of(alias, "JA", 2));
    }

    return arguments.stream();
  }

  @ParameterizedTest()
  @MethodSource("caseLawAdvancedSearchParams")
  void shouldFindCorrectCount(String key, String value, int expectedCount) throws Exception {
    String urlWithSpecificIndex =
        ApiConfig.Paths.CASELAW_ADVANCED_SEARCH + buildAdvancedQuery(key, value);
    String genericUrl = ApiConfig.Paths.DOCUMENT_ADVANCED_SEARCH + buildAdvancedQuery(key, value);

    for (String url : new String[] {urlWithSpecificIndex, genericUrl}) {
      mockMvc
          .perform(get(url).contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.member", hasSize(expectedCount)))
          .andExpect(status().isOk());
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"DATUM", "DAT"})
  @WithJwt("jwtTokens/ValidAccessToken.json")
  @DisplayName("Should return 200 when looking for decision date and aliases")
  void shouldFindCorrectCountForAmbiguousFields(String queryParam) throws Exception {
    String query = buildAdvancedQuery(queryParam, "[2024-01-01 TO 2024-12-31]");
    String urlWithSpecificIndex = ApiConfig.Paths.CASELAW_ADVANCED_SEARCH + query;

    mockMvc
        .perform(get(urlWithSpecificIndex).contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(3)))
        .andExpect(status().isOk());

    String genericUrl = ApiConfig.Paths.DOCUMENT_ADVANCED_SEARCH + query;
    mockMvc
        .perform(get(genericUrl).contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(greaterThan(3))))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName(
      "Should return mixed result when looking for a specific date that match for both norms and case law")
  void shouldReturnOkDateQuery() throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.DOCUMENT_ADVANCED_SEARCH
                    + "?query=DATUM:"
                    + SharedTestConstants.DATE_2_1)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(2)))
        .andExpect(
            jsonPath("$.member[*].item[\"@type\"]", containsInAnyOrder("Decision", "Legislation")))
        .andExpect(jsonPath("$.member[*].item.documentType", contains("Zweites Versäumnisurteil")))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName(
      "Should return mixed result when looking for a specific title that match for both norms and case law")
  void shouldReturnOkTitleQuery() throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.DOCUMENT_ADVANCED_SEARCH + "?query=TITEL:Test")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(4)))
        .andExpect(jsonPath("$.member[0].item[\"@type\"]", Matchers.is("Decision")))
        .andExpect(jsonPath("$.member[0].item.documentType", Matchers.is("Urteil")))
        .andExpect(jsonPath("$.member[1].item[\"@type\"]", Matchers.is("Legislation")))
        .andExpect(jsonPath("$.member[1].member.documentType").doesNotExist())
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(strings = {"document_number", "DOKUMENTNUMMER", "NR"})
  @DisplayName("Should return 200 when looking for a specific document number and aliases")
  void shouldReturnOkDocumentNumberQuery(String queryParam) throws Exception {

    var document = CaseLawTestData.allDocuments.getFirst();
    mockMvc
        .perform(
            get(ApiConfig.Paths.CASELAW_ADVANCED_SEARCH
                    + buildAdvancedQuery(queryParam, document.documentNumber()))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(
            jsonPath("$.member[0].item.documentNumber", Matchers.is(document.documentNumber())))
        .andExpect(jsonPath("$.member[0].item.courtType", Matchers.is(document.courtType())))
        .andExpect(jsonPath("$.member[0].item.location", Matchers.is(document.location())))
        .andExpect(
            jsonPath(
                "$.member[0].item.decisionDate", Matchers.is(document.decisionDate().toString())))
        .andExpect(jsonPath("$.member[0].item[\"@type\"]", Matchers.is("Decision")))
        .andExpect(jsonPath("$.member[0].item.documentType", Matchers.is(document.documentType())))
        .andExpect(status().isOk());
  }

  static Stream<Arguments> provideSortingTestArguments() {
    Stream.Builder<Arguments> stream = SortingTestArguments.provideSortingTestArguments();

    int caseLawSize = CaseLawTestData.allDocuments.size();
    int normsSize = NormsTestData.allDocuments.size();
    int combinedSize = caseLawSize + normsSize;
    stream.add(Arguments.of("", combinedSize, null, null));

    return stream.build();
  }

  @ParameterizedTest
  @MethodSource("provideSortingTestArguments")
  @DisplayName("Should return correct ordering")
  void shouldReturnCorrectSort(
      String sortParam, int expectedCount, ResultMatcher matcher, ResultMatcher otherMatcher)
      throws Exception {
    String url =
        ApiConfig.Paths.DOCUMENT_ADVANCED_SEARCH
            + "?query=TITEL:Test OR %s".formatted(CaseLawTestData.matchAllTerm)
            + String.format("&sort=%s", sortParam);

    var perform =
        mockMvc
            .perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.member", hasSize(expectedCount)));

    if (matcher != null) {
      perform.andExpect(matcher);
    }
    if (otherMatcher != null) {
      perform.andExpect(otherMatcher);
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"official_title", "AL"})
  @DisplayName("Should return 200 when looking for a specific official title and aliases")
  void shouldReturnOkOfficialTitleQuery(String queryParam) throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH
                    + String.format("?query=%s:\"Test Gesetz Nr. 2\"", queryParam))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0].item.abbreviation", Matchers.is("TeG2")))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(strings = {"official_short_title", "AK"})
  @DisplayName("Should return 200 when looking for official short title and aliases")
  void shouldReturnOkOfficialShortTitleQuery(String queryParam) throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH
                    + String.format("?query=%s:TestG3", queryParam))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0].item.abbreviation", Matchers.is("TeG3")))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should return 200 when looking for work_eli")
  void shouldReturnOkLocationQueryForLegislation() throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH
                    + "?query=work_eli.keyword:eli\\/2024\\/teg\\/*")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.member", hasSize(2)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(strings = {"official_abbreviation", "AB"})
  @DisplayName("Should return 200 when looking for official abbreviation and aliases")
  void shouldReturnOkOfficialAbbreviation(String queryParam) throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH
                    + String.format("?query=%s:TeG2", queryParam))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0].item.abbreviation", Matchers.is("TeG2")))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should return result even if there are no highlights in parts or title")
  void shouldReturnResultsEvenIfNoHighlightsFoundInArticlesOrTitle() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH
                    + String.format("?query=%s:TeG2 AND %s:TeG2", "official_abbreviation", "AB"))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(strings = {"norms_date", "DATUM", "DAT"})
  @WithJwt("jwtTokens/ValidAccessToken.json")
  @DisplayName("Should return 200 when looking for decision date and aliases")
  void shouldReturnOkDecisionDateQueryForLegislation(String queryParam) throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH
                    + String.format("?query=%s:[2024-01-01 TO 2024-12-31]", queryParam))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.member", hasSize(2)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        ApiConfig.Paths.DOCUMENT_ADVANCED_SEARCH,
        ApiConfig.Paths.CASELAW_ADVANCED_SEARCH,
        ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH
      })
  @DisplayName("When required request parameter is missing it returns json error response")
  void itSendsJsonErrorResponseWhenRequiredRequestParamIsMissing(String url) throws Exception {

    mockMvc
        .perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(
            content()
                .json(
                    """
                                        {
                                          "errors": [
                                            {
                                              "code": "information_missing",
                                              "parameter": "query",
                                              "message": "Required request parameter 'query' for method parameter type String is not present"
                                            }
                                          ]
                                        }
                                    """));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        ApiConfig.Paths.DOCUMENT_ADVANCED_SEARCH + "?query=id:foobar%20OR",
        ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH + "?query=work_eli:foobar%20OR",
        ApiConfig.Paths.CASELAW_ADVANCED_SEARCH + "?query=document_number:BFRE000047655%20OR"
      })
  @DisplayName("Should return an error when the search has invalid lucene query")
  void invalidLuceneQuery(String url) throws Exception {

    mockMvc
        .perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(
            content()
                .json(
                    """
                                        {
                                          "errors": [
                                            {
                                              "code": "invalid_lucene_query",
                                              "parameter": "query",
                                              "message": "Invalid lucene query"
                                            }
                                          ]
                                        }
                                    """));
  }

  private static Stream<Arguments> provideSortTestData() {
    return Stream.of(
        Arguments.of(
            ApiConfig.Paths.DOCUMENT_ADVANCED_SEARCH,
            "must match \\\"^-?(|default|date|DATUM|courtName|documentNumber|temporalCoverageFrom|legislationIdentifier)$\\\""),
        Arguments.of(
            ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH,
            "must match \\\"^-?(|default|date|temporalCoverageFrom|legislationIdentifier|DATUM)$\\\""),
        Arguments.of(
            ApiConfig.Paths.CASELAW_ADVANCED_SEARCH,
            "must match \\\"^-?(|default|date|DATUM|courtName|documentNumber)$\\\""));
  }

  @ParameterizedTest
  @DisplayName("Should return an error when the search has invalid sort parameter")
  @MethodSource("provideSortTestData")
  void invalidSortParameter(String baseUrl, String expectedError) throws Exception {
    mockMvc
        .perform(
            get(baseUrl + "?query=anything&sort=invalidsortparameter")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(
            content()
                .json(
                    """
                                {
                                  "errors": [
                                    {
                                      "code": "invalid_parameter_value",
                                      "message": "%s",
                                      "parameter": "sort"
                                    }
                                  ]
                                }
                                """
                        .formatted(expectedError)));
  }
}
