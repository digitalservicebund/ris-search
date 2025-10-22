package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.schema.LiteratureSchema;

public class LiteratureSchemaMapper {
  private LiteratureSchemaMapper() {}

  public static LiteratureSchema fromDomain(Literature entity) {
    var entityURI = ApiConfig.Paths.LITERATURE + "/" + entity.documentNumber();
    var encodings = EncodingSchemaFactory.literatureEncodingSchemas(entityURI);

    return LiteratureSchema.builder()
        .id(entityURI)
        .inLanguage("de")
        .documentNumber(entity.documentNumber())
        .yearsOfPublication(entity.yearsOfPublication())
        .documentTypes(entity.documentTypes())
        .dependentReferences(entity.dependentReferences())
        .independentReferences(entity.independentReferences())
        .headline(entity.mainTitle())
        .alternativeHeadline(entity.documentaryTitle())
        .authors(entity.authors())
        .collaborators(entity.collaborators())
        .shortReport(entity.shortReport())
        .outline(entity.outline())
        .encoding(encodings)
        .build();
  }
}
