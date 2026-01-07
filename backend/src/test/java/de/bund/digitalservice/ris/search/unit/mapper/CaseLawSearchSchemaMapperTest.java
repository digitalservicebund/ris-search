package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.mapper.CaseLawSearchSchemaMapper;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.schema.CaseLawSearchSchema;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;

class CaseLawSearchSchemaMapperTest {

  @Test
  @DisplayName("correctly maps a single Page")
  void fromDomainPage() {
    var element =
        CaseLawDocumentationUnit.builder()
            .id("id1")
            .documentNumber("BRR RRR RRR R1")
            .ecli("some-ecli")
            .build();
    SearchHit<CaseLawDocumentationUnit> searchHit =
        new SearchHit<>("1", "1", "routing", 1, null, null, null, null, null, null, element);
    SearchHits<CaseLawDocumentationUnit> searchHits =
        new SearchHitsImpl<>(
            1,
            TotalHitsRelation.EQUAL_TO,
            1,
            Duration.of(10, ChronoUnit.SECONDS),
            null,
            null,
            List.of(searchHit),
            null,
            null,
            null);
    SearchPage<CaseLawDocumentationUnit> source =
        SearchHitSupport.searchPageFor(searchHits, PageRequest.of(0, 1));

    CollectionSchema<SearchMemberSchema<CaseLawSearchSchema>> destination =
        CaseLawSearchSchemaMapper.fromSearchPage(source);
    CaseLawSearchSchema expectedItem = destination.member().get(0).item();
    assertEquals(element.documentNumber(), expectedItem.documentNumber());
    assertEquals(1, destination.totalItems());
    assertEquals(destination.member().size(), destination.totalItems());
    assertTrue(destination.id().startsWith(ApiConfig.Paths.CASELAW));
  }

  @Test
  @DisplayName("has correct links to other pages if the result set has more than one page")
  void fromDomainPageMultiple() {
    int pageSize = 5;
    int lastPageSize = 3;

    IntFunction<SearchHit<AbstractSearchEntity>> buildItem =
        index ->
            new SearchHit<>(
                String.valueOf(index),
                String.valueOf(index),
                "routing",
                1,
                null,
                null,
                null,
                null,
                null,
                null,
                (AbstractSearchEntity)
                    CaseLawDocumentationUnit.builder().documentNumber("doc-" + index).build());

    var firstPageContents = IntStream.range(0, pageSize).mapToObj(buildItem).toList();
    var middlePageContents = IntStream.range(pageSize, pageSize * 2).mapToObj(buildItem).toList();
    var lastPageContents =
        IntStream.range(pageSize * 2, pageSize * 2 + lastPageSize).mapToObj(buildItem).toList();

    int total = firstPageContents.size() + middlePageContents.size() + lastPageContents.size();

    var firstPageImpl = createSearchPage(firstPageContents, 0, pageSize, total);
    CollectionSchema<SearchMemberSchema<CaseLawSearchSchema>> firstPage =
        CaseLawSearchSchemaMapper.fromSearchPage(firstPageImpl);

    var middlePageImpl = createSearchPage(middlePageContents, 1, pageSize, total);
    CollectionSchema<SearchMemberSchema<CaseLawSearchSchema>> middlePage =
        CaseLawSearchSchemaMapper.fromSearchPage(middlePageImpl);

    var lastPageImpl = createSearchPage(lastPageContents, 2, pageSize, total);
    CollectionSchema<SearchMemberSchema<CaseLawSearchSchema>> lastPage =
        CaseLawSearchSchemaMapper.fromSearchPage(lastPageImpl);

    String prefix = ApiConfig.Paths.CASELAW + "/doc-";

    assertEquals(prefix + "0", getItemId(firstPage, 0));
    assertEquals(prefix + "4", getItemId(firstPage, firstPage.member().size() - 1));

    assertEquals(prefix + "5", getItemId(middlePage, 0));
    assertEquals(prefix + "9", getItemId(middlePage, middlePage.member().size() - 1));

    assertEquals(prefix + "10", getItemId(lastPage, 0));
    assertEquals(prefix + "12", getItemId(lastPage, lastPage.member().size() - 1));

    assertEquals(firstPageContents.size(), firstPage.member().size());
    assertEquals(middlePageContents.size(), middlePage.member().size());
    assertEquals(lastPageContents.size(), lastPage.member().size());

    assertEquals(total, firstPage.totalItems());
    assertEquals(total, middlePage.totalItems());
    assertEquals(total, lastPage.totalItems());

    // all IDs should point to case law resources
    assertTrue(firstPage.id().startsWith(ApiConfig.Paths.CASELAW));
    assertTrue(middlePage.id().startsWith(ApiConfig.Paths.CASELAW));
    assertTrue(lastPage.id().startsWith(ApiConfig.Paths.CASELAW));
  }

  @Test
  void convertTextmatchKeys() {
    var searchHit =
        new SearchHit<>(
            "my-index",
            "1",
            null,
            1.0f,
            null,
            Map.of("snake_case.text", List.of("match")),
            Map.of(),
            null,
            null,
            null,
            CaseLawDocumentationUnit.builder().documentNumber("docNr").build());

    var list = CaseLawSearchSchemaMapper.getTextMatches(searchHit);
    assertThat(list.getFirst().name()).isEqualTo("snakeCase");
  }

  private SearchPage<AbstractSearchEntity> createSearchPage(
      List<SearchHit<AbstractSearchEntity>> searchHits, int pageNumber, int pageSize, int total) {
    return SearchHitSupport.searchPageFor(
        new SearchHitsImpl<>(
            total,
            TotalHitsRelation.EQUAL_TO,
            1,
            Duration.of(10, ChronoUnit.SECONDS),
            null,
            null,
            searchHits,
            null,
            null,
            null),
        PageRequest.of(pageNumber, pageSize));
  }

  private String getItemId(
      CollectionSchema<SearchMemberSchema<CaseLawSearchSchema>> page, int index) {
    return (page.member().get(index).item()).id();
  }
}
