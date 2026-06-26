package de.bund.digitalservice.ris.search.integration.jobs;

import static de.bund.digitalservice.ris.search.service.BulkExportService.JOB_STATE_STORAGE_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.BulkExportJob;
import de.bund.digitalservice.ris.search.service.BulkExportService;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import de.bund.digitalservice.ris.search.service.Job;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("integration")
class BulkExportJobTest extends ContainersIntegrationBase {

  @Autowired PortalBucket portalBucket;

  @Qualifier("normsBulkExport")
  @Autowired
  BulkExportJob bulkExportJob;

  @Qualifier("normsBulkExportService")
  @MockitoSpyBean
  BulkExportService bulkExportService;

  @Autowired private NormsBucket normsBucket;

  @Test
  void runJob_returnsEarlyWhenNoChangesAreDetected() {
    portalBucket.save(
        JOB_STATE_STORAGE_PREFIX + DocumentKind.LEGISLATION.getBulkZipPath(),
        Instant.now().minus(25, ChronoUnit.HOURS).toString());
    assertThat(bulkExportJob.runJob()).isEqualTo(Job.ReturnCode.SUCCESS);
    verify(bulkExportService, never()).updateLatestZip(any());
  }

  @Test
  void runJob_returnsEarlyWhenBeforeRegenerationTime() {
    portalBucket.save(
        JOB_STATE_STORAGE_PREFIX + DocumentKind.LEGISLATION.getBulkZipPath(),
        Instant.now().minus(1, ChronoUnit.HOURS).toString());
    normsBucket.save(
        ChangelogService.CHANGELOGS_PREFIX + Instant.now().toString(),
        "{\"changed\":[\"something.xml\"]}");

    assertThat(bulkExportJob.runJob()).isEqualTo(Job.ReturnCode.SUCCESS);
    verify(bulkExportService, never()).updateLatestZip(any());
  }

  @Test
  void runJob_triesToRecreateOnDetectedDeletion() {
    portalBucket.save(
        JOB_STATE_STORAGE_PREFIX + DocumentKind.LEGISLATION.getBulkZipPath(),
        Instant.now().minus(1, ChronoUnit.HOURS).toString());
    normsBucket.save(
        ChangelogService.CHANGELOGS_PREFIX + Instant.now().toString(),
        "{\"deleted\":[\"something.xml\"]}");

    assertThat(bulkExportJob.runJob()).isEqualTo(Job.ReturnCode.SUCCESS);
    verify(bulkExportService, times(1)).deleteArchives();
    verify(bulkExportService, times(1)).updateLatestZip(any());
  }
}
