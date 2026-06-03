package de.bund.digitalservice.ris.search.config;

import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.BulkExportService;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import java.util.function.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configurations for bulk export Jobs */
@Configuration
public class BulkExportConfig {

  @Bean
  public BulkExportService normsBulkExport(NormsBucket source, PortalBucket target) {
    return new BulkExportService(source, target, "output", "eli", key -> true);
  }

  @Bean
  public BulkExportService caseLawBulkExport(CaseLawBucket source, PortalBucket target) {
    return new BulkExportService(source, target, "output", "", filterChangelogFiles());
  }

  private Predicate<String> filterChangelogFiles() {
    return key -> !key.startsWith(ChangelogService.CHANGELOGS_PREFIX);
  }
}
