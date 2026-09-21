package de.bund.digitalservice.ris.search.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.ArticleWithExpressions;
import de.bund.digitalservice.ris.search.models.opensearch.LegislationPartType;
import de.bund.digitalservice.ris.search.repository.opensearch.ArticlesRepository;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@SpringBootTest
class ArticleServiceIntegrationTest extends ContainersIntegrationBase {

  @Autowired ArticleService articleService;

  @Autowired ArticlesRepository articlesRepository;

  @Test
  void itRetrievesAllArticleVersionsOfAWorkWhenGivenADocumentNumberPrefix() {

    List<Article> articles = new ArrayList<>();
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
    articles.add(
        Article.builder()
            .id(Article.buildId(work1Expression1.toString(), "art-z1"))
            .expressionEli(work1Expression1.toString())
            .eId("art-z1")
            .documentNumber("DKNR0E80B0026DKNE000100010")
            .documentType(LegislationPartType.ARTICLE)
            .build());
    articles.add(
        Article.builder()
            .id(Article.buildId(work1Expression2.toString(), "art-z1"))
            .expressionEli(work1Expression2.toString())
            .eId("art-z1")
            .documentNumber("DKNR0E80B0026DKNE000100010")
            .documentType(LegislationPartType.ARTICLE)
            .build());
    articles.add(
        Article.builder()
            .id(Article.buildId(work1Expression3.toString(), "art-I"))
            .eId("art-I")
            .expressionEli(work1Expression3.toString())
            .documentNumber("DKNR0E80B0026DKNE000100020")
            .documentType(LegislationPartType.ARTICLE)
            .build());

    // articles that are not expected in the result
    articles.add(
        Article.builder()
            .id(Article.buildId(work1Expression1.toString(), "art-z2"))
            .expressionEli(work1Expression1.toString())
            .eId("art-z2")
            .documentNumber("DKNR0E80B0026DKNE000200010")
            .documentType(LegislationPartType.ARTICLE)
            .build());

    articles.add(
        Article.builder()
            .id(Article.buildId(work1Expression2.toString(), "art-II"))
            .eId("art-II")
            .expressionEli(work1Expression2.toString())
            .documentNumber("DKNR0E80B0026DKNE000200010")
            .documentType(LegislationPartType.ARTICLE)
            .build());

    articles.add(
        Article.builder()
            .id(Article.buildId(work2Expression1.toString(), "art-zi"))
            .expressionEli(work2Expression1.toString())
            .eId("art-zi")
            .documentNumber("DKNR0E70B0026DKNE000100020")
            .build());

    articlesRepository.saveAll(articles);

    // retrieve all versions of the article 1 of work 1
    Page<ArticleWithExpressions> actualArticlesWithExpressions =
        articleService.getAllArticleVersions(work1Expression1, "art-z1", Pageable.ofSize(100));

    assertThat(actualArticlesWithExpressions.toList()).hasSize(2);
    assertThat(actualArticlesWithExpressions)
        .extracting(a -> a.article().getDocumentNumber())
        .contains("DKNR0E80B0026DKNE000100010", "DKNR0E80B0026DKNE000100020");

    // article 1 in version 1 should be grouped together with the expressionElis its part of
    ArticleWithExpressions article1v1 =
        actualArticlesWithExpressions.stream()
            .filter(a -> a.article().getDocumentNumber().equals("DKNR0E80B0026DKNE000100010"))
            .findFirst()
            .orElseThrow();
    assertThat(article1v1.expressionElis())
        .containsExactlyInAnyOrder(work1Expression1.toString(), work1Expression2.toString());

    // article 1 in version 2 is only part of expression 3
    ArticleWithExpressions article1v2 =
        actualArticlesWithExpressions.stream()
            .filter(a -> a.article().getDocumentNumber().equals("DKNR0E80B0026DKNE000100020"))
            .findFirst()
            .orElseThrow();
    assertThat(article1v2.expressionElis()).containsExactlyInAnyOrder(work1Expression3.toString());
  }

  @Test
  void itRetrievesAnEmptyListOnNotFoundArticles() {
    Page<ArticleWithExpressions> actualArticles =
        articleService.getAllArticleVersions(
            new ExpressionEli(
                "bund", "bgbl-1", "2020", "s1126", LocalDate.of(2025, 5, 5), 1, "deu"),
            "notFound",
            Pageable.ofSize(1));
    assertThat(actualArticles.toList()).isEmpty();
  }

  @Test
  void itOnlyRetrievesArticlesOfTypeArticle() {
    ExpressionEli eli =
        new ExpressionEli("bund", "bgbl-1", "2020", "s1126", LocalDate.of(2025, 5, 5), 1, "deu");
    articlesRepository.saveAll(
        List.of(
            Article.builder()
                .id(Article.buildId(eli.toString(), "einleitung-n1"))
                .eId("einleitung-n1")
                .documentNumber("DKNR0E80B0026")
                .documentType(LegislationPartType.PREAMBLE)
                .build(),
            Article.builder()
                .id(Article.buildId(eli.toString(), "art-z1"))
                .eId("art-z1")
                .documentNumber("DKNR0E80B0026DKNE000100010")
                .documentType(LegislationPartType.ARTICLE)
                .build()));

    Page<ArticleWithExpressions> actualArticles =
        articleService.getAllArticleVersions(eli, "art-z1", Pageable.ofSize(100));
    assertThat(actualArticles).hasSize(1);
    assertThat(actualArticles.toList().getFirst().article().getDocumentNumber())
        .isEqualTo("DKNR0E80B0026DKNE000100010");
  }
}
