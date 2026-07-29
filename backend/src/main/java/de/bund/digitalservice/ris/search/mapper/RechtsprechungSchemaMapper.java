package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.schema.RechtsprechungSchema;

public class RechtsprechungSchemaMapper {

  private RechtsprechungSchemaMapper() {}

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
        .aktenzeichenListe(doc.fileNumbers())
        .gericht(doc.courtKeyword())
        .gliederung(doc.outline())
        .spruchkoerper(doc.judicialBody())
        .schlagwoerter(doc.keywords())
        .entscheidungsnamen(doc.decisionName())
        .abweichendeDokumentnummern(doc.deviatingDocumentNumber())
        .courtName(doc.courtKeyword())
        .build();
  }
}
