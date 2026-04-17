package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import org.springframework.stereotype.Component;

/** Job for synchronizing the index of literature. */
@Component
public class LiteratureIndexSyncJob extends IndexSyncJob {

  public static final String LITERATURE_STATUS_FILENAME = "literature_status.json";

  public LiteratureIndexSyncJob(
      IndexStatusService indexStatusService,
      ChangelogService changelogService,
      LiteratureBucket bucket,
      IndexLiteratureService indexLiteratureService) {
    super(
        indexStatusService,
        changelogService,
        bucket,
        indexLiteratureService,
        LITERATURE_STATUS_FILENAME);
  }
}
