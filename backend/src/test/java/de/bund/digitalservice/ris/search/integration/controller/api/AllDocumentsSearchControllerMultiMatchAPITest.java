package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import de.bund.digitalservice.ris.search.service.IndexAliasService;
import java.io.IOException;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
@WithJwt("jwtTokens/ValidAccessToken.json")
class AllDocumentsSearchControllerMultiMatchAPITest extends ContainersIntegrationBase {

  @Autowired private NormsRepository normsRepository;

  @Autowired private CaseLawRepository caseLawRepository;

  @Autowired private IndexAliasService indexAliasService;

  @Autowired private MockMvc mockMvc;

  Boolean initialized = false;

  @BeforeEach
  void setUpSearchControllerApiTest() throws IOException {
    if (initialized) return; // replacement for @BeforeAll setup, which causes errors

    assertTrue(openSearchContainer.isRunning());

    super.recreateIndex();
    super.updateMapping();

    indexAliasService.setIndexAlias();

    // ecli and workEli are mis-used to carry assertion information
    var caseLawData =
        List.of(
            CaseLawDocumentationUnit.builder()
                .documentNumber("C1 {m,t,r} in different fields")
                .tenor("Mord")
                .headline("Totschlag")
                .guidingPrinciple("Raub")
                .build(),
            CaseLawDocumentationUnit.builder()
                .documentNumber("C2 {m,t,r} in one field")
                .tenor("Mord, Totschlag, Raub")
                .build(),
            CaseLawDocumentationUnit.builder()
                .documentNumber("C3 {m,t,r} in one field but different order")
                .tenor("Raub, Mord, Totschlag")
                .build());
    var normsData =
        List.of(
            Norm.builder()
                .workEli("N1 {m,t} in one field")
                .officialTitle("Mord Totschlag")
                .tableOfContents(Collections.emptyList())
                .build(),
            Norm.builder()
                .workEli("N2 {r} in one field")
                .officialTitle("Raub")
                .tableOfContents(Collections.emptyList())
                .build(),
            Norm.builder()
                .workEli("N3 {r} in one field")
                .officialTitle("Raub")
                .tableOfContents(Collections.emptyList())
                .build());

    caseLawRepository.saveAll(caseLawData);
    normsRepository.saveAll(normsData);

    initialized = true;
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

  /**
   * Asserts that there is a 1:1 match between the member IDs and values specified. Every value
   * should be contained in the JSON exactly once (expressed by {@link
   * Matchers::containsInAnyOrder}).
   */
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
        .andExpect(idsContainAll("C1", "C2", "C3", "N1"));
  }

  @Test
  @DisplayName("Should return correct results for two terms")
  void shouldReturnCorrectResultForTwoTerms() throws Exception {
    String query = "?searchTerm=Mord Totschlag";

    mockMvc
        .perform(get(ApiConfig.Paths.DOCUMENT + query).contentType(MediaType.APPLICATION_JSON))
        .andExpect(everyIdContainsEither("{m,t}", "{m,t,r}"))
        .andExpect(idsContainAll("C1", "C2", "C3", "N1"));
  }

  @Test
  @DisplayName("Should return correct results for one term")
  void shouldReturnCorrectResultForOtherTerm() throws Exception {
    String query = "?searchTerm=Raub";

    mockMvc
        .perform(get(ApiConfig.Paths.DOCUMENT + query).contentType(MediaType.APPLICATION_JSON))
        .andExpect(everyIdContainsEither("{r}", "{m,t,r}"))
        .andExpect(idsContainAll("C1", "C2", "C3", "N2", "N3"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"Mord Totschlag Raub", "Raub Mord Totschlag", "totschlag MORD, raub"})
  @DisplayName("Should return correct results for 3 terms")
  void shouldReturnCorrectResultForThreeTerms(String searchTerm) throws Exception {
    String query = "?searchTerm=%s".formatted(searchTerm);

    mockMvc
        .perform(get(ApiConfig.Paths.DOCUMENT + query).contentType(MediaType.APPLICATION_JSON))
        .andExpect(everyIdContainsEither("{m,t,r}"))
        .andExpect(idsContainAll("C1", "C2", "C3"));
  }

  static Stream<Arguments> shouldMatchPhraseIfQuotedArguments() {
    return Stream.of(
        Arguments.of("\"Mord, Totschlag, Raub\"", 1, idsContainAll("C2")),
        Arguments.of("\"Raub, Mord, Totschlag\"", 1, idsContainAll("C3")),
        Arguments.of("\"raub mord TOTSCHLAG\"", 1, idsContainAll("C3")),
        Arguments.of("\"mord Totschlag\" Raub", 2, idsContainAll("C2", "C3")),
        Arguments.of("\"mord Totschlag\" \"Totschlag Raub\"", 1, idsContainAll("C2")),
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
