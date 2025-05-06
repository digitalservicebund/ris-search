package de.bund.digitalservice.ris.search.integration.importer.caselaw;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.IndexingState;
import de.bund.digitalservice.ris.search.repository.objectstorage.PersistedIndexingState;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawSynthesizedRepository;
import de.bund.digitalservice.ris.search.service.ImportService;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.utils.CaseLawLdmlTemplateUtils;
import java.io.IOException;
import org.junit.AfterClass;
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
  @Autowired CaseLawSynthesizedRepository caseLawSynthesizedRepository;

  @Autowired CaseLawBucket caseLawBucket;
  @Autowired PortalBucket portalBucket;
  @Autowired IndexCaselawService indexCaselawService;
  @Autowired ImportService importService;
  @Autowired IndexStatusService indexStatusService;
  private final CaseLawLdmlTemplateUtils caseLawLdmlTemplateUtils = new CaseLawLdmlTemplateUtils();

  private final String oldTimestamp = "2000-01-01T00:00:00Z";

  @AfterEach
  void cleanUp() {
    portalBucket.delete(ImportService.CASELAW_STATUS_FILENAME);
  }

  @AfterClass
  public void cleanUpClass() {
    caseLawBucket.close();
    portalBucket.close();
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
    PersistedIndexingState persistedIndexingState =
        new PersistedIndexingState(null, oldTimestamp, null, null);
    indexStatusService.saveStatus(ImportService.CASELAW_STATUS_FILENAME, persistedIndexingState);
  }

  @Test
  void createsLastSuccessFileProperly() throws ObjectStoreServiceException {
    Assertions.assertEquals(5, caseLawBucket.getAllKeys().size());
    IndexingState state =
        new IndexingState(
            caseLawBucket, ImportService.CASELAW_STATUS_FILENAME, indexCaselawService);
    importService.lockAndImportChangelogs(state);
    Assertions.assertEquals(5, caseLawBucket.getAllKeys().size());
    PersistedIndexingState result =
        indexStatusService.loadStatus(ImportService.CASELAW_STATUS_FILENAME);
    Assertions.assertNotNull(result.lastSuccessInstant());
  }

  @Test
  void testLocking() throws ObjectStoreServiceException {
    IndexingState state =
        new IndexingState(
            caseLawBucket, ImportService.CASELAW_STATUS_FILENAME, indexCaselawService);
    state.setPersistedIndexingState(indexStatusService.loadStatus(state.getStatusFileName()));
    boolean locked = indexStatusService.lockIndex(state);
    Assertions.assertTrue(locked);
    PersistedIndexingState result =
        indexStatusService.loadStatus(ImportService.CASELAW_STATUS_FILENAME);
    Assertions.assertEquals(state.getStartTime().toString(), result.lockTime());
  }
}
