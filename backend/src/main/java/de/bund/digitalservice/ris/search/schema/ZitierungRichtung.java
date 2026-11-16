package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonValue;

/** API representation of whether a citation is cited by or citing the document. */
public enum ZitierungRichtung {
  AKTIV("aktiv"),
  PASSIV("passiv");

  private final String value;

  ZitierungRichtung(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
