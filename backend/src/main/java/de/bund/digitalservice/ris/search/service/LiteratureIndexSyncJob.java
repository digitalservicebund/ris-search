package de.bund.digitalservice.ris.search.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Job for synchronizing the index of literature. */
@Component
public class LiteratureIndexSyncJob extends IndexSyncJob {

  public static final String LITERATURE_STATUS_FILENAME = "literature_status.json";

  /**
   * Scheduled Job to index literature files
   *
   * @param indexStatusService service to manage the status of an index job
   * @param changelogService service to manage changelogs
   * @param indexLiteratureService service to index literature files
   */
  public LiteratureIndexSyncJob(
      IndexStatusService indexStatusService,
      @Qualifier("literatureChangelogService") ChangelogService changelogService,
      IndexLiteratureService indexLiteratureService) {
    super(indexStatusService, changelogService, indexLiteratureService, LITERATURE_STATUS_FILENAME);
  }
}
