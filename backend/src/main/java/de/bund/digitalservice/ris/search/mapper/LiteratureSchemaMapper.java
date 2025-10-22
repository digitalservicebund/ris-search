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
        .recordingDate(entity.recordingDate())
        .documentNumber(entity.documentNumber())
        .yearsOfPublication(entity.yearsOfPublication())
        .documentTypes(entity.documentTypes())
        .dependentReferences(entity.dependentReferences())
        .independentReferences(entity.independentReferences())
        .normReferences(entity.normReferences())
        .headline(entity.mainTitle())
        .alternativeHeadline(entity.documentaryTitle())
        .headlineAdditions(entity.mainTitleAdditions())
        .authors(entity.authors())
        .collaborators(entity.collaborators())
        .originators(entity.originators())
        .conferenceNotes(entity.conferenceNotes())
        .languages(entity.languages())
        .shortReport(entity.shortReport())
        .outline(entity.outline())
        .encoding(encodings)
        .build();
  }
}
