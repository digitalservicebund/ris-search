package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.schema.CaseLawSchema;

public class CaseLawSchemaMapper {
  private CaseLawSchemaMapper() {}

  public static CaseLawSchema fromDomain(CaseLawDocumentationUnit doc) {
    String entityURI = ApiConfig.Paths.CASELAW + "/" + doc.documentNumber();
    var encodings = EncodingSchemaFactory.caselawEncodingSchemas(entityURI);

    return CaseLawSchema.builder()
        // JSON-LD-specific fields
        .id(entityURI)
        .inLanguage("de")
        // links to other resource representations
        .encoding(encodings)
        // equivalent fields
        .documentNumber(doc.documentNumber())
        .ecli(doc.ecli())
        .caseFacts(doc.caseFacts())
        .decisionGrounds(doc.decisionGrounds())
        .dissentingOpinion(doc.dissentingOpinion())
        .grounds(doc.grounds())
        .guidingPrinciple(doc.guidingPrinciple())
        .headline(doc.headline())
        .headnote(doc.headnote())
        .otherHeadnote(doc.otherHeadnote())
        .otherLongText(doc.otherLongText())
        .tenor(doc.tenor())
        .decisionDate(doc.decisionDate())
        .fileNumbers(doc.fileNumbers())
        .courtType(doc.courtType())
        .location(doc.location())
        .documentType(doc.documentType())
        .outline(doc.outline())
        .judicialBody(doc.judicialBody())
        .keywords(doc.keywords())
        .decisionName(doc.decisionName())
        .deviatingDocumentNumber(doc.deviatingDocumentNumber())
        // fields with different name
        .courtName(doc.courtKeyword())
        // end
        .build();
  }
}
