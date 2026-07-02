package de.bund.digitalservice.ris.search.config;

import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.repository.objectstorage.AdministrativeDirectiveBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PublicFilesBucket;
import de.bund.digitalservice.ris.search.service.BulkExportJob;
import de.bund.digitalservice.ris.search.service.BulkExportService;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Value;
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
  public BulkExportService normsBulkExportService(
      NormsBucket source,
      @Value("${s3.file-storage.norm.versionPrefix}") String versionPrefix,
      PublicFilesBucket target) {
    return new BulkExportService(
        source,
        target,
        DocumentKind.LEGISLATION.getBulkZipPath(),
        versionPrefix + "eli",
        key -> true);
  }

  /**
   * @param source sourceBucket to create the document snapshot from
   * @param target targetBucket to create the archive in
   * @return BulkExportService
   */
  @Bean
  public BulkExportService caseLawBulkExportService(
      CaseLawBucket source,
      @Value("${s3.file-storage.case-law.versionPrefix}") String versionPrefix,
      PublicFilesBucket target) {
    return new BulkExportService(
        source,
        target,
        DocumentKind.CASE_LAW.getBulkZipPath(),
        versionPrefix,
        filterChangelogFiles());
  }

  /**
   * @param source sourceBucket to create the document snapshot from
   * @param target targetBucket to create the archive in
   * @return BulkExportService
   */
  @Bean
  public BulkExportService adminBulkExportService(
      AdministrativeDirectiveBucket source,
      @Value("${s3.file-storage.administrative-directive.versionPrefix}") String versionPrefix,
      PublicFilesBucket target) {
    return new BulkExportService(
        source,
        target,
        DocumentKind.ADMINISTRATIVE_DIRECTIVE.getBulkZipPath(),
        versionPrefix,
        filterChangelogFiles());
  }

  /**
   * @param source sourceBucket to create the document snapshot from
   * @param target targetBucket to create the archive in
   * @return BulkExportService
   */
  @Bean
  public BulkExportService literatureBulkExportService(
      LiteratureBucket source,
      @Value("${s3.file-storage.literature.versionPrefix}") String versionPrefix,
      PublicFilesBucket target) {
    return new BulkExportService(
        source,
        target,
        DocumentKind.LITERATURE.getBulkZipPath(),
        versionPrefix,
        filterChangelogFiles());
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
   * @param caseLawBulkExportService the export service for case law
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

  /**
   * @param adminBulkExportService the export service for admin
   * @param changelogService the changelogService used to observe file changes
   * @param portalBucket portalBucket to store the job state
   * @return BulkExportJob
   */
  @Bean
  public BulkExportJob adminBulkExport(
      BulkExportService adminBulkExportService,
      ChangelogService<AdministrativeDirectiveBucket> changelogService,
      PortalBucket portalBucket) {
    return new BulkExportJob(
        adminBulkExportService,
        portalBucket,
        DocumentKind.ADMINISTRATIVE_DIRECTIVE.getBulkZipPath(),
        changelogService);
  }

  /**
   * @param literatureBulkExportService the export service for literature
   * @param changelogService the changelogService used to observe file changes
   * @param portalBucket portalBucket to store the job state
   * @return BulkExportJob
   */
  @Bean
  public BulkExportJob literatureBulkExport(
      BulkExportService literatureBulkExportService,
      ChangelogService<LiteratureBucket> changelogService,
      PortalBucket portalBucket) {
    return new BulkExportJob(
        literatureBulkExportService,
        portalBucket,
        DocumentKind.LITERATURE.getBulkZipPath(),
        changelogService);
  }

  private Predicate<String> filterChangelogFiles() {
    return key -> !key.startsWith(ChangelogService.CHANGELOGS_PREFIX);
  }
}
