package de.bund.digitalservice.ris.search.utils.eli;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents the Manifestation level of a European Legislation Identifier (ELI), providing metadata
 * about a specific digital format of a legislative document.
 *
 * <p>This immutable record encapsulates detailed information at the most granular level of the ELI
 * hierarchy.
 *
 * <p>Fields: - jurisdiction: Defines the jurisdiction where the legislation is applicable. - agent:
 * Identifies the agent responsible for the legislation or its dissemination. - year: Specifies the
 * year associated with the legislative document's creation or publication.
 */
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
