package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.schema.RechtsprechungSchema;

/** Maps Rechtsprechung (case law document) objects to API schema models. */
public class RechtsprechungSchemaMapper {

  private RechtsprechungSchemaMapper() {}

  /**
   * Creates a {@link RechtsprechungSchema} from a case law document.
   *
   * @param doc source domain object
   * @return mapped schema representation for API responses
   */
  public static RechtsprechungSchema fromDomain(CaseLawDocumentationUnit doc) {
    String entityURI = ApiConfig.Paths.RECHTSPRECHUNG + "/" + doc.documentNumber();
    var encodings = EncodingSchemaFactory.documentEncodingSchemas(entityURI);

    return RechtsprechungSchema.builder()
        .id(entityURI)
        .inLanguage("de")
        .encoding(encodings)
        .dokumentNummer(doc.documentNumber())
        .dokumenttyp(doc.documentType())
        .ecli(doc.ecli())
        .celex(doc.celex())
        .tatbestand(doc.caseFacts())
        .entscheidungsgruende(doc.decisionGrounds())
        .abweichendeMeinung(doc.dissentingOpinion())
        .gruende(doc.grounds())
        .leitsatz(doc.guidingPrinciple())
        .kurztitel(doc.headline())
        .titelzeile(doc.titleLine())
        .orientierungssatz(doc.headnote())
        .sonstigerOrientierungssatz(doc.otherHeadnote())
        .sonstigerLangtext(doc.otherLongText())
        .rechtsfrageGesamt(doc.rechtsfrageGesamt())
        .rechtsfrage(doc.rechtsfrage())
        .tenor(doc.tenor())
        .datum(doc.decisionDate())
        .aktenzeichen(doc.fileNumber())
        .aktenzeichenListe(doc.fileNumbers())
        .abweichendeAktenzeichen(doc.abweichendeAktenzeichen())
        .gericht(doc.courtKeyword())
        .gerichtsbarkeit(doc.gerichtsbarkeit())
        .gliederung(doc.outline())
        .spruchkoerper(doc.judicialBody())
        .schlagwoerter(doc.keywords())
        .entscheidungsnamen(doc.decisionName())
        .abweichendeDokumentnummern(doc.deviatingDocumentNumber())
        .abweichendeDaten(doc.abweichendeDaten())
        .abweichendeEclis(doc.abweichendeEclis())
        .berufsbilder(doc.berufsbilder())
        .kuendigungsarten(doc.kuendigungsarten())
        .herkunftslaender(doc.herkunftslaender())
        .regionen(doc.regionen())
        .tarifvertraege(doc.tarifvertraege())
        .kuendigungsgruende(doc.kuendigungsgruende())
        .mitwirkendeRichter(doc.mitwirkendeRichter())
        .vorgehendeEntscheidungen(doc.previousDecisions())
        .nachgehendeEntscheidungen(doc.ensuingDecisions())
        .aktivzitierungLiteraturUnselbstaendig(doc.aktivzitierungLiteraturUnselbstaendig())
        .passivzitierungLiteraturUnselbstaendig(doc.passivzitierungLiteraturUnselbstaendig())
        .aktivzitierungLiteraturSelbstaendig(doc.aktivzitierungLiteraturSelbstaendig())
        .passivzitierungLiteraturSelbstaendig(doc.passivzitierungLiteraturSelbstaendig())
        .aktivzitierungRechtsprechung(doc.aktivzitierungRechtsprechung())
        .passivzitierungRechtsprechung(doc.passivzitierungRechtsprechung())
        .aktivzitierungVerwaltungsvorschriften(doc.aktivzitierungVerwaltungsvorschriften())
        .passivzitierungVerwaltungsvorschriften(doc.passivzitierungVerwaltungsvorschriften())
        .amtlicheFundstellen(doc.amtlicheFundstellen())
        .nichtamtlicheFundstellen(doc.nichtamtlicheFundstellen())
        .gesetzeskraft(doc.gesetzeskraft())
        .normenkette(doc.normenkette())
        .sachgebiete(doc.sachgebiete())
        .streitjahre(doc.streitjahre())
        .fehlerhafteGerichte(doc.fehlerhafteGerichte())
        .datenDerMuendlichenVerhandlung(doc.datenDerMuendlichenVerhandlung())
        .definitionen(doc.definitionen())
        .erledigung(doc.erledigung())
        .rechtskraft(doc.legalEffect())
        .gesetzgebungsauftrag(doc.hasLegislativeMandate())
        .langtextdatum(doc.langtextdatum())
        .rechtsmittelfuehrer(doc.rechtsmittelfuehrer())
        .rechtsmittelzulassung(doc.rechtsmittelzulassung())
        .revision(doc.revision())
        .letzteVeroeffentlichung(doc.letzteVeroeffentlichung())
        .erledigungsvermerk(doc.erledigungsvermerk())
        .erstveroeffentlichung(doc.erstveroeffentlichung())
        .mitteilungsdatum(doc.mitteilungsdatum())
        .courtName(doc.courtKeyword())
        .vorabdokument(doc.vorabdokument())
        .build();
  }
}
