package de.bund.digitalservice.ris.search.models.errors;

import java.io.Serializable;
import lombok.Builder;

/** Represents custom error details. */
@Builder
public record CustomError(String code, String message, String parameter) implements Serializable {}
