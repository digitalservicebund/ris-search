package de.bund.digitalservice.ris.search.unit.utils;

import static de.bund.digitalservice.ris.search.utils.DateUtils.toDateIntervalString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.bund.digitalservice.ris.search.utils.DateUtils;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DateUtilsTest {
  private static final Clock FIXED_BERLIN_CLOCK =
      Clock.fixed(Instant.parse("2024-01-01T12:00:00Z"), ZoneId.of("Europe/Berlin"));

  @ParameterizedTest(name = "Test valid date from {0} to {1} is {2}")
  @MethodSource("getTestDates")
  void testIsValidDate(LocalDate from, LocalDate to, boolean expected) {
    assertEquals(expected, DateUtils.isActive(from, to, FIXED_BERLIN_CLOCK));
  }

  private static List<Arguments> getTestDates() {
    LocalDate pastDate = LocalDate.of(2023, Month.JANUARY, 1);
    LocalDate currentDateInGermany = LocalDate.now(FIXED_BERLIN_CLOCK);
    return List.of(
        Arguments.of(null, null, true),
        Arguments.of(pastDate, null, true),
        Arguments.of(pastDate, pastDate, false),
        Arguments.of(pastDate, currentDateInGermany.plusDays(1), true),
        Arguments.of(null, currentDateInGermany.plusDays(1), true),
        Arguments.of(null, currentDateInGermany, true),
        Arguments.of(currentDateInGermany, currentDateInGermany, true),
        Arguments.of(currentDateInGermany, null, true),
        Arguments.of(null, pastDate, false),
        Arguments.of(currentDateInGermany.plusDays(1), null, false));
  }

  @Test
  void testToDateStringReturnsNullWhenDateIsNull() {
    assertNull(DateUtils.toDateString(null));
  }

  @Test
  void testToDateIntervalString() {
    LocalDate start = LocalDate.of(2021, Month.JANUARY, 1);
    LocalDate end = LocalDate.of(2021, Month.DECEMBER, 31);

    // Case: Start Date, End Date
    assertEquals("2021-01-01/2021-12-31", toDateIntervalString(start, end));

    // Case: Start Date is null, End Date
    assertEquals("../2021-12-31", toDateIntervalString(null, end));

    // Case: Start Date, End Date is null
    assertEquals("2021-01-01/..", toDateIntervalString(start, null));

    // Case: Both Dates are null
    assertNull(toDateIntervalString(null, null));
  }
}
