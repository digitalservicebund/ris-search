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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
      case LEGISLATION -> mapLegislation(changelog);
      case CASE_LAW ->
          mapStandardDocument(changelog, ApiConfig.Paths.CASELAW, JsonldTypes.DECISION);
      case LITERATURE ->
          mapStandardDocument(changelog, ApiConfig.Paths.LITERATURE, JsonldTypes.LITERATURE);
      case ADMINISTRATIVE_DIRECTIVE ->
          mapStandardDocument(
              changelog,
              ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE,
              JsonldTypes.ADMINISTRATIVE_DIRECTIVE);
    };
  }

  private static ChangelogResponse mapStandardDocument(
      Changelog changelog, String apiPath, String jsonldType) {
    return createResponse(
        changelog,
        path ->
            Stream.of(
                toChangedDocument(getDocumentBaseUrl(apiPath, path), JsonldTypes.MEDIA_OBJECT)),
        path ->
            Stream.of(new ChangelogDeletedDocument(getDocumentBaseUrl(apiPath, path), jsonldType)));
  }

  private static ChangelogResponse mapLegislation(Changelog changelog) {
    return createResponse(
        changelog,
        id ->
            EliFile.fromString(id).stream()
                .map(
                    eli ->
                        toChangedDocument(
                            getLegislationBaseUrl(eli.getManifestationEli().getManifestationRoot()),
                            JsonldTypes.LEGISLATION_OBJECT)),
        id ->
            EliFile.fromString(id).stream()
                .map(
                    eli ->
                        new ChangelogDeletedDocument(
                            getLegislationBaseUrl(eli.getExpressionEli().toString()),
                            JsonldTypes.LEGISLATION)));
  }

  private static ChangelogResponse createResponse(
      Changelog changelog,
      Function<String, Stream<ChangelogChangedDocument>> changeMapper,
      Function<String, Stream<ChangelogDeletedDocument>> deleteMapper) {

    Set<ChangelogChangedDocument> changed =
        changelog.getChanged().stream().flatMap(changeMapper).collect(Collectors.toSet());

    Set<ChangelogDeletedDocument> deleted =
        changelog.getDeleted().stream().flatMap(deleteMapper).collect(Collectors.toSet());

    return new ChangelogResponse(changed, deleted, changelog.isChangeAll());
  }

  private static ChangelogChangedDocument toChangedDocument(String baseUrl, String mediaType) {
    String id = EncodingSchemaFactory.id(EncodingSchemaFactory.SchemaType.ZIP, baseUrl);
    String contentUrl =
        EncodingSchemaFactory.contentUrl(EncodingSchemaFactory.SchemaType.ZIP, baseUrl);
    return new ChangelogChangedDocument(id, mediaType, contentUrl);
  }

  private static String getLegislationBaseUrl(String path) {
    return ApiConfig.Paths.LEGISLATION + "/" + path;
  }

  private static String getDocumentBaseUrl(String apiPath, String filePath) {
    int firstSlash = filePath.indexOf("/");
    String cleanedPath = (firstSlash == -1) ? filePath : filePath.substring(0, firstSlash);
    return apiPath + "/" + cleanedPath;
  }
}
