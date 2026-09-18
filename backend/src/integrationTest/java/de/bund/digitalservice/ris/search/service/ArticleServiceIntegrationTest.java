package de.bund.digitalservice.ris.search.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
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
    ExpressionEli work2Expression1 =
        new ExpressionEli("bund", "bgbl-1", "2020", "s2222", LocalDate.of(2025, 5, 5), 1, "deu");
    articles.add(
        Article.builder()
            .id(Article.buildId(work1Expression1.toString(), "art-z1"))
            .eId("art-z1")
            .documentNumber("DKNR0E80B0026DKNE000100010")
            .documentType(LegislationPartType.ARTICLE)
            .build());
    articles.add(
        Article.builder()
            .id(Article.buildId(work1Expression1.toString(), "art-z2"))
            .eId("art-z2")
            .documentNumber("DKNR0E80B0026DKNE000200010")
            .documentType(LegislationPartType.ARTICLE)
            .build());

    articles.add(
        Article.builder()
            .id(Article.buildId(work1Expression2.toString(), "art-I"))
            .eId("art-I")
            .documentNumber("DKNR0E80B0026DKNE000100020")
            .documentType(LegislationPartType.ARTICLE)
            .build());
    articles.add(
        Article.builder()
            .id(Article.buildId(work1Expression2.toString(), "art-II"))
            .eId("art-II")
            .documentNumber("DKNR0E80B0026DKNE000200010")
            .documentType(LegislationPartType.ARTICLE)
            .build());

    articles.add(
        Article.builder()
            .id(Article.buildId(work2Expression1.toString(), "art-zi"))
            .eId("art-zi")
            .documentNumber("DKNR0E70B0026DKNE000100020")
            .build());

    articlesRepository.saveAll(articles);

    // retrieve all versions of the article 1 of work 1
    Page<Article> actualArticles = articleService.getAllArticleVersions(work1Expression1, "art-z1");

    assertThat(actualArticles.toList()).hasSize(2);
    assertThat(actualArticles)
        .extracting(Article::getDocumentNumber)
        .contains("DKNR0E80B0026DKNE000100010", "DKNR0E80B0026DKNE000100020");
  }

  @Test
  void itRetrievesAnEmptyListOnNotFoundArticles() {
    Page<Article> actualArticles =
        articleService.getAllArticleVersions(
            new ExpressionEli(
                "bund", "bgbl-1", "2020", "s1126", LocalDate.of(2025, 5, 5), 1, "deu"),
            "notFound");
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

    Page<Article> actualArticles = articleService.getAllArticleVersions(eli, "art-z1");
    assertThat(actualArticles).hasSize(1);
    assertThat(actualArticles.toList().getFirst().getDocumentNumber())
        .isEqualTo("DKNR0E80B0026DKNE000100010");
  }
}
