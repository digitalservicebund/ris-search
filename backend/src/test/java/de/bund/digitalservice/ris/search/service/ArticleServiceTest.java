package de.bund.digitalservice.ris.search.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.LegislationPartType;
import de.bund.digitalservice.ris.search.repository.opensearch.ArticlesRepository;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
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
  void getAllArticleVersionsQueriesRepositoryUsingDocumentNumberPrefix() {
    ExpressionEli eli =
        new ExpressionEli("bund", "bgbl-1", "2020", "s1126", LocalDate.of(2025, 5, 5), 1, "deu");

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
        .findAllByDocumentNumberStartingWithAndDocumentType(
            "DKNR0E80B0026DKNE0001", LegislationPartType.ARTICLE, Pageable.unpaged());
  }
}
