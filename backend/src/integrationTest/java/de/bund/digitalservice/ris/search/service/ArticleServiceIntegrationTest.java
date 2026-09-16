package de.bund.digitalservice.ris.search.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.repository.opensearch.ArticlesRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ArticleServiceIntegrationTest extends ContainersIntegrationBase {

  @Autowired ArticleService articleService;

  @Autowired ArticlesRepository articlesRepository;

  @Test
  void itRetrievesAllArticleVersionsOfAWorkWhenGivenADocumentNumberPrefix() {

    List<Article> articles = new ArrayList<>();
    articles.add(
        Article.builder()
            .id(Article.buildId("work1/expression1", "art-z1"))
            .eId("art-z1")
            .documentNumber("DKNR0E80B0026DKNE000100010")
            .build());
    articles.add(
        Article.builder()
            .id(Article.buildId("work1/expression1", "art-z2"))
            .eId("art-z2")
            .documentNumber("DKNR0E80B0026DKNE000200010")
            .build());

    articles.add(
        Article.builder()
            .id(Article.buildId("work1/expression2", "art-I"))
            .eId("art-I")
            .documentNumber("DKNR0E80B0026DKNE000100020")
            .build());
    articles.add(
        Article.builder()
            .id(Article.buildId("work1/expression2", "art-II"))
            .eId("art-II")
            .documentNumber("DKNR0E80B0026DKNE000200010")
            .build());

    articles.add(
        Article.builder()
            .id(Article.buildId("work2/expression1", "art-zi"))
            .eId("art-zi")
            .documentNumber("DKNR0E70B0026DKNE000100020")
            .build());

    articlesRepository.saveAll(articles);

    // retrieve all versions of the article 1 of work 1
    List<Article> actualArticles =
        articleService.getAllArticleVersions("work1/expression1", "art-z1");

    assertThat(actualArticles).hasSize(2);
    assertThat(actualArticles)
        .extracting(Article::getDocumentNumber)
        .contains("DKNR0E80B0026DKNE000100010", "DKNR0E80B0026DKNE000100020");
  }

  @Test
  void itRetrievesAnEmptyListOnNotFoundArticles() {
    List<Article> actualArticles =
        articleService.getAllArticleVersions("work1/expression1", "notFound");
    assertThat(actualArticles).isEmpty();
  }

  @Test
  void itRetrievesAnEmptyListOnDocumentNumbersThatAreTooShort() {
    articlesRepository.save(
        Article.builder()
            .id(Article.buildId("work1/expression1", "einleitung-n1"))
            .eId("einleitung-n1")
            .documentNumber("DKNR0E80B0026")
            .build());

    List<Article> actualArticles =
        articleService.getAllArticleVersions("work1/expression1", "einleitung-n1");
    assertThat(actualArticles).isEmpty();
  }
}
