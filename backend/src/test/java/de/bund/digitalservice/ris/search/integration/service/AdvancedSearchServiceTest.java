package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.service.AdvancedSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class AdvancedSearchServiceTest extends ContainersIntegrationBase {

  @Autowired private AdvancedSearchService advancedSearchService;

  @Test
  @DisplayName("Advanced search for all documents works")
  void advancedSearchForAllDocumentsWorks() {
    SearchHits<AbstractSearchEntity> searchHits;

    searchHits =
        advancedSearchService.searchAll("BFRE000087655", Pageable.unpaged()).getSearchHits();
    assertThat(searchHits).hasSize(1);
    CaseLawDocumentationUnit caseLaw =
        (CaseLawDocumentationUnit) searchHits.getSearchHit(0).getContent();
    assertThat(caseLaw.documentNumber()).isEqualTo("BFRE000087655");

    searchHits =
        advancedSearchService.searchAll("KALU000000001", Pageable.unpaged()).getSearchHits();
    assertThat(searchHits).hasSize(1);
    Literature literature = (Literature) searchHits.getSearchHit(0).getContent();
    assertThat(literature.documentNumber()).isEqualTo("KALU000000001");

    searchHits = advancedSearchService.searchAll("n1", Pageable.unpaged()).getSearchHits();
    assertThat(searchHits).hasSize(1);
    Norm norm = (Norm) searchHits.getSearchHit(0).getContent();
    assertThat(norm.getId()).isEqualTo("n1");
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
    Literature caseLaw = searchHits.getSearchHit(0).getContent();
    assertThat(caseLaw.documentNumber()).isEqualTo("KALU000000001");
  }

  @Test
  @DisplayName("Advanced search for norm works")
  void advancedSearchForNormWorks() {

    var searchHits = advancedSearchService.searchNorm("n1", Pageable.unpaged()).getSearchHits();
    assertThat(searchHits).hasSize(1);
    Norm caseLaw = searchHits.getSearchHit(0).getContent();
    assertThat(caseLaw.getId()).isEqualTo("n1");
  }
}
