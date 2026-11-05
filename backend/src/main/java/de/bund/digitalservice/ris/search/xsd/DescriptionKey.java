package de.bund.digitalservice.ris.search.xsd;

import org.jspecify.annotations.NonNull;

/**
 * A single, unambiguous XSD-derived description entry: the element/type {@code key} it documents,
 * the {@code description} text, and its {@code lang}.
 */
public record DescriptionKey(String key, String description, String lang)
    implements Comparable<DescriptionKey> {

  @Override
  public int compareTo(@NonNull DescriptionKey other) {
    var compareValue = key.compareTo(other.key);
    if (compareValue != 0) {
      return compareValue;
    }

    return lang.compareTo(other.lang);
  }
}
