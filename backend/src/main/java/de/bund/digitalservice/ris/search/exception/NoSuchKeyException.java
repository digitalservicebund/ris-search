package de.bund.digitalservice.ris.search.exception;

public class NoSuchKeyException extends ObjectStoreServiceException {
  public NoSuchKeyException(String message, Throwable cause) {
    super(message, cause);
  }
}
