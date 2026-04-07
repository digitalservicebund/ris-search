package de.bund.digitalservice.ris.search.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
// For all sort requests we expect all parameters to have nulls last
class SortParamsConverterTest {

  @ParameterizedTest
  @ValueSource(strings = {"default", "", "null"})
  void defaultSortUsesScoreThenDatumField(String value) {

    if (value.equals("null")) {
      value = null;
    }
    // Act
    Sort result = SortParamsConverter.buildSort(value);

    // Assert
    Sort expected =
        Sort.by(Sort.Order.desc("_score").nullsLast())
            .and(Sort.by(Sort.Order.desc(MappingDefinitions.DATUM_FIELD).nullsLast()));
    Assertions.assertEquals(expected, result);
  }

  @Test
  void dateSortUsesDatumFieldWThenScore() {
    // Act
    Sort result = SortParamsConverter.buildSort("date");

    // Assert
    Sort expected =
        Sort.by(Sort.Order.asc(MappingDefinitions.DATUM_FIELD).nullsLast())
            .and(Sort.by(Sort.Order.desc("_score").nullsLast()));
    Assertions.assertEquals(expected, result);
  }

  @Test
  void reverseDateSortUsesDatumFieldWThenScore() {
    // Act
    Sort result = SortParamsConverter.buildSort("-date");

    // Assert
    Sort expected =
        Sort.by(Sort.Order.desc(MappingDefinitions.DATUM_FIELD).nullsLast())
            .and(Sort.by(Sort.Order.desc("_score").nullsLast()));
    Assertions.assertEquals(expected, result);
  }
}
