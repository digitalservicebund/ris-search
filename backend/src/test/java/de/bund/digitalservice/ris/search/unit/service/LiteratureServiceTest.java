package de.bund.digitalservice.ris.search.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.repository.objectstorage.literature.LiteratureBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.LiteratureRepository;
import de.bund.digitalservice.ris.search.service.LiteratureService;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class LiteratureServiceTest {

  private LiteratureService literatureService;
  private ElasticsearchOperations operationsMock;
  private LiteratureBucket literatureBucketMock;
  private LiteratureRepository literatureRepositoryMock;

  @BeforeEach
  void setUp() {
    literatureRepositoryMock = Mockito.mock(LiteratureRepository.class);
    literatureBucketMock = Mockito.mock(LiteratureBucket.class);
    operationsMock = Mockito.mock(ElasticsearchOperations.class);
    this.literatureService =
        new LiteratureService(literatureRepositoryMock, literatureBucketMock, operationsMock, null);
  }

  @Test
  @DisplayName("Should return the search result from repository")
  void shouldReturnSearchResult() {
    var searchResult = Literature.builder().build();
    var searchHit =
        new SearchHit<>("1", "1", "routing", 1, null, null, null, null, null, null, searchResult);
    Pageable pageable = PageRequest.of(0, 10);
    SearchHits<Literature> searchHits =
        new SearchHitsImpl<>(
            1,
            TotalHitsRelation.EQUAL_TO,
            1.0f,
            Duration.of(3, ChronoUnit.SECONDS),
            "",
            "",
            List.of(searchHit),
            null,
            null,
            null);
    SearchPage<Literature> searchResultPage = SearchHitSupport.searchPageFor(searchHits, pageable);

    when(operationsMock.search((Query) any(), eq(Literature.class))).thenReturn(searchHits);

    var actual = literatureService.searchLiterature("anySearch", pageable);
    Assertions.assertEquals(searchResultPage, actual);
  }

  @Test
  @DisplayName("Should return an existing literature item by its document number")
  void shouldReturnLiteratureByDocumentNumber() {
    var expectedResult = List.of(Literature.builder().build());
    when(literatureRepositoryMock.findByDocumentNumber("XXLU000000001")).thenReturn(expectedResult);

    var actual = literatureService.getByDocumentNumber("XXLU000000001");
    Assertions.assertEquals(expectedResult, actual);
  }

  @Test
  @DisplayName("Should return existing file as bytes from folder")
  void shouldReturnFileAsBytesFromFolder() throws ObjectStoreServiceException {
    Optional<byte[]> expectedResult = Optional.of("file-content".getBytes());
    when(literatureBucketMock.get("XXLU000000001.akn.xml")).thenReturn(expectedResult);

    var actual = literatureService.getFileByDocumentNumber("XXLU000000001");
    Assertions.assertEquals(expectedResult, actual);
  }

  @Test
  @DisplayName("Should throw if file does not")
  void shouldThrowIfFileNotFoundInFolder() throws ObjectStoreServiceException {
    when(literatureBucketMock.get(any())).thenThrow(ObjectStoreServiceException.class);

    Assertions.assertThrows(
        ObjectStoreServiceException.class,
        () -> literatureService.getFileByDocumentNumber("XXLU000000001"));
  }
}
