package de.bund.digitalservice.ris.search.models.errors;

import java.util.List;
import lombok.Builder;

/** Represents custom error response. */
@Builder
public record CustomErrorResponse(List<CustomError> errors) {}
