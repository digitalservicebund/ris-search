package de.bund.digitalservice.ris.search.repository.opensearch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.ArticleWithExpressions;
import de.bund.digitalservice.ris.search.models.opensearch.LegislationPartType;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;
import org.springframework.data.elasticsearch.core.query.Query;

class ArticlesRepositoryCustomImplTest {

  private final ElasticsearchOperations operations = mock(ElasticsearchOperations.class);
  private final ArticlesRepositoryCustomImpl repository =
      new ArticlesRepositoryCustomImpl(operations);

  @Test
  void collectsDistinctExpressionElisFromTheCollapsedGroupsInnerHits() {
    Article representative =
        Article.builder()
            .id("expr-1/art-z1")
            .documentNumber("DOC0001")
            .expressionEli("expr-1")
            .documentType(LegislationPartType.ARTICLE)
            .build();
    Article sameArticleOtherExpression =
        Article.builder()
            .id("expr-2/art-z1")
            .documentNumber("DOC0001")
            .expressionEli("expr-2")
            .documentType(LegislationPartType.ARTICLE)
            .build();
    // an article can occur twice within the same expression (e.g. re-indexed); the mapping must
    // still deduplicate expressionElis
    Article duplicateOfRepresentative =
        Article.builder()
            .id("expr-1/art-z1-again")
            .documentNumber("DOC0001")
            .expressionEli("expr-1")
            .documentType(LegislationPartType.ARTICLE)
            .build();

    SearchHits<Article> innerHits =
        toSearchHits(
            List.of(
                toSearchHit(representative, null),
                toSearchHit(sameArticleOtherExpression, null),
                toSearchHit(duplicateOfRepresentative, null)));
    SearchHit<Article> collapsedHit = toSearchHit(representative, Map.of("expressions", innerHits));
    when(operations.search((Query) any(), eq(Article.class)))
        .thenReturn(toSearchHits(List.of(collapsedHit)));

    Page<ArticleWithExpressions> result =
        repository.findAllByDocumentNumberStartingWithAndDocumentType(
            "DOC", LegislationPartType.ARTICLE, PageRequest.of(0, 10));

    assertThat(result.getContent()).hasSize(1);
    ArticleWithExpressions articleWithExpressions = result.getContent().getFirst();
    assertThat(articleWithExpressions.article()).isEqualTo(representative);
    assertThat(articleWithExpressions.expressionElis())
        .containsExactlyInAnyOrder("expr-1", "expr-2");
  }

  @Test
  void fallsBackToTheHitsOwnExpressionEliWhenNoInnerHitsArePresent() {
    Article representative =
        Article.builder()
            .id("expr-1/art-z1")
            .documentNumber("DOC0001")
            .expressionEli("expr-1")
            .documentType(LegislationPartType.ARTICLE)
            .build();
    when(operations.search((Query) any(), eq(Article.class)))
        .thenReturn(toSearchHits(List.of(toSearchHit(representative, null))));

    Page<ArticleWithExpressions> result =
        repository.findAllByDocumentNumberStartingWithAndDocumentType(
            "DOC", LegislationPartType.ARTICLE, PageRequest.of(0, 10));

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().expressionElis()).containsExactly("expr-1");
  }

  @Test
  void returnsAnEmptyPageWhenNoArticlesMatch() {
    Pageable pageable = PageRequest.of(0, 10);
    when(operations.search((Query) any(), eq(Article.class))).thenReturn(toSearchHits(List.of()));

    Page<ArticleWithExpressions> result =
        repository.findAllByDocumentNumberStartingWithAndDocumentType(
            "DOC", LegislationPartType.ARTICLE, pageable);

    assertThat(result.getContent()).isEmpty();
  }

  private SearchHit<Article> toSearchHit(Article article, Map<String, SearchHits<?>> innerHits) {
    return new SearchHit<>(
        null, article.getId(), null, 1f, null, null, innerHits, null, null, null, article);
  }

  private SearchHits<Article> toSearchHits(List<SearchHit<Article>> hits) {
    return new SearchHitsImpl<>(
        hits.size(),
        TotalHitsRelation.EQUAL_TO,
        1f,
        Duration.ZERO,
        null,
        null,
        hits,
        null,
        null,
        null);
  }
}
