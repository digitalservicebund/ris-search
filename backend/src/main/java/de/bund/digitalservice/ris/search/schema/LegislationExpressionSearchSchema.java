package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the schema for a legislation expression search result, providing details about a
 * specific legislative expression within the broader context of a legislation item.
 */
@Builder
@Schema(description = "A legislation expression and references to its manifestations.")
public record LegislationExpressionSearchSchema(
    @JsonProperty("@id")
        @Schema(
            example = ApiConfig.Paths.LEGISLATION + "/eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String id,
    @Schema(
            example = "Verordnung über Kakao und Kakaoerzeugnisse",
            description = "Amtliche Langüberschrift",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
    @Schema(
            example = "eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String legislationIdentifier,
    @Schema(
            description = "the work the expression is based on",
            requiredMode = Schema.RequiredMode.REQUIRED)
        LegislationWorkSchema exampleOfWork,
    @Schema(
            description =
                """
                         Textual string indicating a time period in [ISO 8601 time interval format](https://en.wikipedia.org/wiki/ISO_8601#Time_intervals)
                         """,
            example = "1998-02-06/..",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String temporalCoverage,
    @Nullable @Schema(example = "KakaoV 2003", description = "Amtliche Buchstabenabkürzung")
        String abbreviation,
    @Schema(
            example = "Kakaoverordnung",
            description = "Amtliche Kurzüberschrift",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String alternateName,
    @Schema(
            description = "Whether the legislation expression is currently in force.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        LegalForceStatus legislationLegalForce,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<LegislationObjectSchema> encoding)
    implements AbstractDocumentSchema, JsonldResource {

  @Override
  @Schema(example = "Legislation")
  public String getType() {
    return "Legislation";
  }
}
