package de.bund.digitalservice.ris.search.schema.zitierungen;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** API schema for a citation of unselbstständige (dependent) literature. */
@Builder
public record ZitierungLiteraturUnselbststaendigSchema(
    @Schema(example = "ris:/literatur/SBLU000539216") String referenzUri,
    @Schema(example = "SBLU000539216") String dokumentnummer,
    @Schema(example = "Knopp, Lothar") String autor,
    @Schema(example = "Aufsatz") String dokumenttyp,
    FundstelleSchema fundstelle) {}
