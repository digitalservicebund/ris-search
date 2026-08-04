package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.mapper.CaseLawLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.utils.CaseLawLdmlTemplateUtils;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CaseLawLdmlToOpenSearchMapperTest {

  private String testCaseLawLdml;
  private final CaseLawLdmlTemplateUtils caseLawLdmlTemplateUtils = new CaseLawLdmlTemplateUtils();
  private final CaseLawLdmlToOpenSearchMapper mapper = new CaseLawLdmlToOpenSearchMapper();

  @BeforeEach
  void beforeEach() throws IOException {
    testCaseLawLdml = caseLawLdmlTemplateUtils.getXmlFromTemplate(null);
  }

  @Test
  void shouldMapCoreCaseLawFieldsCorrectly() {
    CaseLawDocumentationUnit caseLaw = mapper.fromString(testCaseLawLdml);

    assertThat(caseLaw.id()).isEqualTo("testDocNumber");
    assertThat(caseLaw.caseFacts())
        .isEqualTo("Example Tatbestand/CaseFacts. More background even more background");
    assertThat(caseLaw.decisionGrounds()).isEqualTo("Example Entscheidungsgründe/DecisionGrounds");
    assertThat(caseLaw.documentNumber()).isEqualTo("testDocNumber");
    assertThat(caseLaw.ecli()).isEqualTo("testEcli");
    assertThat(caseLaw.celex()).isEqualTo("testCelex");
    assertThat(caseLaw.guidingPrinciple()).isEqualTo("Example Leitsatz/GuidingPrinciple");
    assertThat(caseLaw.headline()).isEqualTo("the title");
    assertThat(caseLaw.titleLine()).isEqualTo("Title");
    assertThat(caseLaw.decisionDate()).isEqualTo(LocalDate.of(2020, Month.JANUARY, 1));
    assertThat(caseLaw.tenor()).isEqualTo("Example Tenor/Tenor");
    assertThat(caseLaw.fileNumber()).isEqualTo("fileNumber test");
    assertThat(caseLaw.courtType()).isEqualTo("Test court type");
    assertThat(caseLaw.location()).isEqualTo("Test court location");
    assertThat(caseLaw.courtKeyword()).isEqualTo("Test court label");
    assertThat(caseLaw.documentType()).isEqualTo("Urteil");
    assertThat(caseLaw.outline()).isEqualTo("Example Gliederung/Outline");
    assertThat(caseLaw.judicialBody()).isEqualTo("Test judicial body");
    assertThat(caseLaw.dissentingOpinion())
        .isEqualTo(
            "dissenting test, Dr. Phil. Max Mustermann: referenced opinions test 1, Maxima Mustermann: referenced opinions test 2");
  }

  @Test
  void shouldMapCaseLawCollectionsCorrectly() {
    CaseLawDocumentationUnit caseLaw = mapper.fromString(testCaseLawLdml);

    assertThat(caseLaw.fileNumbers()).hasToString("[Test file number 1, Test file number 2]");
    assertThat(caseLaw.keywords()).hasToString("[keyword1, keyword2]");
    assertThat(caseLaw.decisionName()).hasToString("[Test decision name]");
    assertThat(caseLaw.deviatingDocumentNumber()).hasToString("[ABC, DEF]");
    assertThat(caseLaw.previousDecisions())
        .containsExactlyInAnyOrder(
            "previous decision file number, previous decision court type",
            "previous decision file number, previous decision court type");
    assertThat(caseLaw.ensuingDecisions())
        .containsExactlyInAnyOrder(
            "ensuing decision file number, ensuing decision court type",
            "ensuing decision file number, ensuing decision court type");
    assertThat(caseLaw.abweichendeDaten()).hasToString("[2020-03-03, 2022-02-04]");
    assertThat(caseLaw.abweichendeEclis()).hasToString("[ECLI 1, ECLI 2]");
    assertThat(caseLaw.berufsbilder()).hasToString("[jobProfile test 1, jobProfile test 2]");
    assertThat(caseLaw.vorabdokument()).isFalse();
  }

  @Test
  void setsVorabdokumentToTrueIfDocumentIsMarkedIncomplete() throws IOException {
    String caselawXml = caseLawLdmlTemplateUtils.getXmlFromTemplate(Map.of("vorabdokument", true));

    CaseLawDocumentationUnit caseLaw = mapper.fromString(caselawXml);
    assertThat(caseLaw.vorabdokument()).isTrue();
  }
}
