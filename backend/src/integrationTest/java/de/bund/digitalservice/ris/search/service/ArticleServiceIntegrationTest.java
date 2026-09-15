package de.bund.digitalservice.ris.search.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.repository.opensearch.ArticlesRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArticleServiceIntegrationTest extends ContainersIntegrationBase {

  private static final String FIRST_WORK_FIRST_EXPRESSION_ELI = "work1/expression1";
  private static final String FIRST_WORK_SECOND_EXPRESSION_ELI = "work1/expression2";
  private static final String SECOND_WORK_FIRST_EXPRESSION_ELI = "work2/expression1";
  private static final String EID = "art-z1";

  @Autowired ArticleService articleService;

  @Autowired ArticlesRepository articlesRepository;

  @BeforeAll
  void setup() {
    Article firstArticle =
        Article.builder()
            .id(Article.buildId(FIRST_WORK_FIRST_EXPRESSION_ELI, EID))
            .eId(EID)
            .documentNumber("DKNR0E80B0026DKNE000100010")
            .build();

    Article secondArticle =
        Article.builder()
            .id(Article.buildId(FIRST_WORK_SECOND_EXPRESSION_ELI, EID))
            .eId(EID)
            .documentNumber("DKNR0E80B0026DKNE000100020")
            .build();

    Article invalidDokNrArticle =
        Article.builder()
            .id(Article.buildId(SECOND_WORK_FIRST_EXPRESSION_ELI, EID))
            .eId(EID)
            .documentNumber("DKNR0E70B0026DKNE000100020")
            .build();

    articlesRepository.saveAll(List.of(firstArticle, secondArticle, invalidDokNrArticle));
  }

  @Test
  void itRetrievesAllArticleVersionsOfAWorkWhenGivenADocumentNumberPrefix() {
    List<Article> actualArticles =
        articleService.getAllArticleVersions(FIRST_WORK_FIRST_EXPRESSION_ELI, EID);

    assertThat(actualArticles).hasSize(2);
  }

  @Test
  void itRetrievesAnEmptyListOnNotFoundArticles() {
    List<Article> actualArticles =
        articleService.getAllArticleVersions(FIRST_WORK_FIRST_EXPRESSION_ELI, "notFound");

    assertThat(actualArticles).isEmpty();
  }
}
