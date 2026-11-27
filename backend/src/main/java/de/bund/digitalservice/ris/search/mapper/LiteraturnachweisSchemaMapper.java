package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.schema.LiteraturnachweisSchema;

/**
 * Provides mapping functionality between the domain entity {@link Literature} and the data transfer
 * object {@link LiteraturnachweisSchema}.
 *
 * <p>The class is a utility class with a static method for converting {@link Literature} objects to
 * {@link LiteraturnachweisSchema} objects. It cannot be instantiated.
 */
public class LiteraturnachweisSchemaMapper {

  private LiteraturnachweisSchemaMapper() {}

  /**
   * Maps a {@link Literature} domain entity to a {@link LiteraturnachweisSchema} data transfer
   * object.
   *
   * @param entity the {@link Literature} domain entity representing the literature details to be
   *     mapped
   * @param remoteJsonContext the URL of the JSON-LD context document
   * @return a {@link LiteraturnachweisSchema} object containing the mapped literature data
   */
  public static LiteraturnachweisSchema fromDomain(Literature entity, String remoteJsonContext) {
    var entityURI = ApiConfig.Paths.LITERATURNACHWEISE + "/" + entity.documentNumber();
    var encodings = EncodingSchemaFactory.documentEncodingSchemas(entityURI);

    return LiteraturnachweisSchema.builder()
        .id(entityURI)
        .context(remoteJsonContext)
        .dokumentnummer(entity.documentNumber())
        .veroeffentlichungsJahre(entity.yearsOfPublication())
        .dokumenttypen(entity.documentTypes())
        .fundstellenUnselbstaendig(entity.fundstellenUnselbststaendig())
        .fundstellenSelbstaendig(entity.fundstellenSelbststaendig())
        .referenzenNorm(entity.normReferences())
        .haupttitel(entity.mainTitle())
        .haupttitelzusatz(entity.mainTitleAdditions())
        .dokumentarischerTitel(entity.documentaryTitle())
        .ausgabe(entity.edition())
        .bestellnummern(entity.internationalIdentifiers())
        .verfasser(entity.authors())
        .mitarbeiter(entity.collaborators())
        .bearbeiter(entity.editors())
        .begruender(entity.founder())
        .herausgeberPersonen(entity.publisherPersons())
        .herausgeberInstitutionen(entity.publisherOrganizations())
        .verlagsangaben(entity.publisherInformation())
        .urheber(entity.originators())
        .kongressvermerke(entity.conferenceNotes())
        .hochschulschriftenvermerke(entity.universityNotes())
        .teilbaende(entity.volumes())
        .literaturtyp(LiteratureTypeMapper.mapLiteratureType(entity.documentNumber()))
        .encoding(encodings)
        .build();
  }
}
