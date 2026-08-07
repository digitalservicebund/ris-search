package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.SharedTestConstants;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.PublicationStatus;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.utils.CaseLawLdmlTemplateUtils;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class RechtsprechungControllerTest extends ContainersIntegrationBase {

  @Autowired private IndexCaselawService indexCaselawService;
  @Autowired private MockMvc mockMvc;
  @Autowired private CaseLawBucket bucket;
  private final CaseLawLdmlTemplateUtils caseLawLdmlTemplateUtils = new CaseLawLdmlTemplateUtils();
  private final String documentNumber = "BFRE000107055";

  private String createTestCaseLawLdml() throws IOException {
    LocalDate decisionDate = LocalDate.of(2023, Month.JANUARY, 2);

    Map<String, Object> context = new HashMap<>();
    context.put("documentNumber", this.documentNumber);
    context.put("courtType", "FG");
    context.put("location", "Berlin");
    context.put("documentType", "Urteil");
    context.put("decisionDate", decisionDate.toString());
    context.put("leitsatz", "Das ist der Leitsatz");
    context.put("otherLongText", "Sonstiger Langtext");
    context.put("otherHeadnote", "Sonstiger Orientierungssatz");
    context.put("publicationStatus", PublicationStatus.UNPUBLISHED.toString());
    context.put(
        "background",
        """
                    <akn:hcontainer ris:domainTerm="Randnummer" eId="randnummer-1" name="Randnummer">
                        <akn:num>1</akn:num>
                        <akn:content>
                            <akn:p>Example Tatbestand/CaseFacts. More background</akn:p>
                        </akn:content>
                    </akn:hcontainer>
                    <akn:p style="text-align:center">
                        <akn:img src="Attachment.png" alt="Abbildung"/>
                    </akn:p>
        """);

    return caseLawLdmlTemplateUtils.getXmlFromTemplate(context);
  }

  @BeforeEach
  void setUpSearchControllerApiTest() throws IOException {
    clearRepositoryData();
    String testCaseLawLdml = createTestCaseLawLdml();
    caseLawBucket.save(this.documentNumber + "/" + this.documentNumber + ".xml", testCaseLawLdml);
    caseLawBucket.save(this.documentNumber + "/Attachment.png", "picture");
  }

  @Test
  @DisplayName("Should return rechtsprechung when using api endpoint with document number")
  void shouldReturnSingleCaselawJson() throws Exception {
    indexCaselawService.reindexAll(SharedTestConstants.TIMESTAMP_2024_01_01_AS_STRING);

    mockMvc
        .perform(
            get(ApiConfig.Paths.RECHTSPRECHUNG + "/" + this.documentNumber)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.aktenzeichen", Matchers.is("fileNumber test")),
            jsonPath("$.dokumentNummer", Matchers.is(this.documentNumber)),
            jsonPath("$.kurztitel", Matchers.is("the title")),
            jsonPath("$.celex", Matchers.is("testCelex")),
            jsonPath("$.gericht", Matchers.is("Test court label")),
            jsonPath("$.gerichtsbarkeit", Matchers.is("Test jurisdiction type")),
            jsonPath("$.dokumenttyp", Matchers.is("Urteil")),
            jsonPath("$.datum", Matchers.is("2023-01-02")),
            jsonPath("$.leitsatz", Matchers.is("Das ist der Leitsatz")),
            jsonPath("$.sonstigerLangtext", Matchers.is("Sonstiger Langtext")),
            jsonPath("$.rechtsfrageGesamt", Matchers.is("Rechtsfrage (gesamt)")),
            jsonPath("$.rechtsfrage", Matchers.is("Rechtsfrage")),
            jsonPath("$.sonstigerOrientierungssatz", Matchers.is("Sonstiger Orientierungssatz")),
            jsonPath("$.tatbestand", Matchers.is("Example Tatbestand/CaseFacts. More background")),
            jsonPath("$.publicationStatus").doesNotExist(),
            jsonPath("$.error").doesNotExist(),
            jsonPath("$.documentationOffice").doesNotExist(),
            jsonPath("$.abweichendeDaten[0]").value("2020-03-03"),
            jsonPath("$.abweichendeDaten[1]").value("2022-02-04"),
            jsonPath("$.abweichendeDokumentnummern[0]").value("ABC"),
            jsonPath("$.abweichendeDokumentnummern[1]").value("DEF"),
            jsonPath("$.abweichendeEclis[0]").value("ECLI 1"),
            jsonPath("$.abweichendeEclis[1]").value("ECLI 2"),
            jsonPath("$.berufsbilder[0]").value("jobProfile test 1"),
            jsonPath("$.berufsbilder[1]").value("jobProfile test 2"),
            jsonPath("$.kuendigungsarten[0]").value("dismissalType test"),
            jsonPath("$.fehlerhafteGerichte[0]").value("deviating court 1"),
            jsonPath("$.fehlerhafteGerichte[1]").value("deviating court 2"),
            jsonPath("$.datenDerMuendlichenVerhandlung[0]").value("2021-02-03"),
            jsonPath("$.definitionen[0]").value("indirekte Steuern"),
            jsonPath("$.definitionen[1]").value("Sachgesamtheit"),
            jsonPath("$.erledigung").value("Ja"),
            jsonPath("$.gesetzgebungsauftrag").value("Ja"),
            jsonPath("$.langtextdatum").value("2016-06-15"),
            jsonPath("$.letzteVeroeffentlichung").value("2026-03-20"),
            jsonPath("$.erledigungsvermerk").value("Erledigungsvermerk"),
            jsonPath("$.erstveroeffentlichung").value("2026-03-18"),
            jsonPath("$.abweichendeMeinung")
                .value(
                    "dissenting test, Dr. Phil. Max Mustermann: referenced opinions test 1, Maxima Mustermann: referenced opinions test 2"),
            jsonPath(
                "$.encoding[*]['@id']",
                containsInAnyOrder(
                    "/v1/rechtsprechung/" + this.documentNumber + "/html",
                    "/v1/rechtsprechung/" + this.documentNumber + "/xml",
                    "/v1/rechtsprechung/" + this.documentNumber + "/zip")),
            jsonPath(
                "$.encoding[*].contentUrl",
                containsInAnyOrder(
                    "/v1/rechtsprechung/" + this.documentNumber + ".html",
                    "/v1/rechtsprechung/" + this.documentNumber + ".xml",
                    "/v1/rechtsprechung/" + this.documentNumber + ".zip")));
  }

  @Test
  @DisplayName("Should set vorabdokument to true if vorabdokument")
  void responseContainsVorabdokumentValue() throws Exception {
    CaseLawDocumentationUnit docUnit =
        CaseLawDocumentationUnit.builder()
            .documentNumber("FOOB000000001")
            .vorabdokument(true)
            .build();

    this.caseLawRepository.save(docUnit);

    mockMvc
        .perform(
            get(ApiConfig.Paths.CASELAW + "/FOOB000000001").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.vorabdokument", equalTo(true)));
  }

  @Test
  @DisplayName("Should return not found when using document number is not found")
  void shouldReturnNotFound() throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.RECHTSPRECHUNG + "/TEST").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}
