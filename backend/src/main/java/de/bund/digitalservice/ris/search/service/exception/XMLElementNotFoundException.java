package de.bund.digitalservice.ris.search.service.exception;

/** Exception thrown when a required XML element is not found during processing. */
public class XMLElementNotFoundException extends RuntimeException {
  public XMLElementNotFoundException(String message) {
    super(message);
  }

  public XMLElementNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
