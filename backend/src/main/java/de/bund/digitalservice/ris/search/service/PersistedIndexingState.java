package de.bund.digitalservice.ris.search.service;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import lombok.With;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * PersistedIndexingState holds the state of the last indexing process.
 *
 * @param lastSuccess The timestamp of the last successful indexing in ISO-8601 format.
 * @param lockTime The timestamp of when the indexing process was locked in ISO-8601 format.
 * @param currentChangelogFile The name of the current changelog file being processed.
 * @param successIndex The index of the last successful change in the changelog.
 */
@With
public record PersistedIndexingState(
    String lastSuccess, String lockTime, String currentChangelogFile, Integer successIndex) {

  private static final Logger logger = LogManager.getLogger(PersistedIndexingState.class);

  public PersistedIndexingState() {
    this(null, null, null, null);
  }

  /**
   * Returns the last successful indexing time as an Instant. note: getLastSuccess &
   * getLastSuccessInstant can't be used due to automatic serialization logic
   *
   * @return Instant of last successful indexing or null if not available
   */
  public Instant lastSuccessInstant() {
    if (lastSuccess == null) {
      return null;
    } else {
      try {
        return Instant.parse(lastSuccess);
      } catch (DateTimeParseException e) {
        logger.error("Status file has an invalid format.");
        return null;
      }
    }
  }
}
