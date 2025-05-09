package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import org.springframework.stereotype.Component;

@Component
public class NormImportService extends ImportService {

  public static final String NORM_STATUS_FILENAME = "norm_status.json";

  public NormImportService(
      IndexStatusService indexStatusService,
      ChangelogService changelogService,
      NormsBucket normsBucket,
      IndexNormsService indexNormsService) {
    super(
        indexStatusService, changelogService, normsBucket, indexNormsService, NORM_STATUS_FILENAME);
  }
}
