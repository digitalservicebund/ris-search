package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * Represents the schema for a legislation expression search result, providing details about a
 * specific legislative expression within the broader context of a legislation item.
 *
 * <p>This schema is designed to link legislation expressions to their respective works
 * (`workExample` key) and provides information about the
 */
@Builder
@Schema(
    description =
        "A legislation item, across different expressions and manifestations. May be used to provide context to a `LegislationExpression` (under key `workExample`).")
@JsonldType("Legislation")
public record LegislationExpressionSearchSchema(
    @JsonProperty("@type") String type,
    @JsonProperty("@id")
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
        LegalForceStatus legislationLegalForce) {

  public LegislationExpressionSearchSchema {
    type = "Legislation";
  }
}
