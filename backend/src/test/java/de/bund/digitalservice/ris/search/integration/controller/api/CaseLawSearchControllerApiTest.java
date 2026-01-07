package de.bund.digitalservice.ris.search.integration.controller.api;

import static de.bund.digitalservice.ris.search.integration.controller.api.testData.CaseLawTestData.BESCHLUSS_COUNT;
import static de.bund.digitalservice.ris.search.integration.controller.api.testData.CaseLawTestData.OTHER_COUNT;
import static de.bund.digitalservice.ris.search.integration.controller.api.testData.CaseLawTestData.URTEIL_COUNT;
import static de.bund.digitalservice.ris.search.integration.controller.api.testData.CaseLawTestData.matchAllTerm;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.CaseLawTestData;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class CaseLawSearchControllerApiTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("Should return correct item when filtering for ECLI and using filter")
  void shouldReturnDocumentationUnitsWhenFilteringForEcli() throws Exception {
    String ecli = "ECLI:DE:FGHH:1972:0630.III10.72.0";
    String markedEcli =
        "<mark>ECLI:DE:FGHH</mark>:<mark>1972</mark>:<mark>0630</mark>.<mark>III10.72.0</mark>";

    mockMvc
        .perform(
            get(ApiConfig.Paths.CASELAW
                    + String.format("?searchTerm=%s&ecli=%s", matchAllTerm, ecli))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0]['item'].ecli", Matchers.is(ecli)))
        .andExpect(jsonPath("$.member[0]['textMatches'][*]['name']", Matchers.hasItem("ecli")))
        .andExpect(jsonPath("$.member[0]['textMatches'][*]['text']", Matchers.hasItem(markedEcli)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("should return the correct items for legal effect when using filter")
  void shouldFilterByLegalEffect() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.CASELAW
                    + String.format("?searchTerm=%s&legalEffect=true", matchAllTerm))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(2)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should return correct items when looking for a specific court")
  void shouldReturnDocumentationUnitsWhenSearchingForSpecificCourt() throws Exception {
    String query = String.format("?searchTerm=%s&court=FG Berlin", matchAllTerm);
    mockMvc
        .perform(get(ApiConfig.Paths.CASELAW + query).contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0].item.courtName", is("FG Berlin")))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should return correct items when looking for court type")
  void shouldReturnDocumentationUnitsWhenSearchingForCourtType() throws Exception {
    String query = String.format("?searchTerm=%s&court=FG", matchAllTerm);
    mockMvc
        .perform(get(ApiConfig.Paths.CASELAW + query).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.member", hasSize(4)),
            jsonPath("$.member[*].item.courtType", everyItem(is("FG"))));
  }

  @ParameterizedTest
  @ValueSource(strings = {"IX ZR 100/10", "ix zr 100 10", "iX ZR 100 / 10"})
  @DisplayName("Should return only correct item when looking for a file number as such")
  void testFileNumberSearch(String fileNumberFormat) throws Exception {
    String query = String.format("?searchTerm=%s&fileNumber=%s", matchAllTerm, fileNumberFormat);
    mockMvc
        .perform(get(ApiConfig.Paths.CASELAW + query).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.member", hasSize(1)),
            jsonPath("$.member[0].item.fileNumbers", hasSize(1)),
            jsonPath("$.member[0].item.fileNumbers[0]", equalTo("IX ZR 100/10")));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "IX ZR 100/10", // with original spelling
        "ix ZR 100 10", // with spaces instead of slashes
        "\"iX ZR 100 / 10\"", // with extra spaces if quoted
      })
  @DisplayName("Should return correct item first when looking for a file number as search term")
  void testFileNumberSearchTerm(String fileNumberFormat) throws Exception {
    String query = String.format("?searchTerm=%s", fileNumberFormat);
    mockMvc
        .perform(get(ApiConfig.Paths.CASELAW + query).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.member", hasSize(greaterThan(0))),
            jsonPath("$.member[0].item.fileNumbers[0]", equalTo("IX ZR 100/10")),
            jsonPath("$.member[0].textMatches[?(@.name == 'fileNumbers')]", hasSize(1)),
            jsonPath(
                "$.member[0].textMatches[?(@.name == 'fileNumbers')].text",
                everyItem(is("<mark>IX</mark> <mark>ZR</mark> <mark>100</mark>/<mark>10</mark>"))));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "IX ZR 100", // with original spelling
        "\"iX ZR 100\"", // when quoted
      })
  @DisplayName("Should return correct items when searching for a file number prefix as searchTerm")
  void testPartialFileNumberSearch(String fileNumberFormat) throws Exception {
    String query = String.format("?searchTerm=%s", fileNumberFormat);
    mockMvc
        .perform(get(ApiConfig.Paths.CASELAW + query).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.member", hasSize(3)),
            jsonPath(
                "$.member[*].item.fileNumbers[*]",
                containsInAnyOrder("IX ZR 100/10", "IX ZR 100/20", "IX ZR 100/30")),
            jsonPath("$.member[*].textMatches[?(@.name == 'fileNumbers')]", hasSize(3)),
            jsonPath(
                "$.member[*].textMatches[?(@.name == 'fileNumbers')].text",
                everyItem(startsWith("<mark>IX</mark> <mark>ZR</mark> <mark>100</mark>"))));
  }

  private static Stream<Arguments> documentTypeParameters() {
    return Stream.of(
        Arguments.of("typeGroup=Beschluss", BESCHLUSS_COUNT),
        Arguments.of("typeGroup=Urteil", URTEIL_COUNT),
        Arguments.of("type=Urteil", 1),
        Arguments.of("type=urteil", 1),
        Arguments.of("typeGroup=other", OTHER_COUNT),
        Arguments.of("type=Versäumnisurteil", 1),
        Arguments.of("type=Zweites Versäumnisurteil", 1),
        Arguments.of("type=EuGH-Vorlage", 1),
        Arguments.of("typeGroup=Urteil&type=Versäumnisurteil", 1),
        Arguments.of("typeGroup=Urteil&type=Entscheidung", 0) // conflicting parameters
        );
  }

  @ParameterizedTest
  @MethodSource("documentTypeParameters")
  @DisplayName("documentType filter")
  void shouldFilterByDocumentType(String queryStringPart, int expectedCount) throws Exception {
    String query = "?searchTerm=%s&%s".formatted(matchAllTerm, queryStringPart);
    mockMvc
        .perform(get(ApiConfig.Paths.CASELAW + query).contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(expectedCount)));
  }

  private static Stream<Arguments> courtTypeArguments() {
    String[] allIds = {
      "KG Berlin", "FG Berlin", "FG Hamburg", "FG Gotha", "FG Hannover", "LG Saarbrücken"
    };
    String[] allLabels = {
      "Kammergericht Berlin",
      "Finanzgericht Berlin",
      "Finanzgericht Hamburg",
      "Finanzgericht Gotha",
      "Finanzgericht Hannover",
      "Landgericht Saarbrücken"
    };
    String[] fgIds = {"FG Berlin", "FG Hamburg", "FG Gotha", "FG Hannover"};
    String[] fgLabels = {
      "Finanzgericht Berlin",
      "Finanzgericht Hamburg",
      "Finanzgericht Gotha",
      "Finanzgericht Hannover"
    };
    String[] empty = {};
    return Stream.of(
        Arguments.of(null, allIds, allLabels),
        Arguments.of("Finanzgericht", fgIds, fgLabels),
        Arguments.of("Fin", fgIds, fgLabels),
        Arguments.of("FG", fgIds, fgIds), // should return the raw IDs as labels
        Arguments.of("kg b", new String[] {"KG Berlin"}, new String[] {"KG Berlin"}),
        Arguments.of(
            "Finanzgericht Ha",
            new String[] {"FG Hamburg", "FG Hannover"},
            new String[] {"Finanzgericht Hamburg", "Finanzgericht Hannover"}),
        Arguments.of(
            "FG Ha",
            new String[] {"FG Hamburg", "FG Hannover"},
            new String[] {"FG Hamburg", "FG Hannover"}), // should return the raw IDs as labels
        Arguments.of("bi", empty, empty));
  }

  @ParameterizedTest
  @MethodSource("courtTypeArguments")
  @DisplayName("returns court names and counts matching a prefix")
  void shouldReturnCourtTypes(String prefix, String[] expectedIds, String[] expectedLabels)
      throws Exception {
    var query = prefix != null ? "?prefix=%s".formatted(prefix) : "";

    mockMvc
        .perform(
            get(ApiConfig.Paths.CASELAW + "/courts" + query)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*.id", containsInAnyOrder(expectedIds)))
        .andExpect(jsonPath("$.*.label", containsInAnyOrder(expectedLabels)));
  }

  @Test
  @DisplayName("Should return ok and empty page for no search results")
  void noSearchResultsShouldReturnOk() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                    ApiConfig.Paths.CASELAW + "?query=document_number:BFRE000047655")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should return highlighted search results")
  void shouldReturnHighlightedSearchResults() throws Exception {
    String searchTerm = "test";
    mockMvc
        .perform(
            get(ApiConfig.Paths.CASELAW + String.format("?searchTerm=%s", searchTerm))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member[*].textMatches").exists())
        .andExpect(
            jsonPath("$.member[0].textMatches[*].name")
                .value(
                    Matchers.containsInRelativeOrder(
                        "caseFacts", "headnote", "otherHeadnote", "decisionGrounds", "headline")))
        .andExpect(
            jsonPath("$.member[0].textMatches[?(@.name == 'headline')].text")
                .value(
                    Matchers.everyItem(Matchers.equalTo("<mark>Test</mark> mit 1.000 € im Titel"))))
        .andExpect(
            jsonPath("$.member[0].textMatches[*].location")
                .value(Matchers.everyItem(Matchers.nullValue())));
  }

  public static Stream<Arguments> provideSortingTestArguments() {
    Stream.Builder<Arguments> stream = Stream.builder();

    stream.add(Arguments.of("", null));

    List<String> sortedDocumentNumbers =
        CaseLawTestData.allDocuments.stream()
            .map(CaseLawDocumentationUnit::documentNumber)
            .sorted()
            .toList();
    stream.add(
        Arguments.of(
            "documentNumber",
            jsonPath("$.member[*].item.documentNumber", Matchers.is(sortedDocumentNumbers))));

    List<String> inverseSortedDocumentNumbers = new ArrayList<>(sortedDocumentNumbers);
    Collections.reverse(inverseSortedDocumentNumbers);
    stream.add(
        Arguments.of(
            "-documentNumber",
            jsonPath(
                "$.member[*].item.documentNumber", Matchers.is(inverseSortedDocumentNumbers))));

    List<String> caseLawDates =
        new ArrayList<>(
            CaseLawTestData.allDocuments.stream()
                .map(d -> d.decisionDate().toString())
                .sorted()
                .toList());

    stream.add(
        Arguments.of("date", jsonPath("$.member[*].item.decisionDate", Matchers.is(caseLawDates))));

    List<String> invertedCaseLawDates = new ArrayList<>(caseLawDates);
    Collections.reverse(invertedCaseLawDates);

    stream.add(
        Arguments.of(
            "-date", jsonPath("$.member[*].item.decisionDate", Matchers.is(invertedCaseLawDates))));

    List<String> courtNames =
        CaseLawTestData.allDocuments.stream()
            .map(CaseLawDocumentationUnit::courtKeyword)
            .sorted()
            .toList();
    stream.add(
        Arguments.of("courtName", jsonPath("$.member[*].item.courtName", Matchers.is(courtNames))));

    List<String> invertedCourtNames = new ArrayList<>(courtNames);
    Collections.reverse(invertedCourtNames);
    stream.add(
        Arguments.of(
            "-courtName", jsonPath("$.member[*].item.courtName", Matchers.is(invertedCourtNames))));
    return stream.build();
  }

  @ParameterizedTest
  @MethodSource("provideSortingTestArguments")
  @DisplayName("sorts results correctly")
  void shouldReturnCorrectSort(String sortParam, ResultMatcher matcher) throws Exception {

    var perform =
        mockMvc
            .perform(
                get(ApiConfig.Paths.CASELAW
                        + String.format("?searchTerm=%s&sort=%s", matchAllTerm, sortParam))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.member", hasSize(CaseLawTestData.allDocuments.size())));

    if (matcher != null) {
      perform.andExpect(matcher);
    }
  }
}
