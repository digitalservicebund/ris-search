package de.bund.digitalservice.ris.search.service.helper;

import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.mapper.MappingDefinitions;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.errors.CustomError;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.utils.SortingUtils;
import lombok.NonNull;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PaginationParamsConverter {

  private PaginationParamsConverter() {}

  public static PageRequest convert(
      PaginationParams paginationParams, MappingDefinitions.ResolutionMode resolutionMode)
      throws CustomValidationException {
    return PageRequest.of(
        paginationParams.getPageIndex(),
        paginationParams.getSize(),
        buildSort(paginationParams.getSort(), resolutionMode));
  }

  public static @NonNull Sort buildSort(
      String sort, MappingDefinitions.ResolutionMode resolutionMode)
      throws CustomValidationException {
    if (Strings.isEmpty(sort) || sort.equalsIgnoreCase("default")) {
      return getDefaultSort(resolutionMode);
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

    if (!isSupportedField(fieldName, resolutionMode)) {
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

  private static boolean isSupportedField(
      String sort, MappingDefinitions.ResolutionMode resolutionMode) {
    return switch (resolutionMode) {
      case ALL -> SortingUtils.documentSortFields.contains(sort);
      case NORMS -> SortingUtils.normsSortFields.contains(sort);
      case CASE_LAW -> SortingUtils.caseLawSortFields.contains(sort);
    };
  }

  private static Sort getDefaultSort(MappingDefinitions.ResolutionMode resolutionMode) {
    return switch (resolutionMode) {
      case ALL -> Sort.by(Sort.Direction.DESC, "DATUM");
      case NORMS -> Sort.by(Sort.Direction.DESC, Norm.Fields.NORMS_DATE);
      case CASE_LAW -> Sort.by(Sort.Direction.DESC, CaseLawDocumentationUnit.Fields.DECISION_DATE);
    };
  }
}
