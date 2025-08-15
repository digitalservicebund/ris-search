package de.bund.digitalservice.ris.search.service.helper;

import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.mapper.MappingDefinitions;
import de.bund.digitalservice.ris.search.mapper.SortParamsConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class SortParamsConverterTest {

  @ParameterizedTest
  @ValueSource(strings = {"default", "", "null"})
  void testBuildSort_withUnsorted_shouldReturnDefaultSort(String value)
      throws CustomValidationException {
    if (value.equals("null")) {
      value = null;
    }
    // Act
    Sort result =
        SortParamsConverter.buildSort(value, MappingDefinitions.ResolutionMode.ALL, false);

    // Assert
    Assertions.assertEquals(Sort.by(Sort.Direction.DESC, "DATUM"), result);

    // Act
    Sort resultWithDefault =
        SortParamsConverter.buildSort(value, MappingDefinitions.ResolutionMode.ALL, true);

    // Assert
    Assertions.assertEquals(Sort.unsorted(), resultWithDefault);
  }

  @Test
  void testBuildSort_withValidAscField_shouldReturnAscSort() {
    // Act
    Sort result =
        SortParamsConverter.buildSort("date", MappingDefinitions.ResolutionMode.ALL, true);

    // Assert
    Assertions.assertEquals(Sort.by(Sort.Direction.ASC, "DATUM"), result);
  }

  @Test
  void testBuildSort_withValidDescField_shouldReturnDescSort() {
    // Act
    Sort result =
        SortParamsConverter.buildSort("-date", MappingDefinitions.ResolutionMode.ALL, true);

    // Assert
    Assertions.assertEquals(Sort.by(Sort.Direction.DESC, "DATUM"), result);
  }
}
