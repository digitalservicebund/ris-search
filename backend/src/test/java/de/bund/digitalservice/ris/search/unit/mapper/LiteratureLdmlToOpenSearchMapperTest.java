package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.bund.digitalservice.ris.LoadXmlUtils;
import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.mapper.LiteratureLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import java.time.Instant;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class LiteratureLdmlToOpenSearchMapperTest {

  private static final String VALID_XML =
      LoadXmlUtils.loadXmlAsString(Literature.class, "literatureLdml-1.akn.xml");

  @Test
  @DisplayName("Correctly maps all available fields from valid literature LDML XML")
  void correctlyMapsAllAvailableFields() {
    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(VALID_XML);

    // Core identifiers
    assertThat(literature.id()).isEqualTo("ABCD0000000001");
    assertThat(literature.documentNumber()).isEqualTo("ABCD0000000001");

    // Publication info
    assertThat(literature.yearsOfPublication()).containsExactly("2025");
    assertThat(literature.firstPublicationDate()).isEqualTo(LocalDate.of(2025, 1, 1));

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

    assertThatThrownBy(() -> LiteratureLdmlToOpenSearchMapper.mapLdml(literatureLdml))
        .isInstanceOf(OpenSearchMapperException.class)
        .hasMessageContaining("unable to parse file to Literature");
  }

  @Test
  @DisplayName("Sets indexedAt to current time")
  void setsIndexedAtToCurrentTime() {
    Instant fixedInstant = Instant.parse("2025-01-01T10:00:00Z");

    try (MockedStatic<Instant> mockedInstant = Mockito.mockStatic(Instant.class)) {
      mockedInstant.when(Instant::now).thenReturn(fixedInstant);

      Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(VALID_XML);
      assertThat(literature.indexedAt()).isEqualTo(fixedInstant.toString());
    }
  }

  @Test
  @DisplayName("Does not set values for missing optional datapoints")
  void doesNotSetValuesForMissingOptionalDatapoints() {
    final String minimalValidLdml =
        """
                <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0" xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/metadata/">
                  <akn:doc name="offene-struktur">
                    <akn:meta>
                      <akn:identification>
                        <akn:FRBRExpression>
                          <akn:FRBRalias name="documentNumber" value="BJLU002758328" />
                        </akn:FRBRExpression>
                      </akn:identification>
                                  <akn:classification source="doktyp">
                                      <akn:keyword dictionary="attributsemantik-noch-undefiniert" showAs="Auf" value="Auf"/>
                                  </akn:classification>
                       <akn:proprietary source="attributsemantik-noch-undefiniert">
                                          <ris:metadata>
                                              <ris:veroeffentlichungsJahre>
                                                  <ris:veroeffentlichungsJahr>2025</ris:veroeffentlichungsJahr>
                                              </ris:veroeffentlichungsJahre>
                                          </ris:metadata>
                                      </akn:proprietary>
                    </akn:meta>
                  </akn:doc>
                </akn:akomaNtoso>
                """
            .stripIndent();

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(minimalValidLdml);

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

  @ParameterizedTest(name = "Parses \"{0}\" → {1}")
  @MethodSource("provideYearsOfPublication")
  @DisplayName("Correctly parses veroeffentlichungsJahr into firstPublicationDate")
  void parsesVariousYearFormats(String yearValue, LocalDate expectedDate) {
    final String xmlTemplate =
        """
                  <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
                                   xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/metadata/">
                    <akn:doc name="offene-struktur">
                      <akn:meta>
                        <akn:identification>
                          <akn:FRBRExpression>
                            <akn:FRBRalias name="documentNumber" value="DOC-PARAM" />
                          </akn:FRBRExpression>
                        </akn:identification>
                        <akn:classification source="doktyp">
                          <akn:keyword dictionary="attributsemantik-noch-undefiniert" showAs="Auf" value="Auf"/>
                        </akn:classification>
                        <akn:proprietary source="test">
                          <ris:metadata>
                            <ris:veroeffentlichungsJahre>
                              <ris:veroeffentlichungsJahr>%s</ris:veroeffentlichungsJahr>
                            </ris:veroeffentlichungsJahre>
                          </ris:metadata>
                        </akn:proprietary>
                      </akn:meta>
                    </akn:doc>
                  </akn:akomaNtoso>
                  """;

    String xml = xmlTemplate.formatted(yearValue);
    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(xml);

    assertThat(literature.firstPublicationDate()).isEqualTo(expectedDate);
  }

  private static Stream<Arguments> provideYearsOfPublication() {
    return Stream.of(
        Arguments.of(" 2020 ", LocalDate.of(2020, 1, 1)),
        Arguments.of("2020", LocalDate.of(2020, 1, 1)),
        Arguments.of(" 2020-05 ", LocalDate.of(2020, 5, 1)),
        Arguments.of("2020-05", LocalDate.of(2020, 5, 1)),
        Arguments.of(" 2020-05-23 ", LocalDate.of(2020, 5, 23)),
        Arguments.of("2020-05-23", LocalDate.of(2020, 5, 23)),
        Arguments.of("XX", null),
        Arguments.of("1986 - 1987", null),
        Arguments.of("2001 (vermutlich)", null),
        Arguments.of("2003, 127-133 (Schriften des Vereins für Socialpolitik", null));
  }
}
