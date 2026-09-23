package de.bund.digitalservice.ris.search.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.ArticleWithExpressions;
import de.bund.digitalservice.ris.search.models.opensearch.LegislationPartType;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@SpringBootTest
class ArticlesRepositoryCustomImplIntegrationTest extends ContainersIntegrationBase {

  @BeforeEach
  void setUp() {
    articlesRepository.deleteAll();
  }

  @Test
  void itGroupsArticlesByDocumentNumberAndReturnsTheirExpressionElis() {
    ExpressionEli work1Expression1 =
        new ExpressionEli("bund", "bgbl-1", "2020", "s1126", LocalDate.of(2025, 5, 5), 1, "deu");
    ExpressionEli work1Expression2 =
        new ExpressionEli("bund", "bgbl-1", "2020", "s1126", LocalDate.of(2026, 5, 5), 1, "deu");
    ExpressionEli work1Expression3 =
        new ExpressionEli("bund", "bgbl-1", "2020", "s1126", LocalDate.of(2027, 5, 5), 1, "deu");
    ExpressionEli work2Expression1 =
        new ExpressionEli("bund", "bgbl-1", "2020", "s2222", LocalDate.of(2025, 5, 5), 1, "deu");

    // Given is an article that exists in the same exact version across expression 1 and 2 and in
    // another version on expression 3
    List<Article> articles =
        new ArrayList<>(
            List.of(
                buildArticle(
                    work1Expression1,
                    "art-zi",
                    "DKNR0E1000000D000000100010",
                    LegislationPartType.ARTICLE),
                buildArticle(
                    work1Expression2,
                    "art-z1",
                    "DKNR0E1000000D000000100010",
                    LegislationPartType.ARTICLE),
                buildArticle(
                    work1Expression3,
                    "art-I",
                    "DKNR0E1000000D000000100020",
                    LegislationPartType.ARTICLE)));

    // articles that are not expected in the result
    articles.addAll(
        List.of(
            buildArticle(
                work1Expression1,
                "art-z2",
                "DKNR0000000000000000200010",
                LegislationPartType.ARTICLE),
            buildArticle(
                work1Expression1,
                "art-II",
                "DKNR0000000000000000200010",
                LegislationPartType.ARTICLE),
            buildArticle(
                work2Expression1,
                "art-zi",
                "DKNR0000000000000000300010",
                LegislationPartType.ARTICLE)));

    articlesRepository.saveAll(articles);

    Page<ArticleWithExpressions> actual =
        articlesRepository.findAllVersionsByDocumentNumber(
            "DKNR0E1000000D000000100010", LegislationPartType.ARTICLE, null, Pageable.unpaged());

    assertThat(actual.toList()).hasSize(2);
    // 3 articles (one per expressionEli) match the prefix, but they collapse into 2 distinct
    // document numbers - totalElements must reflect the distinct count, not the raw hit count.
    assertThat(actual.getTotalElements()).isEqualTo(2);
    assertThat(actual)
        .extracting(a -> a.article().getDocumentNumber())
        .containsExactlyInAnyOrder("DKNR0E1000000D000000100010", "DKNR0E1000000D000000100020");

    // article 1 in version 1 should be grouped together with the expressionElis its part of
    ArticleWithExpressions article1v1 =
        actual.stream()
            .filter(a -> a.article().getDocumentNumber().equals("DKNR0E1000000D000000100010"))
            .findFirst()
            .orElseThrow();
    assertThat(article1v1.expressionElis())
        .containsExactlyInAnyOrder(work1Expression1.toString(), work1Expression2.toString());

    // article 1 in version 2 is only part of expression 3
    ArticleWithExpressions article1v2 =
        actual.stream()
            .filter(a -> a.article().getDocumentNumber().equals("DKNR0E1000000D000000100020"))
            .findFirst()
            .orElseThrow();
    assertThat(article1v2.expressionElis()).containsExactlyInAnyOrder(work1Expression3.toString());
  }

  @Test
  void itPrefersTheGivenExpressionEliAsTheRepresentativeArticle() {
    ExpressionEli work1Expression1 =
        new ExpressionEli("bund", "bgbl-1", "2020", "s1126", LocalDate.of(2025, 5, 5), 1, "deu");
    ExpressionEli work1Expression2 =
        new ExpressionEli("bund", "bgbl-1", "2020", "s1126", LocalDate.of(2026, 5, 5), 1, "deu");

    // Given is an article that exists in the same exact version across two expressions, so
    // OpenSearch's collapsing could pick either one as the representative document.
    articlesRepository.saveAll(
        List.of(
            buildArticle(
                work1Expression1,
                "art-z1",
                "DKNR0E1000000D000000100010",
                LegislationPartType.ARTICLE),
            buildArticle(
                work1Expression2,
                "art-z1",
                "DKNR0E1000000D000000100010",
                LegislationPartType.ARTICLE)));

    Page<ArticleWithExpressions> requestedFirstExpression =
        articlesRepository.findAllVersionsByDocumentNumber(
            "DKNR0E1000000D000000100010",
            LegislationPartType.ARTICLE,
            work1Expression1.toString(),
            Pageable.unpaged());

    assertThat(requestedFirstExpression)
        .singleElement()
        .extracting(a -> a.article().getExpressionEli())
        .isEqualTo(work1Expression1.toString());

    Page<ArticleWithExpressions> requestedSecondExpression =
        articlesRepository.findAllVersionsByDocumentNumber(
            "DKNR0E1000000D000000100010",
            LegislationPartType.ARTICLE,
            work1Expression2.toString(),
            Pageable.unpaged());

    assertThat(requestedSecondExpression)
        .singleElement()
        .extracting(a -> a.article().getExpressionEli())
        .isEqualTo(work1Expression2.toString());
  }

  @Test
  void itReturnsAnEmptyPageWhenNoDocumentNumberMatchesThePrefix() {
    assertThatThrownBy(
            () -> {
              articlesRepository.findAllVersionsByDocumentNumber(
                  "notFound", LegislationPartType.ARTICLE, null, Pageable.unpaged());
            })
        .isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"PREAMBLE", "ARTICLE", "CONCLUSION"})
  void itFiltersByDocumentType(LegislationPartType type) {
    ExpressionEli eli =
        new ExpressionEli("bund", "bgbl-1", "2020", "s1126", LocalDate.of(2025, 5, 5), 1, "deu");

    // we use the same docNumber across all entities to check the filter
    String expectedDocNumber = "DKNR0E1000000D000000100000";

    articlesRepository.saveAll(
        List.of(
            buildArticle(eli, "einleitung-n1", expectedDocNumber, LegislationPartType.PREAMBLE),
            buildArticle(eli, "art-z1", expectedDocNumber, LegislationPartType.ARTICLE),
            buildArticle(eli, "conclusion-1", expectedDocNumber, LegislationPartType.CONCLUSION)));

    Page<ArticleWithExpressions> page =
        articlesRepository.findAllVersionsByDocumentNumber(
            expectedDocNumber, type, null, Pageable.ofSize(100));

    assertThat(page)
        .singleElement()
        .extracting(
            item -> item.article().getDocumentNumber(), item -> item.article().getDocumentType())
        .containsExactly(expectedDocNumber, type);
  }

  private Article buildArticle(
      ExpressionEli eli, String eId, String docNum, LegislationPartType type) {
    return Article.builder()
        .id(Article.buildId(eli.toString(), eId))
        .expressionEli(eli.toString())
        .eId(eId)
        .documentNumber(docNum)
        .documentType(type)
        .build();
  }
}
