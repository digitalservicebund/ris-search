package de.bund.digitalservice.ris.search.utils.eli;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents the "expression" level of a European Legislation Identifier (ELI) structure. The
 * ExpressionEli class encapsulates metadata about a specific legislative expression, providing
 * detailed identification and context information for legislative documents within the broader ELI
 * hierarchy.
 *
 * <p>Fields: - jurisdiction: Specifies the jurisdiction where the legislative expression applies. -
 * agent: Identifies the responsible agent or authority behind the legislative expression. - year:
 * Denotes the year of the legislative expression. - naturalIdentifier: Encodes a unique identifier
 * for the legislative expression within the context of jurisdiction and agent. - pointInTime:
 * Represents the specific point in time of the legislative expression. - version: Indicates the
 * version number associated with the legislative expression. - language: Specifies the language of
 * the legislative expression.
 *
 * <p>Main Functionalities: - Serves as a hierarchical component within the complete ELI structure,
 * acting as a bridge between the WorkEli and ManifestationEli levels. - Provides a standardized
 * string representation in the format
 * "eli/{jurisdiction}/{agent}/{year}/{naturalIdentifier}/{pointInTime}/{version}/{language}".
 *
 * <p>Overrides: - toString(): Formats the instance as a standardized ELI string representation to
 * accurately describe the legislative expression.
 */
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
