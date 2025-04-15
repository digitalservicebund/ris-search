package de.bund.digitalservice.ris.search.exception;

public class RetryableObjectStoreException extends Exception {

  public RetryableObjectStoreException(String message, Throwable cause) {
    super(message, cause);
  }
}
