package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * A DTO for administrative directives in a specific encoding, following schema.org naming
 * guidelines.
 */
@Builder
public record AdministrativeDirectiveEncodingSchema(
    @JsonProperty("@id") String id,
    String contentUrl,
    @Schema(example = "text/html") String encodingFormat,
    @Schema(example = "de") String inLanguage)
    implements JsonldResource {

  @Override
  @Schema(example = "MediaObject")
  public String getType() {
    return "MediaObject";
  }
}
