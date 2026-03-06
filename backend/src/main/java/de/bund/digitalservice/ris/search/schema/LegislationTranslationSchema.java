package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** A DTO for legislation translations, following schema.org naming guidelines. */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Schema(description = "A translation of a legislation item")
public record LegislationTranslationSchema(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @JsonProperty("@id") String id,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String name,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String inLanguage,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String translator,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String translationOfWork,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String about,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @JsonProperty("ris:filename")
        String filename)
    implements JsonldResource {

  @Override
  @Schema(example = "Legislation")
  public String getType() {
    return "Legislation";
  }
}
