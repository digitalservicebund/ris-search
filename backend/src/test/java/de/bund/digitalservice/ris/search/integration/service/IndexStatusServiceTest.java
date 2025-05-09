package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.CaseLawImportService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexingState;
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
  @Autowired private IndexStatusService indexStatusService;

  @Test
  void saveAndloadStatusTest1() throws ObjectStoreServiceException {
    Instant time = Instant.now();
    IndexingState testData =
        new IndexingState("lastSuccess", time.toString(), "lockTime", "currentChangelogFile", 10);
    indexStatusService.saveStatus("testFile.json", testData);
    IndexingState result = indexStatusService.loadStatus("testFile.json");
    assertThat(result).isEqualTo(testData);
  }

  @Test
  void saveAndloadStatusTest2() throws ObjectStoreServiceException {
    IndexingState testData = new IndexingState();
    indexStatusService.saveStatus("testFile.json", testData);
    IndexingState result = indexStatusService.loadStatus("testFile.json");
    assertThat(result).isEqualTo(testData);
  }

  @Test
  void loadMissingStatusFile() throws ObjectStoreServiceException {
    IndexingState result = indexStatusService.loadStatus("testFile2.json");
    assertThat(result).isEqualTo(new IndexingState());
  }

  @Test
  void loadStatusFileMissingFields() throws ObjectStoreServiceException {
    portalBucket.save("testFile.json", "{\"lastSuccess\" : \"lastSuccess\"}");
    IndexingState result = indexStatusService.loadStatus("testFile.json");
    IndexingState expected = new IndexingState().withLastSuccess("lastSuccess");
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void normalLockingWorks() throws ObjectStoreServiceException {
    Instant startTime = Instant.now();
    IndexingState state = new IndexingState().withStartTime(startTime.toString());
    indexStatusService.lockIndex(CaseLawImportService.CASELAW_STATUS_FILENAME, state);
    IndexingState result =
        indexStatusService.loadStatus(CaseLawImportService.CASELAW_STATUS_FILENAME);
    String lockTime = result.lockTime();
    assertThat(lockTime).isEqualTo(startTime.toString());
    portalBucket.delete(CaseLawImportService.CASELAW_STATUS_FILENAME);
  }

  @Test
  void alreadyLockedWorksAsExpected() throws ObjectStoreServiceException {
    Instant startTime = Instant.now();
    IndexingState state = new IndexingState().withStartTime(startTime.toString());
    boolean locked =
        indexStatusService.lockIndex(CaseLawImportService.CASELAW_STATUS_FILENAME, state);
    assertThat(locked).isTrue();

    IndexingState newState =
        indexStatusService.loadStatus(CaseLawImportService.CASELAW_STATUS_FILENAME);

    locked = indexStatusService.lockIndex(CaseLawImportService.CASELAW_STATUS_FILENAME, newState);
    assertThat(locked).isFalse();
    portalBucket.delete(CaseLawImportService.CASELAW_STATUS_FILENAME);
  }
}
