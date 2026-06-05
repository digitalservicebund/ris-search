package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.repository.objectstorage.AdministrativeDirectiveBucket;
import org.springframework.stereotype.Component;

/** Job for synchronizing the index of administrative directives. */
@Component
public class AdministrativeDirectiveIndexSyncJob extends IndexSyncJob {

  public static final String STATUS_FILENAME = "administrative_directive_status.json";

  /**
   * Scheduled Job to index administrative directive files
   *
   * @param indexStatusService service to manage the status of an index job
   * @param changelogService service to manage changelogs
   * @param service service to index administrative directive files
   */
  public AdministrativeDirectiveIndexSyncJob(
      IndexStatusService indexStatusService,
      ChangelogService<AdministrativeDirectiveBucket> changelogService,
      IndexAdministrativeDirectiveService service) {

    super(indexStatusService, changelogService, service, STATUS_FILENAME);
  }
}
