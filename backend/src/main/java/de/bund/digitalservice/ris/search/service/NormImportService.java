package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import org.springframework.stereotype.Component;

@Component
public class NormImportService extends ImportService {

  public static final String NORM_STATUS_FILENAME = "norm_status.json";

  public NormImportService(
      IndexStatusService indexStatusService,
      NormsBucket normsBucket,
      IndexNormsService indexNormsService) {
    super(indexStatusService, normsBucket, indexNormsService, NORM_STATUS_FILENAME);
  }
}
