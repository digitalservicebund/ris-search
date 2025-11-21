package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.schema.AdministrativeDirectiveSchema;

public class AdministrativeDirectiveSchemaMapper {
  private AdministrativeDirectiveSchemaMapper() {}

  public static AdministrativeDirectiveSchema fromDomain(AdministrativeDirective entity) {
    var entityURI = ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE + "/" + entity.documentNumber();
    var encodings = EncodingSchemaFactory.administrativeDirectiveEncodingSchemas(entityURI);

    return AdministrativeDirectiveSchema.builder()
        .id(entityURI)
        .documentNumber(entity.documentNumber())
        .documentType(entity.documentCategory())
        .documentTypeDetail(entity.documentType())
        .referenceNumbers(entity.referenceNumbers())
        .entryIntoForceDate(entity.entryIntoEffectDate())
        .expiryDate(entity.expiryDate())
        .legislationAuthority(entity.normgeber())
        .references(entity.fundstelleReferences())
        .normReferences(entity.normReferences())
        .citationDates(entity.datesToQuote())
        .outline(entity.tableOfContentsEntries())
        .encoding(encodings)
        .build();
  }
}
