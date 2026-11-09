package de.bund.digitalservice.ris.search.schema.zitierungen;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** API schema for where a cited document was published. */
@Builder
public record FundstelleSchema(
    @Schema(example = "NVwZ", description = "Abkürzung des Periodikums") String periodikum,
    @Schema(example = "2016, 1139-1141") String zitatstelle,
    @Schema(example = "amtlich") String fundstellenTyp) {}
