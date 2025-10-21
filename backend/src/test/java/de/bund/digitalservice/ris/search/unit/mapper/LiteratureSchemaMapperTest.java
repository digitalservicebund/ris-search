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
            .id("TEST000000001")
            .documentNumber("TEST000000001")
            .yearsOfPublication(List.of("1979", "2004-09"))
            .documentTypes(List.of("Auf"))
            .dependentReferences(List.of("BUV, 1982, 123-123"))
            .independentReferences(List.of("50 Jahre Betriebs-Berater, 1987, 123-456"))
            .mainTitle("Hauptüberschrift")
            .alternativeHeadline("Dokumentarischer Titel")
            .authors(List.of("Musterfrau, Sabine"))
            .collaborators(List.of("Mustermann, Max"))
            .shortReport("Kurzreferat")
            .outline("Gliederung")
            .build();

    LiteratureSchema literatureSchema = LiteratureSchemaMapper.fromDomain(literature);

    assertThat(literatureSchema.id()).isEqualTo("/v1/literature/TEST000000001");
    assertThat(literatureSchema.inLanguage()).isEqualTo("de");
    assertThat(literatureSchema.documentNumber()).isEqualTo("TEST000000001");
    assertThat(literatureSchema.yearsOfPublication()).containsExactly("1979", "2004-09");
    assertThat(literatureSchema.documentTypes()).containsExactly("Auf");
    assertThat(literatureSchema.dependentReferences()).containsExactly("BUV, 1982, 123-123");
    assertThat(literatureSchema.independentReferences())
        .containsExactly("50 Jahre Betriebs-Berater, 1987, 123-456");
    assertThat(literatureSchema.headline()).isEqualTo("Hauptüberschrift");
    assertThat(literatureSchema.alternativeHeadline()).isEqualTo("Dokumentarischer Titel");
    assertThat(literatureSchema.authors()).containsExactly("Musterfrau, Sabine");
    assertThat(literatureSchema.collaborators()).containsExactly("Mustermann, Max");
    assertThat(literatureSchema.shortReport()).isEqualTo("Kurzreferat");
    assertThat(literatureSchema.outline()).isEqualTo("Gliederung");
  }

  @Test
  @DisplayName("Creates encodings for html, xml and zip")
  void createsEncodings() {
    var literature =
        Literature.builder().id("TEST000000001").documentNumber("TEST000000001").build();

    LiteratureSchema literatureSchema = LiteratureSchemaMapper.fromDomain(literature);

    assertThat(literatureSchema.encoding())
        .containsExactly(
            LiteratureEncodingSchema.builder()
                .id("/v1/literature/TEST000000001/html")
                .contentUrl("/v1/literature/TEST000000001.html")
                .encodingFormat("text/html")
                .inLanguage("de")
                .build(),
            LiteratureEncodingSchema.builder()
                .id("/v1/literature/TEST000000001/xml")
                .contentUrl("/v1/literature/TEST000000001.xml")
                .encodingFormat("application/xml")
                .inLanguage("de")
                .build(),
            LiteratureEncodingSchema.builder()
                .id("/v1/literature/TEST000000001/zip")
                .contentUrl("/v1/literature/TEST000000001.zip")
                .encodingFormat("application/zip")
                .inLanguage("de")
                .build());
  }
}
