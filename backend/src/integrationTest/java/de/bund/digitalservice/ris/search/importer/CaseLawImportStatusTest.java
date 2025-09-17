package de.bund.digitalservice.ris.search.importer;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexingState;
import de.bund.digitalservice.ris.search.setup.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.utils.CaseLawLdmlTemplateUtils;
import java.io.IOException;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Tag("integration")
class CaseLawImportStatusTest extends ContainersIntegrationBase {
  @Autowired CaseLawRepository caseLawRepository;

  @Autowired CaseLawBucket caseLawBucket;
  @Autowired PortalBucket portalBucket;
  @Autowired IndexCaselawService indexCaselawService;
  @Autowired CaseLawIndexSyncJob caseLawIndexSyncJob;

  @Autowired IndexStatusService indexStatusService;
  private final CaseLawLdmlTemplateUtils caseLawLdmlTemplateUtils = new CaseLawLdmlTemplateUtils();

  @AfterEach
  void cleanUp() {
    portalBucket.delete(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME);
  }

  @BeforeEach
  void setUp() {
    for (int i = 0; i < 5; i++) {
      try {
        caseLawBucket.save(
            "caseLawTestLdml" + i + ".xml", caseLawLdmlTemplateUtils.getXmlFromTemplate(null));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    String oldTimestamp = "2000-01-01T00:00:00Z";
    IndexingState indexingState = new IndexingState(null, null, oldTimestamp);
    indexStatusService.saveStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME, indexingState);
  }

  @Test
  void createsLastSuccessFileProperly() throws ObjectStoreServiceException {
    Assertions.assertEquals(5, caseLawBucket.getAllKeys().size());
    caseLawIndexSyncJob.runJob();
    Assertions.assertEquals(5, caseLawBucket.getAllKeys().size());
    IndexingState result =
        indexStatusService.loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME);
    Assertions.assertNotNull(result.lastProcessedChangelogFile());
  }

  @Test
  void testLocking() throws ObjectStoreServiceException {
    IndexingState state =
        indexStatusService
            .loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME)
            .withStartTime(Instant.now().toString());
    boolean locked =
        indexStatusService.lockIndex(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME, state);
    Assertions.assertTrue(locked);
    IndexingState result =
        indexStatusService.loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME);
    Assertions.assertEquals(state.startTime(), result.lockTime());
  }
}
