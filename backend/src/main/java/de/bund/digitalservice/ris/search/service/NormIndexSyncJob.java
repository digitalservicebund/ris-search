package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import org.springframework.stereotype.Component;

/** Job for synchronizing the index of legal norms. */
@Component
public class NormIndexSyncJob extends IndexSyncJob {

  public static final String NORM_STATUS_FILENAME = "norm_status.json";

  /**
   * Scheduled Job to index legislation files
   *
   * @param indexStatusService service to manage the status of an index job
   * @param changelogService service to manage changelogs
   * @param normsBucket root bucket for legislation files
   * @param indexNormsService service to index legislation files
   */
  public NormIndexSyncJob(
      IndexStatusService indexStatusService,
      ChangelogService changelogService,
      NormsBucket normsBucket,
      IndexNormsService indexNormsService) {
    super(
        indexStatusService, changelogService, normsBucket, indexNormsService, NORM_STATUS_FILENAME);
  }
}
