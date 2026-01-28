package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

/**
 * Representation of a legislative expression schema, designed to encapsulate metadata about a
 * specific expression or version of a legislative document. This is aligned with schema.org
 * definitions and the ELI ontology, allowing for structured representation of legislative data.
 *
 * <p>The class includes identifiers, temporal coverage, legal force status, table of contents,
 * parts, and encodings related to a legislative expression. It provides a foundation for working
 * with legislative data across various contexts.
 *
 * <p>Annotations such as `@JsonldType` and `@Schema` are used to accord semantic and documentation
 * support. Components like `workExample` in other legislative schemas may use this schema for
 * contextual linking.
 *
 * <p>Fields:
 *
 * <p>- `id`: Unique identifier for the legislative expression, typically an ELI-compliant URI. -
 * `legislationIdentifier`: Identifier for the legislative expression, usually derived from its
 * underlying ELI. - `temporalCoverage`: Defines the relevant time period using ISO 8601 time
 * interval format. - `legislationLegalForce`: Indicates whether the expression is currently in
 * force. Values may include "in force", "not in force", or "partially in force". -
 * `tableOfContents`: Optional list of entries defining the hierarchical structure of the
 * legislative document. - `hasPart`: Optional list of legislative expression parts representing
 * components like articles or attachments that form a part of this document. - `encoding`: Optional
 * representations of this expression in multiple formats (e.g., HTML, XML).
 */
@Builder
@Schema(
    description =
        "A legislation item, across different expressions and manifestations. May be used to provide context to a `LegislationExpression` (under key `workExample`).")
public record LegislationExpressionSchema(
    @JsonProperty("@id")
        @Schema(
            example = ApiConfig.Paths.LEGISLATION + "/eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu")
        String id,
    @Schema(
            example = "Verordnung über Kakao und Kakaoerzeugnisse",
            description = "Amtliche Langüberschrift")
        String name,
    @Nullable @Schema(example = "KakaoV 2003", description = "Amtliche Buchstabenabkürzung")
        String abbreviation,
    @Schema(example = "Kakaoverordnung", description = "Amtliche Kurzüberschrift")
        String alternateName,
    @Nullable PublicationIssueSchema isPartOf,
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
    @Schema(example = "eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu") String legislationIdentifier,
    @Schema(
            description =
                """
                         Textual string indicating a time period in [ISO 8601 time interval format](https://en.wikipedia.org/wiki/ISO_8601#Time_intervals)
                         """,
            example = "1998-02-06/..")
        String temporalCoverage,
    @Schema(description = "Whether the legislation expression is currently in force.")
        LegalForceStatus legislationLegalForce,
    @Nullable List<TableOfContentsSchema> tableOfContents,
    @Schema(
            description =
                "List of components (articles, preambles, conclusions, attachments, …) that form this legislation item.")
        @Nullable
        List<LegislationExpressionPartSchema> hasPart,
    @Nullable List<LegislationObjectSchema> encoding)
    implements JsonldResource {

  @Override
  @Schema(example = "Legislation")
  public String getType() {
    return "Legislation";
  }
}
