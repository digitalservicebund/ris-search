package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Fundstelle;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Gericht;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Richtung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungLiteraturSelbststaendig;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungLiteraturUnselbststaendig;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungRechtsprechung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungVerwaltungsvorschrift;
import de.bund.digitalservice.ris.search.schema.FundstelleSchema;
import de.bund.digitalservice.ris.search.schema.GerichtSchema;
import de.bund.digitalservice.ris.search.schema.ZitierungLiteraturSelbststaendigSchema;
import de.bund.digitalservice.ris.search.schema.ZitierungLiteraturUnselbststaendigSchema;
import de.bund.digitalservice.ris.search.schema.ZitierungRechtsprechungSchema;
import de.bund.digitalservice.ris.search.schema.ZitierungRichtung;
import de.bund.digitalservice.ris.search.schema.ZitierungVerwaltungsvorschriftSchema;

/** Maps the stored case law citations (Zitierungen) to their API schema representation. */
final class ZitierungenSchemaMapper {

  private ZitierungenSchemaMapper() {}

  static ZitierungRechtsprechungSchema fromDomain(ZitierungRechtsprechung ref) {
    return ZitierungRechtsprechungSchema.builder()
        .richtung(fromDomain(ref.richtung()))
        .referenzUri(ref.referenzUri())
        .dokumentnummer(ref.dokumentnummer())
        .artDerZitierung(ref.artDerZitierung())
        .gericht(fromDomain(ref.gericht()))
        .entscheidungsdatum(ref.entscheidungsdatum())
        .dokumenttyp(ref.dokumenttyp())
        .aktenzeichen(ref.aktenzeichen())
        .fundstelle(fromDomain(ref.fundstelle()))
        .build();
  }

  static ZitierungLiteraturSelbststaendigSchema fromDomain(ZitierungLiteraturSelbststaendig ref) {
    return ZitierungLiteraturSelbststaendigSchema.builder()
        .richtung(fromDomain(ref.richtung()))
        .referenzUri(ref.referenzUri())
        .dokumentnummer(ref.dokumentnummer())
        .autor(ref.autor())
        .titel(ref.titel())
        .veroeffentlichungsjahr(ref.veroeffentlichungsjahr())
        .build();
  }

  static ZitierungLiteraturUnselbststaendigSchema fromDomain(
      ZitierungLiteraturUnselbststaendig ref) {
    return ZitierungLiteraturUnselbststaendigSchema.builder()
        .richtung(fromDomain(ref.richtung()))
        .referenzUri(ref.referenzUri())
        .dokumentnummer(ref.dokumentnummer())
        .autor(ref.autor())
        .dokumenttyp(ref.dokumenttyp())
        .fundstelle(fromDomain(ref.fundstelle()))
        .build();
  }

  static ZitierungVerwaltungsvorschriftSchema fromDomain(ZitierungVerwaltungsvorschrift ref) {
    return ZitierungVerwaltungsvorschriftSchema.builder()
        .richtung(fromDomain(ref.richtung()))
        .referenzUri(ref.referenzUri())
        .dokumentnummer(ref.dokumentnummer())
        .artDerZitierung(ref.artDerZitierung())
        .jurisAbkuerzung(ref.jurisAbkuerzung())
        .fundstelle(fromDomain(ref.fundstelle()))
        .build();
  }

  private static ZitierungRichtung fromDomain(Richtung richtung) {
    return switch (richtung) {
      case null -> null;
      case AKTIV -> ZitierungRichtung.AKTIV;
      case PASSIV -> ZitierungRichtung.PASSIV;
    };
  }

  private static GerichtSchema fromDomain(Gericht gericht) {
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

  private static FundstelleSchema fromDomain(Fundstelle fundstelle) {
    if (fundstelle == null) {
      return null;
    }
    return FundstelleSchema.builder()
        .periodikum(fundstelle.periodikum())
        .zitatstelle(fundstelle.zitatstelle())
        .fundstellenTyp(fundstelle.fundstellenTyp())
        .build();
  }
}
