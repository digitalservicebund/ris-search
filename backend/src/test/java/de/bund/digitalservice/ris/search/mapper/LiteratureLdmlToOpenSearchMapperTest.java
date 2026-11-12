package de.bund.digitalservice.ris.search.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.bund.digitalservice.ris.LoadXmlUtils;
import de.bund.digitalservice.ris.SharedTestConstants;
import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LiteratureLdmlToOpenSearchMapperTest {

  private static final Instant NOW = SharedTestConstants.TIMESTAMP_2024_01_01_AS_INSTANT;

  private static final String VALID_ULI_XML =
      LoadXmlUtils.loadXmlAsString(Literature.class, "XXLU000000001/XXLU000000001.akn.xml");

  private static final String VALID_SLI_XML =
      LoadXmlUtils.loadXmlAsString(Literature.class, "XXLS000000001/XXLS000000001.akn.xml");

  @Test
  @DisplayName("Correctly maps all available fields from valid ULI literature LDML XML")
  void correctlyMapsAllAvailableUliFields() {
    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(VALID_ULI_XML, NOW);

    // Core identifiers
    assertThat(literature.id()).isEqualTo("XXLU000000001");
    assertThat(literature.documentNumber()).isEqualTo("XXLU000000001");

    // Publication info
    assertThat(literature.yearsOfPublication()).containsExactly("2025");
    assertThat(literature.firstPublicationDate()).isEqualTo(LocalDate.of(2025, Month.JANUARY, 1));

    // Metadata
    assertThat(literature.documentTypes()).containsExactly("Auf", "Foo");
    assertThat(literature.dependentReferences()).containsExactly("BB, 1979, 1298-1300");
    assertThat(literature.independentReferences())
        .containsExactly("Titel einer Fundstelle, 1979, 1298-1300");
    assertThat(literature.mainTitle()).isEqualTo("Literatur Test Dokument");
    assertThat(literature.mainTitleAdditions()).isEqualTo("Titelzusatz");
    assertThat(literature.documentaryTitle()).isEqualTo("Dokumentarischer Titel");
    assertThat(literature.authors()).containsExactly("Mustermann, Max", "Musterfrau, Susanne");
    assertThat(literature.collaborators()).containsExactly("Foo, Peter");
    assertThat(literature.originators()).containsExactly("FOO");
    assertThat(literature.conferenceNotes())
        .containsExactly("Internationaler Kongreß für das Recht, 1991, Athen, GRC");
    assertThat(literature.languages()).containsExactly("deu");
    assertThat(literature.normReferences()).containsExactly("GG, Art 6 Abs 2 S 1, 1949-05-23");

    // Content
    assertThat(literature.shortReport())
        .isEqualTo(
            "1. Dies ist ein literature LDML Dokument für Tests. Es werden sub und sup Elemente unterstützt. Außerdem gib es noch EM, hlj, noindex und strong.");
    assertThat(literature.outline()).isEqualTo("I. Äpfel. II. Birnen. III. Orangen.");
  }

  @Test
  @DisplayName("Correctly maps all available fields from valid SLI literature LDML XML")
  void correctlyMapsAllAvailableSliFields() {
    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(VALID_SLI_XML, NOW);

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
            .normReferences(List.of("GG, Art 6 Abs 2 S 1, 1949-05-23"))
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
            .dependentReferences(List.of("BB, 1979, 1298-1300"))
            .independentReferences(List.of("Titel einer Fundstelle, 1979, 1298-1300"))
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
    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(VALID_ULI_XML, NOW);
    assertThat(literature.indexedAt()).isEqualTo(NOW.toString());
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
