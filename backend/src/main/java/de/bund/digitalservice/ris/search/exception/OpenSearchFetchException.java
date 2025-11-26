package de.bund.digitalservice.ris.search.exception;

/** Exception thrown when fetching data from OpenSearch fails. */
public class OpenSearchFetchException extends RuntimeException {
  public OpenSearchFetchException(String message) {
    super(message);
  }

  public OpenSearchFetchException(String message, Exception cause) {
    super(message, cause);
  }
}
