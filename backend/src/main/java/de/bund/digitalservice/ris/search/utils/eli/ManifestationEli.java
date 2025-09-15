package de.bund.digitalservice.ris.search.utils.eli;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record ManifestationEli(
    String jurisdiction,
    String agent,
    String year,
    String naturalIdentifier,
    LocalDate pointInTime,
    Integer version,
    String language,
    LocalDate pointInTimeManifestation,
    String subtype,
    String format) {

  @Override
  public String toString() {
    return "eli/%s/%s/%s/%s/%s/%d/%s/%s/%s.%s"
        .formatted(
            jurisdiction(),
            agent(),
            year(),
            naturalIdentifier(),
            pointInTime().format(DateTimeFormatter.ISO_LOCAL_DATE),
            version(),
            language(),
            pointInTimeManifestation().format(DateTimeFormatter.ISO_LOCAL_DATE),
            subtype(),
            format());
  }
}
