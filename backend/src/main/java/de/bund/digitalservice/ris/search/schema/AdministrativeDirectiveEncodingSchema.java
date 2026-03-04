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
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @JsonProperty("@id") String id,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String contentUrl,
    @Schema(example = "text/html", requiredMode = Schema.RequiredMode.REQUIRED)
        String encodingFormat,
    @Schema(example = "de", requiredMode = Schema.RequiredMode.REQUIRED) String inLanguage)
    implements JsonldResource {

  @Override
  @Schema(example = "MediaObject")
  public String getType() {
    return "MediaObject";
  }
}
