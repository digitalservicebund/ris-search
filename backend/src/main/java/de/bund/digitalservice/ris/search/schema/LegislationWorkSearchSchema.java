package de.bund.digitalservice.ris.search.schema;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldId;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;
import java.time.LocalDate;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a legislation item in various expressions and manifestations. Commonly used to provide
 * context to instances of `LegislationExpression`, typically associated under the key
 * `workExample`.
 *
 * <ul>
 *   <li><b>id</b>: The unique identifier for the legislation item.
 *   <li><b>name</b>: The full official name of the legislation.
 *   <li><b>legislationIdentifier</b>: Identifier for the legislation, typically part of its
 *       citation or URI structure.
 *   <li><b>alternateName</b>: An alternative name or shorthand for the legislation item.
 *   <li><b>abbreviation</b>: Abbreviated name of the legislation, if available.
 *   <li><b>legislationDate</b>: The date on which the legislation was formally adopted or signed.
 *       Even if not immediately effective, this date represents when it attained its legal status.
 *   <li><b>datePublished</b>: The date the legislation was first published, typically in the form
 *       of its release in official gazettes. This may differ from the legislation adoption date.
 *   <li><b>isPartOf</b>: Reference to the publication issue schema where this legislation is part
 *       of. Useful for linking to official publications.
 *   <li><b>workExample</b>: An optional detail providing further context or specific expressions of
 *       this legislation item, particularly in different manifestations or localized versions.
 * </ul>
 *
 * This class provides structured metadata that enables usage in APIs or metadata-driven systems. It
 * adheres to JSON-LD representation under the type `Legislation`.
 */
@Builder
@JsonldResource
@JsonldType("Legislation")
@Schema(
    description =
        "A legislation item, across different expressions and manifestations. May be used to provide context to a `LegislationExpression` (under key `workExample`).")
public record LegislationWorkSearchSchema(
    @JsonldId @Schema(example = ApiConfig.Paths.LEGISLATION + "/eli/bund/bgbl-1/1975/s1760")
        String id,
    @Schema(example = "Verordnung über Kakao und Kakaoerzeugnisse") String name,
    @Schema(example = "eli/bund/bgbl-1/1975/s1760") String legislationIdentifier,
    @Schema(example = "Kakaoverordnung") String alternateName,
    @Nullable @Schema(example = "KakaoV 2003") String abbreviation,
    @Schema(
            example = "2003-12-15",
            description =
                """
            The date of adoption or signature of the legislation. This is the date at which the text is officially acknowledged to be a legislation, even though it might not even be published or in force.
            """)
        LocalDate legislationDate,
    @Schema(
            example = "2003-12-16",
            description =
                """
                The date of first publication of the legislation, when it was published in the official gazette. This may be later than the `legislationDate`.""
                """)
        LocalDate datePublished,
    @Nullable PublicationIssueSchema isPartOf,
    @Nullable LegislationExpressionSearchSchema workExample)
    implements AbstractDocumentSchema {}
