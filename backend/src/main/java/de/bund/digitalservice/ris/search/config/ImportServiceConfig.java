package de.bund.digitalservice.ris.search.config;

import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import de.bund.digitalservice.ris.search.service.ImportService;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportServiceConfig {

  public static final String CASELAW_STATUS_FILENAME = "caselaw_status.json";
  public static final String NORM_STATUS_FILENAME = "norm_status.json";

  @Autowired IndexStatusService indexStatusService;
  @Autowired ChangelogService changelogService;
  @Autowired NormsBucket normsBucket;
  @Autowired IndexNormsService indexNormsService;
  @Autowired CaseLawBucket caseLawBucket;
  @Autowired IndexCaselawService indexCaselawService;

  @Bean
  public ImportService caselawImportService() {
    return new ImportService(
        indexStatusService,
        changelogService,
        caseLawBucket,
        indexCaselawService,
        ImportServiceConfig.CASELAW_STATUS_FILENAME);
  }

  @Bean
  public ImportService normImportService() {
    return new ImportService(
        indexStatusService,
        changelogService,
        normsBucket,
        indexNormsService,
        ImportServiceConfig.NORM_STATUS_FILENAME);
  }
}
