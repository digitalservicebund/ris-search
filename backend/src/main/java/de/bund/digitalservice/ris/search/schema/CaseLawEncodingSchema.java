package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** A DTO for court decisions in a specific encoding, following schema.org naming guidelines. */
@Builder
@JsonldType("DecisionObject")
public record CaseLawEncodingSchema(
    @JsonProperty("@type") String type,
    @JsonProperty("@id") String id,
    String contentUrl,
    @Schema(example = "text/html") String encodingFormat,
    @Schema(example = "de") String inLanguage) {

  /** add default type to lombok builder pattern */
  public static class CaseLawEncodingSchemaBuilder {
    CaseLawEncodingSchemaBuilder() {
      type = "DecisionObject";
    }
  }
}
