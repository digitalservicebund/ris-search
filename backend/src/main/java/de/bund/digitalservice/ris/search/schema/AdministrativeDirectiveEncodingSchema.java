package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * A DTO for administrative directives in a specific encoding, following schema.org naming
 * guidelines.
 */
@Builder
@JsonldType("MediaObject")
public record AdministrativeDirectiveEncodingSchema(
    @JsonProperty("@id") String id,
    String contentUrl,
    @Schema(example = "text/html") String encodingFormat,
    @Schema(example = "de") String inLanguage,
    @JsonProperty("@type") String type) {

  public AdministrativeDirectiveEncodingSchema(
      String id, String contentUrl, String encodingFormat, String inLanguage) {
    this(id, contentUrl, encodingFormat, inLanguage, "MediaObject");
  }

  public static class AdministrativeDirectiveEncodingSchemaBuilder {
    AdministrativeDirectiveEncodingSchemaBuilder() {
      type = "MediaObject";
    }
  }
}
