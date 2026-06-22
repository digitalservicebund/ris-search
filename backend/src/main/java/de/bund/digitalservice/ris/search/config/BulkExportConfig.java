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
   * @param changelogService the changelogService used to observe file changes
   * @param portalBucket portalBucket to store the job state
   * @return BulkExportJob
   */
  @Bean
  public BulkExportJob normsBulkExport(
      NormsBucket source,
      PublicFilesBucket target,
      ChangelogService<NormsBucket> changelogService,
      PortalBucket portalBucket) {
    return new BulkExportJob(
        new BulkExportService(
            source, target, DocumentKind.LEGISLATION.getBulkZipPath(), "eli", key -> true),
        portalBucket,
        "legislation",
        changelogService);
  }

  /**
   * @param source sourceBucket to create the document snapshot from
   * @param target targetBucket to create the archive in
   * @param changelogService the changelogService used to observe file changes
   * @param portalBucket portalBucket to store the job state
   * @return BulkExportJob
   */
  @Bean
  public BulkExportJob caseLawBulkExport(
      CaseLawBucket source,
      PublicFilesBucket target,
      ChangelogService<CaseLawBucket> changelogService,
      PortalBucket portalBucket) {
    return new BulkExportJob(
        new BulkExportService(
            source, target, DocumentKind.CASE_LAW.getBulkZipPath(), "", filterChangelogFiles()),
        portalBucket,
        "legislation",
        changelogService);
  }

  private Predicate<String> filterChangelogFiles() {
    return key -> !key.startsWith(ChangelogService.CHANGELOGS_PREFIX);
  }
}
