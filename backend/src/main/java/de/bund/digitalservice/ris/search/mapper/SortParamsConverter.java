package de.bund.digitalservice.ris.search.mapper;

import lombok.NonNull;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Sort;

/**
 * A utility class for converting sort strings into Spring Data's {@link Sort} objects. This class
 * supports sorting by relevance and an alias for date fields, and also provides various handling
 * for null values during sorting.
 */
public class SortParamsConverter {

  private SortParamsConverter() {}

  /**
   * A universal search alias field that points to more specific date fields, like decision date.
   */
  private static final String DATE_ALIAS_FIELD = "DATUM";

  /**
   * Translate a sort String to a Sort object. Defaults to sort by relevance and date in case of an
   * empty String.
   *
   * @param sort SortString to sort by combined with relevance
   * @param nullHandling how to handle null values during sorting
   * @return returns the sort together with document relevance
   */
  public static @NonNull Sort buildSort(String sort, Sort.NullHandling nullHandling) {

    Sort relevance = Sort.by(Sort.Order.desc("_score").with(nullHandling));
    if (Strings.isEmpty(sort) || sort.equalsIgnoreCase("default")) {
      Sort defaultSort = Sort.by(Sort.Order.desc(DATE_ALIAS_FIELD).with(nullHandling));
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

    Sort.Order order = new Sort.Order(direction, mappedFieldName).with(nullHandling);

    return Sort.by(order).and(relevance);
  }

  /**
   * Translate a sort String to a Sort object adding the handling of null values as native
   * (default).
   *
   * @param sort SortString to sort by combined with relevance
   * @return Sort object
   */
  public static @NonNull Sort buildSort(String sort) {
    return buildSort(
        sort,
        Sort.NullHandling
            .NATIVE); // Default null handling, Spring data does not add any special handling for
    // nulls
  }

  /**
   * Translate a sort String to a Sort object adding the handling of null values as last.
   *
   * @param sort SortString to sort by combined with relevance
   * @return Sort object
   */
  public static @NonNull Sort buildSortWithNullHandlingLast(String sort) {
    return buildSort(
        sort,
        Sort.NullHandling
            .NULLS_LAST); // Those null values in the sorting date will always appear last, both in
    // ASC and DESC
  }
}
