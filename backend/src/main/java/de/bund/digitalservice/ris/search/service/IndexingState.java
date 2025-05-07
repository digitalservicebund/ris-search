package de.bund.digitalservice.ris.search.service;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import lombok.With;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@With
public record IndexingState(
    String lastSuccess,
    String startTime,
    String lockTime,
    String currentChangelogFile,
    Integer successIndex) {

  private static final Logger logger = LogManager.getLogger(IndexingState.class);

  public IndexingState() {
    this(null, null, null, null, null);
  }

  // getLastSuccess & getLastSuccessInstant can't be used due to automatic serialization logic
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

  public Instant startTimeInstant() {
    return Instant.parse(startTime);
  }
}
