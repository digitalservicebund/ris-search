package de.bund.digitalservice.ris.search.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.LegislationPartType;
import de.bund.digitalservice.ris.search.repository.opensearch.ArticlesRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

  @Mock ElasticsearchOperations operations;

  @Mock ArticlesRepository articlesRepository;

  ArticleService service;

  @BeforeEach
  void setup() {
    service = new ArticleService(operations, articlesRepository);
  }

  @Test
  void getAllArticleVersionsQueriesRepositoryUsingDocumentNumberPrefixInDescendingOrder() {
    service.getAllArticleVersions("DKNR0E80B0026DKNE000100010");

    verify(articlesRepository, times(1))
        .findAllVersionsByDocumentNumber(
            "DKNR0E80B0026DKNE000100010",
            LegislationPartType.ARTICLE,
            Pageable.unpaged(Sort.by(Sort.Direction.DESC, "entryIntoForceDate")));
  }

  @Test
  void getAllArticleVersionsReturnsEmptyWhenRepositoryThrowsIllegalArgumentException() {
    String documentNumber = "DKNR0E80B0026DKNE0";
    when(articlesRepository.findAllVersionsByDocumentNumber(
            documentNumber,
            LegislationPartType.ARTICLE,
            Pageable.unpaged(Sort.by(Sort.Direction.DESC, "entryIntoForceDate"))))
        .thenThrow(new IllegalArgumentException("document number is too short"));

    assertThat(service.getAllArticleVersions(documentNumber)).isEmpty();
  }

  @Test
  void findExpressionElisByDocumentNumberMapsArticlesToTheirExpressionElis() {
    String documentNumber = "DKNR0E80B0026DKNE000100010";
    String eli1 = "eli/bund/bgbl-1/2020/s1126/2025-05-05/1/deu";
    String eli2 = "eli/bund/bgbl-1/2020/s1126/2026-05-05/1/deu";
    when(articlesRepository.findExpressionElisByDocumentNumber(documentNumber))
        .thenReturn(
            List.of(
                Article.builder().documentNumber(documentNumber).expressionEli(eli1).build(),
                Article.builder().documentNumber(documentNumber).expressionEli(eli2).build()));

    assertThat(service.findExpressionElisByDocumentNumber(documentNumber))
        .containsExactly(eli1, eli2);
  }
}
