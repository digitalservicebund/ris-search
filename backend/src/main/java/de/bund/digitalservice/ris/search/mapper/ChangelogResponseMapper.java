package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.schema.ChangelogChangedDocument;
import de.bund.digitalservice.ris.search.schema.ChangelogDeletedDocument;
import de.bund.digitalservice.ris.search.schema.ChangelogResponse;
import de.bund.digitalservice.ris.search.schema.JsonldTypes;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import java.util.Set;
import java.util.stream.Collectors;

/** Mapper class to map Changelog objects to api representations of a changelog. */
public class ChangelogResponseMapper {

  private ChangelogResponseMapper() {}

  /**
   * Takes a Changelog representation at maps it to an api representation of its specific
   * documentKind
   *
   * @param changelog Changelog object to be mapped
   * @param documentKind documentKind of the response
   * @return api response of a specific Changelog
   */
  public static ChangelogResponse mapChangelog(Changelog changelog, DocumentKind documentKind) {
    return switch (documentKind) {
      case LEGISLATION -> mapNorms(changelog);
      case CASE_LAW -> mapDocument(changelog, ApiConfig.Paths.CASELAW, JsonldTypes.DECISION);
      case LITERATURE -> mapDocument(changelog, ApiConfig.Paths.LITERATURE, JsonldTypes.LITERATURE);
      case ADMINISTRATIVE_DIRECTIVE ->
          mapDocument(
              changelog,
              ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE,
              JsonldTypes.ADMINISTRATIVE_DIRECTIVE);
    };
  }

  /** Maps a changelog to a ChangelogResponse for non-legislation documents. */
  private static ChangelogResponse mapDocument(
      Changelog changelog, String apiPath, String deletedType) {
    Set<ChangelogChangedDocument> changed =
        changelog.getChanged().stream()
            .map(path -> toChangedDocument(apiPath, path))
            .collect(Collectors.toSet());

    Set<ChangelogDeletedDocument> deleted =
        changelog.getDeleted().stream()
            .map(path -> toDeletedDocument(apiPath, path, deletedType))
            .collect(Collectors.toSet());

    return new ChangelogResponse(changed, deleted, changelog.isChangeAll());
  }

  private static ChangelogChangedDocument toChangedDocument(String apiPath, String filePath) {
    String baseUrl = getDocumentPath(apiPath, filePath);

    String id = EncodingSchemaFactory.id(EncodingSchemaFactory.SchemaType.ZIP, baseUrl);
    String contentUrl =
        EncodingSchemaFactory.contentUrl(EncodingSchemaFactory.SchemaType.ZIP, baseUrl);

    return new ChangelogChangedDocument(id, JsonldTypes.MEDIA_OBJECT, contentUrl);
  }

  private static ChangelogDeletedDocument toDeletedDocument(
      String apiPath, String filePath, String type) {
    String baseUrl = getDocumentPath(apiPath, filePath);
    return new ChangelogDeletedDocument(baseUrl, type);
  }

  /** Maps a legislation changelog. */
  private static ChangelogResponse mapNorms(Changelog changelog) {
    Set<ChangelogChangedDocument> changed =
        changelog.getChanged().stream()
            .flatMap(id -> EliFile.fromString(id).stream())
            .map(ChangelogResponseMapper::toLegislationChangedDocument)
            .collect(Collectors.toSet());

    Set<ChangelogDeletedDocument> deleted =
        changelog.getDeleted().stream()
            .flatMap(id -> EliFile.fromString(id).stream())
            .map(ChangelogResponseMapper::toLegislationDeletedDocument)
            .collect(Collectors.toSet());

    return new ChangelogResponse(changed, deleted, changelog.isChangeAll());
  }

  private static ChangelogChangedDocument toLegislationChangedDocument(EliFile eli) {
    String rootUrl = prefixApiUrl(eli.getManifestationEli().getManifestationRoot());

    String id = EncodingSchemaFactory.id(EncodingSchemaFactory.SchemaType.ZIP, rootUrl);
    String contentUrl =
        EncodingSchemaFactory.contentUrl(EncodingSchemaFactory.SchemaType.ZIP, rootUrl);

    return new ChangelogChangedDocument(id, JsonldTypes.LEGISLATION_OBJECT, contentUrl);
  }

  private static ChangelogDeletedDocument toLegislationDeletedDocument(EliFile eli) {
    String baseUrl = prefixApiUrl(eli.getExpressionEli().toString());
    return new ChangelogDeletedDocument(baseUrl, JsonldTypes.LEGISLATION);
  }

  private static String prefixApiUrl(String path) {
    return ApiConfig.Paths.LEGISLATION + "/" + path;
  }

  private static String getDocumentPath(String apiPath, String filePath) {
    return apiPath + "/" + (filePath.substring(0, filePath.indexOf("/")));
  }
}
