package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class AllDocumentsSearchControllerMultiMatchAPITest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  @BeforeEach
  void setUpSearchControllerApiTest() {
    clearRepositoryData();

    // ecli and workEli are mis-used to carry assertion information
    var caseLawData =
        List.of(
            CaseLawDocumentationUnit.builder()
                .documentNumber("case_acrossFields {m,t,r} in different fields")
                .tenor("Mord")
                .headline("Totschlag")
                .guidingPrinciple("Raub")
                .build(),
            CaseLawDocumentationUnit.builder()
                .documentNumber("case_sameField {m,t,r} in one field")
                .tenor("Mord, Totschlag, Raub")
                .build(),
            CaseLawDocumentationUnit.builder()
                .documentNumber("case_sameFieldReordered {m,t,r} in one field but different order")
                .tenor("Raub, Mord, Totschlag")
                .build());
    var normsData =
        List.of(
            Norm.builder()
                .workEli("N1 {m,t} in one field")
                .expressionEli("N1 {m,t} in one field")
                .officialTitle("Mord Totschlag")
                .tableOfContents(Collections.emptyList())
                .build(),
            Norm.builder()
                .workEli("N2 {r} in one field")
                .expressionEli("N2 {r} in one field")
                .officialTitle("Raub")
                .tableOfContents(Collections.emptyList())
                .build(),
            Norm.builder()
                .workEli("N3 {r} in one field")
                .expressionEli("N3 {r} in one field")
                .officialTitle("Raub")
                .tableOfContents(Collections.emptyList())
                .build());

    caseLawRepository.saveAll(caseLawData);
    normsRepository.saveAll(normsData);
  }

  /** Asserts that every ID contains one of the values specified. */
  static ResultMatcher everyIdContainsEither(String... ids) {
    String jsonPath = "$.member[*].item['@id']";
    // the above expression produces a Map, with the ID in one of the values. Hence, the hasValue
    // matcher is used.
    Matcher<String>[] matchers =
        Arrays.stream(ids).map(Matchers::containsString).toArray(Matcher[]::new);
    return jsonPath(jsonPath, Matchers.everyItem(Matchers.anyOf(matchers)));
  }

  /** Asserts that all specified IDs are present in the result set. */
  static ResultMatcher idsContainAll(String... ids) {
    String jsonPath = "$.member[*].item['@id']";
    // the above expression produces a Map, with the ID in one of the values. Hence, the hasValue
    // matcher is used.
    Matcher<String>[] matchers =
        Arrays.stream(ids).map(Matchers::containsString).toArray(Matcher[]::new);
    return jsonPath(jsonPath, Matchers.containsInAnyOrder(matchers));
  }

  @Test
  @DisplayName("Should return correct results for one term")
  void shouldReturnCorrectResultForOneTerm() throws Exception {
    String query = "?searchTerm=Mord";

    mockMvc
        .perform(get(ApiConfig.Paths.DOCUMENT + query).contentType(MediaType.APPLICATION_JSON))
        .andExpect(everyIdContainsEither("{m,t}", "{m,t,r}"))
        .andExpect(
            idsContainAll("case_acrossFields", "case_sameField", "case_sameFieldReordered", "N1"));
  }

  @Test
  @DisplayName("Should return correct results for two terms")
  void shouldReturnCorrectResultForTwoTerms() throws Exception {
    String query = "?searchTerm=Mord Totschlag";

    mockMvc
        .perform(get(ApiConfig.Paths.DOCUMENT + query).contentType(MediaType.APPLICATION_JSON))
        .andExpect(everyIdContainsEither("{m,t}", "{m,t,r}"))
        .andExpect(
            idsContainAll("case_acrossFields", "case_sameField", "case_sameFieldReordered", "N1"));
  }

  @Test
  @DisplayName("Should return correct results for one term")
  void shouldReturnCorrectResultForOtherTerm() throws Exception {
    String query = "?searchTerm=Raub";

    mockMvc
        .perform(get(ApiConfig.Paths.DOCUMENT + query).contentType(MediaType.APPLICATION_JSON))
        .andExpect(everyIdContainsEither("{r}", "{m,t,r}"))
        .andExpect(
            idsContainAll(
                "case_acrossFields", "case_sameField", "case_sameFieldReordered", "N2", "N3"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"Mord Totschlag Raub", "Raub Mord Totschlag", "totschlag MORD, raub"})
  @DisplayName("Should return correct results for 3 terms")
  void shouldReturnCorrectResultForThreeTerms(String searchTerm) throws Exception {
    String query = "?searchTerm=%s".formatted(searchTerm);

    mockMvc
        .perform(get(ApiConfig.Paths.DOCUMENT + query).contentType(MediaType.APPLICATION_JSON))
        .andExpect(everyIdContainsEither("{m,t,r}"))
        .andExpect(idsContainAll("case_acrossFields", "case_sameField", "case_sameFieldReordered"));
  }

  static Stream<Arguments> shouldMatchPhraseIfQuotedArguments() {
    return Stream.of(
        Arguments.of("\"Mord, Totschlag, Raub\"", 1, idsContainAll("case_sameField")),
        Arguments.of("\"Raub, Mord, Totschlag\"", 1, idsContainAll("case_sameFieldReordered")),
        Arguments.of("\"raub mord TOTSCHLAG\"", 1, idsContainAll("case_sameFieldReordered")),
        Arguments.of(
            "\"mord Totschlag\" Raub",
            2,
            idsContainAll("case_sameField", "case_sameFieldReordered")),
        Arguments.of("\"mord Totschlag\" \"Totschlag Raub\"", 1, idsContainAll("case_sameField")),
        Arguments.of("\"Mord Raub\"", 0, null));
  }

  @ParameterizedTest
  @MethodSource("shouldMatchPhraseIfQuotedArguments")
  void shouldMatchPhraseIfQuoted(
      String searchTerm, int expectedSize, @Nullable ResultMatcher matcher) throws Exception {
    String query = "?searchTerm=" + searchTerm;
    ResultActions actions =
        mockMvc
            .perform(get(ApiConfig.Paths.DOCUMENT + query).contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.member", Matchers.hasSize(expectedSize)));
    if (matcher != null) {
      actions.andExpect(matcher);
    }
  }
}
