package de.bund.digitalservice.ris.search.models.api.parameters;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents the sorting parameters for norms search queries.
 *
 * <p>The `sort` field allows specifying the attribute by which the results should be sorted. By
 * default, the sorting is based on the relevance score calculated by OpenSearch. Valid options for
 * the sort field are: - "date": Sort results by the date. - "temporalCoverageFrom": Sort by the
 * start of the temporal coverage. - "legislationIdentifier": Sort by legislation identifier. -
 * Leaving the field unset or setting it to "default" will sort results by relevance in descending
 * order.
 *
 * <p>To sort in descending order for any field, prepend a `-` to the field name (e.g., `-date`).
 */
@Data
public class NormsSortParam {
  @Schema(
      description =
          "The field to sort the results by. Default is the relevance score calculated by OpenSearch. Valid usage of the sort field are : date, temporalCoverageFrom, legislationIdentifier and not setting the sort field (sort by relevance descending)."
              + "Add a leading - to set the order to descending (-date)")
  @Pattern(regexp = "^-?(|default|date|temporalCoverageFrom|legislationIdentifier|DATUM)$")
  @Nullable
  String sort = null;
}
