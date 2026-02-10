package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

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
public record LegislationExpressionSearchSchema(
    @JsonProperty("@id")
        @Schema(
            example = ApiConfig.Paths.LEGISLATION + "/eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu")
        String id,
    @Schema(
            example = "Verordnung über Kakao und Kakaoerzeugnisse",
            description = "Amtliche Langüberschrift")
        String name,
    @Schema(description = "the work the expression is based on")
        LegislationWorkSchema exampleOfWork,
    @Schema(example = "eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu") String legislationIdentifier,
    @Schema(
            description =
                """
                         Textual string indicating a time period in [ISO 8601 time interval format](https://en.wikipedia.org/wiki/ISO_8601#Time_intervals)
                         """,
            example = "1998-02-06/..")
        String temporalCoverage,
    @Nullable @Schema(example = "KakaoV 2003", description = "Amtliche Buchstabenabkürzung")
        String abbreviation,
    @Schema(example = "Kakaoverordnung", description = "Amtliche Kurzüberschrift")
        String alternateName,
    @Schema(
            example = "2003-12-15",
            description =
                """
                                Ausfertigungsdatum (The date of adoption or signature of the legislation. This is the date at which the text is officially acknowledged to be a legislation, even though it might not even be published or in force.)
                                """)
        LocalDate legislationDate,
    @Schema(
            example = "2003-12-16",
            description =
                """
                                Verkündungsdatum (The date of first publication of the legislation, when it was published in the official gazette. This may be later than the `legislationDate`.)
                                """)
        LocalDate datePublished,
    @Schema(description = "Whether the legislation expression is currently in force.")
        LegalForceStatus legislationLegalForce)
    implements AbstractDocumentSchema, JsonldResource {

  @Override
  @Schema(example = "Legislation")
  public String getType() {
    return "Legislation";
  }
}
