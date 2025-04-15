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
