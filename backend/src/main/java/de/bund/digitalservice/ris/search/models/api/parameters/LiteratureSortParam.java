package de.bund.digitalservice.ris.search.models.api.parameters;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

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
