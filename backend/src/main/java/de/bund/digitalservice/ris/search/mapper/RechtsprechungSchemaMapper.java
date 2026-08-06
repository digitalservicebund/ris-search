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
    var encodings = EncodingSchemaFactory.caselawEncodingSchemas(entityURI);

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
        .headline(doc.headline())
        .titelzeile(doc.titleLine())
        .orientierungssatz(doc.headnote())
        .sonstigerOrientierungssatz(doc.otherHeadnote())
        .sonstigerLangtext(doc.otherLongText())
        .tenor(doc.tenor())
        .datum(doc.decisionDate())
        .aktenzeichen(doc.fileNumber())
        .aktenzeichenListe(doc.fileNumbers())
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
        .fehlerhafteGerichte(doc.fehlerhafteGerichte())
        .datenDerMuendlichenVerhandlung(doc.datenDerMuendlichenVerhandlung())
        .definitionen(doc.definitionen())
        .erledigung(doc.erledigung())
        .gesetzgebungsauftrag(doc.hasLegislativeMandate())
        .erledigungsvermerk(doc.erledigungsvermerk())
        .erstveroeffentlichung(doc.erstveroeffentlichung())
        .courtName(doc.courtKeyword())
        .vorabdokument(doc.vorabdokument())
        .build();
  }
}
