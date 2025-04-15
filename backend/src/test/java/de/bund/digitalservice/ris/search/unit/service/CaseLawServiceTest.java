package de.bund.digitalservice.ris.search.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.config.opensearch.Configurations;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawSynthesizedRepository;
import de.bund.digitalservice.ris.search.service.CaseLawService;
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
class CaseLawServiceTest {

  private CaseLawService caseLawService;
  private ElasticsearchOperations operationsMock;

  @BeforeEach
  public void setUp() {
    CaseLawSynthesizedRepository caseLawSynthesizedRepositoryMock =
        Mockito.mock(CaseLawSynthesizedRepository.class);
    CaseLawRepository caseLawRepositoryMock =
        new CaseLawRepository(caseLawSynthesizedRepositoryMock);
    operationsMock = Mockito.mock(ElasticsearchOperations.class);
    Configurations configurations = Mockito.mock(Configurations.class);
    this.caseLawService = new CaseLawService(caseLawRepositoryMock, operationsMock, configurations);
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
}
