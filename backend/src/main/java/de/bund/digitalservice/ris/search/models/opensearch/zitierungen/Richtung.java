package de.bund.digitalservice.ris.search.models.opensearch.zitierungen;

/**
 * Whether a citation is cited by (aktiv) or citing (passiv) the document. Stored in OpenSearch by
 * its constant name, so renaming a constant changes the indexed values.
 */
public enum Richtung {
  AKTIV,
  PASSIV
}
