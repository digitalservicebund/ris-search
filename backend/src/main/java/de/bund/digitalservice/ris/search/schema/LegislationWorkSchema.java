package de.bund.digitalservice.ris.search.schema;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldId;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;
import java.time.LocalDate;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

@Builder
@JsonldResource
@JsonldType("Legislation")
@Schema(
    description =
        "A legislation item, across different expressions and manifestations. May be used to provide context to a `LegislationExpression` (under key `workExample`).")
public record LegislationWorkSchema(
    @JsonldId
        @Schema(
            example = ApiConfig.Paths.LEGISLATION + "/eli/bund/bgbl-1/1975/s1760",
            description = "Based on the European Legislation Identifier (ELI)")
        String id,
    @Schema(
            example = "Verordnung über Kakao und Kakaoerzeugnisse",
            description = "Amtliche Langüberschrift")
        String name,
    @Schema(
            example = "eli/bund/bgbl-1/1975/s1760/regelungstext-1",
            description = "European Legislation Identifier (ELI)")
        String legislationIdentifier,
    @Schema(example = "Kakaoverordnung", description = "Amtliche Kurzüberschrift")
        String alternateName,
    @Nullable @Schema(example = "KakaoV 2003", description = "Amtliche Buchstabenabkürzung")
        String abbreviation,
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
    @Nullable PublicationIssueSchema isPartOf,
    @Schema(
            description =
                "Expression-level details (<i>an \"exemplary\" expression of this work</i>)")
        @Nullable
        LegislationExpressionSchema workExample) {}
