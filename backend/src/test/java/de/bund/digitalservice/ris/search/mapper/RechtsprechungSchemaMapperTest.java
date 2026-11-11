package de.bund.digitalservice.ris.search.mapper;

import static de.bund.digitalservice.ris.SharedTestConstants.DATE_2023_01_02;
import static de.bund.digitalservice.ris.SharedTestConstants.DATE_2024_01_01;
import static de.bund.digitalservice.ris.SharedTestConstants.DATE_2024_01_03;
import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.SharedTestConstants;
import de.bund.digitalservice.ris.search.models.DatumsTyp;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawUnindexed;
import de.bund.digitalservice.ris.search.models.opensearch.Gericht;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Fundstelle;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Richtung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungLiteraturSelbststaendig;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungLiteraturUnselbststaendig;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungRechtsprechung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungVerwaltungsvorschrift;
import de.bund.digitalservice.ris.search.schema.DocumentEncodingSchema;
import de.bund.digitalservice.ris.search.schema.FundstelleSchema;
import de.bund.digitalservice.ris.search.schema.GerichtSchema;
import de.bund.digitalservice.ris.search.schema.RechtsprechungSchema;
import de.bund.digitalservice.ris.search.schema.ZitierungLiteraturSelbststaendigSchema;
import de.bund.digitalservice.ris.search.schema.ZitierungLiteraturUnselbststaendigSchema;
import de.bund.digitalservice.ris.search.schema.ZitierungRechtsprechungSchema;
import de.bund.digitalservice.ris.search.schema.ZitierungRichtung;
import de.bund.digitalservice.ris.search.schema.ZitierungVerwaltungsvorschriftSchema;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RechtsprechungSchemaMapperTest {

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
        .datumsTyp(DatumsTyp.MITTEILUNGSDATUM)
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
        .unindexed(
            CaseLawUnindexed.builder()
                .zitierungenLiteraturUnselbststaendig(
                    List.of(
                        ZitierungLiteraturUnselbststaendig.builder()
                            .richtung(Richtung.AKTIV)
                            .referenzUri("ris:/literatur/STLU991393280")
                            .dokumentnummer("STLU991393280")
                            .autor("Schmidt-Liebig")
                            .dokumenttyp("Aufsatz")
                            .fundstelle(
                                Fundstelle.builder()
                                    .periodikum("Information StW")
                                    .zitatstelle("1999, 641")
                                    .fundstellenTyp("nichtamtlich")
                                    .build())
                            .build(),
                        ZitierungLiteraturUnselbststaendig.builder()
                            .richtung(Richtung.PASSIV)
                            .dokumentnummer("SBLU000539216")
                            .build()))
                .zitierungenLiteraturSelbststaendig(
                    List.of(
                        ZitierungLiteraturSelbststaendig.builder()
                            .richtung(Richtung.AKTIV)
                            .referenzUri("ris:/literatur/KSLS071671727")
                            .dokumentnummer("KSLS071671727")
                            .autor("Arnauld, Andreas von")
                            .titel("Die Freiheitsrechte und ihre Schranken")
                            .veroeffentlichungsjahr("1999")
                            .build(),
                        ZitierungLiteraturSelbststaendig.builder()
                            .richtung(Richtung.PASSIV)
                            .dokumentnummer("KSLS071671728")
                            .build()))
                .zitierungenRechtsprechung(
                    List.of(
                        ZitierungRechtsprechung.builder()
                            .richtung(Richtung.AKTIV)
                            .referenzUri("ris:/rechtsprechung/BVRE100338409")
                            .dokumentnummer("BVRE100338409")
                            .artDerZitierung("Vergleiche")
                            .gericht(
                                Gericht.builder()
                                    .gerichtstyp("VerfGH")
                                    .gerichtsort("München")
                                    .gerichtsbarkeit("Verfassungsgerichtsbarkeit")
                                    .spruchkoerper("1. Senat")
                                    .sitzDerAussenstelle("Nürnberg")
                                    .build())
                            .entscheidungsdatum(LocalDate.of(1983, Month.DECEMBER, 2))
                            .dokumenttyp("Beschluss")
                            .aktenzeichen("Vf. 94-VI-81")
                            .fundstelle(
                                Fundstelle.builder()
                                    .periodikum("BayVBl")
                                    .zitatstelle("1984, 15")
                                    .build())
                            .build(),
                        ZitierungRechtsprechung.builder()
                            .richtung(Richtung.PASSIV)
                            .dokumentnummer("JURE100074208")
                            .build()))
                .zitierungenVerwaltungsvorschriften(
                    List.of(
                        ZitierungVerwaltungsvorschrift.builder()
                            .richtung(Richtung.AKTIV)
                            .referenzUri("ris:/verwaltungsvorschrift/KSNR008561615")
                            .dokumentnummer("KSNR008561615")
                            .artDerZitierung("Vergleiche")
                            .jurisAbkuerzung("VR VDR 1997-12-04 5883")
                            .fundstelle(
                                Fundstelle.builder()
                                    .periodikum("MittLVA Oberfr")
                                    .zitatstelle("1997, 538-539")
                                    .build())
                            .build(),
                        ZitierungVerwaltungsvorschrift.builder()
                            .richtung(Richtung.PASSIV)
                            .dokumentnummer("KSNR006800006")
                            .build()))
                .build())
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
  @DisplayName("Correctly maps scalar RechtsprechungSchema attributes")
  void fromDomainSingleRechtsprechungSchemaScalarAttributes() {
    RechtsprechungSchema rechtsprechungSchema =
        RechtsprechungSchemaMapper.fromDomain(buildDocumentationUnit(), "jsonLdContext");

    assertThat(rechtsprechungSchema.id()).isEqualTo("/v1/rechtsprechung/BFRE000087655");
    assertThat(rechtsprechungSchema.dokumentNummer()).isEqualTo("BFRE000087655");
    assertThat(rechtsprechungSchema.ecli()).isEqualTo("ECLI:DE:FGNI:1975:0526.IXL180.73.0A");
    assertThat(rechtsprechungSchema.gericht()).isEqualTo("KG Berlin");
    assertThat(rechtsprechungSchema.dokumenttyp()).isEqualTo("Urteil");
    assertThat(rechtsprechungSchema.datum()).isEqualTo(SharedTestConstants.DATE_2024_01_02);
    assertThat(rechtsprechungSchema.datumsTyp()).isEqualTo(DatumsTyp.MITTEILUNGSDATUM);
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
    assertThat(rechtsprechungSchema.context()).isEqualTo("jsonLdContext");
  }

  @Test
  @DisplayName("Correctly maps additional scalar RechtsprechungSchema attributes")
  void fromDomainSingleRechtsprechungSchemaAdditionalScalarAttributes() {
    RechtsprechungSchema rechtsprechungSchema =
        RechtsprechungSchemaMapper.fromDomain(buildDocumentationUnit(), "jsonLdContext");

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
        RechtsprechungSchemaMapper.fromDomain(buildDocumentationUnit(), "jsonLdContext");

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
        RechtsprechungSchemaMapper.fromDomain(buildDocumentationUnit(), "jsonLdContext");

    assertThat(rechtsprechungSchema.vorgehendeEntscheidungen())
        .containsExactly("previous decision file number, previous decision court type");
    assertThat(rechtsprechungSchema.nachgehendeEntscheidungen())
        .containsExactly("ensuing decision file number, ensuing decision court type");
    assertThat(rechtsprechungSchema.amtlicheFundstellen()).containsExactly("BGHSt 67, 273-284");
    assertThat(rechtsprechungSchema.nichtamtlicheFundstellen())
        .containsExactly("DStR 2023, 1430-1435");
    assertThat(rechtsprechungSchema.gesetzeskraft())
        .containsExactly("vereinbar mit höherrangigem Recht, Bremen");
    assertThat(rechtsprechungSchema.normenkette()).containsExactly("BGB § 823");
  }

  @Test
  @DisplayName("Maps Zitierungen Literatur to schema objects")
  void fromDomainMapsZitierungenLiteratur() {
    RechtsprechungSchema rechtsprechungSchema =
        RechtsprechungSchemaMapper.fromDomain(buildDocumentationUnit(), "jsonLdContext");

    assertThat(rechtsprechungSchema.zitierungenLiteraturUnselbststaendig())
        .containsExactly(
            ZitierungLiteraturUnselbststaendigSchema.builder()
                .richtung(ZitierungRichtung.AKTIV)
                .referenzUri("ris:/literatur/STLU991393280")
                .dokumentnummer("STLU991393280")
                .autor("Schmidt-Liebig")
                .dokumenttyp("Aufsatz")
                .fundstelle(
                    FundstelleSchema.builder()
                        .periodikum("Information StW")
                        .zitatstelle("1999, 641")
                        .fundstellenTyp("nichtamtlich")
                        .build())
                .build(),
            ZitierungLiteraturUnselbststaendigSchema.builder()
                .richtung(ZitierungRichtung.PASSIV)
                .dokumentnummer("SBLU000539216")
                .build());
    assertThat(rechtsprechungSchema.zitierungenLiteraturSelbststaendig())
        .containsExactly(
            ZitierungLiteraturSelbststaendigSchema.builder()
                .richtung(ZitierungRichtung.AKTIV)
                .referenzUri("ris:/literatur/KSLS071671727")
                .dokumentnummer("KSLS071671727")
                .autor("Arnauld, Andreas von")
                .titel("Die Freiheitsrechte und ihre Schranken")
                .veroeffentlichungsjahr("1999")
                .build(),
            ZitierungLiteraturSelbststaendigSchema.builder()
                .richtung(ZitierungRichtung.PASSIV)
                .dokumentnummer("KSLS071671728")
                .build());
  }

  @Test
  @DisplayName("Maps Zitierungen Rechtsprechung and Verwaltungsvorschriften to schema objects")
  void fromDomainMapsZitierungenRechtsprechungAndVerwaltungsvorschriften() {
    RechtsprechungSchema rechtsprechungSchema =
        RechtsprechungSchemaMapper.fromDomain(buildDocumentationUnit(), "jsonLdContext");

    assertThat(rechtsprechungSchema.zitierungenRechtsprechung())
        .containsExactly(
            ZitierungRechtsprechungSchema.builder()
                .richtung(ZitierungRichtung.AKTIV)
                .referenzUri("ris:/rechtsprechung/BVRE100338409")
                .dokumentnummer("BVRE100338409")
                .artDerZitierung("Vergleiche")
                .gericht(
                    GerichtSchema.builder()
                        .gerichtstyp("VerfGH")
                        .gerichtsort("München")
                        .gerichtsbarkeit("Verfassungsgerichtsbarkeit")
                        .spruchkoerper("1. Senat")
                        .sitzDerAussenstelle("Nürnberg")
                        .build())
                .entscheidungsdatum(LocalDate.of(1983, Month.DECEMBER, 2))
                .dokumenttyp("Beschluss")
                .aktenzeichen("Vf. 94-VI-81")
                .fundstelle(
                    FundstelleSchema.builder().periodikum("BayVBl").zitatstelle("1984, 15").build())
                .build(),
            ZitierungRechtsprechungSchema.builder()
                .richtung(ZitierungRichtung.PASSIV)
                .dokumentnummer("JURE100074208")
                .build());
    assertThat(rechtsprechungSchema.zitierungenVerwaltungsvorschriften())
        .containsExactly(
            ZitierungVerwaltungsvorschriftSchema.builder()
                .richtung(ZitierungRichtung.AKTIV)
                .referenzUri("ris:/verwaltungsvorschrift/KSNR008561615")
                .dokumentnummer("KSNR008561615")
                .artDerZitierung("Vergleiche")
                .jurisAbkuerzung("VR VDR 1997-12-04 5883")
                .fundstelle(
                    FundstelleSchema.builder()
                        .periodikum("MittLVA Oberfr")
                        .zitatstelle("1997, 538-539")
                        .build())
                .build(),
            ZitierungVerwaltungsvorschriftSchema.builder()
                .richtung(ZitierungRichtung.PASSIV)
                .dokumentnummer("KSNR006800006")
                .build());
  }

  @Test
  @DisplayName("Returns no Zitierungen when none are stored")
  void fromDomainWithoutZitierungen() {
    var documentationUnit =
        CaseLawDocumentationUnit.builder().documentNumber("BFRE000087655").build();

    RechtsprechungSchema rechtsprechungSchema =
        RechtsprechungSchemaMapper.fromDomain(documentationUnit, "jsonLdContext");

    assertThat(rechtsprechungSchema.zitierungenLiteraturSelbststaendig()).isNull();
    assertThat(rechtsprechungSchema.zitierungenLiteraturUnselbststaendig()).isNull();
    assertThat(rechtsprechungSchema.zitierungenRechtsprechung()).isNull();
    assertThat(rechtsprechungSchema.zitierungenVerwaltungsvorschriften()).isNull();
  }

  @Test
  @DisplayName("Creates encodings for html, xml and zip")
  void createsEncodings() {
    var documentationUnit =
        CaseLawDocumentationUnit.builder().documentNumber("BFRE000087655").build();

    RechtsprechungSchema rechtsprechungSchema =
        RechtsprechungSchemaMapper.fromDomain(documentationUnit, "jsonLdContext");

    assertThat(rechtsprechungSchema.encoding())
        .containsExactly(
            DocumentEncodingSchema.builder()
                .id("/v1/rechtsprechung/BFRE000087655/html")
                .contentUrl("/v1/rechtsprechung/BFRE000087655.html")
                .encodingFormat("text/html")
                .inLanguage("de")
                .build(),
            DocumentEncodingSchema.builder()
                .id("/v1/rechtsprechung/BFRE000087655/xml")
                .contentUrl("/v1/rechtsprechung/BFRE000087655.xml")
                .encodingFormat("application/xml")
                .inLanguage("de")
                .build(),
            DocumentEncodingSchema.builder()
                .id("/v1/rechtsprechung/BFRE000087655/zip")
                .contentUrl("/v1/rechtsprechung/BFRE000087655.zip")
                .encodingFormat("application/zip")
                .inLanguage("de")
                .build());
  }
}
