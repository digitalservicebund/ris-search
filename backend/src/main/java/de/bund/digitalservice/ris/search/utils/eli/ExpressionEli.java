package de.bund.digitalservice.ris.search.utils.eli;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record ExpressionEli(
    String jurisdiction,
    String agent,
    String year,
    String naturalIdentifier,
    LocalDate pointInTime,
    Integer version,
    String language) {

  @Override
  public String toString() {
    return "eli/%s/%s/%s/%s/%s/%d/%s"
        .formatted(
            jurisdiction,
            agent,
            year,
            naturalIdentifier,
            pointInTime.format(DateTimeFormatter.ISO_LOCAL_DATE),
            version,
            language);
  }
}
