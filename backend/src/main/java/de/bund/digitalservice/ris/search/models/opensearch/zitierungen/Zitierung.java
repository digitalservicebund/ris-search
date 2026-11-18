package de.bund.digitalservice.ris.search.models.opensearch.zitierungen;

/** A citation of or by a case law document, in either {@link Richtung}. */
public interface Zitierung {

  Richtung richtung();

  /**
   * Concatenates all searchable properties of the citation.
   *
   * @return the properties separated by whitespace
   */
  String searchText();
}
