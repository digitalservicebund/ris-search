package de.bund.digitalservice.ris.search.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.config.opensearch.Configurations;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.mapper.CaseLawLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;
import org.springframework.data.elasticsearch.core.query.Query;

@ExtendWith(MockitoExtension.class)
class CaseLawServiceTest {

  @Mock private CaseLawService caseLawService;
  @Mock private ElasticsearchOperations operationsMock;
  @Mock private CaseLawBucket caseLawBucketMock;
  @Mock private CaseLawLdmlToOpenSearchMapper marshaller;
  @Mock private CaseLawRepository caseLawRepositoryMock;

  @BeforeEach
  void setUp() {
    Configurations configurations = Mockito.mock(Configurations.class);
    this.caseLawService =
        new CaseLawService(
            caseLawRepositoryMock,
            caseLawBucketMock,
            operationsMock,
            configurations,
            null,
            marshaller);
  }

  @Test
  @DisplayName("Should return the search result from repository")
  void shouldReturnSearchResult() {
    var searchResult = CaseLawDocumentationUnit.builder().build();
    var searchHit =
        new SearchHit<>("1", "1", "routing", 1, null, null, null, null, null, null, searchResult);
    Pageable pageable = PageRequest.of(0, 10);
    SearchHits<CaseLawDocumentationUnit> searchHits =
        new SearchHitsImpl<>(
            1, TotalHitsRelation.EQUAL_TO, 1.0f, "", "", List.of(searchHit), null, null, null);
    SearchPage<CaseLawDocumentationUnit> searchResultPage =
        SearchHitSupport.searchPageFor(searchHits, pageable);

    when(operationsMock.search((Query) any(), eq(CaseLawDocumentationUnit.class)))
        .thenReturn(searchHits);

    var actual = caseLawService.searchCaseLaws("anySearch", pageable);
    Assertions.assertEquals(searchResultPage, actual);
  }

  @Test
  @DisplayName("Should return existing file as bytes from folder if not in prototype envrionment")
  void shouldReturnFileAsBytesFromFolderWhenNotInPrototypeEnvironment()
      throws ObjectStoreServiceException {
    Optional<byte[]> expectedResult = Optional.of("file-content".getBytes());
    when(caseLawBucketMock.get("STRE201770751/STRE201770751.xml")).thenReturn(expectedResult);

    var actual = caseLawService.getFileByDocumentNumber("STRE201770751");
    Assertions.assertEquals(expectedResult, actual);
  }

  @Test
  @DisplayName("Should throw if file does not")
  void shouldThrowIfFileNotFoundInFolder() throws ObjectStoreServiceException {
    when(caseLawBucketMock.get(any())).thenThrow(ObjectStoreServiceException.class);

    Assertions.assertThrows(
        ObjectStoreServiceException.class,
        () -> caseLawService.getFileByDocumentNumber("STRE201770751"),
        "Expected getFileByDocumentNumber to throw ObjectStoreServiceException, but it didn't");
  }

  @Test
  @DisplayName("Should return all filenames for document number if not in prototype environment")
  void shouldReturnAllFilenamesForADocumentNumberIfNotInPrototypeEnvironment() {
    List<String> expectedResult = List.of("FOO.xml", "FOO-image.png");
    when(caseLawBucketMock.getAllKeysByPrefix("FOO")).thenReturn(expectedResult);

    var actual = caseLawService.getAllFilenamesByDocumentNumber("FOO");
    Assertions.assertEquals(expectedResult, actual);
  }

  @Test
  void itReturnsEmptyForInvalidLdmlFromFile() throws ObjectStoreServiceException {
    String filename = "docNr/docNr.xml";

    when(caseLawBucketMock.getFileAsString(filename)).thenReturn(Optional.of("content"));
    when(marshaller.fromString("content")).thenThrow(OpenSearchMapperException.class);
    Assertions.assertTrue(caseLawService.getFromBucket(filename).isEmpty());
  }

  @Test
  void itReturnsEmptyForMissingContentFromFile() throws ObjectStoreServiceException {
    String filename = "docNr/docNr.xml";

    when(caseLawBucketMock.getFileAsString(filename)).thenReturn(Optional.empty());
    Assertions.assertTrue(caseLawService.getFromBucket(filename).isEmpty());
  }

  @Test
  void itReturnsACaseLawDocumentationUnit() throws ObjectStoreServiceException {
    String filename = "docNr/docNr.xml";
    CaseLawDocumentationUnit unit = CaseLawDocumentationUnit.builder().id("id").build();
    when(caseLawBucketMock.getFileAsString(filename)).thenReturn(Optional.of("content"));
    when(marshaller.fromString("content")).thenReturn(unit);

    var result = caseLawService.getFromBucket(filename);

    Assertions.assertTrue(result.isPresent());
  }
}
