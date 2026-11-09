package de.bund.digitalservice.ris.search.schema.zitierungen;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** API schema for a citation of selbstständige (independent) literature. */
@Builder
public record ZitierungLiteraturSelbststaendigSchema(
    @Schema(example = "ris:/literatur/KSLS071671727") String referenzUri,
    @Schema(example = "KSLS071671727") String dokumentnummer,
    @Schema(example = "Arnauld, Andreas von") String autor,
    @Schema(example = "Die Freiheitsrechte und ihre Schranken") String titel,
    @Schema(example = "1999") String veroeffentlichungsjahr) {}
