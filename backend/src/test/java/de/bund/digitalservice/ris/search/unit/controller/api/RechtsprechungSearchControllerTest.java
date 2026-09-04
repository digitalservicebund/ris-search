package de.bund.digitalservice.ris.search.unit.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.controller.api.RechtsprechungSearchController;
import de.bund.digitalservice.ris.search.models.CourtSearchResult;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSortParam;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;

/**
 * Basic unit-level coverage for {@link RechtsprechungSearchController}, which mirrors {@code
 * CaseLawSearchController} onto {@code /v1/rechtsprechung/**}. Broader end-to-end verification
 * (that each mirrored endpoint actually matches its case-law counterpart) lives in {@code
 * RechtsprechungMirrorsCaseLawApiTest}.
 */
@ExtendWith(MockitoExtension.class)
class RechtsprechungSearchControllerTest {

  @InjectMocks RechtsprechungSearchController controller;

  @Mock CaseLawService caseLawService;

  @Test
  void itSearchesAndFilters() throws Exception {
    var searchHits =
        new SearchHitsImpl<CaseLawDocumentationUnit>(
            0,
            TotalHitsRelation.EQUAL_TO,
            0,
            Duration.of(1, ChronoUnit.SECONDS),
            null,
            null,
            List.of(),
            null,
            null,
            null);
    var page = SearchHitSupport.searchPageFor(searchHits, PageRequest.of(0, 10));
    when(caseLawService.simpleSearchCaseLaw(any(), any(), any())).thenReturn(page);

    var response =
        controller.searchAndFilter(
            new CaseLawSearchParams(),
            UniversalSearchParams.builder().build(),
            new PaginationParams(),
            new CaseLawSortParam());

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void itReturnsCourts() {
    when(caseLawService.getCourts(eq("BGH")))
        .thenReturn(List.of(new CourtSearchResult("BGH", 1L, "Bundesgerichtshof")));

    var response = controller.getCourts("BGH");

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).hasSize(1);
  }
}
