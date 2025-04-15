package de.bund.digitalservice.ris.search.unit.utils;

import static de.bund.digitalservice.ris.search.utils.DateUtils.toDateIntervalString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.bund.digitalservice.ris.search.utils.DateUtils;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DateUtilsTest {
  @ParameterizedTest(name = "Test valid date from {0} to {1} is {2}")
  @MethodSource("getTestDates")
  void testIsValidDate(LocalDate from, LocalDate to, boolean expected) {
    assertEquals(expected, DateUtils.isActive(from, to));
  }

  private static List<Arguments> getTestDates() {
    LocalDate pastDate = LocalDate.of(2023, 1, 1);
    Clock berlinClock = Clock.system(TimeZone.getTimeZone("Europe/Berlin").toZoneId());
    LocalDate currentDateInGermany = LocalDate.now(berlinClock);
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
    LocalDate start = LocalDate.of(2021, 1, 1);
    LocalDate end = LocalDate.of(2021, 12, 31);

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
