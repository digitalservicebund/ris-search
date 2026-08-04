package de.bund.digitalservice.ris.search.unit.mapper;

import static de.bund.digitalservice.ris.SharedTestConstants.DATE_2023_01_02;
import static de.bund.digitalservice.ris.SharedTestConstants.DATE_2024_01_01;
import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.SharedTestConstants;
import de.bund.digitalservice.ris.search.mapper.CaseLawSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.RechtsprechungSchemaMapper;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.schema.CaseLawEncodingSchema;
import de.bund.digitalservice.ris.search.schema.CaseLawSchema;
import de.bund.digitalservice.ris.search.schema.RechtsprechungSchema;
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
            .celex("62013CA0192")
            .courtType("KG")
            .location("Berlin")
            .documentType("Urteil")
            .decisionDate(SharedTestConstants.DATE_2024_01_02)
            .fileNumbers(List.of("FileNumberTest"))
            .dissentingOpinion("eine abweichende Meinung")
            .decisionGrounds("diese Entscheidungsgründe")
            .headnote("Orientierungssatz")
            .headline("Test")
            .titleLine("Title line")
            .otherHeadnote("Sonstiger Orientierungssatz")
            .otherLongText("Long text")
            .caseFacts("Tatbestand")
            .outline("outlineTest")
            .judicialBody("judicial body")
            .courtKeyword("KG Berlin")
            .keywords(List.of("one", "two"))
            .decisionName(List.of("decisionName"))
            .deviatingDocumentNumber(List.of("deviatingDocumentNumber"))
            .grounds("grounds")
            .guidingPrinciple("guidingPrinciple")
            .tenor("tenor")
            .abweichendeDaten(List.of(DATE_2023_01_02, DATE_2024_01_01))
            .berufsbilder(List.of("jobProfile test 1", "jobProfile test 2"))
            .build();

    CaseLawSchema caseLawSchema = CaseLawSchemaMapper.fromDomain(documentationUnit);

    assertThat(caseLawSchema.id()).isEqualTo("/v1/case-law/BFRE000087655");
    assertThat(caseLawSchema.documentNumber()).isEqualTo("BFRE000087655");
    assertThat(caseLawSchema.ecli()).isEqualTo("ECLI:DE:FGNI:1975:0526.IXL180.73.0A");
    assertThat(caseLawSchema.courtType()).isEqualTo("KG");
    assertThat(caseLawSchema.location()).isEqualTo("Berlin");
    assertThat(caseLawSchema.documentType()).isEqualTo("Urteil");
    assertThat(caseLawSchema.decisionDate()).isEqualTo(SharedTestConstants.DATE_2024_01_02);
    assertThat(caseLawSchema.fileNumbers()).containsExactly("FileNumberTest");
    assertThat(caseLawSchema.dissentingOpinion()).isEqualTo("eine abweichende Meinung");
    assertThat(caseLawSchema.decisionGrounds()).isEqualTo("diese Entscheidungsgründe");
    assertThat(caseLawSchema.headnote()).isEqualTo("Orientierungssatz");
    assertThat(caseLawSchema.headline()).isEqualTo("Test");
    assertThat(caseLawSchema.titleLine()).isEqualTo("Title line");
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

    RechtsprechungSchema rechtsprechungSchema =
        RechtsprechungSchemaMapper.fromDomain(documentationUnit);

    assertThat(rechtsprechungSchema.id()).isEqualTo("/v1/rechtsprechung/BFRE000087655");
    assertThat(rechtsprechungSchema.dokumentNummer()).isEqualTo("BFRE000087655");
    assertThat(rechtsprechungSchema.ecli()).isEqualTo("ECLI:DE:FGNI:1975:0526.IXL180.73.0A");
    assertThat(rechtsprechungSchema.celex()).isEqualTo("62013CA0192");
    assertThat(rechtsprechungSchema.gericht()).isEqualTo("KG Berlin");
    assertThat(rechtsprechungSchema.dokumenttyp()).isEqualTo("Urteil");
    assertThat(rechtsprechungSchema.datum()).isEqualTo(SharedTestConstants.DATE_2024_01_02);
    assertThat(rechtsprechungSchema.aktenzeichenListe()).containsExactly("FileNumberTest");
    assertThat(rechtsprechungSchema.abweichendeMeinung()).isEqualTo("eine abweichende Meinung");
    assertThat(rechtsprechungSchema.entscheidungsgruende()).isEqualTo("diese Entscheidungsgründe");
    assertThat(rechtsprechungSchema.orientierungssatz()).isEqualTo("Orientierungssatz");
    assertThat(rechtsprechungSchema.headline()).isEqualTo("Test");
    assertThat(rechtsprechungSchema.titelzeile()).isEqualTo("Title line");
    assertThat(rechtsprechungSchema.sonstigerOrientierungssatz())
        .isEqualTo("Sonstiger Orientierungssatz");
    assertThat(rechtsprechungSchema.sonstigerLangtext()).isEqualTo("Long text");
    assertThat(rechtsprechungSchema.tatbestand()).isEqualTo("Tatbestand");
    assertThat(rechtsprechungSchema.gliederung()).isEqualTo("outlineTest");
    assertThat(rechtsprechungSchema.spruchkoerper()).isEqualTo("judicial body");
    assertThat(rechtsprechungSchema.courtName()).isEqualTo("KG Berlin");
    assertThat(rechtsprechungSchema.schlagwoerter()).containsExactly("one", "two");
    assertThat(rechtsprechungSchema.entscheidungsnamen()).containsExactly("decisionName");
    assertThat(rechtsprechungSchema.abweichendeDokumentnummern())
        .containsExactly("deviatingDocumentNumber");
    assertThat(rechtsprechungSchema.gruende()).isEqualTo("grounds");
    assertThat(rechtsprechungSchema.leitsatz()).isEqualTo("guidingPrinciple");
    assertThat(rechtsprechungSchema.tenor()).isEqualTo("tenor");
    assertThat(rechtsprechungSchema.inLanguage()).isEqualTo("de");
    assertThat(rechtsprechungSchema.abweichendeDaten())
        .containsExactly(DATE_2023_01_02, DATE_2024_01_01);
    assertThat(rechtsprechungSchema.berufsbilder())
        .containsExactly("jobProfile test 1", "jobProfile test 2");
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
