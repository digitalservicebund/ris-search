package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldId;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;
import lombok.Builder;

/** A DTO for legislation translations, following schema.org naming guidelines. */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@JsonldResource
@JsonldType("Legislation")
@Schema(description = "A translation of a legislation item")
public record LegislationTranslationSchema(
    @JsonldId String id,
    String name,
    String inLanguage,
    String translator,
    String translationOfWork,
    String about,
    @JsonProperty("ris:filename") String filename) {}
