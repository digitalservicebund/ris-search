package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import de.bund.digitalservice.ris.search.service.NormsService;
import de.bund.digitalservice.ris.search.utils.eli.WorkEli;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class NormsServiceTest extends ContainersIntegrationBase {

  @Autowired private NormsService normsService;

  @Autowired private NormsRepository repository;

  @BeforeEach
  void reset() {
    clearRepositoryData();
    resetRepositories();
  }

  @Test
  void shouldReturnAllExpressionsSortedForAGivenWorkEli() {
    var latestExpression =
        Norm.builder()
            .id("eli/bund/bgbl-1/2020/s1126/2025-05-05/deu")
            .entryIntoForceDate(LocalDate.of(2025, 5, 5))
            .expiryDate(null)
            .expressionEli("eli/bund/bgbl-1/2020/s1126/2025-05-05/deu")
            .workEli("eli/bund/bgbl-1/2020/s1126")
            .build();

    var olderExpression =
        Norm.builder()
            .id("eli/bund/bgbl-1/2020/s1126/2020-05-05/deu")
            .entryIntoForceDate(LocalDate.of(2020, 5, 5))
            .expiryDate(LocalDate.of(2025, 5, 5))
            .expressionEli("eli/bund/bgbl-1/2020/s1126/2020-05-05/deu")
            .workEli("eli/bund/bgbl-1/2020/s1126")
            .build();
    repository.save(olderExpression);
    repository.save(latestExpression);

    var test =
        normsService.getWorkExpressions(
            new WorkEli("bund", "bgbl-1", "2020", "s1126"), Pageable.ofSize(100).withPage(0));

    assertThat(test.getTotalElements()).isEqualTo(2);
    assertThat(test.getTotalPages()).isEqualTo(1);
    assertThat(test.getContent().getFirst().getExpressionEli())
        .isEqualTo(latestExpression.getExpressionEli());
    assertThat(test.getContent().getFirst().getExpiryDate())
        .isEqualTo(latestExpression.getExpiryDate());
    assertThat(test.getContent().getFirst().getEntryIntoForceDate())
        .isEqualTo(latestExpression.getEntryIntoForceDate());

    assertThat(test.getContent().getLast().getExpressionEli())
        .isEqualTo(olderExpression.getExpressionEli());
    assertThat(test.getContent().getLast().getExpiryDate())
        .isEqualTo(olderExpression.getExpiryDate());
    assertThat(test.getContent().getLast().getEntryIntoForceDate())
        .isEqualTo(olderExpression.getEntryIntoForceDate());
  }

  @Test
  void shouldReturnPaginatedExpressionsForAGivenWorkEli() {
    var latestExpression =
        Norm.builder()
            .id("eli/bund/bgbl-1/2020/s1126/2025-05-05/deu")
            .entryIntoForceDate(LocalDate.of(2025, 5, 5))
            .expiryDate(null)
            .expressionEli("eli/bund/bgbl-1/2020/s1126/2025-05-05/deu")
            .workEli("eli/bund/bgbl-1/2020/s1126")
            .build();
    repository.save(latestExpression);
    repository.save(
        Norm.builder()
            .entryIntoForceDate(LocalDate.of(2020, 5, 5))
            .expiryDate(LocalDate.of(2025, 5, 5))
            .expressionEli("eli/bund/bgbl-1/2020/s1126/2020-05-05/deu")
            .workEli("eli/bund/bgbl-1/2020/s1126")
            .build());

    var test =
        normsService.getWorkExpressions(
            new WorkEli("bund", "bgbl-1", "2020", "s1126"), Pageable.ofSize(1).withPage(0));

    assertThat(test.getNumber()).isZero();
    assertThat(test.getTotalElements()).isEqualTo(2);
    assertThat(test.getTotalPages()).isEqualTo(2);
    assertThat(test.getContent().getFirst().getExpressionEli())
        .isEqualTo(latestExpression.getExpressionEli());
  }

  @Test
  @DisplayName("Should return highlights matching the norm article using search query")
  void shouldReturnHighlightsMatchingANormArticleUsingSearchQuery() {
    String articleName = "§ 1 Example article";
    NormsSearchParams normsSearchParams = new NormsSearchParams();
    UniversalSearchParams universalSearchParams = new UniversalSearchParams();
    PaginationParams pagination = new PaginationParams();
    universalSearchParams.setSearchTerm("text");
    PageRequest pageable = PageRequest.of(pagination.getPageIndex(), pagination.getSize());
    SearchPage<Norm> result =
        normsService.simpleSearchNorms(universalSearchParams, normsSearchParams, pageable);
    assertThat(result.getContent()).hasSize(1);
    var searchHits = result.getContent().getFirst().getInnerHits().get("articles").getSearchHits();
    assertThat(searchHits).hasSize(2);

    var textHighlight = searchHits.getFirst().getHighlightField("articles.text");
    assertThat(textHighlight).hasSize(1);
    assertThat(textHighlight.getFirst()).isEqualTo("example <mark>text</mark> 1");

    var textHighlight2 = searchHits.get(1).getHighlightField("articles.text");
    assertThat(textHighlight).hasSize(1);
    assertThat(textHighlight2.getFirst()).isEqualTo("example <mark>text</mark> 2");

    Article firstArticle = (Article) searchHits.getFirst().getContent();
    assertThat(firstArticle.name()).contains(articleName);
  }
}
