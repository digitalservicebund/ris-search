package de.bund.digitalservice.ris.search.api.schema;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Follows the <a href="https://schema.org/LegalForceStatus">Schema.org</a> definition derived from
 * the ELI ontology.
 */
public enum LegalForceStatus {
  IN_FORCE("InForce"),
  NOT_IN_FORCE("NotInForce"),
  PARTIALLY_IN_FORCE("PartiallyInForce");

  private final String value;

  LegalForceStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
