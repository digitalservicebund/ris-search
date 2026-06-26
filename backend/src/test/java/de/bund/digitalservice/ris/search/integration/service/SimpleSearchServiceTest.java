package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.service.AdministrativeDirectiveService;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.service.LiteratureService;
import de.bund.digitalservice.ris.search.service.NormsService;
import de.bund.digitalservice.ris.search.utils.RisHighlightBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Pageable;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class SimpleSearchServiceTest extends ContainersIntegrationBase {

  @Autowired private NormsService normsService;
  @Autowired private CaseLawService caseLawService;
  @Autowired private LiteratureService literatureService;
  @Autowired private AdministrativeDirectiveService administrativeDirectiveService;

  @BeforeEach
  void setup() {
    reset();
  }

  @Test
  @DisplayName(
      "norm simple search returns the full official title when the match is in a title longer than 320 characters")
  void normSimpleSearchReturnsFullOfficialTitleLongerThan320Characters() {
    String token = "__token__";
    String title = longTitleContaining(token);
    normsRepository.save(
        Norm.builder()
            .id("long-title-norm-simple")
            .workEli("eli/long-title-norm-simple/work")
            .expressionEli("eli/long-title-norm-simple/expression")
            .officialTitle(title)
            .build());

    var params = new UniversalSearchParams();
    params.setSearchTerm(token);
    var hit =
        normsService.simpleSearchNorms(params, null, Pageable.unpaged()).getContent().getFirst();

    assertThat(hit.getHighlightField("officialTitle")).containsExactly(highlight(title, token));
  }

  @Test
  @DisplayName(
      "case law simple search returns the full headline when the match is in a title longer than 320 characters")
  void caseLawSimpleSearchReturnsFullHeadlineLongerThan320Characters() {
    String token = "__token__";
    String title = longTitleContaining(token);
    caseLawRepository.save(
        CaseLawDocumentationUnit.builder()
            .id("long-title-caselaw-simple")
            .documentNumber("LONGCASELAW02")
            .headline(title)
            .build());

    var params = new UniversalSearchParams();
    params.setSearchTerm(token);
    var hit =
        caseLawService
            .simpleSearchCaseLaw(params, null, Pageable.unpaged())
            .getContent()
            .getFirst();

    assertThat(hit.getHighlightField("headline")).containsExactly(highlight(title, token));
  }

  @Test
  @DisplayName(
      "literature simple search returns the full main title when the match is in a title longer than 320 characters")
  void literatureSimpleSearchReturnsFullMainTitleLongerThan320Characters() {
    String token = "__token__";
    String title = longTitleContaining(token);
    literatureRepository.save(
        Literature.builder()
            .id("long-title-literature-simple")
            .documentNumber("LONGLIT0000002")
            .mainTitle(title)
            .build());

    var params = new UniversalSearchParams();
    params.setSearchTerm(token);
    var hit =
        literatureService
            .simpleSearchLiterature(params, null, Pageable.unpaged())
            .getContent()
            .getFirst();

    assertThat(hit.getHighlightField("mainTitle")).containsExactly(highlight(title, token));
  }

  @Test
  @DisplayName(
      "administrative directive simple search returns the full headline when the match is in a title longer than 320 characters")
  void administrativeDirectiveSimpleSearchReturnsFullHeadlineLongerThan320Characters() {
    String token = "__token__";
    String title = longTitleContaining(token);
    administrativeDirectiveRepository.save(
        AdministrativeDirective.builder()
            .id("long-title-administrative-directive-simple")
            .documentNumber("LONGADMIN00002")
            .headline(title)
            .build());

    var params = new UniversalSearchParams();
    params.setSearchTerm(token);
    var hit =
        administrativeDirectiveService
            .simpleSearch(params, null, Pageable.unpaged())
            .getContent()
            .getFirst();

    assertThat(hit.getHighlightField("headline")).containsExactly(highlight(title, token));
  }

  /**
   * Builds a title that is longer than the highlight fragment size ({@link
   * RisHighlightBuilder#HIGHLIGHT_FRAGMENT_SIZE}) and embeds the given (unique) token so it can be
   * matched on. The token is placed in the middle so that a truncated fragment would not contain
   * the whole title.
   */
  private String longTitleContaining(String token) {
    String filler = "Lorem ipsum dolor sit amet ".repeat(15); // ~405 characters
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
}
