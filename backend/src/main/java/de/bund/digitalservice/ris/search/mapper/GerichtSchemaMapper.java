package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.opensearch.common.Gericht;
import de.bund.digitalservice.ris.search.schema.GerichtSchema;

/** Maps a stored court of a referenced decision to its API schema representation. */
final class GerichtSchemaMapper {

  private GerichtSchemaMapper() {}

  static GerichtSchema fromDomain(Gericht gericht) {
    if (gericht == null) {
      return null;
    }
    return GerichtSchema.builder()
        .gerichtstyp(gericht.gerichtstyp())
        .gerichtsort(gericht.gerichtsort())
        .gerichtsbarkeit(gericht.gerichtsbarkeit())
        .spruchkoerper(gericht.spruchkoerper())
        .sitzDerAussenstelle(gericht.sitzDerAussenstelle())
        .build();
  }
}
