package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.repository.objectstorage.AdministrativeDirectiveBucket;
import org.springframework.stereotype.Component;

/** Job for synchronizing the index of administrative directives. */
@Component
public class AdministrativeDirectiveIndexSyncJob extends IndexSyncJob {

  public static final String STATUS_FILENAME = "administrative_directive_status.json";

  public AdministrativeDirectiveIndexSyncJob(
      IndexStatusService indexStatusService,
      AdministrativeDirectiveBucket bucket,
      IndexAdministrativeDirectiveService service) {
    super(indexStatusService, bucket, service, STATUS_FILENAME);
  }
}
