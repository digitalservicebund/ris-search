package de.bund.digitalservice.ris.search.service;

import java.time.Instant;
import lombok.With;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the state of an indexing process, holding relevant metadata such as the last processed
 * changelog file, the starting timestamp of the indexing process, and a timestamp indicating when
 * the indexing was locked.
 */
@With
public record IndexingState(
    @Nullable String lastProcessedChangelogFile, String startTime, String lockTime) {

  public IndexingState() {
    this(null, null, null);
  }

  public Instant startTimeInstant() {
    return Instant.parse(startTime);
  }
}
