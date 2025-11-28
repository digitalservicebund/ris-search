package de.bund.digitalservice.ris.search.models.api.parameters;

import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.models.errors.CustomError;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents the parameters used for searching norms within the system.
 *
 * <p>The provided parameters allow filtering and refining search results based on specific criteria
 * related to European legal norms.
 *
 * <p>Fields included:
 *
 * <p>- `eli`: Searches based on the European Legislation Identifier (ELI). Currently, only
 * searching by work ELI is supported. Future enhancements might allow general ELI prefix matching.
 *
 * <p>- `temporalCoverageFrom`: Filters results to include only expressions in force on or after the
 * specified date. The date must be provided in `YYYY-MM-DD` format. Differs from `dateFrom` which
 * refers to the adoption or signature date of the legislation. If both `temporalCoverageFrom` and
 * `temporalCoverageTo` are set, expressions active during any day within the date range are
 * returned.
 *
 * <p>- `temporalCoverageTo`: Filters results to include only expressions in force on or before the
 * specified date. The date must be provided in `YYYY-MM-DD` format. Differs from `dateTo` which
 * refers to the adoption or signature date of the legislation.
 *
 * <p>- `mostRelevantOn`: Limits results to exactly one expression per work, selected as the most
 * relevant per the specified date. Relevance is determined by checking the expression in force on
 * the given date, followed by the expression that comes into force soonest after, and lastly by the
 * most recent past expression in force. Filtering may exclude the most relevant expression
 * depending on other parameters.
 *
 * <p>The functionality also includes validation:
 *
 * <p>- The `validate` method ensures logical consistency between `temporalCoverageFrom` and
 * `temporalCoverageTo`. Throws an exception if `temporalCoverageFrom` is after
 * `temporalCoverageTo`.
 */
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

  @Schema(
      description =
          "Filters the result set so every work returns exactly one expression. Most relevant is defined as : The expression in force on that date if it exists, then the expression that would next be in force if that exists, then the most recent expression that was in force. If other filters are used the work may return 0 expressions due to the most relevant expression being filtered out.")
  LocalDate mostRelevantOn;

  /**
   * Validates the temporal coverage range for the search parameters.
   *
   * <p>This method checks if both `temporalCoverageFrom` and `temporalCoverageTo` are non-null and
   * ensures that the value of `temporalCoverageFrom` is not after `temporalCoverageTo`.
   *
   * <p>If the validation fails, a {@link CustomValidationException} is thrown containing an error
   * with a code of "invalid_range", specifying "temporalCoverageFrom" as the parameter, and
   * providing a relevant error message.
   *
   * @throws CustomValidationException if `temporalCoverageFrom` is after `temporalCoverageTo`
   */
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
