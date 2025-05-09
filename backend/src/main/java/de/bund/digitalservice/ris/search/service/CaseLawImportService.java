package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import org.springframework.stereotype.Component;

@Component
public class CaseLawImportService extends ImportService {

  public static final String CASELAW_STATUS_FILENAME = "caselaw_status.json";

  public CaseLawImportService(
      IndexStatusService indexStatusService,
      ChangelogService changelogService,
      CaseLawBucket caseLawBucket,
      IndexCaselawService indexCaselawService) {
    super(
        indexStatusService,
        changelogService,
        caseLawBucket,
        indexCaselawService,
        CASELAW_STATUS_FILENAME);
  }
}
