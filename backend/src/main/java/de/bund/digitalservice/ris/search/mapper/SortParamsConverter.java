package de.bund.digitalservice.ris.search.mapper;

import lombok.NonNull;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Sort;

public class SortParamsConverter {

  private SortParamsConverter() {}

  /**
   * A universal search alias field that points to more specific date fields, like decision date.
   */
  private static final String DATE_ALIAS_FIELD = "DATUM";

  /**
   * Translate a sort String to a Sort object.
   *
   * @param sort SortString to sort by combined with relevance
   * @param defaultSort Sort to be applied in case sort String is empty or "default"
   * @return returns the sort together with document relevance
   */
  public static @NonNull Sort buildSort(String sort, Sort defaultSort) {

    Sort relevance = Sort.by(Sort.Order.desc("_score"));
    if (Strings.isEmpty(sort) || sort.equalsIgnoreCase("default")) {
      return relevance.and(defaultSort);
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

    String mappedFieldName =
        MappingDefinitions.getOpenSearchName(fieldName, MappingDefinitions.ResolutionMode.ALL);
    if (mappedFieldName == null) {
      mappedFieldName = fieldName;
    }
    return Sort.by(direction, mappedFieldName).and(relevance);
  }

  /**
   * Translate a sort String to a Sort object. Defaults to sort by relevance and date in case of an
   * empty String
   *
   * @param sort SortString to sort by combined with relevance
   * @return Sort object
   */
  public static @NonNull Sort buildSort(String sort) {
    return buildSort(sort, Sort.by(Sort.Direction.DESC, DATE_ALIAS_FIELD));
  }
}
