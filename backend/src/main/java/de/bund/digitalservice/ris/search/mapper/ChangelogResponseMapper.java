package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.schema.ChangelogDocument;
import de.bund.digitalservice.ris.search.schema.ChangelogResponse;
import de.bund.digitalservice.ris.search.schema.JsonldTypes;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import java.util.Set;
import java.util.stream.Collectors;

/** Mapper class to map Changelog objects to api representations of a changelog. */
public class ChangelogResponseMapper {

  private ChangelogResponseMapper() {}

  /**
   * Maps A changelog to a ChangelogResponse based on the documentKind.
   *
   * @param changelog Changelog to me mapped
   * @param documentKind documentKind to apply document specific mapping logic
   * @return ChangelogResponse
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

  /**
   * Maps a changelog to a ChangelogResponse. It groups all changed files to a set of affected
   * expressions.
   *
   * @param changelog Changelog to me mapped
   * @return ChangelogResponse with all changes by expression
   */
  private static ChangelogResponse mapNorms(Changelog changelog) {
    String apiUrl = ApiConfig.Paths.LEGISLATION + "/";
    Set<ChangelogDocument> changed =
        changelog.getChanged().stream()
            .flatMap(
                id ->
                    EliFile.fromString(id)
                        .map(
                            eli ->
                                new ChangelogDocument(
                                    apiUrl + eli.getExpressionEli(), JsonldTypes.LEGISLATION))
                        .stream())
            .collect(Collectors.toSet());

    Set<ChangelogDocument> deleted =
        changelog.getDeleted().stream()
            .flatMap(
                id ->
                    EliFile.fromString(id)
                        .map(
                            eli ->
                                new ChangelogDocument(
                                    apiUrl + eli.getExpressionEli(), JsonldTypes.LEGISLATION))
                        .stream())
            .collect(Collectors.toSet());

    return new ChangelogResponse(changed, deleted, changelog.isChangeAll());
  }

  /**
   * Maps a changelog to a ChangelogResponse of any other DocumentType. It groups all changed files
   * to a set of affected expressions.
   *
   * @param changelog Changelog to me mapped
   * @param type String representation of the @type of a document
   * @return ChangelogResponse with all changes by expression
   */
  private static ChangelogResponse mapDocument(Changelog changelog, String apiPath, String type) {
    var changed =
        changelog.getChanged().stream()
            .map(
                path ->
                    new ChangelogDocument(
                        apiPath + "/" + (path.substring(0, path.indexOf("/"))), type))
            .collect(Collectors.toSet());
    var deleted =
        changelog.getDeleted().stream()
            .map(
                path ->
                    new ChangelogDocument(
                        apiPath + "/" + (path.substring(0, path.indexOf("/"))), type))
            .collect(Collectors.toSet());

    return new ChangelogResponse(changed, deleted, changelog.isChangeAll());
  }
}
