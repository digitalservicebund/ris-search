package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.SharedTestConstants;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexingState;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class IndexStatusServiceTest extends ContainersIntegrationBase {
  @Autowired private PortalBucket portalBucket;
  @Autowired private IndexStatusService indexStatusService;

  @Test
  void saveAndloadStatusTest1() throws ObjectStoreServiceException {
    IndexingState testData =
        new IndexingState(
            "lastProcessedChangelogFile", SharedTestConstants.TIMESTAMP_2024_01_01_AS_STRING);
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
    portalBucket.save(
        "testFile.json", "{\"lastProcessedChangelogFile\" : \"lastProcessedChangelogFile\"}");
    IndexingState result = indexStatusService.loadStatus("testFile.json");
    IndexingState expected =
        new IndexingState().withLastProcessedChangelogFile("lastProcessedChangelogFile");
    assertThat(result).isEqualTo(expected);
  }
}
