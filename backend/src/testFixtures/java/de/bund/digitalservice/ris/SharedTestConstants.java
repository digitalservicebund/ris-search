package de.bund.digitalservice.ris;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;

/** Shared test constants for deterministic unit and integration tests. */
public final class SharedTestConstants {
  public static final LocalDate DATE_2023_01_02 = LocalDate.of(2023, Month.JANUARY, 2);
  public static final LocalDate DATE_2024_01_01 = LocalDate.of(2024, Month.JANUARY, 1);
  public static final LocalDate DATE_2024_01_02 = LocalDate.of(2024, Month.JANUARY, 2);
  public static final LocalDate DATE_2024_01_03 = LocalDate.of(2024, Month.JANUARY, 3);
  public static final String TIMESTAMP_2024_01_01_AS_STRING = "2024-01-01T00:00:00Z";
  public static final Instant TIMESTAMP_2024_01_01_AS_INSTANT =
      Instant.parse(TIMESTAMP_2024_01_01_AS_STRING);

  private SharedTestConstants() {}
}
