package de.bund.digitalservice.ris.search.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import de.bund.digitalservice.ris.search.utils.eli.WorkEli;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class NormsServiceIntegrationTest extends ContainersIntegrationBase {

  @Autowired private NormsRepository repository;

  @BeforeEach
  void setup() {
    cleanup();
  }

  @Test
  void shouldReturnAllExpressionsSortedForAGivenWorkEli() {
    loadDefaultData();
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
            .abbreviation("latest abbr")
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
            .abbreviation("oldest abbr")
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
    loadDefaultData();

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
    loadDefaultData();

    String articleName = "§ 1 Example article";
    SearchPage<Norm> result = searchNormsHit("text", null);
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
    loadDefaultData();

    repository.save(Norm.builder().abbreviation("FooBar 2009").build());

    repository.save(Norm.builder().abbreviation("FooBar").build());

    repository.save(Norm.builder().abbreviation("BarBaz 2009").build());

    NormsSearchParams params = new NormsSearchParams();
    params.setAbbreviation(abbreviationParam);
    var result = searchNormsHit("", params);

    assertThat(result).hasSize(1);
    assertThat(result.getSearchHits().getSearchHits().getFirst().getContent().getAbbreviation())
        .isEqualTo("FooBar 2009");
  }

  @Test
  void shouldFilterOnlyOnFullKeywordMatchesByAbbreviation() {
    loadDefaultData();

    repository.save(Norm.builder().abbreviation("FooBar Baz 2009").build());

    NormsSearchParams params = new NormsSearchParams();
    params.setAbbreviation("Baz");
    var result = searchNormsHit("Baz", params);
    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(strings = {"FooBar 2009", "foobar 2009", "foobar_2009"})
  void shouldFilterNormByRisAbbreviation(String risAbbreviation) {
    loadDefaultData();

    repository.save(Norm.builder().abbreviation("FooBar").risAbbreviation("FooBar 2009").build());

    repository.save(Norm.builder().abbreviation("FooBar").risAbbreviation("FooBar").build());

    repository.save(Norm.builder().abbreviation("FooBar").risAbbreviation("BarBaz 2009").build());

    NormsSearchParams params = new NormsSearchParams();
    params.setRisAbbreviation(risAbbreviation);
    var result = searchNormsHit("", params);

    assertThat(result).hasSize(1);
    assertThat(result.getSearchHits().getSearchHits().getFirst().getContent().getRisAbbreviation())
        .isEqualTo("FooBar 2009");
  }

  @Test
  void shouldFilterOnlyOnFullKeywordMatchesByRisAbbreviation() {
    loadDefaultData();

    repository.save(Norm.builder().risAbbreviation("FooBar Baz 2009").build());

    NormsSearchParams params = new NormsSearchParams();
    params.setAbbreviation("Baz");
    var result = searchNormsHit("Baz", params);

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("a targeted article is at the top of the results")
  void aTargetedArticleIsAtTheTopOfResults() {
    // GIVEN : 2 norms each with 3 articles where norm2 ranks higher for the search "§ 3 StVO blah"
    // norm2 ranks higher because both norms contain all 4 tokens (passed filtering logic), and have
    // the same tokens in the same fields, except norm2 has 3 in the abbreviation and norm1 only has
    // it in an article name. Abbreviation is boosted more than article name.
    Norm norm1 =
        NormsTestData.buildTestNorm(
            "StVO",
            List.of("§ 1", "§ 2", "§ 3"),
            List.of("blah", "blah", "§ paragraph paragraf article artikel art abs"));
    Norm norm2 =
        NormsTestData.buildTestNorm(
            "StVO AusnV 3",
            List.of("Eingangsformel", "§ 1", "§ 2"),
            List.of("blah", "blah", "§ paragraph paragraf article artikel art abs"));
    normsRepository.saveAll(List.of(norm1, norm2));
    // check norm2 actually ranks higher to make sure this logic doesn't silently fail later
    var norms = searchNorms("§ 3 StVO blah");
    assertThat(norms.getFirst().getId()).isEqualTo(norm2.getId());

    // WHEN : we search various targeted article searches, THEN : it makes norm1 rank on top
    assertThat(searchNorms("§ 3 StVO").getFirst().getId()).isEqualTo(norm1.getId());
    assertThat(searchNorms("3 StVO").getFirst().getId()).isEqualTo(norm1.getId());
    assertThat(
            searchNorms("3 StVO § paragraph paragraf article artikel art abs").getFirst().getId())
        .isEqualTo(norm1.getId());
  }

  List<Norm> searchNorms(String searchTerm) {
    return searchNormsHit(searchTerm, null).get().map(SearchHit::getContent).toList();
  }

  SearchPage<Norm> searchNormsHit(String searchTerm, NormsSearchParams searchParams) {
    return normsService.simpleSearchNorms(
        UniversalSearchParams.builder().searchTerm(searchTerm).build(),
        searchParams,
        Pageable.ofSize(10000));
  }
}
