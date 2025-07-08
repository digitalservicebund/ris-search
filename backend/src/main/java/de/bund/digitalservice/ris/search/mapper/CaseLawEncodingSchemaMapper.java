package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.schema.CaseLawEncodingSchema;
import java.util.List;
import org.springframework.http.MediaType;

public class CaseLawEncodingSchemaMapper {
  private CaseLawEncodingSchemaMapper() {}

  public static List<CaseLawEncodingSchema> fromDomain(CaseLawDocumentationUnit entity) {
    String base = ApiConfig.Paths.CASELAW + "/" + entity.documentNumber();
    var htmlObjectSchema =
        CaseLawEncodingSchema.builder()
            .id(base + "/html")
            .contentUrl(base + ".html")
            .encodingFormat(MediaType.TEXT_HTML_VALUE)
            .inLanguage("de")
            .build();

    var xmlObjectSchema =
        CaseLawEncodingSchema.builder()
            .id(base + "/xml")
            .contentUrl(base + ".xml")
            .encodingFormat(MediaType.APPLICATION_XML_VALUE)
            .inLanguage("de")
            .build();

    var zipObjectSchema =
        CaseLawEncodingSchema.builder()
            .id(base + "/zip")
            .contentUrl(base + ".zip")
            .encodingFormat("application/zip")
            .inLanguage("de")
            .build();

    return List.of(htmlObjectSchema, xmlObjectSchema, zipObjectSchema);
  }
}
