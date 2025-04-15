package de.bund.digitalservice.ris.search.schema;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldId;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;
import lombok.Builder;

@Builder
@JsonldResource
@JsonldType("Legislation")
@Schema(
    description =
        "A legislation item, across different expressions and manifestations. May be used to provide context to a `LegislationExpression` (under key `workExample`).")
public record LegislationExpressionSearchSchema(
    @JsonldId
        @Schema(
            example = ApiConfig.Paths.LEGISLATION + "/eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu")
        String id,
    @Schema(example = "eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu") String legislationIdentifier,
    @Schema(
            description =
                """
                 Textual string indicating a time period in [ISO 8601 time interval format](https://en.wikipedia.org/wiki/ISO_8601#Time_intervals)
                 """,
            example = "1998-02-06/..")
        String temporalCoverage,
    @Schema(description = "Whether the legislation expression is currently in force.")
        LegalForceStatus legislationLegalForce) {}
