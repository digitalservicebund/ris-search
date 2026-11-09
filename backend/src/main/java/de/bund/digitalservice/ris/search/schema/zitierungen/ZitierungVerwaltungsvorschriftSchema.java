package de.bund.digitalservice.ris.search.schema.zitierungen;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** API schema for a citation of an administrative directive (Verwaltungsvorschrift). */
@Builder
public record ZitierungVerwaltungsvorschriftSchema(
    @Schema(example = "ris:/verwaltungsvorschrift/KSNR008561615") String referenzUri,
    @Schema(example = "KSNR008561615") String dokumentnummer,
    @Schema(example = "Vergleiche") String artDerZitierung,
    @Schema(example = "VR VDR 1997-12-04 5883") String jurisAbkuerzung,
    FundstelleSchema fundstelle) {}
