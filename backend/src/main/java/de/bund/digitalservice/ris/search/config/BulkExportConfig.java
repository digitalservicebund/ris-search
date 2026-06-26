package de.bund.digitalservice.ris.search.config;

import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PublicFilesBucket;
import de.bund.digitalservice.ris.search.service.BulkExportJob;
import de.bund.digitalservice.ris.search.service.BulkExportService;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import java.util.function.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configurations for bulk export Jobs */
@Configuration
public class BulkExportConfig {

  /**
   * @param source sourceBucket to create the document snapshot from
   * @param target targetBucket to create the archive in
   * @return BulkExportService
   */
  @Bean
  public BulkExportService normsBulkExportService(NormsBucket source, PublicFilesBucket target) {
    return new BulkExportService(
        source, target, DocumentKind.LEGISLATION.getBulkZipPath(), "eli", key -> true);
  }

  /**
   * @param source sourceBucket to create the document snapshot from
   * @param target targetBucket to create the archive in
   * @return BulkExportService
   */
  @Bean
  public BulkExportService caseLawBulkExportService(
      CaseLawBucket source, PublicFilesBucket target) {
    return new BulkExportService(
        source, target, DocumentKind.CASE_LAW.getBulkZipPath(), "", filterChangelogFiles());
  }

  /**
   * @param normsBulkExportService the export service for norms
   * @param changelogService the changelogService used to observe file changes
   * @param portalBucket portalBucket to store the job state
   * @return BulkExportJob
   */
  @Bean
  public BulkExportJob normsBulkExport(
      BulkExportService normsBulkExportService,
      ChangelogService<NormsBucket> changelogService,
      PortalBucket portalBucket) {
    return new BulkExportJob(
        normsBulkExportService,
        portalBucket,
        DocumentKind.LEGISLATION.getBulkZipPath(),
        changelogService);
  }

  /**
   * @param caseLawBulkExportService the export service for norms
   * @param changelogService the changelogService used to observe file changes
   * @param portalBucket portalBucket to store the job state
   * @return BulkExportJob
   */
  @Bean
  public BulkExportJob caseLawBulkExport(
      BulkExportService caseLawBulkExportService,
      ChangelogService<CaseLawBucket> changelogService,
      PortalBucket portalBucket) {
    return new BulkExportJob(
        caseLawBulkExportService,
        portalBucket,
        DocumentKind.CASE_LAW.getBulkZipPath(),
        changelogService);
  }

  private Predicate<String> filterChangelogFiles() {
    return key -> !key.startsWith(ChangelogService.CHANGELOGS_PREFIX);
  }
}
