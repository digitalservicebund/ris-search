package de.bund.digitalservice.ris.search.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsSynthesizedRepository;
import de.bund.digitalservice.ris.search.service.NormsService;
import java.util.List;
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
class NormsServiceTest {

  private NormsService normsService;
  private ElasticsearchOperations operationsMock;

  @BeforeEach
  void setUp() {
    NormsRepository normsRepositoryMock =
        new NormsRepository(Mockito.mock(NormsSynthesizedRepository.class));
    operationsMock = Mockito.mock(ElasticsearchOperations.class);
    NormsBucket normsBucketMock = Mockito.mock(NormsBucket.class);
    this.normsService =
        new NormsService(normsRepositoryMock, normsBucketMock, operationsMock, null);
  }

  @Test
  @DisplayName("Should return the search result from OpenSearch")
  void shouldReturnSearchResult() {
    var searchResult = Norm.builder().build();
    var searchHit =
        new SearchHit<>("1", "1", "routing", 1, null, null, null, null, null, null, searchResult);
    Pageable pageable = PageRequest.of(0, 10);
    SearchHits<Norm> searchHits =
        new SearchHitsImpl<>(
            1, TotalHitsRelation.EQUAL_TO, 1.0f, "", "", List.of(searchHit), null, null, null);
    SearchPage<Norm> searchResultPage = SearchHitSupport.searchPageFor(searchHits, pageable);
    when(operationsMock.search((Query) any(), eq(Norm.class))).thenReturn(searchHits);

    var actual = normsService.searchNorms("anySearch", pageable);
    Assertions.assertEquals(searchResultPage, actual);
  }
}
