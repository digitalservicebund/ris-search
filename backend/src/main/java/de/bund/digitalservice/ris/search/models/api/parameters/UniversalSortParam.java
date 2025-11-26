package de.bund.digitalservice.ris.search.models.api.parameters;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents the sorting parameters for universal search queries.
 *
 * <p>The `sort` field specifies the attribute by which the results should be sorted. By default,
 * results are sorted based on the relevance score calculated by OpenSearch.
 *
 * <p>Valid values for the `sort` field include the following: - "date": Sort by date. -
 * "temporalCoverageFrom": Sort by the start of the temporal coverage. - "legislationIdentifier":
 * Sort by legislation identifier. - "courtName": Sort by court name. - "documentNumber": Sort by
 * the document number. - Not setting the `sort` field or using "default": Sort by descending
 * relevance.
 *
 * <p>To sort in descending order for any field, prefix the field with a `-` (e.g., `-date`).
 */
@Data
public class UniversalSortParam {
  @Schema(
      description =
          "The field to sort the results by. Default is the relevance score calculated by OpenSearch. Valid usage of the sort field are : date, temporalCoverageFrom, legislationIdentifier, courtName, documentNumber and not setting the sort field (sort by relevance descending)."
              + "Add a leading - to set the order to descending (-date)")
  @Pattern(
      regexp =
          "^-?(|default|date|DATUM|courtName|documentNumber|temporalCoverageFrom|legislationIdentifier)$")
  @Nullable
  String sort = null;
}
