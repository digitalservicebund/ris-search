package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.PersistedIndexingState;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
public class IndexStatusServiceTest extends ContainersIntegrationBase {
  @Autowired private PortalBucket portalBucket;
  @Autowired private IndexStatusService indexStatusService;

  @Test
  void saveAndloadStatusTest1() throws ObjectStoreServiceException {
    PersistedIndexingState testData =
        new PersistedIndexingState("lastSuccess", "lockTime", "currentChangelogFile", 10);
    indexStatusService.saveStatus("testFile.json", testData);
    PersistedIndexingState result = indexStatusService.loadStatus("testFile.json");
    assertThat(result).isEqualTo(testData);
  }

  @Test
  void saveAndloadStatusTest2() throws ObjectStoreServiceException {
    PersistedIndexingState testData = new PersistedIndexingState(null, null, null, null);
    indexStatusService.saveStatus("testFile.json", testData);
    PersistedIndexingState result = indexStatusService.loadStatus("testFile.json");
    assertThat(result).isEqualTo(testData);
  }

  @Test
  void loadMissingStatusFile() throws ObjectStoreServiceException {
    PersistedIndexingState result = indexStatusService.loadStatus("testFile2.json");
    assertThat(result).isNull();
  }

  @Test
  void loadStatusFileMissingFields() throws ObjectStoreServiceException {
    portalBucket.save("testFile.json", "{\"lastSuccess\" : \"lastSuccess\"}");
    PersistedIndexingState result = indexStatusService.loadStatus("testFile.json");
    PersistedIndexingState expected = new PersistedIndexingState("lastSuccess", null, null, null);
    assertThat(result).isEqualTo(expected);
  }
}
