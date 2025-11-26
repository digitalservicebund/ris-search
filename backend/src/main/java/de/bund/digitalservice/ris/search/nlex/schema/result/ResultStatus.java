package de.bund.digitalservice.ris.search.nlex.schema.result;

/**
 * Represents the status of a result in the application.
 *
 * <p>This is an abstract utility class that defines constants for result status values used across
 * the application.
 *
 * <p>Constants: - {@code OK}: Represents a successful operation. - {@code ERROR}: Represents an
 * error or failure operation.
 *
 * <p>This class cannot be instantiated as it contains a private constructor. It is designed to
 * provide a fixed set of string constants for use in XML schemas or other logic requiring result
 * status values.
 */
public abstract class ResultStatus {
  private ResultStatus() {}

  public static final String OK = "OK";
  public static final String ERROR = "error";
}
