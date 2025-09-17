package de.bund.digitalservice.ris.search.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.utils.CaseLawLdmlTemplateUtils;
import java.io.IOException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CaseLawLdmlToOpenSearchMapperTest {

  private String testCaseLawLdml;
  private final CaseLawLdmlTemplateUtils caseLawLdmlTemplateUtils = new CaseLawLdmlTemplateUtils();

  @BeforeEach
  void beforeEach() throws IOException {
    testCaseLawLdml = caseLawLdmlTemplateUtils.getXmlFromTemplate(null);
  }

  @Test
  void shouldMapToCaseLawDocumentationUnitCorrectly() {
    CaseLawDocumentationUnit caseLaw = CaseLawLdmlToOpenSearchMapper.fromString(testCaseLawLdml);

    assertThat(caseLaw.id()).isEqualTo("testDocNumber");
    assertThat(caseLaw.documentationOffice()).isEqualTo("documentationOffice");
    assertThat(caseLaw.caseFacts())
        .isEqualTo("Example Tatbestand/CaseFacts. More background even more background");
    assertThat(caseLaw.decisionGrounds()).isEqualTo("Example Entscheidungsgründe/DecisionGrounds");
    assertThat(caseLaw.documentNumber()).isEqualTo("testDocNumber");
    assertThat(caseLaw.ecli()).isEqualTo("testEcli");
    assertThat(caseLaw.guidingPrinciple()).isEqualTo("Example Leitsatz/GuidingPrinciple");
    assertThat(caseLaw.headline()).isEqualTo("Title");
    assertThat(caseLaw.decisionDate()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(caseLaw.tenor()).isEqualTo("Example Tenor/Tenor");
    assertThat(caseLaw.publicationStatus()).isEqualTo("PUBLISHED");
    assertThat(caseLaw.fileNumbers()).hasToString("[Test file number 1, Test file number 2]");
    assertThat(caseLaw.courtType()).isEqualTo("Test court type");
    assertThat(caseLaw.location()).isEqualTo("Test court location");
    assertThat(caseLaw.courtKeyword()).isEqualTo("Test court type Test court location");
    assertThat(caseLaw.documentType()).isEqualTo("Test document type");
    assertThat(caseLaw.outline()).isEqualTo("Example Gliederung/Outline");
    assertThat(caseLaw.judicialBody()).isEqualTo("Test judicial body");
    assertThat(caseLaw.keywords()).hasToString("[keyword1, keyword2]");
    assertThat(caseLaw.decisionName()).hasToString("[Test decision name]");
    assertThat(caseLaw.deviatingDocumentNumber()).hasToString("[Test deviatingDocumentNumber]");
  }
}
