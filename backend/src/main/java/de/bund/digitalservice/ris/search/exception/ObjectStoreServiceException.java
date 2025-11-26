package de.bund.digitalservice.ris.search.exception;

/** Exception thrown when an error occurs in the Object Store Service. */
public class ObjectStoreServiceException extends Exception {

  public ObjectStoreServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public ObjectStoreServiceException(String message) {
    super(message);
  }
}
