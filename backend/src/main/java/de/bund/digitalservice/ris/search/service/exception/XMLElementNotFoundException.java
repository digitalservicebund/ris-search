package de.bund.digitalservice.ris.search.service.exception;

public class XMLElementNotFoundException extends RuntimeException {
  public XMLElementNotFoundException(String message) {
    super(message);
  }

  public XMLElementNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
