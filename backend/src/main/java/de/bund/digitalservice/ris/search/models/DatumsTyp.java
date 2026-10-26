package de.bund.digitalservice.ris.search.models;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

/** The kind of date a case law document's {@code datum} represents. */
public enum DatumsTyp {
  ENTSCHEIDUNGSDATUM("entscheidungsdatum", "Entscheidungsdatum"),
  MITTEILUNGSDATUM("mitteilungsdatum", "Mitteilungsdatum"),
  DATUM_DER_ZUSTELLUNG_AN_VERKUENDUNGS_STATT(
      "datumDerZustellungAnVerkuendungsStatt", "Datum der Zustellung an Verkündungs statt");

  private final String ldmlName;
  private final String value;

  DatumsTyp(String ldmlName, String value) {
    this.ldmlName = ldmlName;
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  /**
   * Resolves the date type from the {@code name} attribute of an LDML FRBRdate.
   *
   * @param ldmlName the name attribute, matched case-insensitively
   * @return the matching date type, {@link #ENTSCHEIDUNGSDATUM} for unknown names, or {@code null}
   *     if the name is {@code null}
   */
  public static DatumsTyp fromLdmlName(String ldmlName) {
    if (ldmlName == null) {
      return null;
    }
    return Arrays.stream(values())
        .filter(typ -> typ.ldmlName.equalsIgnoreCase(ldmlName))
        .findFirst()
        .orElse(ENTSCHEIDUNGSDATUM);
  }
}
