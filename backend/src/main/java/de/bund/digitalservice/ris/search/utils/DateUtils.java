package de.bund.digitalservice.ris.search.utils;

import static de.bund.digitalservice.ris.search.models.opensearch.Norm.Fields.ENTRY_INTO_FORCE_DATE;
import static de.bund.digitalservice.ris.search.models.opensearch.Norm.Fields.EXPIRY_DATE;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.ExistsQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.RangeQueryBuilder;

/**
 * Utility class for date-related operations, including formatting, parsing, and building date-based
 * OpenSearch query filters. This class provides a variety of static utility methods to handle and
 * process dates.
 */
public class DateUtils {

  private static final Logger logger = LogManager.getLogger(DateUtils.class);
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private DateUtils() {}

  /**
   * Builds a query based on the provided field name and date range. If both start and end dates are
   * null, an empty Optional is returned. If the start and end dates are the same, a term query is
   * created. Otherwise, a range query is constructed with the specified bounds.
   *
   * @param fieldName The name of the field to query. Must not be null.
   * @param from The start date for the range query (inclusive). Can be null for an open start.
   * @param to The end date for the range query (inclusive). Can be null for an open end.
   * @return An Optional containing the constructed QueryBuilder if either 'from' or 'to' is
   *     non-null, or an empty Optional otherwise.
   */
  public static Optional<QueryBuilder> buildQuery(String fieldName, LocalDate from, LocalDate to) {
    if (from == null && to == null) {
      return Optional.empty();
    }
    if (Objects.equals(from, to)) {
      return Optional.of(QueryBuilders.termQuery(fieldName, to));
    } else {
      RangeQueryBuilder range = QueryBuilders.rangeQuery(fieldName);
      if (from != null) {
        range.gte(from);
      }
      if (to != null) {
        range.lte(to);
      }
      return Optional.of(range);
    }
  }

  /**
   * Builds an OpenSearch query to ensure an overlap between the ranges denoted by [from, to] and
   * resulting documents' [ENTRY_INTO_FORCE_DATE, EXPIRY_DATE].
   *
   * @param from The start date of the search range (inclusive). May be null to indicate an open
   *     start.
   * @param to The end date of the search range (inclusive). May be null to indicate an open end.
   * @return An Optional containing the constructed QueryBuilder if at least one of the parameters
   *     is non-null; otherwise, an empty Optional.
   */
  public static Optional<QueryBuilder> buildQueryForTemporalCoverage(
      @Nullable LocalDate from, @Nullable LocalDate to) {
    if (from == null && to == null) {
      return Optional.empty();
    }

    BoolQueryBuilder query = QueryBuilders.boolQuery();

    if (from != null) {
      /*
       If from is given, the user is searching for norms that are valid on or after that day,
       meaning they shouldn't be expired. Their EXPIRY_DATE may not be before that day (but may be null).
       This is equivalent to EXPIRY_DATE ≥ from OR EXPIRY_DATE == null.
       According to https://hdr.bmj.de/page_b.1.html#an_149, a period ends at the end of the day at midnight
       ("Fristende [ist] der Schluss eines Kalendertages um Mitternacht").
       See also § 188 BGB.
      */
      BoolQueryBuilder either = QueryBuilders.boolQuery();
      either.should(QueryBuilders.rangeQuery(EXPIRY_DATE).gte(from));
      either.should(not(QueryBuilders.existsQuery(EXPIRY_DATE)));
      query.must(either);
    }

    if (to != null) {
      /*
       If to is given, the user is searching for norms that are valid on or before that day,
       meaning they should not come into force in the future. Their ENTRY_INTO_FORCE_DATE may not be after that day
       (but may be null). This is
       equivalent to ENTRY_INTO_FORCE_DATE ≤ from OR ENTRY_INTO_FORCE_DATE == null.
       According to https://hdr.bmj.de/page_b.1.html#an_149, a period starts at the
       beginning of the day at midnight ("Fristbeginn ist der Anfang eines Kalendertages um Mitternacht").
       See also § 187 Abs. 2 BGB.
      */
      BoolQueryBuilder either = QueryBuilders.boolQuery();
      either.should(QueryBuilders.rangeQuery(ENTRY_INTO_FORCE_DATE).lte(to));
      either.should(not(QueryBuilders.existsQuery(ENTRY_INTO_FORCE_DATE)));
      query.must(either);
    }
    return Optional.of(query);
  }

  private static BoolQueryBuilder not(ExistsQueryBuilder queryBuilder) {
    return QueryBuilders.boolQuery().mustNot(queryBuilder);
  }

  /**
   * Converts a {@link LocalDate} object to its string representation using a predefined date
   * format. If the input date is null, the method returns null.
   *
   * @param date the {@link LocalDate} object to format. If null, the output will also be null.
   * @return the formatted date as a string, or null if the input date is null.
   */
  public static String toDateString(LocalDate date) {
    if (date == null) {
      return null;
    }
    return date.format(DATE_FORMATTER);
  }

  /**
   * Parses a date string in the format "yyyyMMdd" into a {@link LocalDate} object. If the input is
   * null, the method safely returns null instead of throwing an exception.
   *
   * @param date the date string to parse, expected to be in "yyyyMMdd" format. If the input is
   *     null, the result will also be null.
   * @return a {@link LocalDate} object corresponding to the parsed date string or null if the input
   *     date string is null.
   */
  public static LocalDate nullSafeParseyyyyMMdd(String date) {
    if (date == null) {
      return null;
    }
    return LocalDate.parse(date);
  }

  /**
   * Determines whether the current date falls within the specified date range. The date range is
   * defined by the start and end dates. If either the start or the end date is null, they are
   * treated as open intervals.
   *
   * @param start The start date of the interval, inclusive. A null value represents an open start
   *     interval.
   * @param end The end date of the interval, inclusive. A null value represents an open end
   *     interval.
   * @return true if the current date falls within the specified range or the interval is open and
   *     inclusive of the current date. Otherwise, false.
   */
  public static boolean isActive(LocalDate start, LocalDate end) {

    Clock berlinClock = Clock.system(TimeZone.getTimeZone("Europe/Berlin").toZoneId());
    LocalDate currentDateInGermany = LocalDate.now(berlinClock);

    // Nulls are considered as open intervals
    boolean hasStarted = start == null || !start.isAfter(currentDateInGermany);
    boolean hasNotEnded = end == null || !end.isBefore(currentDateInGermany);
    return hasStarted && hasNotEnded;
  }

  /**
   * @return A String following <a href="https://en.wikipedia.org/wiki/ISO_8601#Time_intervals">ISO
   *     8601 time intervals</a>, with only a date representation. Following the standards laid out
   *     in <a href="https://eur-lex.europa.eu/eli-register/legis_schema_org.html">A Guide to
   *     describe Legislation in schema.org</a>, open intervals are denoted with ".."
   * @param start The start date of the interval.
   * @param end The end date of the interval.
   */
  public static @Nullable String toDateIntervalString(LocalDate start, LocalDate end) {
    if (start == null && end == null) {
      return null;
    }
    String startString = start == null ? ".." : start.format(DATE_FORMATTER);
    String endString = end == null ? ".." : end.format(DATE_FORMATTER);
    return startString + "/" + endString;
  }

  /**
   * Introduces a small delay to mitigate potential issues in OpenSearch resulting from timestamps
   * differing by sub-millisecond amounts. OpenSearch may treat such closely spaced timestamps as
   * equal, which can lead to incorrect behavior in certain scenarios (e.g., during reindexing).
   * Adding a short delay ensures sufficient distinction between timestamps, helping to avoid side
   * effects caused by eventual consistency.
   *
   * <p>This method enforces a delay of 10 milliseconds. If the thread is interrupted during the
   * sleep period, it logs a warning and restores the interrupt status of the thread.
   */
  public static void avoidOpenSearchSubMillisecondDateBug() {
    // If two timestamps are too close (less than 1 millisecond different) than opensearch might
    // consider them equal. This can sometimes result in a bug (for example with reindexing) so
    // sometimes we need a very small delay to avoid side effects of an eventually consistent system
    try {
      // wait 10 milliseconds
      Thread.sleep(10);
    } catch (InterruptedException e) {
      logger.warn("Unexpected interruption during DateUtils Thread sleep", e);
      Thread.currentThread().interrupt();
    }
  }
}
