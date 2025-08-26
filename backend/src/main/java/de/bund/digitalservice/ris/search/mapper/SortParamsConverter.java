package de.bund.digitalservice.ris.search.mapper;

import lombok.NonNull;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Sort;

public class SortParamsConverter {

  private SortParamsConverter() {}

  public static @NonNull Sort buildSort(
      String sort, MappingDefinitions.ResolutionMode resolutionMode, boolean sortByRelevance) {
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

    String mappedFieldName = MappingDefinitions.getOpenSearchName(fieldName, resolutionMode);
    if (mappedFieldName == null) {
      mappedFieldName = fieldName;
    }
    return Sort.by(direction, mappedFieldName);
  }
}
