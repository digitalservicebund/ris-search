package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.schema.PartialCollectionViewSchema;
import org.springframework.data.domain.Page;

/**
 * Utility class for constructing a {@link PartialCollectionViewSchema} based on pagination details.
 * The class provides methods to generate navigation links (first, last, next, previous) for a
 * paginated collection view.
 */
public class PartialCollectionViewMapper {
  // Private constructor to hide the implicit public one and prevent instantiation
  private PartialCollectionViewMapper() {}

  /**
   * Constructs a PartialCollectionViewSchema object based on the given prefix and page information.
   * The method generates navigation links (first, last, next, previous) for a paginated result set.
   *
   * @param prefix the base URL or path that forms the prefix for the generated navigation links
   * @param page the page object containing pagination details such as current page number, size,
   *     and total pages
   * @return a PartialCollectionViewSchema representing the first, last, next, and previous links
   *     for the paginated collection view
   */
  public static PartialCollectionViewSchema fromPage(final String prefix, final Page<?> page) {
    var builder = PartialCollectionViewSchema.builder();

    String queryString = "%s?pageIndex=%d&size=%d";

    builder.first(String.format(queryString, prefix, 0, page.getSize()));
    builder.last(String.format(queryString, prefix, page.getTotalPages() - 1, page.getSize()));

    if (page.hasNext()) {
      builder.next(String.format(queryString, prefix, page.getNumber() + 1, page.getSize()));
    }
    if (page.hasPrevious()) {
      builder.previous(String.format(queryString, prefix, page.getNumber() - 1, page.getSize()));
    }
    return builder.build();
  }
}
