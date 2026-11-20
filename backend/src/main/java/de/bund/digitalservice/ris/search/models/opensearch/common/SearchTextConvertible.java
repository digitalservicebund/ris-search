package de.bund.digitalservice.ris.search.models.opensearch.common;

/** An object that can be represented as a plain text for full-text search indexing. */
public interface SearchTextConvertible {

  /**
   * Concatenates all searchable properties of the object.
   *
   * @return the properties separated by whitespace
   */
  String toSearchText();
}
