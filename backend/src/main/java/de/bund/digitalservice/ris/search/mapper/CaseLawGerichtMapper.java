package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.ldml.caselaw.RisGericht;
import de.bund.digitalservice.ris.search.models.opensearch.Gericht;

/** Maps a court of a case law LDML document to {@link Gericht}. */
final class CaseLawGerichtMapper {

  private CaseLawGerichtMapper() {}

  static Gericht map(RisGericht gericht) {
    if (gericht == null) {
      return null;
    }
    return Gericht.builder()
        .gerichtstyp(gericht.getGerichtstyp())
        .gerichtsort(gericht.getGerichtsort())
        .gerichtsbarkeit(gericht.getGerichtsbarkeit())
        .spruchkoerper(gericht.getSpruchkoerperValue())
        .sitzDerAussenstelle(gericht.getSitzDerAussenstelle())
        .build();
  }
}
