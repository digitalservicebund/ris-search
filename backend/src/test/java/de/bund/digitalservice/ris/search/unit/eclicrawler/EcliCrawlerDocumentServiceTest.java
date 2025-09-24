package de.bund.digitalservice.ris.search.unit.eclicrawler;

import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.eclicrawler.mapper.EcliCrawlerDocumentMapper;
import de.bund.digitalservice.ris.search.eclicrawler.model.EcliCrawlerDocument;
import de.bund.digitalservice.ris.search.eclicrawler.repository.EcliCrawlerDocumentRepository;
import de.bund.digitalservice.ris.search.eclicrawler.service.EcliCrawlerDocumentService;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EcliCrawlerDocumentServiceTest {

  @Mock EcliCrawlerDocumentRepository repository;
  @Mock CaseLawIndexSyncJob syncJob;
  @Mock CaseLawBucket caseLawBucket;
  @Mock CaseLawService caselawService;

  EcliCrawlerDocumentService documentService;

  @BeforeEach
  void setup() {
    documentService =
        new EcliCrawlerDocumentService(caseLawBucket, repository, caselawService, "frontend-url/");
  }

  private CaseLawDocumentationUnit getTestDocUnit(String docNumber) {
    return CaseLawDocumentationUnit.builder()
        .documentNumber(docNumber)
        .ecli("ECLI:DE:XX:2025:1111111")
        .courtType("BGH")
        .decisionDate(LocalDate.of(2025, 1, 1))
        .documentType("type")
        .build();
  }

  private EcliCrawlerDocument getTestDocument(String docNumber, boolean isPublished) {
    return new EcliCrawlerDocument(
        docNumber,
        docNumber + ".xml",
        "ECLI:DE:XX:2025:1111111",
        "BGH",
        "2025-01-01",
        "type",
        "frontend-url/case-law/" + docNumber,
        isPublished);
  }

  @Test
  void itCreatesAFullDiffWithAnEmptyRepository() throws ObjectStoreServiceException {
    var testUnit = getTestDocUnit("docNumber");
    when(caseLawBucket.getAllKeys()).thenReturn(List.of("key1.xml"));
    when(caselawService.getFromBucket("key1.xml")).thenReturn(Optional.of(testUnit));

    var expectedDocument =
        EcliCrawlerDocumentMapper.fromCaseLawDocumentationUnit(
            "frontend-url/case-law/", "key1.xml", testUnit);
    var actualDocument = documentService.getFullDiff().getFirst();

    Assertions.assertEquals(expectedDocument, actualDocument);
  }

  @Test
  void itCreatesChangesFromChangelog() throws ObjectStoreServiceException {
    var testUnit = getTestDocUnit("docNumber");
    var deletedDoc = getTestDocument("abc", false);

    Changelog log = new Changelog();
    log.setChanged(new HashSet<>(List.of("key1.xml")));
    log.setDeleted(new HashSet<>(List.of("key2.xml")));

    when(caselawService.getFromBucket("key1.xml")).thenReturn(Optional.of(testUnit));
    when(repository.findAllByFilenameIn(log.getDeleted())).thenReturn(List.of(deletedDoc));

    var expectedDocument =
        EcliCrawlerDocumentMapper.fromCaseLawDocumentationUnit(
            "frontend-url/case-law/", "key1.xml", testUnit);
    var actualDocuments = documentService.getFromChangelogs(List.of(log));

    Assertions.assertEquals(expectedDocument, actualDocuments.getFirst());
    Assertions.assertEquals(deletedDoc, actualDocuments.get(1));
  }

  @Test
  void itCreatesAFullDiffWhenEncounteringAChangeAll() throws ObjectStoreServiceException {
    var createdDuringChangeAll = getTestDocument("createdDuringChangeAll", true);
    var unitCreatedDuringChangeAll = getTestDocUnit("createdDuringChangeAll");
    var deletedDuringChangeAll = getTestDocument("deletedDuringChangeAll", false);

    Changelog log = new Changelog();
    log.setChanged(new HashSet<>(List.of("toBeIgnored.xml")));
    Changelog changeAll = new Changelog();
    changeAll.setChangeAll(true);

    // republish existing files and remove missing ones during changeall
    when(caseLawBucket.getAllKeys()).thenReturn(List.of(createdDuringChangeAll.filename()));
    when(caselawService.getFromBucket(createdDuringChangeAll.filename()))
        .thenReturn(Optional.of(unitCreatedDuringChangeAll));
    when(repository.findAllByIsPublishedIsTrue())
        .thenAnswer(invocationOnMock -> Stream.of(deletedDuringChangeAll));

    var actualDocuments = documentService.getFromChangelogs(List.of(changeAll, log));

    Assertions.assertEquals(2, actualDocuments.size());

    Map<String, EcliCrawlerDocument> result =
        actualDocuments.stream()
            .collect(Collectors.toMap(EcliCrawlerDocument::documentNumber, Function.identity()));

    Assertions.assertTrue(result.get(createdDuringChangeAll.documentNumber()).isPublished());
    Assertions.assertFalse(result.get(deletedDuringChangeAll.documentNumber()).isPublished());
  }
}
