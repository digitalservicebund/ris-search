package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.schema.DocumentEncodingSchema;
import de.bund.digitalservice.ris.search.schema.LegislationObjectSchema;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.MediaType;

/**
 * Factory class for creating various encoding schema objects, such as for legislation, case law,
 * literature, and administrative directives in different formats (HTML, XML, ZIP). This class
 * provides static methods to generate lists of schemas based on provided base URLs and predefined
 * formats.
 *
 * <p>The class is designed to operate as a utility and cannot be instantiated.
 */
public class EncodingSchemaFactory {
  private EncodingSchemaFactory() {}

  private static final String LANGUAGE = "de";

  /** valid schemaTypes to appear in an encoding array */
  public enum SchemaType {
    HTML("html"),
    XML("xml"),
    ZIP("zip");

    public final String value;

    SchemaType(String value) {
      this.value = value;
    }
  }

  public static String id(SchemaType type, String baseUrl) {
    return baseUrl + "/" + type.value;
  }

  public static String contentUrl(SchemaType type, String baseUrl) {
    return baseUrl + "." + type.value;
  }

  private static String encodingFormat(SchemaType type) {
    return switch (type) {
      case HTML -> MediaType.TEXT_HTML_VALUE;
      case XML -> MediaType.APPLICATION_XML_VALUE;
      case ZIP -> "application/zip";
    };
  }

  /**
   * Generates a legislation encoding schema based on the provided base URL and SchemaType
   *
   * @param type the SchemaType for the requested encoding schema
   * @param baseUrl the base URL to be used for constructing the encoding schema attributes for HTML
   *     and XML formats
   * @return {@code LegislationObjectSchema} according to the given SchemaType
   */
  public static LegislationObjectSchema legislationEncodingSchema(SchemaType type, String baseUrl) {
    return LegislationObjectSchema.builder()
        .id(id(type, baseUrl))
        .contentUrl(contentUrl(type, baseUrl))
        .encodingFormat(encodingFormat(type))
        .inLanguage(LANGUAGE)
        .build();
  }

  private static DocumentEncodingSchema documentEncodingSchema(SchemaType type, String baseUrl) {
    return DocumentEncodingSchema.builder()
        .id(id(type, baseUrl))
        .contentUrl(contentUrl(type, baseUrl))
        .encodingFormat(encodingFormat(type))
        .inLanguage(LANGUAGE)
        .build();
  }

  /**
   * Generates a list of legislation encoding schemas based on the provided base URLs for different
   * formats.
   *
   * @param baseUrl the base URL to be used for constructing the encoding schema attributes for HTML
   *     and XML formats
   * @param zipBaseUrl the base URL to be used for constructing the encoding schema attributes for
   *     ZIP format
   * @return a list of {@code LegislationObjectSchema} representing the encoding schemas for
   *     legislation in various formats
   */
  public static List<LegislationObjectSchema> legislationEncodingSchemas(
      String baseUrl, String zipBaseUrl) {
    return List.of(
        legislationEncodingSchema(SchemaType.HTML, baseUrl),
        legislationEncodingSchema(SchemaType.XML, baseUrl),
        legislationEncodingSchema(SchemaType.ZIP, zipBaseUrl));
  }

  /**
   * Generates a list of document encoding schemas based on the provided base URL.
   *
   * @param baseUrl the base URL to be used for constructing the encoding schema attributes
   * @return a list of {@code DocumentEncodingSchema} representing the encoding schemas for
   *     documents
   */
  public static List<DocumentEncodingSchema> documentEncodingSchemas(String baseUrl) {
    return Arrays.stream(SchemaType.values())
        .map(type -> documentEncodingSchema(type, baseUrl))
        .toList();
  }
}
