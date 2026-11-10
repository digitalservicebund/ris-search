package de.bund.digitalservice.ris.search.models;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

/** Whether a citation is cited by ("aktiv") or citing ("passiv") the document. */
public enum Richtung {
  AKTIV("aktiv"),
  PASSIV("passiv");

  private final String value;

  Richtung(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  /**
   * Resolves the Richtung from the {@code ris:richtung} element of an LDML reference.
   *
   * @param ldmlValue the element value, matched case-insensitively
   * @return the matching Richtung, or {@code null} if the value is {@code null} or unknown
   */
  public static Richtung fromLdml(String ldmlValue) {
    return Arrays.stream(values())
        .filter(richtung -> richtung.value.equalsIgnoreCase(ldmlValue))
        .findFirst()
        .orElse(null);
  }
}
