package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.ldml.caselaw.Meta;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawUnindexed;

/** Maps the parts of a case law LDML document that are stored but not indexed. */
final class CaseLawUnindexedMapper {

  private CaseLawUnindexedMapper() {}

  static CaseLawUnindexed map(Meta meta) {
    return CaseLawUnindexed.builder()
        .zitierungenLiteraturSelbststaendig(
            CaseLawZitierungenMapper.mapLiteraturSelbststaendig(meta))
        .zitierungenLiteraturUnselbststaendig(
            CaseLawZitierungenMapper.mapLiteraturUnselbststaendig(meta))
        .zitierungenRechtsprechung(CaseLawZitierungenMapper.mapRechtsprechung(meta))
        .zitierungenVerwaltungsvorschriften(
            CaseLawZitierungenMapper.mapVerwaltungsvorschriften(meta))
        .build();
  }
}
