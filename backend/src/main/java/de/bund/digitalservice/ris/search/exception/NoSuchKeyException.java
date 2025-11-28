package de.bund.digitalservice.ris.search.exception;

/** Exception thrown when a requested key does not exist. */
public class NoSuchKeyException extends Exception {
  public NoSuchKeyException(String message, Throwable cause) {
    super(message, cause);
  }
}
