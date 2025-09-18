package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.schema.CaseLawEncodingSchema;
import de.bund.digitalservice.ris.search.schema.LegislationObjectSchema;
import de.bund.digitalservice.ris.search.schema.LiteratureEncodingSchema;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.MediaType;

public class EncodingSchemaFactory {
  private EncodingSchemaFactory() {}

  private static final String LANGUAGE = "de";

  private enum SchemaType {
    HTML("html"),
    XML("xml"),
    ZIP("zip");

    public final String value;

    SchemaType(String value) {
      this.value = value;
    }
  }

  private static String id(SchemaType type, String baseUrl) {
    return baseUrl + "/" + type.value;
  }

  private static String contentUrl(SchemaType type, String baseUrl) {
    return baseUrl + "." + type.value;
  }

  private static String encodingFormat(SchemaType type) {
    return switch (type) {
      case HTML -> MediaType.TEXT_HTML_VALUE;
      case XML -> MediaType.APPLICATION_XML_VALUE;
      case ZIP -> "application/zip";
    };
  }

  private static LegislationObjectSchema legislationEncodingSchema(
      SchemaType type, String baseUrl) {
    return LegislationObjectSchema.builder()
        .id(id(type, baseUrl))
        .contentUrl(contentUrl(type, baseUrl))
        .encodingFormat(encodingFormat(type))
        .inLanguage(LANGUAGE)
        .build();
  }

  private static CaseLawEncodingSchema caselawEncodingSchema(SchemaType type, String baseUrl) {
    return CaseLawEncodingSchema.builder()
        .id(id(type, baseUrl))
        .contentUrl(contentUrl(type, baseUrl))
        .encodingFormat(encodingFormat(type))
        .inLanguage(LANGUAGE)
        .build();
  }

  private static LiteratureEncodingSchema literatureEncodingSchema(
      SchemaType type, String baseUrl) {
    return LiteratureEncodingSchema.builder()
        .id(id(type, baseUrl))
        .contentUrl(contentUrl(type, baseUrl))
        .encodingFormat(encodingFormat(type))
        .inLanguage(LANGUAGE)
        .build();
  }

  public static List<LegislationObjectSchema> legislationEncodingSchemas(
      String baseUrl, String zipBaseUrl) {
    return List.of(
        legislationEncodingSchema(SchemaType.HTML, baseUrl),
        legislationEncodingSchema(SchemaType.XML, baseUrl),
        legislationEncodingSchema(SchemaType.ZIP, zipBaseUrl));
  }

  public static List<CaseLawEncodingSchema> caselawEncodingSchemas(String baseUrl) {
    return Arrays.stream(SchemaType.values())
        .map(type -> caselawEncodingSchema(type, baseUrl))
        .toList();
  }

  public static List<LiteratureEncodingSchema> literatureEncodingSchemas(String baseUrl) {
    return Arrays.stream(SchemaType.values())
        .map(type -> literatureEncodingSchema(type, baseUrl))
        .toList();
  }
}
