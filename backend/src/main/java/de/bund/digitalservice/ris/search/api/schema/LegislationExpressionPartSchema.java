package de.bund.digitalservice.ris.search.api.schema;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldId;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

@Builder
@JsonldResource
@JsonldType("Legislation")
public record LegislationExpressionPartSchema(
    @JsonldId
        @Schema(
            example =
                ApiConfig.Paths.LEGISLATION
                    + "/eli/bund/bgbl-1/1975/s1760/regelungstext-1.xml#hauptteitel-para-1")
        String id,
    @Schema(
            description =
                "Expression-level identifier, uniquely identifying this element in an FRBR expression",
            example = "hauptteitel-para-1")
        String eId,
    @Schema(example = "550e8400-e29b-41d4-a716-446655440000") String guid,
    @Schema(example = "§ 1") String name,
    @Nullable @Schema(example = "true") Boolean isActive,
    @Nullable @Schema(example = "2003-12-15") LocalDate entryIntoForceDate,
    @Nullable @Schema(example = "2003-12-15") LocalDate expiryDate,
    @Nullable @Schema(description = "The source data for this part, if available on its own")
        List<LegislationObjectSchema> encoding) {}
