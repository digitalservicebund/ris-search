package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.opensearch.LegislationPartType;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionPartType;

/** Mapping fields from the articles index to their api representation */
public class LegislationExpressionPartSchemaMapper {

  private LegislationExpressionPartSchemaMapper() {}

  /**
   * maps an opensearch LegislationPartType to an LegislationExpressionPartType
   *
   * @param type opensearch LegislationPartType
   * @return partType of the Api Schema
   */
  public static LegislationExpressionPartType mapLegislationPartType(LegislationPartType type) {
    return switch (type) {
      case null -> null;
      case ARTICLE -> LegislationExpressionPartType.ARTICLE;
      case ATTACHMENT -> LegislationExpressionPartType.ATTACHMENT;
      case CONCLUSION -> LegislationExpressionPartType.CONCLUSION;
      case PREAMBLE -> LegislationExpressionPartType.PREAMBLE;
    };
  }
}
