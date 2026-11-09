package de.bund.digitalservice.ris.search.models.ldml.caselaw;

/** A reference to another document that is either cited by ("aktiv") or citing ("passiv") it. */
public interface RisZitierung {

  String getRichtung();

  default boolean isAktiv() {
    return "aktiv".equalsIgnoreCase(getRichtung());
  }

  default boolean isPassiv() {
    return "passiv".equalsIgnoreCase(getRichtung());
  }
}
