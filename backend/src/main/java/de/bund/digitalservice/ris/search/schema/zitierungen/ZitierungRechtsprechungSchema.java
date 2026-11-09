package de.bund.digitalservice.ris.search.schema.zitierungen;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;

/** API schema for a citation of a case law document. */
@Builder
public record ZitierungRechtsprechungSchema(
    @Schema(example = "ris:/rechtsprechung/BVRE100338409") String referenzUri,
    @Schema(example = "BVRE100338409") String dokumentnummer,
    @Schema(example = "Vergleiche") String artDerZitierung,
    GerichtSchema gericht,
    LocalDate entscheidungsdatum,
    @Schema(example = "Beschluss") String dokumenttyp,
    @Schema(example = "Vf. 94-VI-81") String aktenzeichen,
    FundstelleSchema fundstelle) {}
