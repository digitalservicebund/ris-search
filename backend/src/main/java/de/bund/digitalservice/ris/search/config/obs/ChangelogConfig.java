package de.bund.digitalservice.ris.search.config.obs;

import static de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME;
import static de.bund.digitalservice.ris.search.service.LiteratureIndexSyncJob.LITERATURE_STATUS_FILENAME;
import static de.bund.digitalservice.ris.search.service.NormIndexSyncJob.NORM_STATUS_FILENAME;

import de.bund.digitalservice.ris.search.repository.objectstorage.AdministrativeDirectiveBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.AdministrativeDirectiveIndexSyncJob;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Registering ChangelogService Beans for specific document types */
@Configuration
public class ChangelogConfig {

  /**
   * @param bucket root bucket of case law files
   * @param indexStatusService IndexStatusService to manage status file handling
   * @return ChangelogService configured to manage case law files
   */
  @Bean
  public ChangelogService caseLawChangelogService(
      CaseLawBucket bucket, IndexStatusService indexStatusService) {
    return new ChangelogService(bucket, indexStatusService, CASELAW_STATUS_FILENAME);
  }

  /**
   * @param bucket root bucket of case law files
   * @param indexStatusService IndexStatusService to manage status file handling
   * @return IndexedChangelogService configured to manage legislation files
   */
  @Bean
  public ChangelogService normsChangelogService(
      NormsBucket bucket, IndexStatusService indexStatusService) {
    return new ChangelogService(bucket, indexStatusService, NORM_STATUS_FILENAME);
  }

  /**
   * @param bucket root bucket of case law files
   * @param indexStatusService IndexStatusService to manage status file handling
   * @return IndexedChangelogService configured to manage literature files
   */
  @Bean
  public ChangelogService literatureChangelogService(
      LiteratureBucket bucket, IndexStatusService indexStatusService) {
    return new ChangelogService(bucket, indexStatusService, LITERATURE_STATUS_FILENAME);
  }

  /**
   * @param bucket root bucket of case law files
   * @param indexStatusService IndexStatusService to manage status file handling
   * @return IndexedChangelogService configured to manage literature files
   */
  @Bean
  public ChangelogService administrativeDirectiveChangelogService(
      AdministrativeDirectiveBucket bucket, IndexStatusService indexStatusService) {
    return new ChangelogService(
        bucket, indexStatusService, AdministrativeDirectiveIndexSyncJob.STATUS_FILENAME);
  }
}
