package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import org.springframework.stereotype.Component;

@Component
public class NormIndexSyncJob extends IndexSyncJob {

  public static final String NORM_STATUS_FILENAME = "norm_status.json";

  public NormIndexSyncJob(
      IndexStatusService indexStatusService,
      NormsBucket normsBucket,
      IndexNormsService indexNormsService) {
    super(indexStatusService, normsBucket, indexNormsService, NORM_STATUS_FILENAME);
  }
}
