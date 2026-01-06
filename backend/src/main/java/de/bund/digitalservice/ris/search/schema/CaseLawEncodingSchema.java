package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** A DTO for court decisions in a specific encoding, following schema.org naming guidelines. */
@Builder
public record CaseLawEncodingSchema(
    @JsonProperty("@id") String id,
    String contentUrl,
    @Schema(example = "text/html") String encodingFormat,
    @Schema(example = "de") String inLanguage)
    implements JsonldResource {

  @Override
  @Schema(example = "DecisionObject")
  public String getType() {
    return "DecisionObject";
  }
}
