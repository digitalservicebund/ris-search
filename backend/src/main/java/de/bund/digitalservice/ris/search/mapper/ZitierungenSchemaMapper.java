package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Fundstelle;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Gericht;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ReferenzRechtsprechung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ReferenzSelbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ReferenzUnselbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ReferenzVerwaltungsvorschrift;
import de.bund.digitalservice.ris.search.schema.FundstelleSchema;
import de.bund.digitalservice.ris.search.schema.GerichtSchema;
import de.bund.digitalservice.ris.search.schema.ZitierungLiteraturSelbststaendigSchema;
import de.bund.digitalservice.ris.search.schema.ZitierungLiteraturUnselbststaendigSchema;
import de.bund.digitalservice.ris.search.schema.ZitierungRechtsprechungSchema;
import de.bund.digitalservice.ris.search.schema.ZitierungVerwaltungsvorschriftSchema;

/** Maps the stored case law citations (Zitierungen) to their API schema representation. */
final class ZitierungenSchemaMapper {

  private ZitierungenSchemaMapper() {}

  static ZitierungRechtsprechungSchema fromDomain(ReferenzRechtsprechung ref) {
    return ZitierungRechtsprechungSchema.builder()
        .richtung(ref.richtung())
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

  static ZitierungLiteraturSelbststaendigSchema fromDomain(ReferenzSelbststaendigeLiteratur ref) {
    return ZitierungLiteraturSelbststaendigSchema.builder()
        .richtung(ref.richtung())
        .referenzUri(ref.referenzUri())
        .dokumentnummer(ref.dokumentnummer())
        .autor(ref.autor())
        .titel(ref.titel())
        .veroeffentlichungsjahr(ref.veroeffentlichungsjahr())
        .build();
  }

  static ZitierungLiteraturUnselbststaendigSchema fromDomain(
      ReferenzUnselbststaendigeLiteratur ref) {
    return ZitierungLiteraturUnselbststaendigSchema.builder()
        .richtung(ref.richtung())
        .referenzUri(ref.referenzUri())
        .dokumentnummer(ref.dokumentnummer())
        .autor(ref.autor())
        .dokumenttyp(ref.dokumenttyp())
        .fundstelle(fromDomain(ref.fundstelle()))
        .build();
  }

  static ZitierungVerwaltungsvorschriftSchema fromDomain(ReferenzVerwaltungsvorschrift ref) {
    return ZitierungVerwaltungsvorschriftSchema.builder()
        .richtung(ref.richtung())
        .referenzUri(ref.referenzUri())
        .dokumentnummer(ref.dokumentnummer())
        .artDerZitierung(ref.artDerZitierung())
        .jurisAbkuerzung(ref.jurisAbkuerzung())
        .fundstelle(fromDomain(ref.fundstelle()))
        .build();
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
