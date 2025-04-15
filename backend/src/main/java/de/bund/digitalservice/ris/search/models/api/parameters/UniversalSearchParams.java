package de.bund.digitalservice.ris.search.models.api.parameters;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UniversalSearchParams {
  @Schema(
      description =
          "Searches for the given tokens in searchTerm. If searchTerm contains more than one token, all tokens must be in the document for the document to match.")
  String searchTerm;

  @Schema(
      description =
          "The from (greater than or equal) parameter returns all entities where date is later than, or equal to, the given date.")
  LocalDate dateFrom;

  @Schema(
      description =
          "The to (less than or equal) parameter returns all entities where date is earlier than, or equal to, the given date.")
  LocalDate dateTo;
}
