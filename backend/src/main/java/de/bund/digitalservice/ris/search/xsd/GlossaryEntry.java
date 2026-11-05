package de.bund.digitalservice.ris.search.xsd;

import org.jspecify.annotations.NonNull;

/**
 * A single glossary row: the {@code key} of an XSD-documented element/type and its German/English
 * description text pair. Either description may be blank if no documentation was found for that
 * language.
 */
public record GlossaryEntry(String key, String descriptionDe, String descriptionEn)
    implements Comparable<GlossaryEntry> {

  @Override
  public int compareTo(@NonNull GlossaryEntry other) {
    var compareValue = key.compareTo(other.key);
    if (compareValue != 0) {
      return compareValue;
    }

    compareValue = descriptionDe.compareTo(other.descriptionDe);
    if (compareValue != 0) {
      return compareValue;
    }

    return descriptionEn.compareTo(other.descriptionEn);
  }
}
