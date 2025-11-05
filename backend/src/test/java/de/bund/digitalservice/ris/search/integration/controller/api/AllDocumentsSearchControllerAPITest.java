package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.oneOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.CaseLawTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.LiteratureTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.SharedTestConstants;
import de.bund.digitalservice.ris.search.integration.controller.api.values.SortingTestArguments;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
@WithJwt("jwtTokens/ValidAccessToken.json")
class AllDocumentsSearchControllerAPITest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("Should return correct result for term search, with textMatches")
  void shouldReturnCorrectResultForFilterSearch() throws Exception {
    String query = "?searchTerm=Test";

    mockMvc
        .perform(get(ApiConfig.Paths.DOCUMENT + query).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.member", hasSize(4)),
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
                                "fileNumbers"))
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
  @DisplayName("Should return correct result for search with single date filter")
  void shouldReturnCorrectResultForSearchWithDateFilter() throws Exception {
    String query =
        "?dateFrom=%s&dateTo=%s"
            .formatted(SharedTestConstants.DATE_2_1, SharedTestConstants.DATE_2_1);

    mockMvc
        .perform(get(ApiConfig.Paths.DOCUMENT + query).contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(2)))
        .andExpect(
            jsonPath(
                "$.member[0]['item'].decisionDate",
                Matchers.is(SharedTestConstants.DATE_2_1.toString())))
        .andExpect(
            jsonPath(
                "$.member[1]['item'].legislationDate",
                Matchers.is(SharedTestConstants.DATE_2_1.toString())));
  }

  @Test
  @DisplayName("Should return correct result for search with date range filter")
  void shouldReturnCorrectResultForSearchWithDateRangeFilter() throws Exception {
    final String query =
        "?dateFrom=%s&dateTo=%s"
            .formatted(SharedTestConstants.DATE_2_1, SharedTestConstants.DATE_2_2);

    Matcher<Iterable<? extends String>> onlyContainsGivenDates =
        everyItem(
            either(is(SharedTestConstants.DATE_2_1.toString()))
                .or(is(SharedTestConstants.DATE_2_2.toString())));
    mockMvc
        .perform(get(ApiConfig.Paths.DOCUMENT + query).contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(4)))
        .andExpect(jsonPath("$.member[*].decisionDate", onlyContainsGivenDates))
        .andExpect(jsonPath("$.member[*].legislationDate", onlyContainsGivenDates));
  }

  @Test
  @DisplayName("Should return correct result for search with dateBefore filter")
  void shouldReturnCorrectResultForSearchWithDateLtFilter() throws Exception {
    final String url =
        ApiConfig.Paths.DOCUMENT
            + "?searchTerm=Test&dateTo=%s".formatted(SharedTestConstants.DATE_2_1.minusDays(1));
    mockMvc
        .perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(
            jsonPath(
                "$.member[0]['item'].legislationDate",
                Matchers.is(SharedTestConstants.DATE_1_1.toString())))
        .andExpect(
            jsonPath(
                "$.member[0].textMatches[?(@.name == 'name')].text",
                Matchers.containsInAnyOrder(Matchers.is("<mark>Test</mark> Gesetz Nr. 2"))));
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

  static Stream<Arguments> provideOrderingTestArgs() {
    int caseLawSize = CaseLawTestData.allDocuments.size();
    int literatureSize = LiteratureTestData.allDocuments.size();
    int normsSize = NormsTestData.allDocuments.size();
    int combinedSize = caseLawSize + literatureSize + normsSize;

    var stream = SortingTestArguments.provideSortingTestArguments();
    final ResultMatcher dateDescendingResultMatcher =
        result -> {
          JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
          ArrayList<String> dates = new ArrayList<>();
          for (Object member : jsonObject.getJSONArray("member")) {
            var item = ((JSONObject) member).getJSONObject("item");
            if (item.has("decisionDate")) {
              dates.add(item.getString("decisionDate"));
            } else if (item.has("legislationDate")) {
              dates.add(item.getString("legislationDate"));
            } else if (item.has("firstPublicationDate")) {
              dates.add(item.getString("firstPublicationDate"));
            }
          }
          assertThat(dates).isSortedAccordingTo(Comparator.reverseOrder());
        };
    stream.add(Arguments.of("", combinedSize, dateDescendingResultMatcher, null));

    return stream.build();
  }

  @ParameterizedTest
  @MethodSource("provideOrderingTestArgs")
  @DisplayName("Should return correct ordering")
  void shouldReturnCorrectOrdering(
      String sortParam, int expectedCount, ResultMatcher matcher, ResultMatcher otherMatcher)
      throws Exception {
    String url = ApiConfig.Paths.DOCUMENT + String.format("?sort=%s", sortParam);

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
  @DisplayName("should throw an error when pagesize exceeds 100")
  void testForExceededPageSize() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.DOCUMENT
                    + "?searchTerm=Leitsatz mit ein paar Wörtern und Ergänzungen&size=101&pageIndex=0")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().is(422),
            jsonPath("$.errors[0].code", Matchers.is("invalid_parameter_value")),
            jsonPath("$.errors[0].parameter", Matchers.is("size")),
            jsonPath("$.errors[0].message", Matchers.is("size must not exceed 100")));
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
