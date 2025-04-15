package de.bund.digitalservice.ris.search.unit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.bund.digitalservice.ris.search.mapper.PartialCollectionViewMapper;
import de.bund.digitalservice.ris.search.schema.PartialCollectionViewSchema;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class PartialCollectionViewMapperTest {

  @Test
  @DisplayName("has correct links to other pages if the result set has more than one page")
  void fromDomainPageMultiple() {
    int pageSize = 5;
    int lastPageSize = 3;

    int total = pageSize * 2 + lastPageSize;
    String prefix = "/item";

    var firstPageImpl = new PageImpl<>(List.of(), PageRequest.of(0, pageSize), total);
    PartialCollectionViewSchema firstPage =
        PartialCollectionViewMapper.fromPage(prefix, firstPageImpl);

    var middlePageImpl = new PageImpl<>(List.of(), PageRequest.of(1, pageSize), total);
    PartialCollectionViewSchema middlePage =
        PartialCollectionViewMapper.fromPage(prefix, middlePageImpl);

    var lastPageImpl = new PageImpl<>(List.of(), PageRequest.of(2, pageSize), total);
    PartialCollectionViewSchema lastPage =
        PartialCollectionViewMapper.fromPage(prefix, lastPageImpl);

    // all first links should point to the first ID
    String firstPageId = prefix + "?pageIndex=0&size=" + pageSize;
    assertEquals(firstPageId, firstPage.first());
    assertEquals(firstPageId, middlePage.first());
    assertEquals(firstPageId, lastPage.first());

    // all last links should point to the last ID
    String lastPageId = prefix + "?pageIndex=2&size=" + pageSize;
    assertEquals(lastPageId, firstPage.last());
    assertEquals(lastPageId, middlePage.last());
    assertEquals(lastPageId, lastPage.last());

    // links between first and middle page should be correct
    String middlePageId = prefix + "?pageIndex=1&size=" + pageSize;
    assertEquals(middlePageId, firstPage.next());
    assertEquals(firstPageId, middlePage.previous());

    // links between middle and last page should be correct
    assertEquals(lastPageId, middlePage.next());
    assertEquals(middlePageId, lastPage.previous());
  }
}
