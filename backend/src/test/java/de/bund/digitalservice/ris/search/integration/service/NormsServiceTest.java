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
import java.time.Month;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
  void setup() {
    reset();
  }

  @Test
  void shouldReturnAllExpressionsSortedForAGivenWorkEli() {
    var latestExpression =
        Norm.builder()
            .id("eli/bund/bgbl-1/2020/s1126/2025-05-05/deu")
            .entryIntoForceDate(LocalDate.of(2025, Month.MAY, 5))
            .expiryDate(null)
            .expressionEli("eli/bund/bgbl-1/2020/s1126/2025-05-05/deu")
            .manifestationEliExample(
                "eli/bund/bgbl-1/2020/s1126/2025-05-05/deu/2920-08-04/regelungstext-1.xml")
            .officialTitle("latest title")
            .datePublished(LocalDate.of(2022, Month.JANUARY, 1))
            .officialShortTitle("latest short title")
            .normsDate(LocalDate.of(2022, Month.JANUARY, 1))
            .officialAbbreviation("latest abbr")
            .workEli("eli/bund/bgbl-1/2020/s1126")
            .build();

    var olderExpression =
        Norm.builder()
            .id("eli/bund/bgbl-1/2022/s1126/2022-05-05/deu")
            .entryIntoForceDate(LocalDate.of(2022, Month.MAY, 5))
            .expiryDate(LocalDate.of(2025, Month.MAY, 5))
            .expressionEli("eli/bund/bgbl-1/2022/s1126/2022-05-05/deu")
            .manifestationEliExample(
                "eli/bund/bgbl-1/2022/s1126/2025-05-05/deu/2920-08-04/regelungstext-1.xml")
            .officialTitle("oldest title")
            .datePublished(LocalDate.of(2020, Month.JANUARY, 1))
            .officialShortTitle("oldest short title")
            .normsDate(LocalDate.of(2020, Month.JANUARY, 1))
            .officialAbbreviation("oldest abbr")
            .workEli("eli/bund/bgbl-1/2020/s1126")
            .build();
    repository.save(olderExpression);
    repository.save(latestExpression);

    var test =
        normsService.getWorkExpressions(
            new WorkEli("bund", "bgbl-1", "2020", "s1126"), Pageable.ofSize(100).withPage(0));

    assertThat(test.getTotalElements()).isEqualTo(2);
    assertThat(test.getTotalPages()).isEqualTo(1);

    assertThat(test.getContent().getFirst()).isEqualTo(latestExpression);
    assertThat(test.getContent().getLast()).isEqualTo(olderExpression);
  }

  @Test
  void shouldReturnPaginatedExpressionsForAGivenWorkEli() {
    var latestExpression =
        Norm.builder()
            .id("eli/bund/bgbl-1/2020/s1126/2025-05-05/deu")
            .entryIntoForceDate(LocalDate.of(2025, Month.MAY, 5))
            .expiryDate(null)
            .expressionEli("eli/bund/bgbl-1/2020/s1126/2025-05-05/deu")
            .workEli("eli/bund/bgbl-1/2020/s1126")
            .build();
    repository.save(latestExpression);
    repository.save(
        Norm.builder()
            .entryIntoForceDate(LocalDate.of(2020, Month.MAY, 5))
            .expiryDate(LocalDate.of(2025, Month.MAY, 5))
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
    var searchHits =
        result.getContent().getFirst().getInnerHits().get("top_three_articles").getSearchHits();
    assertThat(searchHits).hasSize(2);

    var textHighlight = searchHits.getFirst().getHighlightField("text");
    assertThat(textHighlight).hasSize(1);
    assertThat(textHighlight.getFirst()).isEqualTo("example <mark>text</mark> 1");

    var textHighlight2 = searchHits.get(1).getHighlightField("text");
    assertThat(textHighlight).hasSize(1);
    assertThat(textHighlight2.getFirst()).isEqualTo("example <mark>text</mark> 2");

    Article firstArticle = (Article) searchHits.getFirst().getContent();
    assertThat(firstArticle.getName()).contains(articleName);
  }

  @ParameterizedTest
  @ValueSource(strings = {"FooBar 2009", "foobar 2009"})
  void shouldFilterNormByAbbreviation(String abbreviationParam) {
    repository.save(Norm.builder().officialAbbreviation("FooBar 2009").build());

    repository.save(Norm.builder().officialAbbreviation("FooBar").build());

    repository.save(Norm.builder().officialAbbreviation("BarBaz 2009").build());

    NormsSearchParams params = new NormsSearchParams();
    params.setAbbreviation(abbreviationParam);
    var result =
        normsService.simpleSearchNorms(new UniversalSearchParams(), params, Pageable.unpaged());

    assertThat(result).hasSize(1);
    assertThat(
            result
                .getSearchHits()
                .getSearchHits()
                .getFirst()
                .getContent()
                .getOfficialAbbreviation())
        .isEqualTo("FooBar 2009");
  }

  @Test
  void shouldFilterOnlyOnFullKeywordMatchesByAbbreviation() {
    repository.save(Norm.builder().officialAbbreviation("FooBar Baz 2009").build());

    NormsSearchParams params = new NormsSearchParams();
    params.setAbbreviation("Baz");
    var result =
        normsService.simpleSearchNorms(new UniversalSearchParams(), params, Pageable.unpaged());

    assertThat(result).isEmpty();
  }
}
