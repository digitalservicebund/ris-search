package de.bund.digitalservice.ris.search.models.api.parameters;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents the sorting parameters for case law search queries.
 *
 * <p>The `sort` field specifies the attribute by which the results should be sorted. By default,
 * results are sorted by the relevance score calculated by OpenSearch.
 *
 * <p>Valid options for the `sort` field include: - "date": Sorts results by the date. -
 * "courtName": Sorts results by the name of the court. - "documentNumber": Sorts results by the
 * document number. - Not setting the `sort` field or using "default": Sorts results in descending
 * order of relevance.
 *
 * <p>To sort in descending order for any field, prepend a `-
 */
@Data
public class CaseLawSortParam {
  @Schema(
      description =
          "The field to sort the results by. Default is the relevance score calculated by OpenSearch. Valid usage of the sort field are : date, courtName, documentNumber and not setting the sort field (sort by relevance descending)."
              + "Add a leading - to set the order to descending (-date)")
  @Pattern(regexp = "^-?(|default|date|DATUM|courtName|documentNumber)$")
  @Nullable
  String sort = null;
}
