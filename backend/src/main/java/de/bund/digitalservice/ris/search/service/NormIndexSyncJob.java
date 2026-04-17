package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import org.springframework.stereotype.Component;

/** Job for synchronizing the index of legal norms. */
@Component
public class NormIndexSyncJob extends IndexSyncJob {

  public static final String NORM_STATUS_FILENAME = "norm_status.json";

  public NormIndexSyncJob(
      IndexStatusService indexStatusService,
      ChangelogService changelogService,
      NormsBucket normsBucket,
      IndexNormsService indexNormsService) {
    super(
        indexStatusService, changelogService, normsBucket, indexNormsService, NORM_STATUS_FILENAME);
  }
}
