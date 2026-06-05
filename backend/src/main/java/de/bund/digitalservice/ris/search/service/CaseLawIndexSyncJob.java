package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import org.springframework.stereotype.Component;

/** Job for synchronizing the index of case law. */
@Component
public class CaseLawIndexSyncJob extends IndexSyncJob {

  public static final String CASELAW_STATUS_FILENAME = "caselaw_status.json";

  /**
   * Scheduled Job to index caselaw files
   *
   * @param indexStatusService service to manage the status of an index job
   * @param changelogService service to manage changelogs
   * @param indexCaselawService service to index caselawfiles
   */
  public CaseLawIndexSyncJob(
      IndexStatusService indexStatusService,
      ChangelogService<CaseLawBucket> changelogService,
      IndexCaselawService indexCaselawService) {
    super(indexStatusService, changelogService, indexCaselawService, CASELAW_STATUS_FILENAME);
  }
}
