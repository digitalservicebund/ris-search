package de.bund.digitalservice.ris.search.utils;

import static de.bund.digitalservice.ris.search.models.opensearch.Norm.Fields.ENTRY_INTO_FORCE_DATE;
import static de.bund.digitalservice.ris.search.models.opensearch.Norm.Fields.EXPIRY_DATE;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import org.jetbrains.annotations.Nullable;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.ExistsQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.RangeQueryBuilder;

public class DateUtils {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private DateUtils() {}

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

  public static String toDateString(LocalDate date) {
    if (date == null) {
      return null;
    }
    return date.format(DATE_FORMATTER);
  }

  public static LocalDate nullSafeParseyyyyMMdd(String date) {
    if (date == null) {
      return null;
    }
    return LocalDate.parse(date);
  }

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
   */
  public static @Nullable String toDateIntervalString(LocalDate start, LocalDate end) {
    if (start == null && end == null) {
      return null;
    }
    String startString = start == null ? ".." : start.format(DATE_FORMATTER);
    String endString = end == null ? ".." : end.format(DATE_FORMATTER);
    return startString + "/" + endString;
  }
}
