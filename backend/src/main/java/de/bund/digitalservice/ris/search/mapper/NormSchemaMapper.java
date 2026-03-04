package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.models.opensearch.TableOfContentsItem;
import de.bund.digitalservice.ris.search.schema.LegalForceStatus;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionPartSchema;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionSchema;
import de.bund.digitalservice.ris.search.schema.LegislationObjectSchema;
import de.bund.digitalservice.ris.search.schema.LegislationWorkSchema;
import de.bund.digitalservice.ris.search.schema.PublicationIssueSchema;
import de.bund.digitalservice.ris.search.schema.TableOfContentsSchema;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.springframework.http.MediaType;

/**
 * Utility class for mapping {@link Norm} domain objects to a {@link LegislationExpressionSchema}.
 * This class provides methods to transform legislative data into the corresponding schema structure
 * used for legal work representations.
 *
 * <p>This class is non-instantiable, as it contains only static utility methods.
 */
public class NormSchemaMapper {
  private NormSchemaMapper() {}

  private static final String CONTENT_BASE_URL = ApiConfig.Paths.LEGISLATION + "/";

  /**
   * Maps a {@link Norm} object to a {@link LegislationExpressionSchema} object.
   *
   * @param norm the {@link Norm} instance to be converted; it contains all necessary fields such as
   *     ELI references, metadata, and publication information.
   * @return a {@link LegislationExpressionSchema} object representing the input {@link Norm} with
   *     its associated legal force, temporal coverage, and publication details.
   */
  public static LegislationExpressionSchema fromDomain(Norm norm) {
    String expressionEli = norm.getExpressionEli();
    String manifestationEliXml = norm.getManifestationEliExample();

    var encodings = getEncodings(CONTENT_BASE_URL, manifestationEliXml);

    LegalForceStatus legislationLegalForce =
        DateUtils.isActive(norm.getEntryIntoForceDate(), norm.getExpiryDate())
            ? LegalForceStatus.IN_FORCE
            : LegalForceStatus.NOT_IN_FORCE;

    String temporalCoverage =
        DateUtils.toDateIntervalString(norm.getEntryIntoForceDate(), norm.getExpiryDate());

    String expressionId = CONTENT_BASE_URL + expressionEli;

    PublicationIssueSchema publicationIssue =
        norm.getPublishedIn() != null ? new PublicationIssueSchema(norm.getPublishedIn()) : null;

    return LegislationExpressionSchema.builder()
        .id(CONTENT_BASE_URL + norm.getExpressionEli())
        .abbreviation(norm.getOfficialAbbreviation())
        .alternateName(norm.getOfficialShortTitle())
        .exampleOfWork(new LegislationWorkSchema(norm.getWorkEli()))
        .legislationIdentifier(norm.getExpressionEli())
        .legislationDate(norm.getNormsDate())
        .datePublished(norm.getDatePublished())
        .name(norm.getOfficialTitle())
        .isPartOf(publicationIssue)
        .legislationLegalForce(legislationLegalForce)
        .temporalCoverage(temporalCoverage)
        .encoding(encodings)
        .tableOfContents(buildTableOfContents(norm.getTableOfContents()))
        .hasPart(buildPartList(norm, expressionId))
        .build();
  }

  private static List<LegislationObjectSchema> getEncodings(
      String contentBaseUrl, @Nullable String manifestationEli) {
    if (manifestationEli == null) {
      return Collections.emptyList();
    }

    var encodingBaseUrl = contentBaseUrl + manifestationEli.replace(".xml", "");
    var encodingZipBaseUrl =
        contentBaseUrl + manifestationEli.substring(0, manifestationEli.lastIndexOf('/'));

    return EncodingSchemaFactory.legislationEncodingSchemas(encodingBaseUrl, encodingZipBaseUrl);
  }

  private static List<TableOfContentsSchema> buildTableOfContents(
      @Nullable List<TableOfContentsItem> tableOfContentsItems) {
    if (tableOfContentsItems == null) {
      return List.of();
    }
    return tableOfContentsItems.stream()
        .map(
            item ->
                new TableOfContentsSchema(
                    item.id(),
                    item.marker(),
                    item.heading(),
                    buildTableOfContents(item.children())))
        .toList();
  }

  private static List<LegislationExpressionPartSchema> buildPartList(Norm norm, String idPrefix) {
    if (norm.getArticles() == null) return Collections.emptyList();
    return norm.getArticles().stream().map(article -> buildPart(article, idPrefix)).toList();
  }

  /**
   * This maps articles and attachments of a norm. Both are currently part of the articles array.
   *
   * @param article Article or Attachment of a norm
   * @param idPrefix idPrefix referencing the corresponding norm
   * @return LegislationExpressionPartSchema object
   */
  private static LegislationExpressionPartSchema buildPart(Article article, String idPrefix) {
    List<LegislationObjectSchema> encoding;
    // Only attachments have their own manifestationELi and receive an encoding object.
    if (article.manifestationEli() != null) {
      final LegislationObjectSchema encodingItem =
          LegislationObjectSchema.builder()
              .encodingFormat(MediaType.APPLICATION_XML_VALUE)
              .contentUrl(CONTENT_BASE_URL + article.manifestationEli())
              .build();
      encoding = List.of(encodingItem);
    } else {
      encoding = null;
    }
    return LegislationExpressionPartSchema.builder()
        .id(idPrefix + "#" + article.eId())
        .name(article.name())
        .eId(article.eId())
        .guid(article.guid())
        .entryIntoForceDate(article.entryIntoForceDate())
        .expiryDate(article.expiryDate())
        .isActive(DateUtils.isActive(article.entryIntoForceDate(), article.expiryDate()))
        .encoding(encoding)
        .build();
  }
}
