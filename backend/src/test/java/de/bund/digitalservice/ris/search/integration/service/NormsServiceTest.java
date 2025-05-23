package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.mapper.MappingDefinitions;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import de.bund.digitalservice.ris.search.service.NormsService;
import de.bund.digitalservice.ris.search.service.helper.PaginationParamsConverter;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
@WithJwt("jwtTokens/ValidAccessToken.json")
class NormsServiceTest extends ContainersIntegrationBase {

  @Autowired private NormsRepository normsRepository;
  @Autowired private NormsService normsService;

  Boolean initialized = false;

  @BeforeEach
  void setUpSearchControllerApiTest() throws IOException {
    if (initialized) return; // replacement for @BeforeAll setup, which causes errors
    initialized = true;

    assertTrue(openSearchContainer.isRunning());

    super.recreateIndex();
    super.updateMapping();

    normsRepository.saveAll(NormsTestData.allDocuments);
  }

  @Test
  @DisplayName("Should return highlights matching the norm article using search query")
  void shouldReturnHighlightsMatchingANormArticleUsingSearchQuery() throws Exception {
    String articleName = "§ 1 Example article";
    NormsSearchParams normsSearchParams = new NormsSearchParams();
    UniversalSearchParams universalSearchParams = new UniversalSearchParams();
    PaginationParams pagination = new PaginationParams();
    universalSearchParams.setSearchTerm("text");
    Pageable pageable =
        PaginationParamsConverter.convert(
            pagination, MappingDefinitions.ResolutionMode.NORMS, true);
    SearchPage<Norm> result =
        normsService.searchAndFilterNorms(universalSearchParams, normsSearchParams, pageable);
    assertThat(result.getContent()).hasSize(1);
    var searchHits = result.getContent().get(0).getInnerHits().get("articles").getSearchHits();
    assertThat(searchHits).hasSize(2);

    var textHighlight = searchHits.get(0).getHighlightField("articles.text");
    assertThat(textHighlight).hasSize(1);
    assertThat(textHighlight.get(0)).isEqualTo("example <mark>text</mark> 1");

    var textHighlight2 = searchHits.get(1).getHighlightField("articles.text");
    assertThat(textHighlight).hasSize(1);
    assertThat(textHighlight2.get(0)).isEqualTo("example <mark>text</mark> 2");

    Article firstArticle = (Article) searchHits.get(0).getContent();
    assertThat(firstArticle.name()).contains(articleName);
  }
}
