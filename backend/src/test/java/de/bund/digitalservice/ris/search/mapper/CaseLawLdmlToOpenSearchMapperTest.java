package de.bund.digitalservice.ris.search.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.models.DatumsTyp;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawUnindexed;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Fundstelle;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Gericht;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Richtung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungLiteraturSelbststaendig;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungLiteraturUnselbststaendig;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungRechtsprechung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungVerwaltungsvorschrift;
import de.bund.digitalservice.ris.search.utils.CaseLawLdmlTemplateUtils;
import de.bund.digitalservice.ris.utils.CaseLawXmlValidator;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CaseLawLdmlToOpenSearchMapperTest {

  private String testCaseLawLdml;
  private String testCaseLawLdmlWithMissingDate;
  private final CaseLawLdmlTemplateUtils caseLawLdmlTemplateUtils = new CaseLawLdmlTemplateUtils();
  private final CaseLawLdmlToOpenSearchMapper mapper = new CaseLawLdmlToOpenSearchMapper();

  @BeforeEach
  void beforeEach() throws IOException {
    testCaseLawLdml =
        caseLawLdmlTemplateUtils.getXmlFromTemplateWithValidation(
            null, CaseLawXmlValidator.Type.DECISION);
    testCaseLawLdmlWithMissingDate =
        caseLawLdmlTemplateUtils.getXmlFromTemplateWithValidation(
            Map.of("hideDecisionDate", true), CaseLawXmlValidator.Type.DECISION);
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
    assertThat(caseLaw.gerichtsbarkeit()).isEqualTo("Test jurisdiction type");
    assertThat(caseLaw.courtKeyword()).isEqualTo("Test court label");
    assertThat(caseLaw.documentType()).isEqualTo("Urteil");
    assertThat(caseLaw.outline()).isEqualTo("Example Gliederung/Outline");
    assertThat(caseLaw.judicialBody()).isEqualTo("Test judicial body");
    assertThat(caseLaw.dissentingOpinion())
        .isEqualTo(
            "dissenting test, Dr. Phil. Max Mustermann: referenced opinions test 1, Maxima Mustermann: referenced opinions test 2");
  }

  @Test
  void shouldMapAdditionalCaseLawFieldsCorrectly() {
    CaseLawDocumentationUnit caseLaw = mapper.fromString(testCaseLawLdml);

    assertThat(caseLaw.hasLegislativeMandate()).isEqualTo("Ja");
    assertThat(caseLaw.langtextdatum()).isEqualTo(LocalDate.of(2016, Month.JUNE, 15));
    assertThat(caseLaw.revision()).isEqualTo("Ja");
    assertThat(caseLaw.letzteVeroeffentlichung()).isEqualTo(LocalDate.of(2026, Month.MARCH, 20));
    assertThat(caseLaw.legalEffect()).isEqualTo("Ja");
    assertThat(caseLaw.erstveroeffentlichung()).isEqualTo(LocalDate.of(2026, Month.MARCH, 18));
  }

  @Test
  void shouldMapPendingProceedingFieldsCorrectly() throws IOException {
    String pendingProceedingXml =
        caseLawLdmlTemplateUtils.getXmlFromTemplateWithValidation(
            Map.of("pendingProceeding", true), CaseLawXmlValidator.Type.PENDING_PROCEEDING);
    CaseLawDocumentationUnit caseLaw = mapper.fromString(pendingProceedingXml);

    assertThat(caseLaw.erledigung()).isEqualTo("Ja");
    assertThat(caseLaw.rechtsmittelfuehrer()).isEqualTo("Rechtsmittelführer");
    assertThat(caseLaw.rechtsmittelzulassung()).isEqualTo("Rechtsmittelzulassung");
    assertThat(caseLaw.erledigungsvermerk()).isEqualTo("Erledigungsvermerk");
    assertThat(caseLaw.rechtsfrageGesamt()).isEqualTo("Rechtsfrage (gesamt)");
    assertThat(caseLaw.rechtsfrage()).isEqualTo("Rechtsfrage");
    assertThat(caseLaw.mitteilungsdatum()).isEqualTo(LocalDate.of(2020, Month.JANUARY, 1));
    assertThat(caseLaw.decisionDate()).isEqualTo(LocalDate.of(2020, Month.JANUARY, 1));
    assertThat(caseLaw.datumsTyp()).isEqualTo(DatumsTyp.MITTEILUNGSDATUM);
  }

  @Test
  void shouldMapCaseLawCollectionsCorrectly() {
    CaseLawDocumentationUnit caseLaw = mapper.fromString(testCaseLawLdml);

    assertThat(caseLaw.fileNumbers()).hasToString("[Test file number 1, Test file number 2]");
    assertThat(caseLaw.abweichendeAktenzeichen()).hasToString("[1 BvR 839, 899/96]");
    assertThat(caseLaw.keywords()).hasToString("[keyword1, keyword2]");
    assertThat(caseLaw.decisionName()).hasToString("[Test decision name]");
    assertThat(caseLaw.deviatingDocumentNumber()).hasToString("[ABC, DEF]");
    assertThat(caseLaw.abweichendeDaten()).hasToString("[2020-03-03, 2022-02-04]");
    assertThat(caseLaw.abweichendeEclis()).hasToString("[ECLI 1, ECLI 2]");
    assertThat(caseLaw.berufsbilder()).hasToString("[jobProfile test 1, jobProfile test 2]");
    assertThat(caseLaw.kuendigungsarten()).hasToString("[dismissalType test]");
    assertThat(caseLaw.herkunftslaender()).hasToString("[Frankreich, Deutschland]");
    assertThat(caseLaw.regionen()).hasToString("[NW]");
    assertThat(caseLaw.tarifvertraege()).hasToString("[Stehende Bühnen]");
    assertThat(caseLaw.kuendigungsgruende()).hasToString("[Straftat]");
    assertThat(caseLaw.mitwirkendeRichter()).hasToString("[Meier, Müller]");
    assertThat(caseLaw.sachgebiete()).hasToString("[fieldOfLaw test]");
    assertThat(caseLaw.streitjahre()).hasToString("[2024]");
    assertThat(caseLaw.fehlerhafteGerichte()).hasToString("[deviating court 1, deviating court 2]");
    assertThat(caseLaw.datenDerMuendlichenVerhandlung()).hasToString("[2021-02-03]");
    assertThat(caseLaw.definitionen()).hasToString("[indirekte Steuern, Sachgesamtheit]");
    assertThat(caseLaw.vorabdokument()).isFalse();
  }

  @Test
  void shouldMapCaseLawReferenceCollectionsCorrectly() {
    CaseLawDocumentationUnit caseLaw = mapper.fromString(testCaseLawLdml);

    assertThat(caseLaw.previousDecisions())
        .containsExactlyInAnyOrder(
            "previous decision court type previous decision court location, previous decision"
                + " document type vom 1. Januar 2020 - previous decision file number",
            "previous decision court type previous decision court location, previous decision"
                + " document type vom 1. Januar 2020 - previous decision file number");
    assertThat(caseLaw.ensuingDecisions())
        .containsExactlyInAnyOrder(
            "ensuing decision court type, ensuing decision document type vom 1. Oktober 2022 -"
                + " ensuing decision file number (anhängig)",
            "ensuing decision court type, ensuing decision document type vom 1. Oktober 2022 -"
                + " ensuing decision file number (anhängig)");
    assertThat(caseLaw.aktivzitierungLiteraturUnselbstaendig()).containsExactly("STLU991393280");
    assertThat(caseLaw.passivzitierungLiteraturUnselbstaendig()).containsExactly("SBLU000539216");
    assertThat(caseLaw.aktivzitierungLiteraturSelbstaendig()).containsExactly("KSLS071671727");
    assertThat(caseLaw.passivzitierungLiteraturSelbstaendig()).containsExactly("KSLS071671728");
    assertThat(caseLaw.aktivzitierungRechtsprechung()).containsExactly("BVRE100338409");
    assertThat(caseLaw.passivzitierungRechtsprechung()).containsExactly("JURE100074208");
    assertThat(caseLaw.aktivzitierungVerwaltungsvorschriften()).containsExactly("KSNR008561615");
    assertThat(caseLaw.passivzitierungVerwaltungsvorschriften()).containsExactly("KSNR006800006");
    assertThat(caseLaw.amtlicheFundstellen()).containsExactly("BGHSt 67, 273-284");
    assertThat(caseLaw.nichtamtlicheFundstellen()).containsExactly("DStR 2023, 1430-1435");
    assertThat(caseLaw.gesetzeskraft())
        .containsExactly("vereinbar mit höherrangigem Recht, Bremen");
    assertThat(caseLaw.normenkette())
        .containsExactly(
            "normReference test singleNorm test",
            "normReference test singleNorm 2 test",
            "normReference without SingleNorms");
  }

  @Test
  void shouldMapZitierungenLiteraturSelbststaendig() {
    CaseLawUnindexed unindexed = mapper.fromString(testCaseLawLdml).unindexed();

    assertThat(unindexed.zitierungenLiteraturSelbststaendig())
        .containsExactly(
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
                .referenzUri("ris:/literatur/KSLS071671728")
                .dokumentnummer("KSLS071671728")
                .autor("Mustermann, Max")
                .titel("Die Grenzen der Freiheit")
                .veroeffentlichungsjahr("2001")
                .build());
  }

  @Test
  void shouldMapZitierungenLiteraturUnselbststaendig() {
    CaseLawUnindexed unindexed = mapper.fromString(testCaseLawLdml).unindexed();

    assertThat(unindexed.zitierungenLiteraturUnselbststaendig())
        .containsExactly(
            ZitierungLiteraturUnselbststaendig.builder()
                .richtung(Richtung.AKTIV)
                .referenzUri("ris:/literatur/STLU991393280")
                .dokumentnummer("STLU991393280")
                .autor("Schmidt-Liebig")
                .fundstelle(
                    Fundstelle.builder()
                        .periodikum("Information StW")
                        .zitatstelle("1999, 641")
                        .build())
                .build(),
            ZitierungLiteraturUnselbststaendig.builder()
                .richtung(Richtung.PASSIV)
                .referenzUri("ris:/literatur/SBLU000539216")
                .dokumentnummer("SBLU000539216")
                .autor("Knopp, Lothar")
                .dokumenttyp("Aufsatz")
                .fundstelle(
                    Fundstelle.builder().periodikum("NVwZ").zitatstelle("2016, 1139-1141").build())
                .build());
  }

  @Test
  void shouldMapZitierungenRechtsprechung() {
    CaseLawUnindexed unindexed = mapper.fromString(testCaseLawLdml).unindexed();

    assertThat(unindexed.zitierungenRechtsprechung())
        .containsExactly(
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
                        .fundstellenTyp("amtlich")
                        .build())
                .build(),
            ZitierungRechtsprechung.builder()
                .richtung(Richtung.PASSIV)
                .referenzUri("ris:/rechtsprechung/JURE100074208")
                .dokumentnummer("JURE100074208")
                .artDerZitierung("Vergleiche")
                .gericht(
                    Gericht.builder()
                        .gerichtstyp("LArbG")
                        .gerichtsort("Kiel")
                        .gerichtsbarkeit("Arbeitsgerichtsbarkeit")
                        .build())
                .entscheidungsdatum(LocalDate.of(2010, Month.OCTOBER, 26))
                .aktenzeichen("3 Sa 315/10")
                .build());
  }

  @Test
  void shouldMapZitierungenVerwaltungsvorschriften() {
    CaseLawUnindexed unindexed = mapper.fromString(testCaseLawLdml).unindexed();

    assertThat(unindexed.zitierungenVerwaltungsvorschriften())
        .containsExactly(
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
                .referenzUri("ris:/verwaltungsvorschrift/KSNR006800006")
                .dokumentnummer("KSNR006800006")
                .artDerZitierung("Anwendung")
                .jurisAbkuerzung("VR BA 1986-01-16 16/86")
                .fundstelle(
                    Fundstelle.builder()
                        .periodikum("Dienstbl BA RdErl")
                        .zitatstelle("1986, Nr 16, 1-8")
                        .build())
                .build());
  }

  @Test
  void shouldMapToCaseLawDocumentationUnitFrombyteArray() {
    CaseLawDocumentationUnit caseLawFromString = mapper.fromString(testCaseLawLdml);
    CaseLawDocumentationUnit caseLawFromBytes =
        mapper.fromByteArray(testCaseLawLdml.getBytes(StandardCharsets.UTF_8));

    assertThat(caseLawFromBytes)
        .usingRecursiveComparison()
        .ignoringFields("indexedAt")
        .isEqualTo(caseLawFromString);
  }

  @Test
  void setsVorabdokumentToTrueIfDocumentIsMarkedIncomplete() throws IOException {
    String caselawXml =
        caseLawLdmlTemplateUtils.getXmlFromTemplateWithValidation(
            Map.of("vorabdokument", true), CaseLawXmlValidator.Type.DECISION);

    CaseLawDocumentationUnit caseLaw = mapper.fromString(caselawXml);
    assertThat(caseLaw.vorabdokument()).isTrue();
  }

  @Test
  void decisionDateIsReadFromTheSingleFrbrDateRegardlessOfItsName() throws IOException {
    String xml =
        caseLawLdmlTemplateUtils.getXmlFromTemplateWithValidation(
            Map.of(
                "decisionDate",
                "2021-07-07",
                "decisionDateName",
                "datumDerZustellungAnVerkuendungsStatt"),
            CaseLawXmlValidator.Type.DECISION);

    CaseLawDocumentationUnit caseLaw = mapper.fromString(xml);

    assertThat(caseLaw.decisionDate()).isEqualTo(LocalDate.of(2021, Month.JULY, 7));
  }

  @Test
  void decisionDateIsNullWhenNoFrbrDateIsPresentAtAll() {
    CaseLawDocumentationUnit caseLaw = mapper.fromString(testCaseLawLdmlWithMissingDate);
    assertThat(caseLaw.decisionDate()).isNull();
  }

  @ParameterizedTest
  @CsvSource({
    "entscheidungsdatum, ENTSCHEIDUNGSDATUM",
    "Entscheidungsdatum, ENTSCHEIDUNGSDATUM",
    "mitteilungsdatum, MITTEILUNGSDATUM",
    "datumDerZustellungAnVerkuendungsStatt, DATUM_DER_ZUSTELLUNG_AN_VERKUENDUNGS_STATT",
    "unbekannt, ENTSCHEIDUNGSDATUM"
  })
  void datumsTypIsReadFromTheNameOfTheDecisionDate(String name, DatumsTyp expected)
      throws IOException {
    String xml =
        caseLawLdmlTemplateUtils.getXmlFromTemplateWithValidation(
            Map.of("decisionDateName", name), CaseLawXmlValidator.Type.DECISION);

    CaseLawDocumentationUnit caseLaw = mapper.fromString(xml);

    assertThat(caseLaw.datumsTyp()).isEqualTo(expected);
  }

  @Test
  void datumsTypIsNullWhenNoDecisionDateIsPresent() {
    CaseLawDocumentationUnit caseLaw = mapper.fromString(testCaseLawLdmlWithMissingDate);
    assertThat(caseLaw.datumsTyp()).isNull();
  }
}
