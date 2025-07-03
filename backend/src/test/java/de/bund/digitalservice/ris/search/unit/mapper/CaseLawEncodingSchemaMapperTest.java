package de.bund.digitalservice.ris.search.unit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.bund.digitalservice.ris.search.mapper.CaseLawEncodingSchemaMapper;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.schema.CaseLawEncodingSchema;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CaseLawEncodingSchemaMapperTest {
  @Test
  @DisplayName("correctly creates case law encoding schemas")
  void fromDomain() {
    // Given
    var source =
        CaseLawDocumentationUnit.builder().id("id1").documentNumber("BFRE000087655").build();

    // When
    List<CaseLawEncodingSchema> schemas = CaseLawEncodingSchemaMapper.fromDomain(source);

    // Then
    assertEquals(3, schemas.size(), "Should have three schemas");

    String base = "/v1/case-law/" + source.documentNumber();

    // HTML Schema
    CaseLawEncodingSchema htmlSchema = schemas.getFirst();
    assertEquals(htmlSchema.id(), base + "/html");
    assertEquals(htmlSchema.contentUrl(), base + ".html");
    assertEquals(htmlSchema.encodingFormat(), "text/html");
    assertEquals(htmlSchema.inLanguage(), "de");

    // XML Schema
    CaseLawEncodingSchema xmlSchema = schemas.get(1);
    assertEquals(xmlSchema.id(), base + "/xml");
    assertEquals(xmlSchema.contentUrl(), base + ".xml");
    assertEquals(xmlSchema.encodingFormat(), "application/xml");
    assertEquals(xmlSchema.inLanguage(), "de");

    // ZIP Schema
    CaseLawEncodingSchema zipSchema = schemas.get(2);
    assertEquals(zipSchema.id(), base + "/zip");
    assertEquals(zipSchema.contentUrl(), base + ".zip");
    assertEquals(zipSchema.encodingFormat(), "application/zip");
    assertEquals(zipSchema.inLanguage(), "de");
  }
}
