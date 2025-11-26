package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.schema.CaseLawSchema;

/**
 * The {@code CaseLawSchemaMapper} class provides utility methods for mapping domain models of type
 * {@link CaseLawDocumentationUnit} to corresponding data transfer objects of type {@link
 * CaseLawSchema}.
 *
 * <p>This class is intended for use within the data transformation layer to ensure consistent
 * conversion between domain entities and schema representations. As a utility class, {@code
 * CaseLawSchemaMapper} is not designed to be instantiated and only provides static methods for
 * mapping operations.
 */
public class CaseLawSchemaMapper {
  private CaseLawSchemaMapper() {}

  /**
   * Maps a domain model {@link CaseLawDocumentationUnit} to the corresponding data transfer object
   * {@link CaseLawSchema}.
   *
   * @param doc the domain model representing a case law documentation unit to be transformed
   * @return a {@link CaseLawSchema} object representing the specified domain model in the desired
   *     schema
   */
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
