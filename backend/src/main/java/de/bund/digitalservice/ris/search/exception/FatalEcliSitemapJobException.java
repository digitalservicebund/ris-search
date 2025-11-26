package de.bund.digitalservice.ris.search.exception;

/** Exception thrown when a error occurs during the ECLI sitemap job. */
public class FatalEcliSitemapJobException extends RuntimeException {
  public FatalEcliSitemapJobException(String message) {
    super(message);
  }
}
