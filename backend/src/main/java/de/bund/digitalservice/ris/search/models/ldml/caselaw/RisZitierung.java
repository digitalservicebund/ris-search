package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import de.bund.digitalservice.ris.search.models.Richtung;

/** A reference to another document that is either cited by ("aktiv") or citing ("passiv") it. */
public interface RisZitierung {

  String getRichtung();

  default boolean isAktiv() {
    return Richtung.fromLdml(getRichtung()) == Richtung.AKTIV;
  }

  default boolean isPassiv() {
    return Richtung.fromLdml(getRichtung()) == Richtung.PASSIV;
  }
}
