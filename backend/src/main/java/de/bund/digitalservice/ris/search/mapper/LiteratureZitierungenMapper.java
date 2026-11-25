package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.ldml.Periodikum;
import de.bund.digitalservice.ris.search.models.ldml.ReferenzRechtsprechung;
import de.bund.digitalservice.ris.search.models.ldml.ReferenzSelbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.ldml.ReferenzUnselbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.ldml.ReferenzVerwaltungsvorschrift;
import de.bund.digitalservice.ris.search.models.ldml.RisGericht;
import de.bund.digitalservice.ris.search.models.ldml.RisZitierung;
import de.bund.digitalservice.ris.search.models.opensearch.LiteratureUnindexed;
import de.bund.digitalservice.ris.search.models.opensearch.common.Gericht;
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
 * Maps the aktiv- and passivzitierungen of a literature LDML document (sli or uli) into the
 * unindexed part of the literature entity. The sli and uli mappers extract the references from
 * their own implicit references and pass them in.
 */
final class LiteratureZitierungenMapper {

  private LiteratureZitierungenMapper() {}

  static LiteratureUnindexed mapUnindexed(
      List<ReferenzSelbststaendigeLiteratur> selbststaendigeLiteratur,
      List<ReferenzUnselbststaendigeLiteratur> unselbststaendigeLiteratur,
      List<ReferenzRechtsprechung> rechtsprechung,
      List<ReferenzVerwaltungsvorschrift> verwaltungsvorschriften) {
    return LiteratureUnindexed.builder()
        .zitierungenLiteraturSelbststaendig(
            map(selbststaendigeLiteratur, LiteratureZitierungenMapper::mapSelbststaendigeLiteratur))
        .zitierungenLiteraturUnselbststaendig(
            map(
                unselbststaendigeLiteratur,
                LiteratureZitierungenMapper::mapUnselbststaendigeLiteratur))
        .zitierungenRechtsprechung(
            map(rechtsprechung, LiteratureZitierungenMapper::mapRechtsprechung))
        .zitierungenVerwaltungsvorschriften(
            map(verwaltungsvorschriften, LiteratureZitierungenMapper::mapVerwaltungsvorschrift))
        .build();
  }

  /** Maps all references with a valid Richtung, skipping nulls. */
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
    return ref.isAktiv() ? Richtung.AKTIV : Richtung.PASSIV;
  }

  private static Gericht mapGericht(RisGericht gericht) {
    return GerichtMapper.map(gericht);
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
