package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.mapper.LiteratureSchemaMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.schema.LiteratureEncodingSchema;
import de.bund.digitalservice.ris.search.schema.LiteratureSchema;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LiteratureSchemaMapperTest {

  @Test
  @DisplayName("Correctly maps attributes")
  void fromDomainSingle() {
    var literature =
        Literature.builder()
            .id("XXLU000000001")
            .documentNumber("XXLU000000001")
            .yearsOfPublication(List.of("1979", "2004-09"))
            .documentTypes(List.of("Auf"))
            .dependentReferences(List.of("BUV, 1982, 123-123"))
            .independentReferences(List.of("50 Jahre Betriebs-Berater, 1987, 123-456"))
            .normReferences(List.of("GG, Art 6 Abs 2 S 1, 1949-05-23"))
            .mainTitle("Hauptüberschrift")
            .mainTitleAdditions("Zusatz zur Hauptüberschrift")
            .documentaryTitle("Dokumentarischer Titel")
            .authors(List.of("Musterfrau, Sabine"))
            .collaborators(List.of("Mustermann, Max"))
            .originators(List.of("FOO"))
            .conferenceNotes(List.of("Internationaler Kongress 2025, Berlin, GER"))
            .languages(List.of("deu", "eng"))
            .shortReport("Kurzreferat")
            .outline("Gliederung")
            .founder(List.of("founder"))
            .editors(List.of("editor"))
            .edition("edition")
            .publisherOrganizations(List.of("publisher organization"))
            .publisherPersons(List.of("publisher person"))
            .internationalIdentifiers(List.of("ISBN XXXX"))
            .universityNotes(List.of("university note"))
            .volumes(List.of("volume 1", "volume 2"))
            .build();

    LiteratureSchema expected =
        LiteratureSchema.builder()
            .id("/v1/literature/XXLU000000001")
            .languages(List.of("de"))
            .documentNumber("XXLU000000001")
            .inLanguage("de")
            .yearsOfPublication(List.of("1979", "2004-09"))
            .documentTypes(List.of("Auf"))
            .dependentReferences(List.of("BUV, 1982, 123-123"))
            .independentReferences(List.of("50 Jahre Betriebs-Berater, 1987, 123-456"))
            .normReferences(List.of("GG, Art 6 Abs 2 S 1, 1949-05-23"))
            .headline("Hauptüberschrift")
            .alternativeHeadline("Dokumentarischer Titel")
            .headlineAdditions("Zusatz zur Hauptüberschrift")
            .authors(List.of("Musterfrau, Sabine"))
            .collaborators(List.of("Mustermann, Max"))
            .originators(List.of("FOO"))
            .conferenceNotes(List.of("Internationaler Kongress 2025, Berlin, GER"))
            .languages(List.of("deu", "eng"))
            .shortReport("Kurzreferat")
            .outline("Gliederung")
            .founder(List.of("founder"))
            .editors(List.of("editor"))
            .edition("edition")
            .publishingHouses(List.of("publisher organization"))
            .publishers(List.of("publisher person"))
            .internationalIdentifiers(List.of("ISBN XXXX"))
            .universityNotes(List.of("university note"))
            .volumes(List.of("volume 1", "volume 2"))
            .literatureType("uli")
            .encoding(
                List.of(
                    LiteratureEncodingSchema.builder()
                        .id("/v1/literature/XXLU000000001/html")
                        .contentUrl("/v1/literature/XXLU000000001.html")
                        .encodingFormat("text/html")
                        .inLanguage("de")
                        .build(),
                    LiteratureEncodingSchema.builder()
                        .id("/v1/literature/XXLU000000001/xml")
                        .contentUrl("/v1/literature/XXLU000000001.xml")
                        .encodingFormat("application/xml")
                        .inLanguage("de")
                        .build(),
                    LiteratureEncodingSchema.builder()
                        .id("/v1/literature/XXLU000000001/zip")
                        .contentUrl("/v1/literature/XXLU000000001.zip")
                        .encodingFormat("application/zip")
                        .inLanguage("de")
                        .build()))
            .build();

    LiteratureSchema literatureSchema = LiteratureSchemaMapper.fromDomain(literature);
    assertThat(literatureSchema).isEqualTo(expected);
  }
}
