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

  private static Sort RELEVANCE_SORT = Sort.by(Sort.Order.desc("_score"));

  @ParameterizedTest
  @ValueSource(strings = {"default", "", "null"})
  void testBuildSort_withUnsorted_shouldReturnDefaultSort(String value) {

    if (value.equals("null")) {
      value = null;
    }
    // Act
    Sort result = SortParamsConverter.buildSort(value, MappingDefinitions.ResolutionMode.ALL);

    // Assert
    Assertions.assertEquals(RELEVANCE_SORT.and(Sort.by(Sort.Direction.DESC, "DATUM")), result);
  }

  @Test
  void testBuildSort_withValidAscField_shouldReturnAscSort() {
    // Act
    Sort result = SortParamsConverter.buildSort("date", MappingDefinitions.ResolutionMode.ALL);

    // Assert
    Assertions.assertEquals(Sort.by(Sort.Direction.ASC, "DATUM").and(RELEVANCE_SORT), result);
  }

  @Test
  void testBuildSort_withValidDescField_shouldReturnDescSort() {
    // Act
    Sort result = SortParamsConverter.buildSort("-date", MappingDefinitions.ResolutionMode.ALL);

    // Assert
    Assertions.assertEquals(Sort.by(Sort.Direction.DESC, "DATUM").and(RELEVANCE_SORT), result);
  }
}
