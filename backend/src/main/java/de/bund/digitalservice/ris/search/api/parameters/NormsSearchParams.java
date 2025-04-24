package de.bund.digitalservice.ris.search.api.parameters;

import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.models.errors.CustomError;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NormsSearchParams {
  @Schema(
      description =
          "Search by European Legislation Identifier (ELI). Right now only searching by work ELI is supported, but a general eli prefix match might be supported in the future.")
  String eli;

  @Schema(
      description =
          "Filters the result set to only return expressions that are in force *on or after* the provided date. The parameter should be provided in `YYYY-MM-DD` format. Differs from `dateFrom`, which refers to the date of adoption or signature of the legislation. If both `temporalCoverageFrom` and `temporalCoverageTo` are given, this will output all expressions that were in force during at least one day between the two dates. To get all expressions for one specific day, set both parameters to the same day.")
  LocalDate temporalCoverageFrom;

  @Schema(
      description =
          "Filters the result set to only return expressions that are in force *on or before* the provided date. The parameter should be provided in `YYYY-MM-DD` format. Differs from `dateTo`, which refers to the date of adoption or signature of the legislation.")
  LocalDate temporalCoverageTo;

  public void validate() throws CustomValidationException {
    if (temporalCoverageFrom != null
        && temporalCoverageTo != null
        && temporalCoverageFrom.isAfter(temporalCoverageTo)) {
      throw new CustomValidationException(
          CustomError.builder()
              .code("invalid_range")
              .parameter("temporalCoverageFrom")
              .message("temporalCoverageFrom must not be after temporalCoverageTo")
              .build());
    }
  }
}
