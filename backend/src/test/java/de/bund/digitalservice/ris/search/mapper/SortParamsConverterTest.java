package de.bund.digitalservice.ris.search.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class SortParamsConverterTest {

  private static final Sort RELEVANCE = Sort.by(Sort.Order.desc("_score"));
  private static final String DATE_FIELD = "DATUM";

  @ParameterizedTest
  @ValueSource(strings = {"default", "", "null"})
  void testBuildSort_withUnsorted_shouldReturnDefaultSort(String value) {

    if (value.equals("null")) {
      value = null;
    }
    // Act
    Sort result = SortParamsConverter.buildSort(value);

    // Assert
    Sort expected =
        Sort.by(Sort.Order.desc("_score").with(Sort.NullHandling.NATIVE))
            .and(Sort.by(Sort.Order.desc(DATE_FIELD).with(Sort.NullHandling.NATIVE)));
    Assertions.assertEquals(expected, result);
  }

  @Test
  void testBuildSort_withValidAscField_shouldReturnAscSortWithNativeNullHandling() {
    // Act
    Sort result = SortParamsConverter.buildSort("date");

    // Assert
    Sort expected =
        Sort.by(Sort.Order.asc(DATE_FIELD).with(Sort.NullHandling.NATIVE))
            .and(Sort.by(Sort.Order.desc("_score").with(Sort.NullHandling.NATIVE)));
    Assertions.assertEquals(expected, result);
  }

  @Test
  void testBuildSort_withValidDescField_shouldReturnDescSortWithNativeNullHandling() {
    // Act
    Sort result = SortParamsConverter.buildSort("-date");

    // Assert
    Sort expected =
        Sort.by(Sort.Order.desc(DATE_FIELD).with(Sort.NullHandling.NATIVE))
            .and(Sort.by(Sort.Order.desc("_score").with(Sort.NullHandling.NATIVE)));
    Assertions.assertEquals(expected, result);
  }

  @Test
  void testBuildSortWithNullHandlingLast_asc_shouldApplyNullsLast() {
    // Act
    Sort result = SortParamsConverter.buildSortWithNullHandlingLast("date");

    // Assert
    Sort expected =
        Sort.by(Sort.Order.asc(DATE_FIELD).with(Sort.NullHandling.NULLS_LAST))
            .and(Sort.by(Sort.Order.desc("_score").with(Sort.NullHandling.NULLS_LAST)));
    Assertions.assertEquals(expected, result);
  }

  @Test
  void testBuildSortWithNullHandlingLast_desc_shouldApplyNullsLast() {
    // Act
    Sort result = SortParamsConverter.buildSortWithNullHandlingLast("-date");

    // Assert
    Sort expected =
        Sort.by(Sort.Order.desc(DATE_FIELD).with(Sort.NullHandling.NULLS_LAST))
            .and(Sort.by(Sort.Order.desc("_score").with(Sort.NullHandling.NULLS_LAST)));
    Assertions.assertEquals(expected, result);
  }

  @Test
  void testBuildSortWithNullHandlingLast_default_shouldApplyNullsLast() {
    // Act
    Sort result = SortParamsConverter.buildSortWithNullHandlingLast(null);

    // Assert
    Sort expected =
        Sort.by(Sort.Order.desc("_score").with(Sort.NullHandling.NULLS_LAST))
            .and(Sort.by(Sort.Order.desc(DATE_FIELD).with(Sort.NullHandling.NULLS_LAST)));
    Assertions.assertEquals(expected, result);
  }
}
