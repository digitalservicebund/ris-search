package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.service.NormsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchPage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class NormsServiceTest extends ContainersIntegrationBase {

  @Autowired private NormsService normsService;

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
