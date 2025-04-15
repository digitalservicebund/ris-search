package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.schema.PartialCollectionViewSchema;
import org.springframework.data.domain.Page;

public class PartialCollectionViewMapper {
  // Private constructor to hide the implicit public one and prevent instantiation
  private PartialCollectionViewMapper() {}

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
