package de.bund.digitalservice.ris.search.config;

import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import de.bund.digitalservice.ris.search.service.ImportService;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportServiceConfig {

  public static final String CASELAW_STATUS_FILENAME = "caselaw_status.json";
  public static final String NORM_STATUS_FILENAME = "norm_status.json";

  final IndexStatusService indexStatusService;
  final ChangelogService changelogService;

  public ImportServiceConfig(
      IndexStatusService indexStatusService, ChangelogService changelogService) {
    this.indexStatusService = indexStatusService;
    this.changelogService = changelogService;
  }

  @Bean
  public ImportService caselawImportService(
      CaseLawBucket caseLawBucket, IndexCaselawService indexCaselawService) {
    return new ImportService(
        indexStatusService,
        changelogService,
        caseLawBucket,
        indexCaselawService,
        ImportServiceConfig.CASELAW_STATUS_FILENAME);
  }

  @Bean
  public ImportService normImportService(
      NormsBucket normsBucket, IndexNormsService indexNormsService) {
    return new ImportService(
        indexStatusService,
        changelogService,
        normsBucket,
        indexNormsService,
        ImportServiceConfig.NORM_STATUS_FILENAME);
  }
}
