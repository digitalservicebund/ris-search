package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.ldml.caselaw.Analysis;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ImplicitReference;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Meta;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.OtherReferences;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzRechtsprechung;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzSelbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzUnselbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzVerwaltungsvorschrift;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.RisGericht;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.RisZitierung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Fundstelle;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Gericht;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Richtung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungLiteraturSelbststaendig;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungLiteraturUnselbststaendig;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungRechtsprechung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungVerwaltungsvorschrift;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Zitierungen;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/** Maps the aktiv- and passivzitierungen of a case law LDML document to {@link Zitierungen}. */
final class CaseLawZitierungenMapper {

  private CaseLawZitierungenMapper() {}

  static Zitierungen map(Meta meta) {
    List<ImplicitReference> refs =
        Optional.ofNullable(meta.getAnalysis())
            .map(Analysis::getOtherReferences)
            .map(OtherReferences::getImplicitReferences)
            .orElse(List.of());

    return Zitierungen.builder()
        .zitierungenLiteraturSelbststaendig(
            extract(
                refs,
                ImplicitReference::getReferenzSelbststaendigeLiteratur,
                CaseLawZitierungenMapper::mapSelbststaendigeLiteratur))
        .zitierungenLiteraturUnselbststaendig(
            extract(
                refs,
                ImplicitReference::getReferenzUnselbststaendigeLiteratur,
                CaseLawZitierungenMapper::mapUnselbststaendigeLiteratur))
        .zitierungenRechtsprechung(
            extract(
                refs,
                ImplicitReference::getReferenzRechtsprechung,
                CaseLawZitierungenMapper::mapRechtsprechung))
        .zitierungenVerwaltungsvorschriften(
            extract(
                refs,
                ImplicitReference::getReferenzVerwaltungsvorschrift,
                CaseLawZitierungenMapper::mapVerwaltungsvorschrift))
        .build();
  }

  private static <R extends RisZitierung, T> List<T> extract(
      List<ImplicitReference> refs,
      Function<ImplicitReference, R> extractor,
      Function<R, T> mapper) {
    return refs.stream()
        .filter(Objects::nonNull)
        .map(extractor)
        .filter(Objects::nonNull)
        .filter(ref -> ref.isAktiv() || ref.isPassiv())
        .map(mapper)
        .toList();
  }

  private static ZitierungLiteraturSelbststaendig mapSelbststaendigeLiteratur(
      ReferenzSelbststaendigeLiteratur ref) {
    return ZitierungLiteraturSelbststaendig.builder()
        .richtung(mapRichtung(ref))
        .referenzUri(ref.getReferenzUri())
        .dokumentnummer(ref.getDokumentnummer())
        .autor(ref.getAutor())
        .titel(ref.getTitel())
        .veroeffentlichungsjahr(ref.getVeroeffentlichungsjahr())
        .build();
  }

  private static ZitierungLiteraturUnselbststaendig mapUnselbststaendigeLiteratur(
      ReferenzUnselbststaendigeLiteratur ref) {
    return ZitierungLiteraturUnselbststaendig.builder()
        .richtung(mapRichtung(ref))
        .referenzUri(ref.getReferenzUri())
        .dokumentnummer(ref.getDokumentnummer())
        .autor(ref.getAutor())
        .dokumenttyp(ref.getDokumenttyp())
        .fundstelle(mapFundstelle(ref.getFundstelle()))
        .build();
  }

  private static ZitierungRechtsprechung mapRechtsprechung(ReferenzRechtsprechung ref) {
    return ZitierungRechtsprechung.builder()
        .richtung(mapRichtung(ref))
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

  private static ZitierungVerwaltungsvorschrift mapVerwaltungsvorschrift(
      ReferenzVerwaltungsvorschrift ref) {
    return ZitierungVerwaltungsvorschrift.builder()
        .richtung(mapRichtung(ref))
        .referenzUri(ref.getReferenzUri())
        .dokumentnummer(ref.getDokumentnummer())
        .artDerZitierung(ref.getArtDerZitierung())
        .jurisAbkuerzung(ref.getJurisAbkuerzung())
        .fundstelle(mapFundstelle(ref.getFundstelle()))
        .build();
  }

  private static Richtung mapRichtung(RisZitierung ref) {
    if (ref.isAktiv()) {
      return Richtung.AKTIV;
    }
    if (ref.isPassiv()) {
      return Richtung.PASSIV;
    }
    return null;
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
        .sitzDerAussenstelle(gericht.getSitzDerAussenstelle())
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
