package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.repository.objectstorage.literature.LiteratureBucket;
import org.springframework.stereotype.Component;

@Component
public class LiteratureIndexSyncJob extends IndexSyncJob {

  public static final String LITERATURE_STATUS_FILENAME = "literature_status.json";

  public LiteratureIndexSyncJob(
      IndexStatusService indexStatusService,
      LiteratureBucket literatureBucket,
      IndexLiteratureService indexLiteratureService) {
    super(indexStatusService, literatureBucket, indexLiteratureService, LITERATURE_STATUS_FILENAME);
  }
}
