package de.bund.digitalservice.ris.search.models.ldml.literature;

/**
 * A reference of a literature document to another document that is either cited by ("aktiv") or
 * citing ("passiv") it.
 */
public interface RisZitierung {

  String RICHTUNG_AKTIV = "aktiv";
  String RICHTUNG_PASSIV = "passiv";

  String getRichtung();

  default boolean isAktiv() {
    return RICHTUNG_AKTIV.equalsIgnoreCase(getRichtung());
  }

  default boolean isPassiv() {
    return RICHTUNG_PASSIV.equalsIgnoreCase(getRichtung());
  }
}
