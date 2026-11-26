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

  private static Stream<Arguments> caseLawAdvancedSearchParams() {
    var arguments = new ArrayList<Arguments>();
    for (var alias : new String[] {"document_type", "DOKUMENTTYP", "TYP", "DT", "DTYP"}) {
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

    for (var alias : new String[] {"ECLI"}) {
      arguments.add(Arguments.of(alias, "\"ECLI:DE:FGHH:1972:0630.III10.72.0\"", 1));
    }

    for (var alias : new String[] {"grounds", "GRUENDE", "GR"}) {
      arguments.add(Arguments.of(alias, "folgende", 1));
    }

    for (var alias :
        new String[] {"decision_grounds", "ENTSCHEIDUNGSGRUENDE", "EGR", "TXTGRUENDE"}) {
      arguments.add(Arguments.of(alias, "Entscheidungsgründe", 1));
    }

    for (var alias : new String[] {"guiding_principle", "LEITSATZ", "LS", "TXTLS"}) {
      arguments.add(Arguments.of(alias, "Leitsatz", CaseLawTestData.WITH_LEITSATZ_COUNT));
    }

    for (var alias : new String[] {"headnote", "ORIENTIERUNGSSATZ", "OSATZ", "OS", "TXTOS"}) {
      arguments.add(Arguments.of(alias, "Orientierungssatz", 1));
    }

    for (var alias : new String[] {"other_long_text", "SLANGTEXT", "STEXT", "TXTSONSTLT"}) {
      arguments.add(Arguments.of(alias, "Sonstiger", 1));
    }

    for (var alias : new String[] {"other_headnote", "SORIENTIERUNGSSATZ", "SOSATZ", "TXTSOSATZ"}) {
      arguments.add(Arguments.of(alias, "Sonstiger Orientierungssatz", 2));
    }

    for (var alias : new String[] {"case_facts", "TATBESTAND", "TB", "TXTTB"}) {
      arguments.add(Arguments.of(alias, "Tatbestand", 2));
    }

    int documentsWithTenorCount =
        (int)
            CaseLawTestData.allDocuments.stream()
                .filter(d -> Objects.equals(d.tenor(), "Tenor"))
                .count();
    assertThat(documentsWithTenorCount).isPositive();
    for (var alias : new String[] {"TENOR", "TN", "TXTTN"}) {
      arguments.add(Arguments.of(alias, "Tenor", documentsWithTenorCount));
    }

    for (var alias : new String[] {"headline", "SHORTTITLE"}) {
      arguments.add(Arguments.of(alias, "Titelzeile", 1));
    }

    for (var alias : new String[] {"TITELZEILE", "TITEL", "TTZE", "TXTTTZE"}) {
      arguments.add(Arguments.of(alias, "Titelzeile", 1));
    }

    for (var alias : new String[] {"outline", "GLIEDERUNG", "GD"}) {
      arguments.add(Arguments.of(alias, "outlineTest", 1));
    }

    for (var alias : new String[] {"judicial_body", "SPRUCHKOERPER", "SK", "GERSK"}) {
      arguments.add(Arguments.of(alias, "judicialbodyTest", 1));
    }

    for (var alias : new String[] {"court_keyword", "GER", "GERICHT", "G"}) {
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

    for (var alias : new String[] {"legal_effect", "RECHTSKRAFT", "RK"}) {
      arguments.add(Arguments.of(alias, "JA", 2));
    }

    for (var alias : new String[] {"celex", "CELEXNR"}) {
      arguments.add(Arguments.of(alias, "celexTest", 1));
    }

    for (var alias : new String[] {"gerichtsbarkeit", "GERB"}) {
      arguments.add(Arguments.of(alias, "gerichtsbarkeitTest", 1));
    }

    for (var alias : new String[] {"berufsbilder", "BERB"}) {
      arguments.add(Arguments.of(alias, "berufsbilderTest", 1));
    }

    for (var alias : new String[] {"kuendigungsarten", "KUENDIGUNGSART"}) {
      arguments.add(Arguments.of(alias, "kuendigungsartenTest", 1));
    }

    for (var alias : new String[] {"herkunftslaender", "HERKL"}) {
      arguments.add(Arguments.of(alias, "herkunftslaenderTest", 1));
    }

    for (var alias : new String[] {"regionen", "REGION"}) {
      arguments.add(Arguments.of(alias, "regionenTest", 1));
    }

    for (var alias : new String[] {"tarifvertraege", "TV"}) {
      arguments.add(Arguments.of(alias, "tarifvertraegeTest", 1));
    }

    for (var alias : new String[] {"kuendigungsgruende", "KUENDIGUNGSGRUND"}) {
      arguments.add(Arguments.of(alias, "kuendigungsgruendeTest", 1));
    }

    for (var alias : new String[] {"mitwirkende_richter", "MWRICHTER"}) {
      arguments.add(Arguments.of(alias, "mitwirkendeRichterTest", 1));
    }

    for (var alias : new String[] {"sachgebiete", "SG"}) {
      arguments.add(Arguments.of(alias, "sachgebieteTest", 1));
    }

    for (var alias : new String[] {"streitjahre", "STREITJAHR"}) {
      arguments.add(Arguments.of(alias, "streitjahreTest", 1));
    }

    for (var alias : new String[] {"fehlerhafte_gerichte", "ABWGER"}) {
      arguments.add(Arguments.of(alias, "fehlerhafteGerichteTest", 1));
    }

    for (var alias : new String[] {"definitionen", "DEF"}) {
      arguments.add(Arguments.of(alias, "definitionenTest", 1));
    }

    for (var alias : new String[] {"erledigung", "ANHERLEDIGT"}) {
      arguments.add(Arguments.of(alias, "erledigungTest", 1));
    }

    for (var alias : new String[] {"erledigungsvermerk", "ANHERLVERM"}) {
      arguments.add(Arguments.of(alias, "erledigungsvermerkTest", 1));
    }

    for (var alias : new String[] {"rechtsfrage", "ANHRECHTSFRAGE"}) {
      arguments.add(Arguments.of(alias, "rechtsfrageTest", 1));
    }

    for (var alias : new String[] {"rechtsfrage_gesamt", "TXTRECHTSFRAGE"}) {
      arguments.add(Arguments.of(alias, "rechtsfrageGesamtTest", 1));
    }

    for (var alias : new String[] {"previous_decisions", "VORGEHEND"}) {
      arguments.add(Arguments.of(alias, "previousDecisionsTest", 1));
    }

    for (var alias : new String[] {"ensuing_decisions", "NACHGEHEND"}) {
      arguments.add(Arguments.of(alias, "ensuingDecisionsTest", 1));
    }

    for (var alias : new String[] {"aktivzitierung_rechtsprechung", "ZITRSPR"}) {
      arguments.add(Arguments.of(alias, "aktivzitierungRechtsprechungTest", 1));
    }

    for (var alias : new String[] {"passivzitierung_rechtsprechung", "PZITRSPR"}) {
      arguments.add(Arguments.of(alias, "passivzitierungRechtsprechungTest", 1));
    }

    for (var alias : new String[] {"aktivzitierung_verwaltungsvorschriften", "ZITVWV"}) {
      arguments.add(Arguments.of(alias, "aktivzitierungVerwaltungsvorschriftenTest", 1));
    }

    for (var alias : new String[] {"passivzitierung_verwaltungsvorschriften", "PZITVWV"}) {
      arguments.add(Arguments.of(alias, "passivzitierungVerwaltungsvorschriftenTest", 1));
    }

    for (var alias : new String[] {"aktivzitierung_literatur_selbstaendig", "ZITLITSELBST"}) {
      arguments.add(Arguments.of(alias, "aktivzitierungLiteraturSelbstaendigTest", 1));
    }

    for (var alias : new String[] {"aktivzitierung_literatur_unselbstaendig", "ZITLITUNSELBST"}) {
      arguments.add(Arguments.of(alias, "aktivzitierungLiteraturUnselbstaendigTest", 1));
    }

    for (var alias : new String[] {"passivzitierung_literatur_selbstaendig", "PZITLITSELBST"}) {
      arguments.add(Arguments.of(alias, "passivzitierungLiteraturSelbstaendigTest", 1));
    }

    for (var alias : new String[] {"passivzitierung_literatur_unselbstaendig", "PZITLITUNSELBST"}) {
      arguments.add(Arguments.of(alias, "passivzitierungLiteraturUnselbstaendigTest", 1));
    }

    for (var alias : new String[] {"amtliche_fundstellen", "FS"}) {
      arguments.add(Arguments.of(alias, "amtlicheFundstellenTest", 1));
    }

    for (var alias : new String[] {"nichtamtliche_fundstellen", "FSNAMTL"}) {
      arguments.add(Arguments.of(alias, "nichtamtlicheFundstellenTest", 1));
    }

    for (var alias : new String[] {"normenkette", "NORM"}) {
      arguments.add(Arguments.of(alias, "normenketteTest", 1));
    }

    for (var alias : new String[] {"has_legislative_mandate", "GESETZGEBUNGSAUFTRAG"}) {
      arguments.add(Arguments.of(alias, "hasLegislativeMandateTest", 1));
    }

    for (var alias : new String[] {"rechtsmittelfuehrer", "ANHRMFUEHRER"}) {
      arguments.add(Arguments.of(alias, "rechtsmittelfuehrerTest", 1));
    }

    for (var alias : new String[] {"rechtsmittelzulassung", "ANHRMZULASSUNG"}) {
      arguments.add(Arguments.of(alias, "rechtsmittelzulassungTest", 1));
    }

    for (var alias : new String[] {"abweichende_eclis", "ABWECLI"}) {
      arguments.add(Arguments.of(alias, "abweichendeEclisTest", 1));
    }

    for (var alias : new String[] {"abweichende_aktenzeichen", "ABWAZ"}) {
      arguments.add(Arguments.of(alias, "abweichendeAktenzeichenTest", 1));
    }

    for (var alias : new String[] {"vorabdokument", "DVORAB"}) {
      arguments.add(Arguments.of(alias, "true", 1));
    }

    for (var alias : new String[] {"abweichende_daten", "ABWDAT"}) {
      arguments.add(Arguments.of(alias, "[2024-05-05 TO 2024-05-05]", 1));
    }

    for (var alias : new String[] {"daten_der_muendlichen_verhandlung", "MDLVERHDAT"}) {
      arguments.add(Arguments.of(alias, "[2024-05-06 TO 2024-05-06]", 1));
    }

    for (var alias : new String[] {"langtextdatum", "LANGTEXTDAT"}) {
      arguments.add(Arguments.of(alias, "[2024-05-07 TO 2024-05-07]", 1));
    }

    for (var alias : new String[] {"letzte_veroeffentlichung", "LASTPUBLDATE"}) {
      arguments.add(Arguments.of(alias, "[2024-05-08 TO 2024-05-08]", 1));
    }

    for (var alias : new String[] {"erstveroeffentlichung", "FIRSTPUBLDATE"}) {
      arguments.add(Arguments.of(alias, "[2024-05-09 TO 2024-05-09]", 1));
    }

    for (var alias : new String[] {"mitteilungsdatum", "ANHMITT"}) {
      arguments.add(Arguments.of(alias, "[2024-05-10 TO 2024-05-10]", 1));
    }

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
    for (var alias : new String[] {"first_publication_date", "DATUM", "datum"}) {
      arguments.add(Arguments.of(alias, "[2020-01-01 TO 2020-12-31]", 1));
    }

    for (var alias : new String[] {"main_title", "T", "t", "HAUPTTITEL", "haupttitel"}) {
      arguments.add(Arguments.of(alias, "kompakt", 1));
    }

    for (var alias : new String[] {"authors", "V", "v", "VERF", "verf"}) {
      arguments.add(Arguments.of(alias, "Hoffmann", 1));
    }

    for (var alias : new String[] {"dependent_references", "FU", "fu", "FSULIT", "fsulit"}) {
      arguments.add(Arguments.of(alias, "NJW", 1));
    }

    for (var alias : new String[] {"norm_references", "N", "n", "NORM", "norm"}) {
      arguments.add(Arguments.of(alias, "GG", 1));
    }

    for (var alias : new String[] {"documentary_title", "DOKTITEL", "doktitel"}) {
      arguments.add(Arguments.of(alias, "ZPO", 1));
    }

    for (var alias : new String[] {"main_title_additions", "TITELZUS", "titelzus"}) {
      arguments.add(Arguments.of(alias, "Zusatz", 1));
    }

    for (var alias : new String[] {"short_titles", "KURZTITEL", "kurztitel"}) {
      arguments.add(Arguments.of(alias, "kurztitelTest", 1));
    }

    for (var alias : new String[] {"additional_titles", "NEBENTITEL", "nebentitel"}) {
      arguments.add(Arguments.of(alias, "nebentitelTest", 1));
    }

    for (var alias : new String[] {"full_title_additions", "GESAMTTITEL", "gesamttitel"}) {
      arguments.add(Arguments.of(alias, "gesamttitelTest", 1));
    }

    for (var alias : new String[] {"short_report", "KTXT", "ktxt"}) {
      arguments.add(Arguments.of(alias, "Lehrbuch", 1));
    }

    for (var alias : new String[] {"outline", "GLIEDERUNG", "gliederung"}) {
      arguments.add(Arguments.of(alias, "Staatsorganisationsrecht", 1));
    }

    for (var alias : new String[] {"footnotes", "FN", "fn"}) {
      arguments.add(Arguments.of(alias, "fussnoteTest", 1));
    }

    for (var alias : new String[] {"collaborators", "MITARB", "mitarb"}) {
      arguments.add(Arguments.of(alias, "Meier", 1));
    }

    for (var alias : new String[] {"originator", "URH", "urh"}) {
      arguments.add(Arguments.of(alias, "FOO", 1));
    }

    for (var alias : new String[] {"publisher_persons", "HRSGP", "hrsgp"}) {
      arguments.add(Arguments.of(alias, "herausgeberPersonTest", 1));
    }

    for (var alias : new String[] {"publisher_organizations", "HRSGI", "hrsgi"}) {
      arguments.add(Arguments.of(alias, "herausgeberInstitutionTest", 1));
    }

    for (var alias : new String[] {"editor", "BEARB", "bearb"}) {
      arguments.add(Arguments.of(alias, "bearbeiterTest", 1));
    }

    for (var alias : new String[] {"founder", "BEGR", "begr"}) {
      arguments.add(Arguments.of(alias, "begruenderTest", 1));
    }

    for (var alias : new String[] {"independent_references", "FSSLIT", "fsslit"}) {
      arguments.add(Arguments.of(alias, "Festschrift", 1));
    }

    for (var alias : new String[] {"document_number", "DNR", "dnr"}) {
      arguments.add(Arguments.of(alias, "KALU000000003", 1));
    }

    for (var alias : new String[] {"international_identifiers", "ISBN", "isbn"}) {
      arguments.add(Arguments.of(alias, "isbnTest", 1));
    }

    for (var alias : new String[] {"document_types", "DTYP", "dtyp"}) {
      arguments.add(Arguments.of(alias, "Buch", 1));
    }

    for (var alias : new String[] {"years_of_publication", "JAHR", "jahr"}) {
      arguments.add(Arguments.of(alias, "2020", 1));
    }

    for (var alias : new String[] {"edition", "AUSGABE", "ausgabe"}) {
      arguments.add(Arguments.of(alias, "ausgabeTest", 1));
    }

    for (var alias : new String[] {"volumes", "TEILBAND", "teilband"}) {
      arguments.add(Arguments.of(alias, "teilbandTest", 1));
    }

    for (var alias : new String[] {"publisher_information", "VERLAG", "verlag"}) {
      arguments.add(Arguments.of(alias, "verlagTest", 1));
    }

    for (var alias : new String[] {"university_note", "HSSVMK", "hssvmk"}) {
      arguments.add(Arguments.of(alias, "hochschulvermerkTest", 1));
    }

    for (var alias : new String[] {"conference_note", "KGRVMK", "kgrvmk"}) {
      arguments.add(Arguments.of(alias, "Kongress", 1));
    }

    for (var alias : new String[] {"language", "SPRACHE", "sprache"}) {
      arguments.add(Arguments.of(alias, "eng", 1));
    }

    for (var alias : new String[] {"titel_aspekte", "TITEL", "titel"}) {
      arguments.add(Arguments.of(alias, "titelAspektTest", 1));
    }

    for (var alias : new String[] {"bibliothekarische_verweisungen", "ZITFORMAL", "zitformal"}) {
      arguments.add(Arguments.of(alias, "zitformalTest", 1));
    }

    for (var alias : new String[] {"vorauflagen", "VORAUFL", "voraufl"}) {
      arguments.add(Arguments.of(alias, "vorauflageTest", 1));
    }

    for (var alias : new String[] {"kollationsvermerk", "KOLLVMK", "kollvmk"}) {
      arguments.add(Arguments.of(alias, "kollationsvermerkTest", 1));
    }

    for (var alias : new String[] {"schlagworte", "SLW", "slw"}) {
      arguments.add(Arguments.of(alias, "schlagwortTest", 1));
    }

    for (var alias : new String[] {"sachgebiete", "SG", "sg"}) {
      arguments.add(Arguments.of(alias, "sachgebietTest", 1));
    }

    for (var alias : new String[] {"regionen", "REGION", "region"}) {
      arguments.add(Arguments.of(alias, "regionTest", 1));
    }

    for (var alias : new String[] {"berufsbilder", "BERUFSBILD", "berufsbild"}) {
      arguments.add(Arguments.of(alias, "berufsbildTest", 1));
    }

    for (var alias : new String[] {"definitionen", "DEFINITION", "definition"}) {
      arguments.add(Arguments.of(alias, "definitionTest", 1));
    }

    for (var alias : new String[] {"kuendigungsgruende", "KGRUND", "kgrund"}) {
      arguments.add(Arguments.of(alias, "kuendigungsgrundTest", 1));
    }

    for (var alias : new String[] {"tarifvertraege", "TV", "tv"}) {
      arguments.add(Arguments.of(alias, "tarifvertragTest", 1));
    }

    for (var alias : new String[] {"ueber_personen_institutionen", "UEBER", "ueber"}) {
      arguments.add(Arguments.of(alias, "ueberPersonTest", 1));
    }

    for (var alias : new String[] {"ueber_kongresse", "KONGR", "kongr"}) {
      arguments.add(Arguments.of(alias, "ueberKongressTest", 1));
    }

    for (var alias : new String[] {"fuer_personen_institutionen", "FUERPERSINST", "fuerpersinst"}) {
      arguments.add(Arguments.of(alias, "fuerPersonTest", 1));
    }

    for (var alias : new String[] {"abweichende_dokumentnummern", "ABWNR", "abwnr"}) {
      arguments.add(Arguments.of(alias, "abweichendeNummerTest", 1));
    }

    for (var alias : new String[] {"dokumentart", "DART", "dart"}) {
      arguments.add(Arguments.of(alias, "dokumentartTest", 1));
    }

    for (var alias : new String[] {"aktivzitierungen_rechtsprechung", "ZITRSPR", "zitrspr"}) {
      arguments.add(Arguments.of(alias, "zitierungRechtsprechungTest", 1));
    }

    for (var alias :
        new String[] {"aktivzitierungen_literatur_selbstaendig", "ZITSLIT", "zitslit"}) {
      arguments.add(Arguments.of(alias, "zitierungSelbstaendigTest", 1));
    }

    for (var alias :
        new String[] {"aktivzitierungen_literatur_unselbstaendig", "ZITULIT", "zitulit"}) {
      arguments.add(Arguments.of(alias, "zitierungUnselbstaendigTest", 1));
    }

    for (var alias :
        new String[] {"aktivzitierungen_verwaltungsvorschriften", "ZITVWV", "zitvwv"}) {
      arguments.add(Arguments.of(alias, "zitierungVerwaltungTest", 1));
    }

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
