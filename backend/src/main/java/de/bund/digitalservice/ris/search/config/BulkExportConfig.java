package de.bund.digitalservice.ris.search.config;

import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PublicFilesBucket;
import de.bund.digitalservice.ris.search.service.BulkExportService;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import java.util.function.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configurations for bulk export Jobs */
@Configuration
public class BulkExportConfig {

  /**
   * @param source the source bucket
   * @param target the target bucket
   * @return the normsBulkExport bean
   */
  @Bean
  public BulkExportService normsBulkExport(NormsBucket source, PublicFilesBucket target) {
    return new BulkExportService(source, target, "legislation", "eli", key -> true);
  }

  /**
   * @param source the source bucket
   * @param target the target bucket
   * @return the caseLawBulkExport bean
   */
  @Bean
  public BulkExportService caseLawBulkExport(CaseLawBucket source, PublicFilesBucket target) {
    return new BulkExportService(source, target, "case-law", "", filterChangelogFiles());
  }

  private Predicate<String> filterChangelogFiles() {
    return key -> !key.startsWith(ChangelogService.CHANGELOGS_PREFIX);
  }
}
