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
    @JsonProperty("@type") String type,
    @JsonProperty("@id") String id,
    String name,
    String inLanguage,
    String translator,
    String translationOfWork,
    String about,
    @JsonProperty("ris:filename") String filename) {

  public static class LegislationTranslationSchemaBuilder {
    LegislationTranslationSchemaBuilder() {
      type = "Legislation";
    }
  }
}
