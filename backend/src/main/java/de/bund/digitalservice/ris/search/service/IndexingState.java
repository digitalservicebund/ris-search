package de.bund.digitalservice.ris.search.service;

import java.time.Instant;
import lombok.With;
import org.jetbrains.annotations.Nullable;

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
