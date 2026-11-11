package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.ldml.caselaw.Analysis;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ImplicitReference;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Meta;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawUnindexed;
import de.bund.digitalservice.ris.search.models.opensearch.NachgehendeEntscheidung;
import de.bund.digitalservice.ris.search.models.opensearch.VorgehendeEntscheidung;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
        .vorgehendeEntscheidungen(
            mapImplicitReferences(
                meta,
                ImplicitReference::getVorgehendeEntscheidung,
                CaseLawUnindexedMapper::toVorgehendeEntscheidung))
        .nachgehendeEntscheidungen(
            mapImplicitReferences(
                meta,
                ImplicitReference::getNachgehendeEntscheidung,
                CaseLawUnindexedMapper::toNachgehendeEntscheidung))
        .build();
  }

  private static <T, R> List<R> mapImplicitReferences(
      Meta meta, Function<ImplicitReference, T> extractor, Function<T, R> mapper) {
    return Optional.ofNullable(meta.getAnalysis())
        .map(Analysis::getOtherReferences)
        .map(refs -> refs.getReferencesByType(extractor))
        .stream()
        .flatMap(Collection::stream)
        .map(mapper)
        .toList();
  }

  private static VorgehendeEntscheidung toVorgehendeEntscheidung(
      de.bund.digitalservice.ris.search.models.ldml.caselaw.VorgehendeEntscheidung entscheidung) {
    return VorgehendeEntscheidung.builder()
        .referenzUri(entscheidung.getReferenzUri())
        .dokumenttyp(entscheidung.getDokumenttyp())
        .entscheidungsdatum(DateUtils.nullSafeParseyyyyMMdd(entscheidung.getEntscheidungsdatum()))
        .dokumentnummer(entscheidung.getDokumentnummer())
        .aktenzeichen(entscheidung.getAktenzeichen())
        .gericht(CaseLawGerichtMapper.map(entscheidung.getGericht()))
        .build();
  }

  private static NachgehendeEntscheidung toNachgehendeEntscheidung(
      de.bund.digitalservice.ris.search.models.ldml.caselaw.NachgehendeEntscheidung entscheidung) {
    return NachgehendeEntscheidung.builder()
        .art(entscheidung.getArt())
        .referenzUri(entscheidung.getReferenzUri())
        .dokumenttyp(entscheidung.getDokumenttyp())
        .entscheidungsdatum(DateUtils.nullSafeParseyyyyMMdd(entscheidung.getEntscheidungsdatum()))
        .mitteilungsdatum(DateUtils.nullSafeParseyyyyMMdd(entscheidung.getMitteilungsdatum()))
        .dokumentnummer(entscheidung.getDokumentnummer())
        .aktenzeichen(entscheidung.getAktenzeichen())
        .gericht(CaseLawGerichtMapper.map(entscheidung.getGericht()))
        .vermerk(entscheidung.getVermerk())
        .build();
  }
}
