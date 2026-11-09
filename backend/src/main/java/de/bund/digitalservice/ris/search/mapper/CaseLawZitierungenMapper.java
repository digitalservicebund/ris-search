package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.ldml.caselaw.Analysis;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ImplicitReference;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Meta;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.OtherReferences;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.RisGericht;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.RisReferenz;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Fundstelle;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Gericht;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ReferenzRechtsprechung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ReferenzSelbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ReferenzUnselbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ReferenzVerwaltungsvorschrift;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Zitierungen;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/** Maps the aktiv- and passivzitierungen of a case law LDML document to {@link Zitierungen}. */
final class CaseLawZitierungenMapper {

  private static final String AKTIV = "aktiv";
  private static final String PASSIV = "passiv";

  private CaseLawZitierungenMapper() {}

  static Zitierungen map(Meta meta) {
    List<ImplicitReference> refs =
        Optional.ofNullable(meta.getAnalysis())
            .map(Analysis::getOtherReferences)
            .map(OtherReferences::getImplicitReferences)
            .orElse(List.of());

    return Zitierungen.builder()
        .aktivzitierungLiteraturSelbstaendig(
            extract(
                refs,
                ImplicitReference::getReferenzSelbststaendigeLiteratur,
                AKTIV,
                CaseLawZitierungenMapper::mapSelbststaendigeLiteratur))
        .passivzitierungLiteraturSelbstaendig(
            extract(
                refs,
                ImplicitReference::getReferenzSelbststaendigeLiteratur,
                PASSIV,
                CaseLawZitierungenMapper::mapSelbststaendigeLiteratur))
        .aktivzitierungLiteraturUnselbstaendig(
            extract(
                refs,
                ImplicitReference::getReferenzUnselbststaendigeLiteratur,
                AKTIV,
                CaseLawZitierungenMapper::mapUnselbststaendigeLiteratur))
        .passivzitierungLiteraturUnselbstaendig(
            extract(
                refs,
                ImplicitReference::getReferenzUnselbststaendigeLiteratur,
                PASSIV,
                CaseLawZitierungenMapper::mapUnselbststaendigeLiteratur))
        .aktivzitierungRechtsprechung(
            extract(
                refs,
                ImplicitReference::getReferenzRechtsprechung,
                AKTIV,
                CaseLawZitierungenMapper::mapRechtsprechung))
        .passivzitierungRechtsprechung(
            extract(
                refs,
                ImplicitReference::getReferenzRechtsprechung,
                PASSIV,
                CaseLawZitierungenMapper::mapRechtsprechung))
        .aktivzitierungVerwaltungsvorschriften(
            extract(
                refs,
                ImplicitReference::getReferenzVerwaltungsvorschrift,
                AKTIV,
                CaseLawZitierungenMapper::mapVerwaltungsvorschrift))
        .passivzitierungVerwaltungsvorschriften(
            extract(
                refs,
                ImplicitReference::getReferenzVerwaltungsvorschrift,
                PASSIV,
                CaseLawZitierungenMapper::mapVerwaltungsvorschrift))
        .build();
  }

  private static <R extends RisReferenz, T> List<T> extract(
      List<ImplicitReference> refs,
      Function<ImplicitReference, R> extractor,
      String richtung,
      Function<R, T> mapper) {
    return refs.stream()
        .filter(Objects::nonNull)
        .map(extractor)
        .filter(Objects::nonNull)
        .filter(ref -> richtung.equalsIgnoreCase(ref.getRichtung()))
        .map(mapper)
        .toList();
  }

  private static ReferenzSelbststaendigeLiteratur mapSelbststaendigeLiteratur(
      de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzSelbststaendigeLiteratur ref) {
    return ReferenzSelbststaendigeLiteratur.builder()
        .referenzUri(ref.getReferenzUri())
        .dokumentnummer(ref.getDokumentnummer())
        .autor(ref.getAutor())
        .titel(ref.getTitel())
        .veroeffentlichungsjahr(ref.getVeroeffentlichungsjahr())
        .build();
  }

  private static ReferenzUnselbststaendigeLiteratur mapUnselbststaendigeLiteratur(
      de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzUnselbststaendigeLiteratur
          ref) {
    return ReferenzUnselbststaendigeLiteratur.builder()
        .referenzUri(ref.getReferenzUri())
        .dokumentnummer(ref.getDokumentnummer())
        .autor(ref.getAutor())
        .dokumenttyp(ref.getDokumenttyp())
        .fundstelle(mapFundstelle(ref.getFundstelle()))
        .build();
  }

  private static ReferenzRechtsprechung mapRechtsprechung(
      de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzRechtsprechung ref) {
    return ReferenzRechtsprechung.builder()
        .referenzUri(ref.getReferenzUri())
        .dokumentnummer(ref.getDokumentnummer())
        .artDerZitierung(ref.getArtDerZitierung())
        .gericht(mapGericht(ref.getGericht()))
        .entscheidungsdatum(DateUtils.nullSafeParseyyyyMMdd(ref.getEntscheidungsdatum()))
        .dokumenttyp(ref.getDokumenttyp())
        .aktenzeichen(ref.getAktenzeichen())
        .fundstelle(mapFundstelle(ref.getFundstelle()))
        .build();
  }

  private static ReferenzVerwaltungsvorschrift mapVerwaltungsvorschrift(
      de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzVerwaltungsvorschrift ref) {
    return ReferenzVerwaltungsvorschrift.builder()
        .referenzUri(ref.getReferenzUri())
        .dokumentnummer(ref.getDokumentnummer())
        .artDerZitierung(ref.getArtDerZitierung())
        .jurisAbkuerzung(ref.getJurisAbkuerzung())
        .fundstelle(mapFundstelle(ref.getFundstelle()))
        .build();
  }

  private static Gericht mapGericht(RisGericht gericht) {
    if (gericht == null) {
      return null;
    }
    return Gericht.builder()
        .gerichtstyp(gericht.getGerichtstyp())
        .gerichtsort(gericht.getGerichtsort())
        .gerichtsbarkeit(gericht.getGerichtsbarkeit())
        .spruchkoerper(gericht.getSpruchkoerperValue())
        .build();
  }

  private static Fundstelle mapFundstelle(
      de.bund.digitalservice.ris.search.models.ldml.caselaw.Fundstelle fundstelle) {
    if (fundstelle == null) {
      return null;
    }
    return Fundstelle.builder()
        .periodikum(
            fundstelle.getPeriodikum() != null ? fundstelle.getPeriodikum().getAbkuerzung() : null)
        .zitatstelle(fundstelle.getZitatstelle())
        .fundstellenTyp(fundstelle.getFundstellenTyp())
        .build();
  }
}
