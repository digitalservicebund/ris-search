package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.schema.AdministrativeDirectiveEncodingSchema;
import de.bund.digitalservice.ris.search.schema.CaseLawEncodingSchema;
import de.bund.digitalservice.ris.search.schema.LegislationObjectSchema;
import de.bund.digitalservice.ris.search.schema.LiteratureEncodingSchema;
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

  private static AdministrativeDirectiveEncodingSchema administrativeDirectiveEncodingSchema(
      SchemaType type, String baseUrl) {
    return AdministrativeDirectiveEncodingSchema.builder()
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
   * Generates a list of case law encoding schemas based on the provided base URL.
   *
   * @param baseUrl the base URL to be used for constructing the encoding schema attributes
   * @return a list of {@code CaseLawEncodingSchema} representing the encoding schemas for case law
   *     in various formats
   */
  public static List<CaseLawEncodingSchema> caselawEncodingSchemas(String baseUrl) {
    return Arrays.stream(SchemaType.values())
        .map(type -> caselawEncodingSchema(type, baseUrl))
        .toList();
  }

  /**
   * Generates a list of literature encoding schemas based on the provided base URL.
   *
   * @param baseUrl the base URL to be used for constructing the encoding schema attributes
   * @return a list of {@code LiteratureEncodingSchema} representing the encoding schemas for
   *     literature in various formats
   */
  public static List<LiteratureEncodingSchema> literatureEncodingSchemas(String baseUrl) {
    return Arrays.stream(SchemaType.values())
        .map(type -> literatureEncodingSchema(type, baseUrl))
        .toList();
  }

  /**
   * Generates a list of encoding schemas for administrative directives based on the provided base
   * URL.
   *
   * @param baseUrl the base URL to be used for constructing the encoding schema attributes
   * @return a list of {@code AdministrativeDirectiveEncodingSchema} representing the encoding
   *     schemas for administrative directives in various formats
   */
  public static List<AdministrativeDirectiveEncodingSchema> administrativeDirectiveEncodingSchemas(
      String baseUrl) {
    return Arrays.stream(SchemaType.values())
        .map(type -> administrativeDirectiveEncodingSchema(type, baseUrl))
        .toList();
  }
}
