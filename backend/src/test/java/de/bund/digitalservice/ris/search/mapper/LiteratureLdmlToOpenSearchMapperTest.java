package de.bund.digitalservice.ris.search.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.bund.digitalservice.ris.LoadXmlUtils;
import de.bund.digitalservice.ris.SharedTestConstants;
import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.LiteratureUnindexed;
import de.bund.digitalservice.ris.search.models.opensearch.common.Gericht;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Fundstelle;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Richtung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungLiteraturSelbststaendig;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungLiteraturUnselbststaendig;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungRechtsprechung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungVerwaltungsvorschrift;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LiteratureLdmlToOpenSearchMapperTest {

  private static final String ULI_XML =
      LoadXmlUtils.loadXmlAsString(Literature.class, "XXLU000000001/XXLU000000001.xml");

  private static final String SLI_XML =
      LoadXmlUtils.loadXmlAsString(Literature.class, "XXLS000000001/XXLS000000001.xml");

  private static final Instant NOW = SharedTestConstants.TIMESTAMP_2024_01_01_AS_INSTANT;

  @Test
  @DisplayName("Correctly maps all available fields from valid unselbstständige literature LDML")
  void correctlyMapsAllAvailableFieldsOfUli() {
    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(ULI_XML, NOW);

    var expected =
        Literature.builder()
            .id("XXLU000000001")
            .documentNumber("XXLU000000001")
            .yearsOfPublication(List.of("2025"))
            .firstPublicationDate(LocalDate.of(2025, Month.JANUARY, 1))
            .documentTypes(List.of("Auf", "Foo"))
            .dependentReferences(List.of("BB, 1979, 1298-1300"))
            .independentReferences(List.of("Titel einer Fundstelle, 1979, 1298-1300"))
            .normReferences(List.of("GG, Art 6 Abs 2 S 1, 1949-05-23"))
            .mainTitle("Literatur Test Dokument")
            .mainTitleAdditions("Titelzusatz")
            .documentaryTitle("Dokumentarischer Titel")
            .authors(List.of("Mustermann, Max", "Musterfrau, Susanne"))
            .collaborators(List.of("Foo, Peter"))
            .originators(List.of("FOO"))
            .conferenceNotes(List.of("Internationaler Kongreß für das Recht, 1991, Athen, GRC"))
            // selbstständige-only elements are not present in uli documents
            .editors(List.of())
            .founder(List.of())
            .universityNotes(List.of())
            .publisherInformation(List.of())
            .publisherOrganizations(List.of())
            .publisherPersons(List.of())
            .shortTitles(List.of())
            .additionalTitles(List.of())
            .fullTitleAdditions(List.of())
            .footnotes(List.of())
            .internationalIdentifiers(List.of())
            .volumes(List.of())
            .indexedAt(NOW.toString())
            .languages(List.of("deu"))
            .shortReport(
                "1. Dies ist ein literature LDML Dokument für Tests. Es werden sub und sup Elemente unterstützt. Außerdem gib es noch EM, hlj, noindex und strong.")
            .outline("I. Äpfel. II. Birnen. III. Orangen.")
            .titelAspekte(List.of("Testdokument"))
            .schlagworte(
                List.of(
                    "betriebliche Bildung",
                    "betriebliche Bildungsmaßnahme",
                    "paritätische Beteiligung",
                    "paritätische Mitbestimmung"))
            .sachgebiete(List.of("ID-01 Sachgebiet1", "ID-02 Sachgebiet2"))
            .unindexed(
                LiteratureUnindexed.builder()
                    .zitierungenLiteraturSelbststaendig(
                        List.of(
                            ZitierungLiteraturSelbststaendig.builder()
                                .richtung(Richtung.AKTIV)
                                .titel("Selbstständigeliterature")
                                .veroeffentlichungsjahr("2025")
                                .build()))
                    .zitierungenLiteraturUnselbststaendig(
                        List.of(
                            ZitierungLiteraturUnselbststaendig.builder()
                                .richtung(Richtung.PASSIV)
                                .fundstelle(
                                    Fundstelle.builder()
                                        .periodikum("Unselbstständigeliterature")
                                        .zitatstelle("2023")
                                        .build())
                                .build(),
                            ZitierungLiteraturUnselbststaendig.builder()
                                .richtung(Richtung.PASSIV)
                                .fundstelle(
                                    Fundstelle.builder()
                                        .periodikum("Unselbstständigeliterature")
                                        .zitatstelle("1989")
                                        .build())
                                .build()))
                    .zitierungenRechtsprechung(
                        List.of(
                            ZitierungRechtsprechung.builder()
                                .richtung(Richtung.AKTIV)
                                .artDerZitierung("Vergleiche")
                                .gericht(
                                    Gericht.builder()
                                        .gerichtstyp("FooBar")
                                        .spruchkoerper("1. Kammer")
                                        .build())
                                .entscheidungsdatum(LocalDate.of(2008, Month.APRIL, 3))
                                .build()))
                    .zitierungenVerwaltungsvorschriften(
                        List.of(
                            ZitierungVerwaltungsvorschrift.builder()
                                .richtung(Richtung.PASSIV)
                                .artDerZitierung("Vergleiche")
                                .jurisAbkuerzung("NaNu")
                                .build()))
                    .build())
            .build();

    assertThat(literature).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  @DisplayName("Correctly maps all available fields from valid selbstständige literature LDML")
  void correctlyMapsAllAvailableFieldsOfSli() {
    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(SLI_XML, NOW);

    var expected =
        Literature.builder()
            .id("XXLS000000001")
            .documentNumber("XXLS000000001")
            .yearsOfPublication(List.of("2025"))
            .firstPublicationDate(LocalDate.of(2025, Month.JANUARY, 1))
            .documentTypes(List.of("Auf", "Foo"))
            .mainTitle("Literatur Test Dokument")
            .mainTitleAdditions("Titelzusatz")
            .documentaryTitle("Dokumentarischer Titel")
            .authors(List.of("Mustermann, Max", "Musterfrau, Susanne"))
            .editors(List.of("Bearbeiter, Foo"))
            .founder(List.of("Begruender, Foo"))
            .collaborators(List.of("Foo, Peter"))
            .originators(List.of("FOO"))
            .languages(List.of("deu"))
            .universityNotes(List.of("Universität Foo"))
            .publisherInformation(List.of("verlag, Berlin"))
            .publisherOrganizations(List.of("herausgeber institution showAs"))
            .publisherPersons(List.of("Titel, foo herausgeber person"))
            .conferenceNotes(List.of("Internationaler Kongreß für das Recht, 1991, Athen, GRC"))
            .shortTitles(List.of("titel kurzform"))
            .additionalTitles(List.of("sonstiger titel"))
            .fullTitleAdditions(List.of("gesamttitel, bandbezeichnung"))
            .footnotes(List.of("1. Auflage 19XX"))
            .shortReport(
                "1. Dies ist ein literature LDML Dokument für Tests. Es werden sub und sup Elemente unterstützt. Außerdem gib es noch EM, hlj, noindex und strong.")
            .outline("I. Äpfel. II. Birnen. III. Orangen.")
            .indexedAt(NOW.toString())
            .edition("1. Auflage")
            .internationalIdentifiers(List.of("ISBN 3-XXXXX-XX-X", "ISSN XXXX-XXXX"))
            .volumes(List.of("Teilband"))
            .normReferences(List.of("GG, Art 6 Abs 2 S 1, 1949-05-23"))
            // fundstellen only exist in uli documents
            .dependentReferences(List.of())
            .independentReferences(List.of())
            .titelAspekte(List.of("Testdokument"))
            .schlagworte(
                List.of(
                    "betriebliche Bildung",
                    "betriebliche Bildungsmaßnahme",
                    "paritätische Beteiligung",
                    "paritätische Mitbestimmung"))
            .sachgebiete(List.of("ID-01 Sachgebiet1", "ID-02 Sachgebiet2"))
            .unindexed(
                LiteratureUnindexed.builder()
                    .zitierungenLiteraturSelbststaendig(List.of())
                    .zitierungenLiteraturUnselbststaendig(List.of())
                    .zitierungenRechtsprechung(List.of())
                    .zitierungenVerwaltungsvorschriften(
                        List.of(
                            ZitierungVerwaltungsvorschrift.builder()
                                .richtung(Richtung.PASSIV)
                                .artDerZitierung("Vergleiche")
                                .jurisAbkuerzung("NaNu")
                                .build()))
                    .build())
            .build();

    assertThat(literature).isEqualTo(expected);
  }

  @Test
  @DisplayName("Returns empty optional if document number is missing")
  void returnsEmptyOptionalIfDocumentNumberIsMissing() {
    String literatureLdml =
        """
        <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0">
          <akn:doc name="offene-struktur">
          </akn:doc>
        </akn:akomaNtoso>
        """
            .stripIndent();

    assertThatThrownBy(() -> LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml, NOW))
        .isInstanceOf(OpenSearchMapperException.class)
        .hasMessageContaining("unable to parse file to Literature");
  }

  @Test
  @DisplayName("Sets indexedAt to the given time")
  void setsIndexedAtToGivenTime() {
    Instant fixedInstant = Instant.parse("2025-01-01T10:00:00Z");

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(ULI_XML, fixedInstant);

    assertThat(literature.indexedAt()).isEqualTo(fixedInstant.toString());
  }

  @Test
  @DisplayName("Does not set values for missing optional datapoints")
  void doesNotSetValuesForMissingOptionalDatapoints() {
    final String minimalValidLdml =
        """
        <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0" xmlns:ris="http://rechtsinformationen.bund.de/schema/ris/0.1">
          <akn:doc name="offene-struktur">
            <akn:meta>
              <akn:identification>
                <akn:FRBRWork>
                  <akn:FRBRalias name="dokumentnummer" value="BJLU002758328" />
                </akn:FRBRWork>
              </akn:identification>
               <akn:proprietary source="attributsemantik-noch-undefiniert">
                                  <ris:meta>
                                      <ris:dokumenttypen>
                                          <ris:dokumenttyp>Auf</ris:dokumenttyp>
                                      </ris:dokumenttypen>
                                      <ris:veroeffentlichungsJahre>
                                          <ris:veroeffentlichungsJahr>2025</ris:veroeffentlichungsJahr>
                                      </ris:veroeffentlichungsJahre>
                                  </ris:meta>
                              </akn:proprietary>
            </akn:meta>
          </akn:doc>
        </akn:akomaNtoso>
        """
            .stripIndent();

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(minimalValidLdml, NOW);

    assertThat(literature.dependentReferences()).isEmpty();
    assertThat(literature.independentReferences()).isEmpty();
    assertThat(literature.mainTitle()).isNull();
    assertThat(literature.mainTitleAdditions()).isNull();
    assertThat(literature.documentaryTitle()).isNull();
    assertThat(literature.authors()).isEmpty();
    assertThat(literature.collaborators()).isEmpty();
    assertThat(literature.originators()).isEmpty();
    assertThat(literature.conferenceNotes()).isEmpty();
    assertThat(literature.languages()).isEmpty();
    assertThat(literature.normReferences()).isEmpty();
    assertThat(literature.shortReport()).isNull();
    assertThat(literature.outline()).isNull();
  }
}
