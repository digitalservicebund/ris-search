package de.bund.digitalservice.ris.search.repository.objectstorage;

import de.bund.digitalservice.ris.search.service.IndexService;
import java.time.Instant;
import lombok.Data;

@Data
public class IndexingState {

  private ObjectStorage changelogBucket;
  private String statusFileName;
  private PersistedIndexingState persistedIndexingState;
  private IndexService indexService;
  private Instant startTime;

  public IndexingState(
      ObjectStorage changelogBucket, String statusFileName, IndexService indexService) {
    this.changelogBucket = changelogBucket;
    this.statusFileName = statusFileName;
    this.indexService = indexService;
    this.startTime = Instant.now();
  }
}
