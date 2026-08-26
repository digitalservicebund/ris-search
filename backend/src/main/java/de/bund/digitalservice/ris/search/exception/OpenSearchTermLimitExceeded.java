package de.bund.digitalservice.ris.search.exception;

/** Is thrown when the nested search term limit is exceeded */
public class OpenSearchTermLimitExceeded extends RuntimeException {

  public OpenSearchTermLimitExceeded() {
    super("search query limit exceeded");
  }
}
