package de.bund.digitalservice.ris.search.unit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.mapper.LiteratureSearchSchemaMapper;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LiteratureSearchSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
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

class LiteratureSearchSchemaMapperTest {

  @Test
  @DisplayName("correctly maps a single Page")
  void fromDomainPage() {
    var literature =
        Literature.builder().id("XXLU000000001").documentNumber("XXLU000000001").build();

    SearchHit<Literature> searchHit =
        new SearchHit<>("1", "1", "routing", 1, null, null, null, null, null, null, literature);
    SearchHits<Literature> searchHits =
        new SearchHitsImpl<>(
            1,
            TotalHitsRelation.EQUAL_TO,
            1,
            Duration.of(3, ChronoUnit.SECONDS),
            null,
            "",
            List.of(searchHit),
            null,
            null,
            null);
    SearchPage<Literature> source =
        SearchHitSupport.searchPageFor(searchHits, PageRequest.of(0, 1));

    CollectionSchema<SearchMemberSchema<LiteratureSearchSchema>> destination =
        LiteratureSearchSchemaMapper.fromSearchPage(source);
    LiteratureSearchSchema expectedItem = destination.member().get(0).item();
    assertEquals(literature.documentNumber(), expectedItem.documentNumber());
    assertEquals(1, destination.totalItems());
    assertEquals(destination.member().size(), destination.totalItems());
    assertTrue(destination.id().startsWith(ApiConfig.Paths.LITERATURE));
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
                (AbstractSearchEntity) Literature.builder().documentNumber("doc-" + index).build());

    var firstPageContents = IntStream.range(0, pageSize).mapToObj(buildItem).toList();
    var middlePageContents = IntStream.range(pageSize, pageSize * 2).mapToObj(buildItem).toList();
    var lastPageContents =
        IntStream.range(pageSize * 2, pageSize * 2 + lastPageSize).mapToObj(buildItem).toList();

    int total = firstPageContents.size() + middlePageContents.size() + lastPageContents.size();

    var firstPageImpl = createSearchPage(firstPageContents, 0, pageSize, total);
    CollectionSchema<SearchMemberSchema<LiteratureSearchSchema>> firstPage =
        LiteratureSearchSchemaMapper.fromSearchPage(firstPageImpl);

    var middlePageImpl = createSearchPage(middlePageContents, 1, pageSize, total);
    CollectionSchema<SearchMemberSchema<LiteratureSearchSchema>> middlePage =
        LiteratureSearchSchemaMapper.fromSearchPage(middlePageImpl);

    var lastPageImpl = createSearchPage(lastPageContents, 2, pageSize, total);
    CollectionSchema<SearchMemberSchema<LiteratureSearchSchema>> lastPage =
        LiteratureSearchSchemaMapper.fromSearchPage(lastPageImpl);

    String prefix = ApiConfig.Paths.LITERATURE + "/doc-";

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

    // all IDs should point to literature resources
    assertTrue(firstPage.id().startsWith(ApiConfig.Paths.LITERATURE));
    assertTrue(middlePage.id().startsWith(ApiConfig.Paths.LITERATURE));
    assertTrue(lastPage.id().startsWith(ApiConfig.Paths.LITERATURE));
  }

  private SearchPage<AbstractSearchEntity> createSearchPage(
      List<SearchHit<AbstractSearchEntity>> searchHits, int pageNumber, int pageSize, int total) {
    return SearchHitSupport.searchPageFor(
        new SearchHitsImpl<>(
            total,
            TotalHitsRelation.EQUAL_TO,
            1,
            Duration.of(3, ChronoUnit.SECONDS),
            null,
            "",
            searchHits,
            null,
            null,
            null),
        PageRequest.of(pageNumber, pageSize));
  }

  private String getItemId(
      CollectionSchema<SearchMemberSchema<LiteratureSearchSchema>> page, int index) {
    return (page.member().get(index).item()).id();
  }
}
