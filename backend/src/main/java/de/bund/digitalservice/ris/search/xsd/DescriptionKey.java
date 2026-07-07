package de.bund.digitalservice.ris.search.xsd;

import de.bund.digitalservice.ris.search.models.DocumentKind;
import org.jspecify.annotations.NonNull;

public record DescriptionKey(String key, String description, String lang, DocumentKind documentKind) implements Comparable<DescriptionKey> {

  @Override
  public int compareTo(@NonNull DescriptionKey other) {
    var compareValue = key.compareTo(other.key);
    if (compareValue != 0) {
      return compareValue;
    }

    compareValue = lang.compareTo(other.lang);
    if (compareValue != 0) {
      return compareValue;
    }

    compareValue = documentKind.compareTo(other.documentKind);
    return compareValue;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof DescriptionKey other) {
      return key.equals(other.key) && lang.equals(other.lang) && documentKind.equals(other.documentKind);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return key.hashCode() * lang.hashCode() *  documentKind.hashCode();
  }
}

