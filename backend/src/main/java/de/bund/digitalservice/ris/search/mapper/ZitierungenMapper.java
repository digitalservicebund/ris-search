package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.ldml.Periodikum;
import de.bund.digitalservice.ris.search.models.ldml.ReferenzRechtsprechung;
import de.bund.digitalservice.ris.search.models.ldml.ReferenzSelbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.ldml.ReferenzUnselbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.ldml.ReferenzVerwaltungsvorschrift;
import de.bund.digitalservice.ris.search.models.ldml.RisZitierung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Fundstelle;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Richtung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungLiteraturSelbststaendig;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungLiteraturUnselbststaendig;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungRechtsprechung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungVerwaltungsvorschrift;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Maps the aktiv- and passivzitierungen of an LDML document (case law or literature) to their
 * OpenSearch representation. Each document type extracts the references from its own implicit
 * references and passes them in. Null references and references without a valid Richtung are
 * skipped.
 */
final class ZitierungenMapper {

  private ZitierungenMapper() {}

  static List<ZitierungLiteraturSelbststaendig> mapLiteraturSelbststaendig(
      List<ReferenzSelbststaendigeLiteratur> references) {
    return map(references, ZitierungenMapper::mapSelbststaendigeLiteratur);
  }

  static List<ZitierungLiteraturUnselbststaendig> mapLiteraturUnselbststaendig(
      List<ReferenzUnselbststaendigeLiteratur> references) {
    return map(references, ZitierungenMapper::mapUnselbststaendigeLiteratur);
  }

  static List<ZitierungRechtsprechung> mapRechtsprechung(List<ReferenzRechtsprechung> references) {
    return map(references, ZitierungenMapper::mapRechtsprechung);
  }

  static List<ZitierungVerwaltungsvorschrift> mapVerwaltungsvorschriften(
      List<ReferenzVerwaltungsvorschrift> references) {
    return map(references, ZitierungenMapper::mapVerwaltungsvorschrift);
  }

  private static <R extends RisZitierung, T> List<T> map(
      List<R> references, Function<R, T> mapper) {
    if (references == null) {
      return List.of();
    }
    return references.stream()
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
        .gericht(GerichtMapper.map(ref.getGericht()))
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

  /** Only called for references that are either aktiv or passiv (see {@link #map}). */
  private static Richtung mapRichtung(RisZitierung ref) {
    return ref.isAktiv() ? Richtung.AKTIV : Richtung.PASSIV;
  }

  private static Fundstelle mapFundstelle(
      de.bund.digitalservice.ris.search.models.ldml.Fundstelle fundstelle) {
    if (fundstelle == null) {
      return null;
    }
    Periodikum periodikum = fundstelle.getPeriodikum();
    return Fundstelle.builder()
        .periodikum(periodikum != null ? periodikum.getAbkuerzung() : null)
        .periodikumTitel(periodikum != null ? periodikum.getTitel() : null)
        .zitatstelle(fundstelle.getZitatstelle())
        .fundstellenTyp(fundstelle.getFundstellenTyp())
        .build();
  }
}
