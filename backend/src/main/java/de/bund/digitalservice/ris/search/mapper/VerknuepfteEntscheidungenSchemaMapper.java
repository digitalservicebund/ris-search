package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.opensearch.NachgehendeEntscheidung;
import de.bund.digitalservice.ris.search.models.opensearch.VorgehendeEntscheidung;
import de.bund.digitalservice.ris.search.schema.NachgehendeEntscheidungSchema;
import de.bund.digitalservice.ris.search.schema.VorgehendeEntscheidungSchema;

/** Maps the stored prior and subsequent decisions of a case law to their API schema. */
final class VerknuepfteEntscheidungenSchemaMapper {

  private VerknuepfteEntscheidungenSchemaMapper() {}

  static VorgehendeEntscheidungSchema fromDomain(VorgehendeEntscheidung entscheidung) {
    return VorgehendeEntscheidungSchema.builder()
        .referenzUri(entscheidung.referenzUri())
        .dokumenttyp(entscheidung.dokumenttyp())
        .entscheidungsdatum(entscheidung.entscheidungsdatum())
        .dokumentnummer(entscheidung.dokumentnummer())
        .aktenzeichen(entscheidung.aktenzeichen())
        .gericht(GerichtSchemaMapper.fromDomain(entscheidung.gericht()))
        .build();
  }

  static NachgehendeEntscheidungSchema fromDomain(NachgehendeEntscheidung entscheidung) {
    return NachgehendeEntscheidungSchema.builder()
        .art(entscheidung.art())
        .referenzUri(entscheidung.referenzUri())
        .dokumenttyp(entscheidung.dokumenttyp())
        .entscheidungsdatum(entscheidung.entscheidungsdatum())
        .mitteilungsdatum(entscheidung.mitteilungsdatum())
        .dokumentnummer(entscheidung.dokumentnummer())
        .aktenzeichen(entscheidung.aktenzeichen())
        .gericht(GerichtSchemaMapper.fromDomain(entscheidung.gericht()))
        .vermerk(entscheidung.vermerk())
        .build();
  }
}
