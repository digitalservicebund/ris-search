package de.bund.digitalservice.ris.search.exception;

import lombok.Getter;

/** Is thrown when the search term limit is exceeded */
@Getter
public class OpenSearchTermLimitExceeded extends RuntimeException {
  private final int actualTermCount;
  private final int limit;

  /**
   * @param actualTermCount that was determined by the opensearch analyzer
   * @param limit that is configured
   */
  public OpenSearchTermLimitExceeded(int actualTermCount, int limit) {
    super("search term limit exceeded");
    this.actualTermCount = actualTermCount;
    this.limit = limit;
  }
}
