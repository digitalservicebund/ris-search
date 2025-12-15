package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.schema.LiteratureSchema;

/**
 * Provides mapping functionality between the domain entity {@link Literature} and the data transfer
 * object {@link LiteratureSchema}.
 *
 * <p>The class is a utility class with a static method for converting {@link Literature} objects to
 * {@link LiteratureSchema} objects. It cannot be instantiated.
 */
public class LiteratureSchemaMapper {
  private LiteratureSchemaMapper() {}

  /**
   * Maps a {@link Literature} domain entity to a {@link LiteratureSchema} data transfer object.
   *
   * @param entity the {@link Literature} domain entity representing the literature details to be
   *     mapped
   * @return a {@link LiteratureSchema} object containing the mapped literature data
   */
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
        .founder(entity.founder())
        .editors(entity.editors())
        .edition(entity.edition())
        .publishingHouses(entity.publisherOrganizations())
        .publishers(entity.publisherPersons())
        .internationalIdentifiers(entity.internationalIdentifiers())
        .universityNotes(entity.universityNotes())
        .encoding(encodings)
        .literatureType(LiteratureTypeMapper.mapLiteratureType(entity.documentNumber()))
        .build();
  }
}
