package de.bund.digitalservice.ris.search.exception;

public class OpenSearchMapperException extends RuntimeException {
  public OpenSearchMapperException(String message) {
    super(message);
  }

  public OpenSearchMapperException(String message, Exception cause) {
    super(message, cause);
  }
}
