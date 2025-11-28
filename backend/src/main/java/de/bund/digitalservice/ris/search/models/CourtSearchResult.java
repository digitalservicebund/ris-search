package de.bund.digitalservice.ris.search.models;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A record representing the result of a court search.
 *
 * <p>This class encapsulates information about a court, including its ID, the count of results
 * associated with the court, and a label providing a detailed description of the court.
 *
 * @param id The unique identifier of the court.
 * @param count The number of matches or results associated with the court search.
 * @param label A descriptive label for the court, providing additional context or detail.
 */
public record CourtSearchResult(
    @Schema(example = "BGH Karlsruhe") String id,
    @Schema(example = "10000") long count,
    @Schema(example = "Bundesgerichtshof Karlsruhe") String label) {}
