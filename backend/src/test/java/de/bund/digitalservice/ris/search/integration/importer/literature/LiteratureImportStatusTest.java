package de.bund.digitalservice.ris.search.integration.importer.literature;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexingState;
import de.bund.digitalservice.ris.search.service.LiteratureIndexSyncJob;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Tag("integration")
class LiteratureImportStatusTest extends ContainersIntegrationBase {

  @Autowired LiteratureBucket literatureBucket;
  @Autowired PortalBucket portalBucket;

  @Autowired LiteratureIndexSyncJob literatureIndexSyncJob;

  @Autowired IndexStatusService indexStatusService;

  @AfterEach
  void cleanUp() {
    portalBucket.delete(LiteratureIndexSyncJob.LITERATURE_STATUS_FILENAME);
  }

  @BeforeEach
  void setUp() {
    String oldTimestamp = "2000-01-01T00:00:00Z";
    IndexingState indexingState = new IndexingState(null, null, oldTimestamp);
    indexStatusService.saveStatus(LiteratureIndexSyncJob.LITERATURE_STATUS_FILENAME, indexingState);
  }

  @Test
  void createsLastSuccessFileProperly() throws ObjectStoreServiceException {
    assertThat(literatureBucket.getAllKeys()).hasSize(2);
    literatureIndexSyncJob.runJob();
    assertThat(literatureBucket.getAllKeys()).hasSize(2);
    IndexingState result =
        indexStatusService.loadStatus(LiteratureIndexSyncJob.LITERATURE_STATUS_FILENAME);
    assertThat(result.lastProcessedChangelogFile()).isNotNull();
  }

  @Test
  void testLocking() throws ObjectStoreServiceException {
    IndexingState state =
        indexStatusService
            .loadStatus(LiteratureIndexSyncJob.LITERATURE_STATUS_FILENAME)
            .withStartTime(Instant.now().toString());
    boolean locked =
        indexStatusService.lockIndex(LiteratureIndexSyncJob.LITERATURE_STATUS_FILENAME, state);
    assertThat(locked).isTrue();
    IndexingState result =
        indexStatusService.loadStatus(LiteratureIndexSyncJob.LITERATURE_STATUS_FILENAME);
    assertThat(state.startTime()).isEqualTo(result.lockTime());
  }
}
