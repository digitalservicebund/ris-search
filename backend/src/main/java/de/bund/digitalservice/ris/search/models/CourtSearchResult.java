package de.bund.digitalservice.ris.search.models;

import io.swagger.v3.oas.annotations.media.Schema;

public record CourtSearchResult(
    @Schema(example = "BGH Karlsruhe") String id,
    @Schema(example = "10000") long count,
    @Schema(example = "Bundesgerichtshof Karlsruhe") String label) {}
