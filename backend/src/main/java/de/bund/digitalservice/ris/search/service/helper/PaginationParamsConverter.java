package de.bund.digitalservice.ris.search.service.helper;

import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.mapper.MappingDefinitions;
import de.bund.digitalservice.ris.search.mapper.SortingDefinitions;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.errors.CustomError;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PaginationParamsConverter {

  private PaginationParamsConverter() {}

  public static PageRequest convert(
      PaginationParams paginationParams,
      MappingDefinitions.ResolutionMode resolutionMode,
      @NotNull boolean sortByRelevance)
      throws CustomValidationException {
    return PageRequest.of(
        paginationParams.getPageIndex(),
        paginationParams.getSize(),
        buildSort(paginationParams.getSort(), resolutionMode, sortByRelevance));
  }

  public static @NonNull Sort buildSort(
      String sort, MappingDefinitions.ResolutionMode resolutionMode, boolean sortByRelevance)
      throws CustomValidationException {
    if (Strings.isEmpty(sort) || sort.equalsIgnoreCase("default")) {
      return sortByRelevance ? Sort.unsorted() : SortingDefinitions.getDefaultSort(resolutionMode);
    }

    Sort.Direction direction;
    String fieldName;
    if (sort.startsWith("-")) {
      fieldName = sort.substring(1);
      direction = Sort.Direction.DESC;
    } else {
      fieldName = sort;
      direction = Sort.Direction.ASC;
    }

    if (!SortingDefinitions.canSortByField(fieldName, resolutionMode)) {
      throw new CustomValidationException(
          new CustomError(
              "invalid_sort_parameter",
              "Sorting is not supported for %s".formatted(fieldName),
              "sort"));
    }

    String mappedFieldName = MappingDefinitions.getOpenSearchName(fieldName, resolutionMode);
    if (mappedFieldName == null) {
      mappedFieldName = fieldName;
    }
    return Sort.by(direction, mappedFieldName);
  }
}
