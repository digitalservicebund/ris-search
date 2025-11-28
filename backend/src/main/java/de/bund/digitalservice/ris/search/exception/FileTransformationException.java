package de.bund.digitalservice.ris.search.exception;

/** Exception thrown when a file transformation fails. */
public class FileTransformationException extends RuntimeException {

  public FileTransformationException(String message) {
    super(message);
  }

  public FileTransformationException(String message, Throwable cause) {
    super(message, cause);
  }
}
