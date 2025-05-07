package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.ImportService;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexingState;
import de.bund.digitalservice.ris.search.service.PersistedIndexingState;
import java.time.Instant;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class IndexStatusServiceTest extends ContainersIntegrationBase {
  @Autowired private PortalBucket portalBucket;
  @Autowired private CaseLawBucket caseLawBucket;
  @Autowired private IndexStatusService indexStatusService;
  @Autowired private IndexCaselawService indexCaselawService;

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
    assertThat(result).isEqualTo(new PersistedIndexingState());
  }

  @Test
  void loadStatusFileMissingFields() throws ObjectStoreServiceException {
    portalBucket.save("testFile.json", "{\"lastSuccess\" : \"lastSuccess\"}");
    PersistedIndexingState result = indexStatusService.loadStatus("testFile.json");
    PersistedIndexingState expected = new PersistedIndexingState("lastSuccess", null, null, null);
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void normalLockingWorks() throws ObjectStoreServiceException {
    Instant startTime = Instant.now();
    IndexingState state =
        new IndexingState(
            caseLawBucket, ImportService.CASELAW_STATUS_FILENAME, indexCaselawService);
    PersistedIndexingState persistedState = new PersistedIndexingState(null, null, null, null);
    state.setPersistedIndexingState(persistedState);
    state.setStartTime(startTime);
    indexStatusService.lockIndex(state);
    PersistedIndexingState result =
        indexStatusService.loadStatus(ImportService.CASELAW_STATUS_FILENAME);
    String lockTime = result.lockTime();
    assertThat(lockTime).isEqualTo(startTime.toString());
    portalBucket.delete(ImportService.CASELAW_STATUS_FILENAME);
  }

  @Test
  void alreadyLockedWorksAsExpected() {
    Instant startTime = Instant.now();
    IndexingState state =
        new IndexingState(
            caseLawBucket, ImportService.CASELAW_STATUS_FILENAME, indexCaselawService);
    PersistedIndexingState persistedState = new PersistedIndexingState(null, null, null, null);
    state.setPersistedIndexingState(persistedState);
    state.setStartTime(startTime);
    boolean locked = indexStatusService.lockIndex(state);
    assertThat(locked).isTrue();

    locked = indexStatusService.lockIndex(state);
    assertThat(locked).isFalse();
    portalBucket.delete(ImportService.CASELAW_STATUS_FILENAME);
  }
}
