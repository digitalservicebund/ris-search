package de.bund.digitalservice.ris.search.exception;

/** Exception thrown when mapping data to OpenSearch fails. */
public class OpenSearchMapperException extends RuntimeException {
  public OpenSearchMapperException(String message) {
    super(message);
  }

  public OpenSearchMapperException(String message, Exception cause) {
    super(message, cause);
  }
}
