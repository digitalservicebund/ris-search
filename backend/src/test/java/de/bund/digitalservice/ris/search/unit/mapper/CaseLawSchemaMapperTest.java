package de.bund.digitalservice.ris.search.unit.mapper;

import static de.bund.digitalservice.ris.SharedTestConstants.DATE_2023_01_02;
import static de.bund.digitalservice.ris.SharedTestConstants.DATE_2024_01_01;
import static de.bund.digitalservice.ris.SharedTestConstants.DATE_2024_01_03;
import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.SharedTestConstants;
import de.bund.digitalservice.ris.search.mapper.CaseLawSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.RechtsprechungSchemaMapper;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.schema.CaseLawSchema;
import de.bund.digitalservice.ris.search.schema.DocumentEncodingSchema;
import de.bund.digitalservice.ris.search.schema.RechtsprechungSchema;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CaseLawSchemaMapperTest {

  private CaseLawDocumentationUnit buildDocumentationUnit() {
    return CaseLawDocumentationUnit.builder()
        .id("id1")
        .documentNumber("BFRE000087655")
        .ecli("ECLI:DE:FGNI:1975:0526.IXL180.73.0A")
        .celex("62013CA0192")
        .courtType("KG")
        .location("Berlin")
        .gerichtsbarkeit("Ordentliche Gerichtsbarkeit")
        .documentType("Urteil")
        .decisionDate(SharedTestConstants.DATE_2024_01_02)
        .fileNumbers(List.of("FileNumberTest"))
        .abweichendeAktenzeichen(List.of("1 BvR 839, 899/96"))
        .dissentingOpinion("eine abweichende Meinung")
        .decisionGrounds("diese Entscheidungsgründe")
        .headnote("Orientierungssatz")
        .headline("Test")
        .titleLine("Title line")
        .otherHeadnote("Sonstiger Orientierungssatz")
        .otherLongText("Long text")
        .rechtsfrageGesamt("Rechtsfrage gesamt text")
        .rechtsfrage("Rechtsfrage text")
        .legalEffect("Ja")
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
        .kuendigungsarten(List.of("dismissalType test"))
        .herkunftslaender(List.of("Frankreich", "Deutschland"))
        .regionen(List.of("NW"))
        .tarifvertraege(List.of("Stehende Bühnen"))
        .kuendigungsgruende(List.of("Straftat"))
        .mitwirkendeRichter(List.of("Meier", "Müller"))
        .previousDecisions(List.of("previous decision file number, previous decision court type"))
        .ensuingDecisions(List.of("ensuing decision file number, ensuing decision court type"))
        .aktivzitierungLiteraturUnselbstaendig(List.of("STLU991393280"))
        .passivzitierungLiteraturUnselbstaendig(List.of("SBLU000539216"))
        .aktivzitierungLiteraturSelbstaendig(List.of("KSLS071671727"))
        .passivzitierungLiteraturSelbstaendig(List.of("KSLS071671728"))
        .aktivzitierungRechtsprechung(List.of("BVRE100338409"))
        .passivzitierungRechtsprechung(List.of("JURE100074208"))
        .aktivzitierungVerwaltungsvorschriften(List.of("KSNR008561615"))
        .passivzitierungVerwaltungsvorschriften(List.of("KSNR006800006"))
        .amtlicheFundstellen(List.of("BGHSt 67, 273-284"))
        .nichtamtlicheFundstellen(List.of("DStR 2023, 1430-1435"))
        .gesetzeskraft(List.of("vereinbar mit höherrangigem Recht, Bremen"))
        .normenkette(List.of("BGB § 823"))
        .sachgebiete(List.of("fieldOfLaw test"))
        .streitjahre(List.of("2024"))
        .fehlerhafteGerichte(List.of("deviating court 1", "deviating court 2"))
        .datenDerMuendlichenVerhandlung(List.of(DATE_2024_01_03))
        .definitionen(List.of("indirekte Steuern", "Sachgesamtheit"))
        .erledigung("Ja")
        .hasLegislativeMandate("Ja")
        .langtextdatum(LocalDate.of(2016, Month.JUNE, 15))
        .rechtsmittelfuehrer("Rechtsmittelführer")
        .rechtsmittelzulassung("Rechtsmittelzulassung")
        .revision("Ja")
        .letzteVeroeffentlichung(LocalDate.of(2026, Month.MARCH, 20))
        .erledigungsvermerk("Erledigungsvermerk")
        .erstveroeffentlichung(LocalDate.of(2026, Month.MARCH, 18))
        .mitteilungsdatum(LocalDate.of(2020, Month.JANUARY, 1))
        .build();
  }

  @Test
  @DisplayName("Correctly maps scalar CaseLawSchema attributes")
  void fromDomainSingleCaseLawSchemaScalarAttributes() {
    CaseLawSchema caseLawSchema =
        CaseLawSchemaMapper.fromDomain(buildDocumentationUnit(), "jsonLdContext");

    assertThat(caseLawSchema.id()).isEqualTo("/v1/case-law/BFRE000087655");
    assertThat(caseLawSchema.documentNumber()).isEqualTo("BFRE000087655");
    assertThat(caseLawSchema.ecli()).isEqualTo("ECLI:DE:FGNI:1975:0526.IXL180.73.0A");
    assertThat(caseLawSchema.courtType()).isEqualTo("KG");
    assertThat(caseLawSchema.location()).isEqualTo("Berlin");
    assertThat(caseLawSchema.documentType()).isEqualTo("Urteil");
    assertThat(caseLawSchema.decisionDate()).isEqualTo(SharedTestConstants.DATE_2024_01_02);
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
    assertThat(caseLawSchema.grounds()).isEqualTo("grounds");
    assertThat(caseLawSchema.guidingPrinciple()).isEqualTo("guidingPrinciple");
    assertThat(caseLawSchema.tenor()).isEqualTo("tenor");
    assertThat(caseLawSchema.inLanguage()).isEqualTo("de");
  }

  @Test
  @DisplayName("Correctly maps collection CaseLawSchema attributes")
  void fromDomainSingleCaseLawSchemaCollectionAttributes() {
    CaseLawSchema caseLawSchema =
        CaseLawSchemaMapper.fromDomain(buildDocumentationUnit(), "jsonLdContext");

    assertThat(caseLawSchema.fileNumbers()).containsExactly("FileNumberTest");
    assertThat(caseLawSchema.keywords()).containsExactly("one", "two");
    assertThat(caseLawSchema.decisionName()).containsExactly("decisionName");
    assertThat(caseLawSchema.deviatingDocumentNumber()).containsExactly("deviatingDocumentNumber");
    assertThat(caseLawSchema.previousDecisions())
        .containsExactly("previous decision file number, previous decision court type");
    assertThat(caseLawSchema.ensuingDecisions())
        .containsExactly("ensuing decision file number, ensuing decision court type");
    assertThat(caseLawSchema.gesetzeskraft())
        .containsExactly("vereinbar mit höherrangigem Recht, Bremen");
    assertThat(caseLawSchema.streitjahre()).containsExactly("2024");
  }

  @Test
  @DisplayName("Correctly maps scalar RechtsprechungSchema attributes")
  void fromDomainSingleRechtsprechungSchemaScalarAttributes() {
    RechtsprechungSchema rechtsprechungSchema =
        RechtsprechungSchemaMapper.fromDomain(buildDocumentationUnit());

    assertThat(rechtsprechungSchema.id()).isEqualTo("/v1/rechtsprechung/BFRE000087655");
    assertThat(rechtsprechungSchema.dokumentNummer()).isEqualTo("BFRE000087655");
    assertThat(rechtsprechungSchema.ecli()).isEqualTo("ECLI:DE:FGNI:1975:0526.IXL180.73.0A");
    assertThat(rechtsprechungSchema.gericht()).isEqualTo("KG Berlin");
    assertThat(rechtsprechungSchema.dokumenttyp()).isEqualTo("Urteil");
    assertThat(rechtsprechungSchema.datum()).isEqualTo(SharedTestConstants.DATE_2024_01_02);
    assertThat(rechtsprechungSchema.abweichendeMeinung()).isEqualTo("eine abweichende Meinung");
    assertThat(rechtsprechungSchema.entscheidungsgruende()).isEqualTo("diese Entscheidungsgründe");
    assertThat(rechtsprechungSchema.orientierungssatz()).isEqualTo("Orientierungssatz");
    assertThat(rechtsprechungSchema.kurztitel()).isEqualTo("Test");
    assertThat(rechtsprechungSchema.titelzeile()).isEqualTo("Title line");
    assertThat(rechtsprechungSchema.sonstigerOrientierungssatz())
        .isEqualTo("Sonstiger Orientierungssatz");
    assertThat(rechtsprechungSchema.sonstigerLangtext()).isEqualTo("Long text");
    assertThat(rechtsprechungSchema.tatbestand()).isEqualTo("Tatbestand");
    assertThat(rechtsprechungSchema.gliederung()).isEqualTo("outlineTest");
    assertThat(rechtsprechungSchema.spruchkoerper()).isEqualTo("judicial body");
    assertThat(rechtsprechungSchema.courtName()).isEqualTo("KG Berlin");
    assertThat(rechtsprechungSchema.gruende()).isEqualTo("grounds");
    assertThat(rechtsprechungSchema.leitsatz()).isEqualTo("guidingPrinciple");
    assertThat(rechtsprechungSchema.tenor()).isEqualTo("tenor");
    assertThat(rechtsprechungSchema.inLanguage()).isEqualTo("de");
  }

  @Test
  @DisplayName("Correctly maps additional scalar RechtsprechungSchema attributes")
  void fromDomainSingleRechtsprechungSchemaAdditionalScalarAttributes() {
    RechtsprechungSchema rechtsprechungSchema =
        RechtsprechungSchemaMapper.fromDomain(buildDocumentationUnit());

    assertThat(rechtsprechungSchema.celex()).isEqualTo("62013CA0192");
    assertThat(rechtsprechungSchema.gerichtsbarkeit()).isEqualTo("Ordentliche Gerichtsbarkeit");
    assertThat(rechtsprechungSchema.erledigung()).isEqualTo("Ja");
    assertThat(rechtsprechungSchema.erledigungsvermerk()).isEqualTo("Erledigungsvermerk");
    assertThat(rechtsprechungSchema.erstveroeffentlichung())
        .isEqualTo(LocalDate.of(2026, Month.MARCH, 18));
    assertThat(rechtsprechungSchema.mitteilungsdatum())
        .isEqualTo(LocalDate.of(2020, Month.JANUARY, 1));
    assertThat(rechtsprechungSchema.gesetzgebungsauftrag()).isEqualTo("Ja");
    assertThat(rechtsprechungSchema.langtextdatum()).isEqualTo(LocalDate.of(2016, Month.JUNE, 15));
    assertThat(rechtsprechungSchema.rechtsmittelfuehrer()).isEqualTo("Rechtsmittelführer");
    assertThat(rechtsprechungSchema.rechtsmittelzulassung()).isEqualTo("Rechtsmittelzulassung");
    assertThat(rechtsprechungSchema.revision()).isEqualTo("Ja");
    assertThat(rechtsprechungSchema.letzteVeroeffentlichung())
        .isEqualTo(LocalDate.of(2026, Month.MARCH, 20));
    assertThat(rechtsprechungSchema.rechtsfrageGesamt()).isEqualTo("Rechtsfrage gesamt text");
    assertThat(rechtsprechungSchema.rechtsfrage()).isEqualTo("Rechtsfrage text");
    assertThat(rechtsprechungSchema.rechtskraft()).isEqualTo("Ja");
  }

  @Test
  @DisplayName("Correctly maps collection RechtsprechungSchema attributes")
  void fromDomainSingleRechtsprechungSchemaCollectionAttributes() {
    RechtsprechungSchema rechtsprechungSchema =
        RechtsprechungSchemaMapper.fromDomain(buildDocumentationUnit());

    assertThat(rechtsprechungSchema.aktenzeichenListe()).containsExactly("FileNumberTest");
    assertThat(rechtsprechungSchema.abweichendeAktenzeichen()).containsExactly("1 BvR 839, 899/96");
    assertThat(rechtsprechungSchema.schlagwoerter()).containsExactly("one", "two");
    assertThat(rechtsprechungSchema.entscheidungsnamen()).containsExactly("decisionName");
    assertThat(rechtsprechungSchema.abweichendeDokumentnummern())
        .containsExactly("deviatingDocumentNumber");
    assertThat(rechtsprechungSchema.abweichendeDaten())
        .containsExactly(DATE_2023_01_02, DATE_2024_01_01);
    assertThat(rechtsprechungSchema.berufsbilder())
        .containsExactly("jobProfile test 1", "jobProfile test 2");
    assertThat(rechtsprechungSchema.kuendigungsarten()).containsExactly("dismissalType test");
    assertThat(rechtsprechungSchema.herkunftslaender())
        .containsExactly("Frankreich", "Deutschland");
    assertThat(rechtsprechungSchema.regionen()).containsExactly("NW");
    assertThat(rechtsprechungSchema.tarifvertraege()).containsExactly("Stehende Bühnen");
    assertThat(rechtsprechungSchema.kuendigungsgruende()).containsExactly("Straftat");
    assertThat(rechtsprechungSchema.mitwirkendeRichter()).containsExactly("Meier", "Müller");
    assertThat(rechtsprechungSchema.sachgebiete()).containsExactly("fieldOfLaw test");
    assertThat(rechtsprechungSchema.streitjahre()).containsExactly("2024");
    assertThat(rechtsprechungSchema.fehlerhafteGerichte())
        .containsExactly("deviating court 1", "deviating court 2");
    assertThat(rechtsprechungSchema.datenDerMuendlichenVerhandlung())
        .containsExactly(DATE_2024_01_03);
    assertThat(rechtsprechungSchema.definitionen())
        .containsExactly("indirekte Steuern", "Sachgesamtheit");
  }

  @Test
  @DisplayName("Correctly maps reference collection RechtsprechungSchema attributes")
  void fromDomainSingleRechtsprechungSchemaReferenceCollectionAttributes() {
    RechtsprechungSchema rechtsprechungSchema =
        RechtsprechungSchemaMapper.fromDomain(buildDocumentationUnit());

    assertThat(rechtsprechungSchema.vorgehendeEntscheidungen())
        .containsExactly("previous decision file number, previous decision court type");
    assertThat(rechtsprechungSchema.nachgehendeEntscheidungen())
        .containsExactly("ensuing decision file number, ensuing decision court type");
    assertThat(rechtsprechungSchema.aktivzitierungLiteraturUnselbstaendig())
        .containsExactly("STLU991393280");
    assertThat(rechtsprechungSchema.passivzitierungLiteraturUnselbstaendig())
        .containsExactly("SBLU000539216");
    assertThat(rechtsprechungSchema.aktivzitierungLiteraturSelbstaendig())
        .containsExactly("KSLS071671727");
    assertThat(rechtsprechungSchema.passivzitierungLiteraturSelbstaendig())
        .containsExactly("KSLS071671728");
    assertThat(rechtsprechungSchema.aktivzitierungRechtsprechung())
        .containsExactly("BVRE100338409");
    assertThat(rechtsprechungSchema.passivzitierungRechtsprechung())
        .containsExactly("JURE100074208");
    assertThat(rechtsprechungSchema.aktivzitierungVerwaltungsvorschriften())
        .containsExactly("KSNR008561615");
    assertThat(rechtsprechungSchema.passivzitierungVerwaltungsvorschriften())
        .containsExactly("KSNR006800006");
    assertThat(rechtsprechungSchema.amtlicheFundstellen()).containsExactly("BGHSt 67, 273-284");
    assertThat(rechtsprechungSchema.nichtamtlicheFundstellen())
        .containsExactly("DStR 2023, 1430-1435");
    assertThat(rechtsprechungSchema.gesetzeskraft())
        .containsExactly("vereinbar mit höherrangigem Recht, Bremen");
    assertThat(rechtsprechungSchema.normenkette()).containsExactly("BGB § 823");
  }

  @Test
  @DisplayName("Creates encodings for html, xml and zip")
  void createsEncodings() {
    var documentationUnit =
        CaseLawDocumentationUnit.builder().documentNumber("BFRE000087655").build();

    CaseLawSchema caseLawSchema =
        CaseLawSchemaMapper.fromDomain(documentationUnit, "jsonLdContext");

    assertThat(caseLawSchema.encoding())
        .containsExactly(
            DocumentEncodingSchema.builder()
                .id("/v1/case-law/BFRE000087655/html")
                .contentUrl("/v1/case-law/BFRE000087655.html")
                .encodingFormat("text/html")
                .inLanguage("de")
                .build(),
            DocumentEncodingSchema.builder()
                .id("/v1/case-law/BFRE000087655/xml")
                .contentUrl("/v1/case-law/BFRE000087655.xml")
                .encodingFormat("application/xml")
                .inLanguage("de")
                .build(),
            DocumentEncodingSchema.builder()
                .id("/v1/case-law/BFRE000087655/zip")
                .contentUrl("/v1/case-law/BFRE000087655.zip")
                .encodingFormat("application/zip")
                .inLanguage("de")
                .build());
  }
}
