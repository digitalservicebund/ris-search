package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.integration.controller.api.testData.SharedTestConstants;
import de.bund.digitalservice.ris.search.mapper.CaseLawSchemaMapper;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.schema.CaseLawEncodingSchema;
import de.bund.digitalservice.ris.search.schema.CaseLawSchema;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CaseLawSchemaMapperTest {

  @Test
  @DisplayName("Correctly maps attributes")
  void fromDomainSingle() {
    var documentationUnit =
        CaseLawDocumentationUnit.builder()
            .id("id1")
            .documentNumber("BFRE000087655")
            .ecli("ECLI:DE:FGNI:1975:0526.IXL180.73.0A")
            .courtType("KG")
            .location("Berlin")
            .documentType("Urteil")
            .decisionDate(SharedTestConstants.DATE_2_2)
            .fileNumbers(List.of("FileNumberTest"))
            .dissentingOpinion("eine abweichende Meinung")
            .decisionGrounds("diese Entscheidungsgründe")
            .headnote("Orientierungssatz")
            .headline("Test")
            .otherHeadnote("Sonstiger Orientierungssatz")
            .otherLongText("Long text")
            .caseFacts("Tatbestand")
            .outline("outlineTest")
            .judicialBody("judicial body")
            .courtKeyword("KG Berlin")
            .keywords(List.of("one", "two"))
            .decisionName(List.of("decisionName"))
            .deviatingDocumentNumber(List.of("deviatingDocumentNumber"))
            .error(false)
            .grounds("grounds")
            .guidingPrinciple("guidingPrinciple")
            .tenor("tenor")
            .build();

    CaseLawSchema caseLawSchema = CaseLawSchemaMapper.fromDomain(documentationUnit);

    assertThat(caseLawSchema.id()).isEqualTo("/v1/case-law/BFRE000087655");
    assertThat(caseLawSchema.documentNumber()).isEqualTo("BFRE000087655");
    assertThat(caseLawSchema.ecli()).isEqualTo("ECLI:DE:FGNI:1975:0526.IXL180.73.0A");
    assertThat(caseLawSchema.courtType()).isEqualTo("KG");
    assertThat(caseLawSchema.location()).isEqualTo("Berlin");
    assertThat(caseLawSchema.documentType()).isEqualTo("Urteil");
    assertThat(caseLawSchema.decisionDate()).isEqualTo(SharedTestConstants.DATE_2_2);
    assertThat(caseLawSchema.fileNumbers()).containsExactly("FileNumberTest");
    assertThat(caseLawSchema.dissentingOpinion()).isEqualTo("eine abweichende Meinung");
    assertThat(caseLawSchema.decisionGrounds()).isEqualTo("diese Entscheidungsgründe");
    assertThat(caseLawSchema.headnote()).isEqualTo("Orientierungssatz");
    assertThat(caseLawSchema.headline()).isEqualTo("Test");
    assertThat(caseLawSchema.otherHeadnote()).isEqualTo("Sonstiger Orientierungssatz");
    assertThat(caseLawSchema.otherLongText()).isEqualTo("Long text");
    assertThat(caseLawSchema.caseFacts()).isEqualTo("Tatbestand");
    assertThat(caseLawSchema.outline()).isEqualTo("outlineTest");
    assertThat(caseLawSchema.judicialBody()).isEqualTo("judicial body");
    assertThat(caseLawSchema.courtName()).isEqualTo("KG Berlin");
    assertThat(caseLawSchema.keywords()).containsExactly("one", "two");
    assertThat(caseLawSchema.decisionName()).containsExactly("decisionName");
    assertThat(caseLawSchema.deviatingDocumentNumber()).containsExactly("deviatingDocumentNumber");
    assertThat(caseLawSchema.grounds()).isEqualTo("grounds");
    assertThat(caseLawSchema.guidingPrinciple()).isEqualTo("guidingPrinciple");
    assertThat(caseLawSchema.tenor()).isEqualTo("tenor");
    assertThat(caseLawSchema.inLanguage()).isEqualTo("de");
  }

  @Test
  @DisplayName("Creates encodings for html, xml and zip")
  void createsEncodings() {
    var documentationUnit =
        CaseLawDocumentationUnit.builder().documentNumber("BFRE000087655").build();

    CaseLawSchema caseLawSchema = CaseLawSchemaMapper.fromDomain(documentationUnit);

    assertThat(caseLawSchema.encoding())
        .containsExactly(
            CaseLawEncodingSchema.builder()
                .id("/v1/case-law/BFRE000087655/html")
                .contentUrl("/v1/case-law/BFRE000087655.html")
                .encodingFormat("text/html")
                .inLanguage("de")
                .build(),
            CaseLawEncodingSchema.builder()
                .id("/v1/case-law/BFRE000087655/xml")
                .contentUrl("/v1/case-law/BFRE000087655.xml")
                .encodingFormat("application/xml")
                .inLanguage("de")
                .build(),
            CaseLawEncodingSchema.builder()
                .id("/v1/case-law/BFRE000087655/zip")
                .contentUrl("/v1/case-law/BFRE000087655.zip")
                .encodingFormat("application/zip")
                .inLanguage("de")
                .build());
  }
}
