package de.bund.digitalservice.ris.search.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.controller.api.testData.AdministrativeDirectiveTestData;
import de.bund.digitalservice.ris.search.controller.api.testData.CaseLawTestData;
import de.bund.digitalservice.ris.search.controller.api.testData.LiteratureTestData;
import de.bund.digitalservice.ris.search.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.controller.api.values.SortingTestArguments;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

@SuppressWarnings("unchecked")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AdvancedSearchControllerApiIntegrationTest extends ContainersIntegrationBase {

  @BeforeAll
  public void loadDefaults() {
    loadDefaultData();
  }

  @Autowired private MockMvc mockMvc;

  static String buildAdvancedQuery(String key, String value) {
    return "?query=%s:%s".formatted(key, value);
  }

  private static List<Arguments> forAliases(String value, int expectedCount, String... aliases) {
    return Arrays.stream(aliases).map(alias -> Arguments.of(alias, value, expectedCount)).toList();
  }

  private static Stream<Arguments> caseLawAdvancedSearchParams() {
    var arguments =
        new ArrayList<>(
            forAliases(
                "Urteil",
                CaseLawTestData.URTEIL_COUNT,
                "document_type",
                "DOKUMENTTYP",
                "TYP",
                "DT",
                "DTYP"));
    arguments.add(Arguments.of("location", "Berlin", 2));

    arguments.addAll(forAliases("abweichende", 1, "dissenting_opinion", "ABWMEIN"));

    arguments.add(
        Arguments.of(
            "decision_date",
            "[2024-01-01 TO 2024-12-31]",
            3)); // DATUM and DAT aliases are tested separately

    arguments.addAll(forAliases("\"ECLI:DE:FGHH:1972:0630.III10.72.0\"", 1, "ECLI"));

    arguments.addAll(forAliases("folgende", 1, "grounds", "GRUENDE", "GR"));

    arguments.addAll(
        forAliases(
            "Entscheidungsgründe",
            1,
            "decision_grounds",
            "ENTSCHEIDUNGSGRUENDE",
            "EGR",
            "TXTGRUENDE"));

    arguments.addAll(
        forAliases(
            "Leitsatz",
            CaseLawTestData.WITH_LEITSATZ_COUNT,
            "guiding_principle",
            "LEITSATZ",
            "LS",
            "TXTLS"));

    arguments.addAll(
        forAliases(
            "Orientierungssatz", 1, "headnote", "ORIENTIERUNGSSATZ", "OSATZ", "OS", "TXTOS"));

    arguments.addAll(
        forAliases("Sonstiger", 1, "other_long_text", "SLANGTEXT", "STEXT", "TXTSONSTLT"));

    arguments.addAll(
        forAliases(
            "Sonstiger Orientierungssatz",
            2,
            "other_headnote",
            "SORIENTIERUNGSSATZ",
            "SOSATZ",
            "TXTSOSATZ"));

    arguments.addAll(forAliases("Tatbestand", 2, "case_facts", "TATBESTAND", "TB", "TXTTB"));

    int documentsWithTenorCount =
        (int)
            CaseLawTestData.allDocuments.stream()
                .filter(d -> Objects.equals(d.tenor(), "Tenor"))
                .count();
    assertThat(documentsWithTenorCount).isPositive();
    arguments.addAll(forAliases("Tenor", documentsWithTenorCount, "TENOR", "TN", "TXTTN"));

    arguments.addAll(forAliases("Titelzeile", 1, "headline", "SHORTTITLE"));

    arguments.addAll(forAliases("Titelzeile", 1, "TITELZEILE", "TITEL", "TTZE", "TXTTTZE"));

    arguments.addAll(forAliases("outlineTest", 1, "outline", "GLIEDERUNG", "GD"));

    arguments.addAll(
        forAliases("judicialbodyTest", 1, "judicial_body", "SPRUCHKOERPER", "SK", "GERSK"));

    arguments.addAll(forAliases("LG Saarbrücken", 1, "court_keyword", "GER", "GERICHT", "G"));

    arguments.addAll(forAliases("keywordsTest", 1, "keywords", "SLW", "SCHLAGWOERTER"));

    arguments.addAll(forAliases("LG", 1, "court_type", "GERICHTSTYP", "GERTYP"));

    arguments.addAll(forAliases("Hamburg", 1, "location", "GERICHTSORT", "GERORT"));

    arguments.addAll(forAliases("\"IX ZR 100/10\"", 1, "file_numbers", "AKTENZEICHEN", "AZ"));

    arguments.addAll(forAliases("decisionNames", 1, "decision_name", "ENTSCHEIDUNGSNAME", "ENAME"));

    arguments.addAll(forAliases("eine abweichende Meinung", 1, "dissenting_opinion", "ABWMEIN"));

    arguments.addAll(
        forAliases("deviatingDocumentNumbers", 1, "deviating_document_number", "ABWNR"));

    arguments.addAll(forAliases("JA", 2, "legal_effect", "RECHTSKRAFT", "RK"));

    arguments.addAll(forAliases("celexTest", 1, "celex", "CELEXNR"));

    arguments.addAll(forAliases("gerichtsbarkeitTest", 1, "gerichtsbarkeit", "GERB"));

    arguments.addAll(forAliases("berufsbilderTest", 1, "berufsbilder", "BERB"));

    arguments.addAll(forAliases("kuendigungsartenTest", 1, "kuendigungsarten", "KUENDIGUNGSART"));

    arguments.addAll(forAliases("herkunftslaenderTest", 1, "herkunftslaender", "HERKL"));

    arguments.addAll(forAliases("regionenTest", 1, "regionen", "REGION"));

    arguments.addAll(forAliases("tarifvertraegeTest", 1, "tarifvertraege", "TV"));

    arguments.addAll(
        forAliases("kuendigungsgruendeTest", 1, "kuendigungsgruende", "KUENDIGUNGSGRUND"));

    arguments.addAll(forAliases("mitwirkendeRichterTest", 1, "mitwirkende_richter", "MWRICHTER"));

    arguments.addAll(forAliases("sachgebieteTest", 1, "sachgebiete", "SG"));

    arguments.addAll(forAliases("streitjahreTest", 1, "streitjahre", "STREITJAHR"));

    arguments.addAll(forAliases("fehlerhafteGerichteTest", 1, "fehlerhafte_gerichte", "ABWGER"));

    arguments.addAll(forAliases("definitionenTest", 1, "definitionen", "DEF"));

    arguments.addAll(forAliases("erledigungTest", 1, "erledigung", "ANHERLEDIGT"));

    arguments.addAll(forAliases("erledigungsvermerkTest", 1, "erledigungsvermerk", "ANHERLVERM"));

    arguments.addAll(forAliases("rechtsfrageTest", 1, "rechtsfrage", "ANHRECHTSFRAGE"));

    arguments.addAll(
        forAliases("rechtsfrageGesamtTest", 1, "rechtsfrage_gesamt", "TXTRECHTSFRAGE"));

    arguments.addAll(forAliases("previousDecisionsTest", 1, "previous_decisions", "VORGEHEND"));

    arguments.addAll(forAliases("ensuingDecisionsTest", 1, "ensuing_decisions", "NACHGEHEND"));

    arguments.addAll(
        forAliases(
            "aktivzitierungRechtsprechungTest", 1, "aktivzitierung_rechtsprechung", "ZITRSPR"));

    arguments.addAll(
        forAliases(
            "passivzitierungRechtsprechungTest", 1, "passivzitierung_rechtsprechung", "PZITRSPR"));

    arguments.addAll(
        forAliases(
            "aktivzitierungVerwaltungsvorschriftenTest",
            1,
            "aktivzitierung_verwaltungsvorschriften",
            "ZITVWV"));

    arguments.addAll(
        forAliases(
            "passivzitierungVerwaltungsvorschriftenTest",
            1,
            "passivzitierung_verwaltungsvorschriften",
            "PZITVWV"));

    arguments.addAll(
        forAliases(
            "aktivzitierungLiteraturSelbstaendigTest",
            1,
            "aktivzitierung_literatur_selbstaendig",
            "ZITLITSELBST"));

    arguments.addAll(
        forAliases(
            "aktivzitierungLiteraturUnselbstaendigTest",
            1,
            "aktivzitierung_literatur_unselbstaendig",
            "ZITLITUNSELBST"));

    arguments.addAll(
        forAliases(
            "passivzitierungLiteraturSelbstaendigTest",
            1,
            "passivzitierung_literatur_selbstaendig",
            "PZITLITSELBST"));

    arguments.addAll(
        forAliases(
            "passivzitierungLiteraturUnselbstaendigTest",
            1,
            "passivzitierung_literatur_unselbstaendig",
            "PZITLITUNSELBST"));

    arguments.addAll(forAliases("amtlicheFundstellenTest", 1, "amtliche_fundstellen", "FS"));

    arguments.addAll(
        forAliases("nichtamtlicheFundstellenTest", 1, "nichtamtliche_fundstellen", "FSNAMTL"));

    arguments.addAll(forAliases("normenketteTest", 1, "normenkette", "NORM"));

    arguments.addAll(
        forAliases(
            "hasLegislativeMandateTest", 1, "has_legislative_mandate", "GESETZGEBUNGSAUFTRAG"));

    arguments.addAll(
        forAliases("rechtsmittelfuehrerTest", 1, "rechtsmittelfuehrer", "ANHRMFUEHRER"));

    arguments.addAll(
        forAliases("rechtsmittelzulassungTest", 1, "rechtsmittelzulassung", "ANHRMZULASSUNG"));

    arguments.addAll(forAliases("abweichendeEclisTest", 1, "abweichende_eclis", "ABWECLI"));

    arguments.addAll(
        forAliases("abweichendeAktenzeichenTest", 1, "abweichende_aktenzeichen", "ABWAZ"));

    arguments.addAll(forAliases("true", 1, "vorabdokument", "DVORAB"));

    arguments.addAll(forAliases("[2024-05-05 TO 2024-05-05]", 1, "abweichende_daten", "ABWDAT"));

    arguments.addAll(
        forAliases(
            "[2024-05-06 TO 2024-05-06]", 1, "daten_der_muendlichen_verhandlung", "MDLVERHDAT"));

    arguments.addAll(forAliases("[2024-05-07 TO 2024-05-07]", 1, "langtextdatum", "LANGTEXTDAT"));

    arguments.addAll(
        forAliases("[2024-05-08 TO 2024-05-08]", 1, "letzte_veroeffentlichung", "LASTPUBLDATE"));

    arguments.addAll(
        forAliases("[2024-05-09 TO 2024-05-09]", 1, "erstveroeffentlichung", "FIRSTPUBLDATE"));

    arguments.addAll(forAliases("[2024-05-10 TO 2024-05-10]", 1, "mitteilungsdatum", "ANHMITT"));

    return arguments.stream();
  }

  @ParameterizedTest
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

  private static Stream<Arguments> literatureAdvancedSearchParams() {
    var arguments = new ArrayList<Arguments>();
    arguments.addAll(
        forAliases("[2020-01-01 TO 2020-12-31]", 1, "first_publication_date", "DATUM", "datum"));

    arguments.addAll(forAliases("kompakt", 1, "main_title", "T", "t", "HAUPTTITEL", "haupttitel"));

    arguments.addAll(forAliases("Hoffmann", 1, "authors", "V", "v", "VERF", "verf"));

    arguments.addAll(forAliases("NJW", 1, "dependent_references", "FU", "fu", "FSULIT", "fsulit"));

    arguments.addAll(forAliases("GG", 1, "norm_references", "N", "n", "NORM", "norm"));

    arguments.addAll(forAliases("ZPO", 1, "documentary_title", "DOKTITEL", "doktitel"));

    arguments.addAll(forAliases("Zusatz", 1, "main_title_additions", "TITELZUS", "titelzus"));

    arguments.addAll(forAliases("kurztitelTest", 1, "short_titles", "KURZTITEL", "kurztitel"));

    arguments.addAll(
        forAliases("nebentitelTest", 1, "additional_titles", "NEBENTITEL", "nebentitel"));

    arguments.addAll(
        forAliases("gesamttitelTest", 1, "full_title_additions", "GESAMTTITEL", "gesamttitel"));

    arguments.addAll(forAliases("Lehrbuch", 1, "short_report", "KTXT", "ktxt"));

    arguments.addAll(
        forAliases("Staatsorganisationsrecht", 1, "outline", "GLIEDERUNG", "gliederung"));

    arguments.addAll(forAliases("fussnoteTest", 1, "footnotes", "FN", "fn"));

    arguments.addAll(forAliases("Meier", 1, "collaborators", "MITARB", "mitarb"));

    arguments.addAll(forAliases("FOO", 1, "originator", "URH", "urh"));

    arguments.addAll(forAliases("herausgeberPersonTest", 1, "publisher_persons", "HRSGP", "hrsgp"));

    arguments.addAll(
        forAliases("herausgeberInstitutionTest", 1, "publisher_organizations", "HRSGI", "hrsgi"));

    arguments.addAll(forAliases("bearbeiterTest", 1, "editor", "BEARB", "bearb"));

    arguments.addAll(forAliases("begruenderTest", 1, "founder", "BEGR", "begr"));

    arguments.addAll(forAliases("Festschrift", 1, "independent_references", "FSSLIT", "fsslit"));

    arguments.addAll(forAliases("KALU000000003", 1, "document_number", "DNR", "dnr"));

    arguments.addAll(forAliases("isbnTest", 1, "international_identifiers", "ISBN", "isbn"));

    arguments.addAll(forAliases("Buch", 1, "document_types", "DTYP", "dtyp"));

    arguments.addAll(forAliases("2020", 1, "years_of_publication", "JAHR", "jahr"));

    arguments.addAll(forAliases("ausgabeTest", 1, "edition", "AUSGABE", "ausgabe"));

    arguments.addAll(forAliases("teilbandTest", 1, "volumes", "TEILBAND", "teilband"));

    arguments.addAll(forAliases("verlagTest", 1, "publisher_information", "VERLAG", "verlag"));

    arguments.addAll(forAliases("hochschulvermerkTest", 1, "university_note", "HSSVMK", "hssvmk"));

    arguments.addAll(forAliases("Kongress", 1, "conference_note", "KGRVMK", "kgrvmk"));

    arguments.addAll(forAliases("eng", 1, "language", "SPRACHE", "sprache"));

    arguments.addAll(forAliases("titelAspektTest", 1, "titel_aspekte", "TITEL", "titel"));

    arguments.addAll(
        forAliases("zitformalTest", 1, "bibliothekarische_verweisungen", "ZITFORMAL", "zitformal"));

    arguments.addAll(forAliases("vorauflageTest", 1, "vorauflagen", "VORAUFL", "voraufl"));

    arguments.addAll(
        forAliases("kollationsvermerkTest", 1, "kollationsvermerk", "KOLLVMK", "kollvmk"));

    arguments.addAll(forAliases("schlagwortTest", 1, "schlagworte", "SLW", "slw"));

    arguments.addAll(forAliases("sachgebietTest", 1, "sachgebiete", "SG", "sg"));

    arguments.addAll(forAliases("regionTest", 1, "regionen", "REGION", "region"));

    arguments.addAll(forAliases("berufsbildTest", 1, "berufsbilder", "BERUFSBILD", "berufsbild"));

    arguments.addAll(forAliases("definitionTest", 1, "definitionen", "DEFINITION", "definition"));

    arguments.addAll(
        forAliases("kuendigungsgrundTest", 1, "kuendigungsgruende", "KGRUND", "kgrund"));

    arguments.addAll(forAliases("tarifvertragTest", 1, "tarifvertraege", "TV", "tv"));

    arguments.addAll(
        forAliases("ueberPersonTest", 1, "ueber_personen_institutionen", "UEBER", "ueber"));

    arguments.addAll(forAliases("ueberKongressTest", 1, "ueber_kongresse", "KONGR", "kongr"));

    arguments.addAll(
        forAliases(
            "fuerPersonTest", 1, "fuer_personen_institutionen", "FUERPERSINST", "fuerpersinst"));

    arguments.addAll(
        forAliases("abweichendeNummerTest", 1, "abweichende_dokumentnummern", "ABWNR", "abwnr"));

    arguments.addAll(forAliases("dokumentartTest", 1, "dokumentart", "DART", "dart"));

    arguments.addAll(
        forAliases(
            "zitierungRechtsprechungTest",
            1,
            "aktivzitierungen_rechtsprechung",
            "ZITRSPR",
            "zitrspr"));

    arguments.addAll(
        forAliases(
            "zitierungSelbstaendigTest",
            1,
            "aktivzitierungen_literatur_selbstaendig",
            "ZITSLIT",
            "zitslit"));

    arguments.addAll(
        forAliases(
            "zitierungUnselbstaendigTest",
            1,
            "aktivzitierungen_literatur_unselbstaendig",
            "ZITULIT",
            "zitulit"));

    arguments.addAll(
        forAliases(
            "zitierungVerwaltungTest",
            1,
            "aktivzitierungen_verwaltungsvorschriften",
            "ZITVWV",
            "zitvwv"));

    return arguments.stream();
  }

  // Only the literature endpoint: several aliases (e.g. DATUM, NORM, SLW) point to other fields
  // in the other indices, so the generic endpoint would return a different count.
  @ParameterizedTest
  @MethodSource("literatureAdvancedSearchParams")
  void shouldFindCorrectLiteratureCount(String key, String value, int expectedCount)
      throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE_ADVANCED_SEARCH + buildAdvancedQuery(key, value))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(expectedCount)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(strings = {"decision_date", "DATUM", "DAT", "AVSDAT"})
  @DisplayName("Case law date and alias field find 2 documents")
  void caseLawDateAndAliasFieldsFind2Documents(String queryParam) throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.CASELAW_ADVANCED_SEARCH
                    + String.format("?query=%s:[2025-01-01 TO 2025-12-31]", queryParam))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(2)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(strings = {"entry_into_force_date", "DATUM", "DAT"})
  @DisplayName("Legislation entry_into_force_date and aliases find 3 documents")
  void legislationEntryIntoForceDateAndAliasesFind3Documents(String queryParam) throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH
                    + String.format("?query=%s:[2025-01-01 TO 2025-12-31]", queryParam))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(3)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName(
      "Should return mixed result when looking for a specific date that match for both norms and case law")
  void shouldReturnOkDateQuery() throws Exception {

    DocumentContext json =
        JsonPath.parse(
            mockMvc
                .perform(
                    get(ApiConfig.Paths.DOCUMENT_ADVANCED_SEARCH
                            + "?query=DATUM:[2025-01-01 TO 2025-12-31]")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString());

    assertThat(json.read("$.member.length()", Integer.class)).isEqualTo(5);

    List<String> types = json.read("$.member[*].item[\"@type\"]", List.class);
    assertThat(types)
        .containsExactlyInAnyOrder(
            "Decision", "Decision", "Legislation", "Legislation", "Legislation");
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
  @ValueSource(strings = {"document_number", "DOKUMENTNUMMER", "NR", "DNR"})
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
    int literatureSize = LiteratureTestData.allDocuments.size();
    int normsSize = NormsTestData.allNorms.size();
    int administrativeDirectiveSize = AdministrativeDirectiveTestData.allDocuments.size();
    int combinedSize = caseLawSize + literatureSize + normsSize + administrativeDirectiveSize;
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
            + "?query=*:*"
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
  @ValueSource(strings = {"official_title", "AL", "LU"})
  @DisplayName("Should return 200 when looking for a specific official title and aliases")
  void shouldReturnOkOfficialTitleQuery(String queryParam) throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH
                    + String.format("?query=%s:\"Test Gesetz Nr. 2\"", queryParam))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0].item.alternateName", Matchers.is("TestG2")))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(strings = {"official_short_title", "AK", "KU"})
  @DisplayName("Should return 200 when looking for official short title and aliases")
  void shouldReturnOkOfficialShortTitleQuery(String queryParam) throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH
                    + String.format("?query=%s:TestG3", queryParam))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0].item.alternateName", Matchers.is("TestG3")))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(strings = {"article_names", "UP"})
  @DisplayName("Should return 200 when looking for article names and aliases")
  void shouldReturnOkArticleNamesQuery(String queryParam) throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH
                    + String.format("?query=%s:§ 1 Example article", queryParam))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0].item.alternateName", Matchers.is("TestG1")))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(strings = {"article_texts", "TP"})
  @DisplayName("Should return 200 when looking for article text and aliases")
  void shouldReturnOkArticleTextsQuery(String queryParam) throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH
                    + String.format("?query=%s:example text 1", queryParam))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0].item.alternateName", Matchers.is("TestG1")))
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
  @ValueSource(strings = {"abbreviation", "AB"})
  @DisplayName("Should return 200 when looking for the abbreviation and aliases")
  void shouldReturnOkAbbreviation(String queryParam) throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH
                    + String.format("?query=%s:TeG", queryParam))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(3)))
        .andExpect(jsonPath("$.member[0].item.abbreviation", Matchers.is("TeG")))
        .andExpect(jsonPath("$.member[1].item.abbreviation", Matchers.is("TeG")))
        .andExpect(jsonPath("$.member[2].item.abbreviation", Matchers.is("TeG")))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should return result even if there are no highlights in parts or title")
  void shouldReturnResultsEvenIfNoHighlightsFoundInArticlesOrTitle() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH
                    + String.format("?query=%s:TeG AND %s:TeG", "abbreviation", "AB"))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(3)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "?query=id:foobar OR",
        "?query=work_eli:foobar OR",
        "?query=document_number:BFRE000047655 OR"
      })
  @DisplayName("Should return an error when the search has invalid lucene query")
  void invalidLuceneQuery(String url) throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.DOCUMENT_ADVANCED_SEARCH + url)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isUnprocessableContent())
        .andExpect(jsonPath("$.errors[0].code").value("invalid_lucene_query"))
        .andExpect(jsonPath("$.errors[0].parameter").value("query"));
  }

  @Test
  @DisplayName("Should return an error when only Opensearch rejects the query as unparseable")
  void queryOnlyOpensearchRejects() throws Exception {
    // Valid Lucene, so it passes validation and matches norms, but the article query derived from
    // it ends up with an empty clause, which Opensearch can't parse
    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH + "?query=example OR \"\"")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnprocessableContent())
        .andExpect(jsonPath("$.errors[0].code").value("invalid_lucene_query"))
        .andExpect(jsonPath("$.errors[0].parameter").value("query"));
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
        .andExpect(status().isUnprocessableContent())
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

  @ParameterizedTest
  @ValueSource(strings = {"(VT:text content)", "(NG:Ministerium)", "(FU:Ipsum)", "(U:title)"})
  void testAdministrativeDirectiveLuceneEndpoint(String query) throws Exception {
    String url =
        String.format(
            "%s?query=%s&size=50&sort=default&pageIndex=0",
            ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE_ADVANCED_SEARCH, query);

    mockMvc
        .perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().is(200),
            jsonPath("$.member[0].item.documentNumber", Matchers.is("KSNR000000000")));
  }
}
