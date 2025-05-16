package de.bund.digitalservice.ris.search.service;

import java.time.Instant;
import lombok.With;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@With
public record IndexingState(String lastProcessedChangelogFile, String startTime, String lockTime) {

  private static final Logger logger = LogManager.getLogger(IndexingState.class);

  public IndexingState() {
    this(null, null, null);
  }

  public Instant startTimeInstant() {
    return Instant.parse(startTime);
  }
}
