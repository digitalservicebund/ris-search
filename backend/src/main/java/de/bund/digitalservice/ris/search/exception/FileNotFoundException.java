package de.bund.digitalservice.ris.search.exception;

/** Exception thrown when a file is not found. */
public class FileNotFoundException extends RuntimeException {
  public FileNotFoundException(String message) {
    super(message);
  }
}
