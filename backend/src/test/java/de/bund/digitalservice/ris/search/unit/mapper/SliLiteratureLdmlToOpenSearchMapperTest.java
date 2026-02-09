package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.LoadXmlUtils;
import de.bund.digitalservice.ris.search.mapper.SliLiteratureLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SliLiteratureLdmlToOpenSearchMapperTest {

  private static final String VALID_XML =
      LoadXmlUtils.loadXmlAsString(Literature.class, "XXLS000000001.akn.xml");

  @Test
  @DisplayName("Correctly maps all available fields from valid literature LDML XML")
  void correctlyMapsAllAvailableFields() {
    var now = Instant.now();
    Literature literature = SliLiteratureLdmlToOpenSearchMapper.mapLdml(VALID_XML, now);

    var expected =
        Literature.builder()
            .id("XXLS000000001")
            .documentNumber("XXLS000000001")
            .yearsOfPublication(List.of("2025"))
            .firstPublicationDate(LocalDate.of(2025, 1, 1))
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
            .indexedAt(now.toString())
            .edition("1. Auflage")
            .internationalIdentifiers(List.of("ISBN 3-XXXXX-XX-X", "ISSN XXXX-XXXX"))
            .volumes(List.of("Teilband"))
            .normReferences(List.of("GG, Art 6 Abs 2 S 1, 1949-05-23"))
            .dependentReferences(List.of("BB, 1979, 1298-1300"))
            .independentReferences(List.of("Titel einer Fundstelle, 1979, 1298-1300"))
            .build();

    assertThat(literature).isEqualTo(expected);
  }
}
