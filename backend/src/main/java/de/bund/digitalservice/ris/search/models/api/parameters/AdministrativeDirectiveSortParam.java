package de.bund.digitalservice.ris.search.models.api.parameters;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the sorting parameters specifically for administrative directives in a search. This
 * class is used to define the sorting field and order for query results.
 *
 * <p>The sorting field determines the order in which the results are returned. By default, the
 * results are sorted by relevance descending, as calculated by OpenSearch.
 *
 * <p>Valid values for the sort field are: - `date`: Sort results by the date value. -
 * `documentNumber`: Sort results by the document number. - An empty value or "default": Sort
 * results by relevance descending.
 *
 * <p>To specify descending order for a field, prefix the field name with a hyphen (`-`). For
 * instance, a value of `-date` specifies sorting by date in descending order.
 */
@Data
public class AdministrativeDirectiveSortParam {
  @Schema(
      description =
          "The field to sort the results by. Default is the relevance score calculated by OpenSearch. Valid usage of the sort field are : date and documentNumber and not setting the sort field (sort by relevance descending)."
              + "Add a leading - to set the order to descending (-date)")
  @Pattern(regexp = "^-?(|default|DATUM|documentNumber)$")
  @Nullable
  String sort = null;
}
