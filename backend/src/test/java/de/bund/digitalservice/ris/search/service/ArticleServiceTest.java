package de.bund.digitalservice.ris.search.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.LegislationPartType;
import de.bund.digitalservice.ris.search.repository.opensearch.ArticlesRepository;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEliPath;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
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

  @BeforeEach()
  void setup() {
    service = new ArticleService(operations, articlesRepository);
  }

  @Test
  void getAllArticleVersionsQueriesRepositoryUsingDocumentNumberPrefixInDescendingOrder() {
    ExpressionEliPath eli =
        new ExpressionEliPath(
            "bund", "bgbl-1", "2020", "s1126", LocalDate.of(2025, Month.MAY, 5), 1, "deu");

    String eId = "art-z1";
    String id = Article.buildId(eli.toString(), eId);

    when(articlesRepository.existsById(id)).thenReturn(true);
    when(articlesRepository.findById(id))
        .thenReturn(
            Optional.of(
                Article.builder()
                    .id(id)
                    .eId(eId)
                    .expressionEli(eli.toString())
                    .documentNumber("DKNR0E80B0026DKNE000100010")
                    .documentType(LegislationPartType.ARTICLE)
                    .build()));
    service.getAllArticleVersions(eli, eId);

    verify(articlesRepository, times(1))
        .findAllVersionsByDocumentNumber(
            "DKNR0E80B0026DKNE000100010",
            LegislationPartType.ARTICLE,
            eli.toString(),
            Pageable.unpaged(Sort.by(Sort.Direction.DESC, "entryIntoForceDate")));
  }

  @Test
  void getAllArticleVersionReturnsEmptyOnNotFoundExpressionEliAndEid() {
    ExpressionEliPath eli =
        new ExpressionEliPath(
            "bund", "bgbl-1", "2020", "s1126", LocalDate.of(2025, Month.MAY, 5), 1, "deu");

    String eId = "art-z1";
    String id = Article.buildId(eli.toString(), eId);
    when(articlesRepository.existsById(id)).thenReturn(false);

    assertThat(service.getAllArticleVersions(eli, eId)).isEmpty();
  }

  @Test
  void getAllArticleVersionsReturnsEmptyWhenRepositoryThrowsIllegalArgumentException() {
    ExpressionEliPath eli =
        new ExpressionEliPath(
            "bund", "bgbl-1", "2020", "s1126", LocalDate.of(2025, Month.MAY, 5), 1, "deu");

    String eId = "art-z1";
    String id = Article.buildId(eli.toString(), eId);
    String documentNumber = "DKNR0E80B0026DKNE0";

    when(articlesRepository.existsById(id)).thenReturn(true);
    when(articlesRepository.findById(id))
        .thenReturn(
            Optional.of(
                Article.builder()
                    .id(id)
                    .eId(eId)
                    .expressionEli(eli.toString())
                    .documentNumber(documentNumber)
                    .documentType(LegislationPartType.ARTICLE)
                    .build()));
    when(articlesRepository.findAllVersionsByDocumentNumber(
            documentNumber,
            LegislationPartType.ARTICLE,
            eli.toString(),
            Pageable.unpaged(Sort.by(Sort.Direction.DESC, "entryIntoForceDate"))))
        .thenThrow(new IllegalArgumentException("document number is too short"));

    assertThat(service.getAllArticleVersions(eli, eId)).isEmpty();
  }
}
