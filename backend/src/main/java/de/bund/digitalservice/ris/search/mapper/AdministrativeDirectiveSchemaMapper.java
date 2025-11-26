package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.schema.AdministrativeDirectiveSchema;

/**
 * Utility class for mapping {@code AdministrativeDirective} domain entities to {@code
 * AdministrativeDirectiveSchema} data transfer objects.
 *
 * <p>This class provides a static method to transform an {@code AdministrativeDirective}, which
 * represents domain-level metadata about an administrative directive, into a JSON-LD compatible
 * {@code AdministrativeDirectiveSchema} designed for external API communication.
 *
 * <p>The resulting DTO includes the administrative directive's metadata, such as document number,
 * type, headline, references, effective dates, and encodings, adhering to the schema.org structure.
 *
 * <p>The class is structured to ensure immutability by including a private constructor and exposing
 * only static utility methods.
 */
public class AdministrativeDirectiveSchemaMapper {
  private AdministrativeDirectiveSchemaMapper() {}

  /**
   * Maps an {@code AdministrativeDirective} domain entity to an {@code
   * AdministrativeDirectiveSchema} data transfer object for external API communication.
   *
   * @param entity the {@code AdministrativeDirective} entity containing metadata about an
   *     administrative directive
   * @return an {@code AdministrativeDirectiveSchema} object representing the input entity in a
   *     JSON-LD compatible format
   */
  public static AdministrativeDirectiveSchema fromDomain(AdministrativeDirective entity) {
    var entityURI = ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE + "/" + entity.documentNumber();
    var encodings = EncodingSchemaFactory.administrativeDirectiveEncodingSchemas(entityURI);

    return AdministrativeDirectiveSchema.builder()
        .id(entityURI)
        .documentNumber(entity.documentNumber())
        .documentType(entity.documentType())
        .headline(entity.headline())
        .shortReport(entity.shortReport())
        .documentTypeDetail(entity.documentTypeDetail())
        .referenceNumbers(entity.referenceNumbers())
        .entryIntoForceDate(entity.entryIntoEffectDate())
        .expiryDate(entity.expiryDate())
        .legislationAuthority(entity.legislationAuthority())
        .references(entity.references())
        .normReferences(entity.normReferences())
        .citationDates(entity.citationDates())
        .outline(entity.tableOfContentsEntries())
        .encoding(encodings)
        .build();
  }
}
