package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LegalForceStatus;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionSearchSchema;
import de.bund.digitalservice.ris.search.schema.PartialCollectionViewSchema;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import org.springframework.data.domain.Page;

/**
 * Mapper to map Norm objects to the Api LegislationExpressionSearchSchema containing expression
 * level metadata
 */
public class LegislationExpressionSearchSchemaMapper {
  private LegislationExpressionSearchSchemaMapper() {}

  /**
   * Maps an opensearch Norm to a LegislationExpressionSearchSchema
   *
   * @param norm {@link Norm}
   * @return {@link LegislationExpressionSearchSchema}
   */
  public static LegislationExpressionSearchSchema fromNorm(Norm norm) {
    LegalForceStatus legislationLegalForce =
        DateUtils.isActive(norm.getEntryIntoForceDate(), norm.getExpiryDate())
            ? LegalForceStatus.IN_FORCE
            : LegalForceStatus.NOT_IN_FORCE;

    String contentBaseUrl = ApiConfig.Paths.LEGISLATION + "/";

    String expressionEli = norm.getExpressionEli();
    String expressionId = contentBaseUrl + expressionEli;

    String temporalCoverage =
        DateUtils.toDateIntervalString(norm.getEntryIntoForceDate(), norm.getExpiryDate());

    return LegislationExpressionSearchSchema.builder()
        .legislationLegalForce(legislationLegalForce)
        .legislationIdentifier(expressionEli)
        .datePublished(norm.getDatePublished())
        .abbreviation(norm.getOfficialAbbreviation())
        .legislationDate(norm.getNormsDate())
        .name(norm.getOfficialTitle())
        .alternateName(norm.getOfficialShortTitle())
        .id(expressionId)
        .temporalCoverage(temporalCoverage)
        .build();
  }

  /**
   * Maps an opensearch Page of a Norm to a collection of LegislationExpressionSearchSchema
   *
   * @param page the {@link Page} of {@link Norm} instances returned by OpenSearch
   * @param path api path that was used to retrieve norms
   * @return a {@link CollectionSchema} containing {@link LegislationExpressionSearchSchema} items
   */
  public static CollectionSchema<LegislationExpressionSearchSchema> fromNormsPage(
      Page<Norm> page, String path) {
    String id = String.format("%s?pageIndex=%d&size=%d", path, page.getNumber(), page.getSize());
    PartialCollectionViewSchema view = PartialCollectionViewMapper.fromPage(path, page);

    return CollectionSchema.<LegislationExpressionSearchSchema>builder()
        .id(id)
        .totalItems(page.getTotalElements())
        .member(page.stream().map(LegislationExpressionSearchSchemaMapper::fromNorm).toList())
        .view(view)
        .build();
  }
}
