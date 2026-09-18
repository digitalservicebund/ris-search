package de.bund.digitalservice.ris.search.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.bund.digitalservice.ris.SharedTestConstants;
import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.controller.api.testData.CaseLawTestData;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IndexCaselawServiceIntegrationTest extends ContainersIntegrationBase {

  @Autowired IndexCaselawService service;
  @Autowired CaseLawBucket caseLawBucket;

  @BeforeEach
  void setUpSearchControllerApiTest() {
    clearBuckets();
    loadDefaultFiles();
  }

  @Test
  void reindexAllIgnoresInvalidFiles() throws IOException, ObjectStoreServiceException {
    caseLawBucket.save(
        "file1.xml", CaseLawTestData.simpleCaseLawXml(Map.of("documentNumber", "TEST080020093")));
    caseLawBucket.save("file2.xml", "this will not parse");

    String startingTimestamp = SharedTestConstants.TIMESTAMP_2024_01_01_AS_STRING;
    this.service.reindexAll(startingTimestamp);

    assertThat(caseLawRepository.count()).isEqualTo(1);
    assertThat(caseLawRepository.findByDocumentNumberKeyword("TEST080020093")).hasSize(1);
    verify(caseLawRepository, times(1)).deleteByIndexedAtBefore(startingTimestamp);
  }

  @Test
  void changelogWithOneCaseLawindexesProperly() throws IOException, ObjectStoreServiceException {
    caseLawBucket.save(
        "TEST080020093.xml",
        CaseLawTestData.simpleCaseLawXml(Map.of("documentNumber", "TEST080020093")));

    Changelog changelog = new Changelog();
    changelog.setChanged(Sets.newHashSet(List.of("TEST080020093.xml")));
    service.indexChangelog(changelog);
    assertThat(caseLawRepository.count()).isEqualTo(1);
    assertThat(caseLawRepository.findByDocumentNumberKeyword("TEST080020093")).hasSize(1);
  }

  @Test
  void changelogWithReindexAllWorksProperly() throws IOException, ObjectStoreServiceException {
    caseLawBucket.save(
        "TEST080020093.xml",
        CaseLawTestData.simpleCaseLawXml(Map.of("documentNumber", "TEST080020093")));

    Changelog changelog = new Changelog();
    changelog.setChangeAll(true);
    service.indexChangelog(changelog);
    assertThat(caseLawRepository.count()).isEqualTo(1);
    assertThat(caseLawRepository.findByDocumentNumberKeyword("TEST080020093")).hasSize(1);
  }

  @Test
  void doesNotIndexNonXmlFilesListedInChangelog() throws IOException, ObjectStoreServiceException {
    caseLawBucket.save(
        "TEST080020093/TEST080020093.xml",
        CaseLawTestData.simpleCaseLawXml(Map.of("documentNumber", "TEST080020093")));
    caseLawBucket.save("TEST080020093/picture.png", "mockPngData");

    Changelog changelog = new Changelog();
    changelog.setChanged(
        new HashSet<>(Set.of("TEST080020093/TEST080020093.xml", "TEST080020093/picture.png")));
    service.indexChangelog(changelog);

    assertThat(caseLawRepository.count()).isEqualTo(1);
    assertThat(caseLawRepository.findByDocumentNumberKeyword("TEST080020093")).hasSize(1);
  }

  @Test
  void itCanDeleteFromOneSpecificChangelog() throws ObjectStoreServiceException {
    Changelog changelog = new Changelog();
    changelog.setDeleted(
        new HashSet<>(Set.of("TEST080020093.xml", "TEST080020094/TEST080020094.xml")));
    service.indexChangelog(changelog);

    verify(caseLawRepository, times(1)).deleteAllById(Set.of("TEST080020093", "TEST080020094"));
  }

  @Test
  void itReturnsRightNumberOfFiles() {
    caseLawBucket.save("TEST080020093/TEST080020093.xml", "");
    caseLawBucket.save("TEST080020093/TEST080020094.xml", "");
    caseLawBucket.save("changelogs/2025-03-26T14:13:34.096304815Z-caselaw.json", "");
    assertThat(service.getNumberOfIndexableDocumentsInBucket()).isEqualTo(2);
  }
}
