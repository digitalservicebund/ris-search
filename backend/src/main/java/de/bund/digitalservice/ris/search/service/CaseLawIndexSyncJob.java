package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import org.springframework.stereotype.Component;

/** Job for synchronizing the index of case law. */
@Component
public class CaseLawIndexSyncJob extends IndexSyncJob {

  public static final String CASELAW_STATUS_FILENAME = "caselaw_status.json";

  public CaseLawIndexSyncJob(
      IndexStatusService indexStatusService,
      ChangelogService changelogService,
      CaseLawBucket bucket,
      IndexCaselawService indexCaselawService) {
    super(
        indexStatusService, changelogService, bucket, indexCaselawService, CASELAW_STATUS_FILENAME);
  }
}
