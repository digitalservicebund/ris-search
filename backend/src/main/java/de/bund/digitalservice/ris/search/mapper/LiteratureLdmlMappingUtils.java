package de.bund.digitalservice.ris.search.mapper;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.jspecify.annotations.Nullable;

/** Shared mapping functions between uli/sli literature */
public class LiteratureLdmlMappingUtils {
  private LiteratureLdmlMappingUtils() {}

  /**
   * extracts value of first year of publication based on its date format
   *
   * @param yearsOfPublication List of publication years
   * @return LocalDate of the first publication year
   */
  public static @Nullable LocalDate extractFirstYearOfPublication(List<String> yearsOfPublication) {
    final String firstValue = yearsOfPublication.getFirst().trim();
    try {
      if (firstValue.matches("\\d{4}")) {
        // Format: YYYY → YYYY-01-01
        return LocalDate.of(Integer.parseInt(firstValue), Month.JANUARY, 1);
      } else if (firstValue.matches("\\d{4}-\\d{2}")) {
        // Format: YYYY-MM → YYYY-MM-01
        YearMonth yearMonth = YearMonth.parse(firstValue);
        return yearMonth.atDay(1);
      } else if (firstValue.matches("\\d{4}-\\d{2}-\\d{2}")) {
        // Format: YYYY-MM-DD
        return LocalDate.parse(firstValue);
      } else {
        // Any other unexpected format → return null or handle differently
        return null;
      }
    } catch (DateTimeParseException | NumberFormatException _) {
      // Handle malformed numeric values gracefully
      return null;
    }
  }
}
