package de.bund.digitalservice.ris.search.service.helper;

import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.mapper.MappingDefinitions;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class PaginationParamsConverterTest {

  @Test
  void testConvert_withValidParams_shouldReturnPageRequest() throws CustomValidationException {
    // Arrange
    PaginationParams paginationParams = new PaginationParams();
    paginationParams.setPageIndex(0);
    paginationParams.setSize(10);
    paginationParams.setSort("date");
    // Act
    PageRequest result =
        PaginationParamsConverter.convert(
            paginationParams, MappingDefinitions.ResolutionMode.ALL, true);

    // Assert
    Assertions.assertEquals(0, result.getPageNumber());
    Assertions.assertEquals(10, result.getPageSize());
    Assertions.assertEquals(Sort.by(Sort.Direction.ASC, "DATUM"), result.getSort());
  }

  @ParameterizedTest
  @ValueSource(strings = {"default", "", "null"})
  void testBuildSort_withUnsorted_shouldReturnDefaultSort(String value)
      throws CustomValidationException {
    if (value.equals("null")) {
      value = null;
    }
    // Act
    Sort result =
        PaginationParamsConverter.buildSort(value, MappingDefinitions.ResolutionMode.ALL, false);

    // Assert
    Assertions.assertEquals(Sort.by(Sort.Direction.DESC, "DATUM"), result);

    // Act
    Sort resultWithDefault =
        PaginationParamsConverter.buildSort(value, MappingDefinitions.ResolutionMode.ALL, true);

    // Assert
    Assertions.assertEquals(Sort.unsorted(), resultWithDefault);
  }

  @Test
  void testBuildSort_withValidAscField_shouldReturnAscSort() throws CustomValidationException {
    // Act
    Sort result =
        PaginationParamsConverter.buildSort("date", MappingDefinitions.ResolutionMode.ALL, true);

    // Assert
    Assertions.assertEquals(Sort.by(Sort.Direction.ASC, "DATUM"), result);
  }

  @Test
  void testBuildSort_withValidDescField_shouldReturnDescSort() throws CustomValidationException {
    // Act
    Sort result =
        PaginationParamsConverter.buildSort("-date", MappingDefinitions.ResolutionMode.ALL, true);

    // Assert
    Assertions.assertEquals(Sort.by(Sort.Direction.DESC, "DATUM"), result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"unsupportedField", "-unsupportedField"})
  void testBuildSort_withUnsupportedField_shouldThrowCustomValidationException(String value) {

    // Act & Assert
    CustomValidationException exception =
        Assertions.assertThrows(
            CustomValidationException.class,
            () -> {
              PaginationParamsConverter.buildSort(
                  value, MappingDefinitions.ResolutionMode.ALL, true);
            });

    Assertions.assertEquals(
        "Sorting is not supported for unsupportedField", exception.getErrors().get(0).message());
  }
}
