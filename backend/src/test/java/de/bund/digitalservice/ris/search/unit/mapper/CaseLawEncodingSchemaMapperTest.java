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
    var source =
        CaseLawDocumentationUnit.builder().id("id1").documentNumber("BFRE000087655").build();

    List<CaseLawEncodingSchema> schemas = CaseLawEncodingSchemaMapper.fromDomain(source);

    assertEquals(3, schemas.size(), "Should have three schemas");

    String base = "/v1/case-law/" + source.documentNumber();

    CaseLawEncodingSchema htmlSchema = schemas.getFirst();
    assertEquals(base + "/html", htmlSchema.id());
    assertEquals(base + ".html", htmlSchema.contentUrl());
    assertEquals("text/html", htmlSchema.encodingFormat());
    assertEquals("de", htmlSchema.inLanguage());

    CaseLawEncodingSchema xmlSchema = schemas.get(1);
    assertEquals(base + "/xml", xmlSchema.id());
    assertEquals(base + ".xml", xmlSchema.contentUrl());
    assertEquals("application/xml", xmlSchema.encodingFormat());
    assertEquals("de", xmlSchema.inLanguage());

    CaseLawEncodingSchema zipSchema = schemas.get(2);
    assertEquals(base + "/zip", zipSchema.id());
    assertEquals(base + ".zip", zipSchema.contentUrl());
    assertEquals("application/zip", zipSchema.encodingFormat());
    assertEquals("de", zipSchema.inLanguage());
  }
}
