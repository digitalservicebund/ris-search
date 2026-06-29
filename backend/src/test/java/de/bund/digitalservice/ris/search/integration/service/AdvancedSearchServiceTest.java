package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.service.AdvancedSearchService;
import de.bund.digitalservice.ris.search.utils.RisHighlightBuilder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class AdvancedSearchServiceTest extends ContainersIntegrationBase {

  @Autowired private AdvancedSearchService advancedSearchService;

  @Test
  @DisplayName("Advanced search for all documents works")
  void advancedSearchForAllDocumentsWorks() {
    checkSingleSearchHitsEquivalence(
        advancedSearchService.searchAll("BFRE000087655", Pageable.unpaged()).getSearchHits(),
        advancedSearchService.searchCaseLaw("BFRE000087655", Pageable.unpaged()).getSearchHits());
    checkSingleSearchHitsEquivalence(
        advancedSearchService.searchAll("KALU000000001", Pageable.unpaged()).getSearchHits(),
        advancedSearchService
            .searchLiterature("KALU000000001", Pageable.unpaged())
            .getSearchHits());
    checkSingleSearchHitsEquivalence(
        advancedSearchService
            .searchAll("expression_eli:2000-10-06", Pageable.unpaged())
            .getSearchHits(),
        advancedSearchService
            .searchNorm("expression_eli:2000-10-06", Pageable.unpaged())
            .getSearchHits());
    checkSingleSearchHitsEquivalence(
        advancedSearchService.searchAll("KSNR0000", Pageable.unpaged()).getSearchHits(),
        advancedSearchService
            .searchAdministrativeDirective("KSNR0000", Pageable.unpaged())
            .getSearchHits());
  }

  private <T, V> void checkSingleSearchHitsEquivalence(SearchHits<T> hits1, SearchHits<V> hits2) {
    assertThat(hits1.getTotalHits()).isEqualTo(hits2.getTotalHits()).isEqualTo(1);
    SearchHit<T> hit1 = hits1.getSearchHit(0);
    SearchHit<V> hit2 = hits2.getSearchHit(0);
    assertThat(convertMapKeys(hit1.getHighlightFields()))
        .containsExactlyInAnyOrderEntriesOf(convertMapKeys(hit2.getHighlightFields()));
    assertThat(hit1.getContent()).isEqualTo(hit2.getContent());
  }

  private <T> Map<String, T> convertMapKeys(Map<String, T> input) {
    return input.entrySet().stream()
        .collect(
            Collectors.toMap(e -> e.getKey().replace("_", "").toLowerCase(), Map.Entry::getValue));
  }

  @Test
  @DisplayName("Advanced search for case law works")
  void advancedSearchForCaseLawWorks() {

    var searchHits =
        advancedSearchService.searchCaseLaw("BFRE000087655", Pageable.unpaged()).getSearchHits();
    assertThat(searchHits).hasSize(1);
    CaseLawDocumentationUnit caseLaw = searchHits.getSearchHit(0).getContent();
    assertThat(caseLaw.documentNumber()).isEqualTo("BFRE000087655");
  }

  @Test
  @DisplayName("Advanced search for literature works")
  void advancedSearchForLiteratureWorks() {

    var searchHits =
        advancedSearchService.searchLiterature("KALU000000001", Pageable.unpaged()).getSearchHits();
    assertThat(searchHits).hasSize(1);
    Literature literature = searchHits.getSearchHit(0).getContent();
    assertThat(literature.documentNumber()).isEqualTo("KALU000000001");
  }

  @Test
  @DisplayName("Advanced search for administrative directive works")
  void advancedSearchForAdministrativeDirectiveWorks() {

    var searchHits =
        advancedSearchService
            .searchAdministrativeDirective("KSNR0000", Pageable.unpaged())
            .getSearchHits();
    assertThat(searchHits).hasSize(1);
    AdministrativeDirective directive = searchHits.getSearchHit(0).getContent();
    assertThat(directive.documentNumber()).isEqualTo("KSNR0000");
  }

  @Test
  @DisplayName("norm advanced search works")
  void normAdvancedSearchWorks() {
    var searchHits =
        advancedSearchService
            .searchNorm("expression_eli:2000-10-06", Pageable.unpaged())
            .getSearchHits();

    // check that the metadata is returned
    assertThat(searchHits).hasSize(1);
    SearchHit<Norm> searchHit = searchHits.getSearchHit(0);
    assertThat(searchHit.getId()).isEqualTo("eli/bund/bgbl-1/1000/test/2000-10-06/2/deu");
    Norm norm = searchHit.getContent();
    assertThat(norm.getOfficialShortTitle()).isEqualTo("TestG1");
    // check that the content is NOT returned
    assertThat(norm.getArticleNames()).isNull();
  }

  @Test
  @DisplayName("norm advanced search with highlights works")
  void normAdvancedSearchWithHighlightsWorks() {

    var searchHit =
        advancedSearchService
            .searchNorm("expression_eli:2000-10-06 AND test OR example", Pageable.unpaged())
            .getSearchHits()
            .getSearchHit(0);

    // check that the title was highlighted
    Map<String, String> nonArticleHighlights = getFieldMatches(searchHit);
    assertThat(nonArticleHighlights)
        .containsOnly(Map.entry("officialTitle", "<mark>Test</mark> Gesetz"));

    // check that the article highlights work
    List<Pair<String, String>> articleHighlights =
        searchHit
            .getInnerHits()
            .get("top_three_articles")
            .get()
            .map(this::getArticleTextMatch)
            .toList();
    assertThat(articleHighlights)
        .containsExactly(
            Pair.of("§ 1 <mark>Example</mark> article", "<mark>example</mark> text 1"),
            Pair.of("§ 2 <mark>Example</mark> article", "<mark>example</mark> text 2"));
  }

  @Test
  @DisplayName("norm advanced search without highlights returns no articles")
  void normAdvancedSearchWithoutHighlightsReturnsNoArticles() {

    var searchHit =
        advancedSearchService
            .searchNorm("expression_eli:2000-10-06", Pageable.unpaged())
            .getSearchHits()
            .getSearchHit(0);

    Map<String, String> nonArticleHighlights = getFieldMatches(searchHit);
    assertThat(nonArticleHighlights).isEmpty();

    var articleInnerHits = searchHit.getInnerHits().get("top_three_articles");
    assertThat(articleInnerHits).isNull();
  }

  @Test
  @DisplayName(
      "norm advanced search returns the full official title when the match is in a title longer than fragment size")
  void normAdvancedSearchReturnsFullOfficialTitleLongerThan320Characters() {
    String token = "__token__";
    String title = longTitleContaining(token);
    normsRepository.save(
        Norm.builder()
            .id("long-title-norm")
            .workEli("eli/long-title-norm/work")
            .expressionEli("eli/long-title-norm/expression")
            .officialTitle(title)
            .build());

    var searchHit =
        advancedSearchService.searchNorm(token, Pageable.unpaged()).getSearchHits().getSearchHit(0);

    assertThat(getFieldMatches(searchHit)).containsEntry("officialTitle", highlight(title, token));
  }

  @Test
  @DisplayName(
      "case law advanced search returns the full headline when the match is in a title longer than fragment size")
  void caseLawAdvancedSearchReturnsFullHeadlineLongerThan320Characters() {
    String token = "__token__";
    String title = longTitleContaining(token);
    caseLawRepository.save(
        CaseLawDocumentationUnit.builder()
            .id("long-title-caselaw")
            .documentNumber("LONGCASELAW01")
            .headline(title)
            .build());

    var searchHit =
        advancedSearchService
            .searchCaseLaw(token, Pageable.unpaged())
            .getSearchHits()
            .getSearchHit(0);

    assertThat(getFieldMatches(searchHit)).containsEntry("headline", highlight(title, token));
  }

  @Test
  @DisplayName(
      "literature advanced search returns the full main title when the match is in a title longer than fragment size")
  void literatureAdvancedSearchReturnsFullMainTitleLongerThan320Characters() {
    String token = "__token__";
    String title = longTitleContaining(token);
    literatureRepository.save(
        Literature.builder()
            .id("long-title-literature")
            .documentNumber("LONGLIT0000001")
            .mainTitle(title)
            .build());

    var searchHit =
        advancedSearchService
            .searchLiterature(token, Pageable.unpaged())
            .getSearchHits()
            .getSearchHit(0);

    assertThat(getFieldMatches(searchHit)).containsEntry("mainTitle", highlight(title, token));
  }

  @Test
  @DisplayName(
      "administrative directive advanced search returns the full headline when the match is in a title longer than fragment size")
  void administrativeDirectiveAdvancedSearchReturnsFullHeadlineLongerThan320Characters() {
    String token = "__token__";
    String title = longTitleContaining(token);
    administrativeDirectiveRepository.save(
        AdministrativeDirective.builder()
            .id("long-title-administrative-directive")
            .documentNumber("LONGADMIN00001")
            .headline(title)
            .build());

    var searchHit =
        advancedSearchService
            .searchAdministrativeDirective(token, Pageable.unpaged())
            .getSearchHits()
            .getSearchHit(0);

    assertThat(getFieldMatches(searchHit)).containsEntry("headline", highlight(title, token));
  }

  /**
   * Builds a title that is longer than the highlight fragment size ({@link
   * RisHighlightBuilder#HIGHLIGHT_FRAGMENT_SIZE}) and embeds the given (unique) token so it can be
   * matched on. The token is placed in the middle so that a truncated fragment would not contain
   * the whole title.
   */
  private String longTitleContaining(String token) {
    String filler = "Lorem ipsum dolor sit amet ".repeat(15); // 405 characters
    String title = (filler + token + " " + filler).strip();
    assertThat(title.length()).isGreaterThan(RisHighlightBuilder.HIGHLIGHT_FRAGMENT_SIZE);
    return title;
  }

  /** Returns the title with the (single, whole-word) token wrapped in highlight tags. */
  private String highlight(String title, String token) {
    return title.replace(
        token,
        RisHighlightBuilder.HIGHLIGHT_PRE_TAG + token + RisHighlightBuilder.HIGHLIGHT_POST_TAG);
  }

  private Pair<String, String> getArticleTextMatch(SearchHit<?> searchHit) {
    Map<String, String> fieldMatches = getFieldMatches(searchHit);
    assertThat(fieldMatches.keySet()).containsExactlyInAnyOrder("name", "text");
    return Pair.of(fieldMatches.get("name"), fieldMatches.get("text"));
  }

  // Convert the SearchHit highlights to a simple map since we can assume there is one match per
  // field
  private Map<String, String> getFieldMatches(SearchHit<?> searchHit) {
    Map<String, List<String>> textMatches = searchHit.getHighlightFields();
    for (List<String> textMatch : textMatches.values()) {
      // we set numOfFragments = 1 so we expect exactly one match per field
      assertThat(textMatch).hasSize(1);
    }
    return textMatches.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getFirst()));
  }
}
