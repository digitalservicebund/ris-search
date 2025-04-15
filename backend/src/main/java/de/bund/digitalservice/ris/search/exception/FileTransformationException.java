package de.bund.digitalservice.ris.search.exception;

public class FileTransformationException extends RuntimeException {

  public FileTransformationException(String message) {
    super(message);
  }

  public FileTransformationException(String message, Throwable cause) {
    super(message, cause);
  }
}
