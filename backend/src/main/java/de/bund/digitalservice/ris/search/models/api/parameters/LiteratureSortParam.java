package de.bund.digitalservice.ris.search.models.api.parameters;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents the sorting parameters for literature search queries.
 *
 * <p>The `sort` field determines the attribute by which the results should be sorted. By default,
 * results are sorted based on the relevance score calculated by OpenSearch.
 *
 * <p>Valid options for the `sort` field include: - "date": Sort results by the date. -
 * "documentNumber": Sort results by the document number. - "default" or leaving it unset: Sort
 * results in descending order of relevance.
 *
 * <p>To sort in descending order, prepend a `-` to the field name (e.g., `-date`).
 */
@Data
public class LiteratureSortParam {
  @Schema(
      description =
          "The field to sort the results by. Default is the relevance score calculated by OpenSearch. Valid usage of the sort field are : date and documentNumber and not setting the sort field (sort by relevance descending)."
              + "Add a leading - to set the order to descending (-date)")
  @Pattern(regexp = "^-?(|default|date|DATUM|documentNumber)$")
  @Nullable
  String sort = null;
}
