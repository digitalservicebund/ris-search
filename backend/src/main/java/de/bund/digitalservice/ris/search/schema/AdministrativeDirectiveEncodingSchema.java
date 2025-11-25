package de.bund.digitalservice.ris.search.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldId;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;
import lombok.Builder;

/**
 * A DTO for administrative directives in a specific encoding, following schema.org naming
 * guidelines.
 */
@Builder
@JsonldResource
@JsonldType("MediaObject")
public record AdministrativeDirectiveEncodingSchema(
    @JsonldId String id,
    String contentUrl,
    @Schema(example = "text/html") String encodingFormat,
    @Schema(example = "de") String inLanguage) {}
