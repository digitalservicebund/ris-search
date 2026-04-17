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

    var encodings = getEncodings(manifestationEliXml);

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
        .exampleOfWork(
            new LegislationWorkSchema(
                CONTENT_BASE_URL + norm.getWorkEli(),
                norm.getWorkEli(),
                norm.getNormsDate(),
                norm.getDatePublished(),
                publicationIssue))
        .legislationIdentifier(norm.getExpressionEli())
        .name(norm.getOfficialTitle())
        .legislationLegalForce(legislationLegalForce)
        .temporalCoverage(temporalCoverage)
        .encoding(encodings)
        .hasPart(buildNestedHasPart(norm.getTableOfContents(), expressionId, norm.getArticles()))
        .build();
  }

  /**
   * create manifestation references from the manifestationEli
   *
   * @param manifestationEli manifestationEli of a Norm
   * @return List of Legislation manifestation reference objects
   */
  public static List<LegislationObjectSchema> getEncodings(@Nullable String manifestationEli) {
    if (manifestationEli == null) {
      return Collections.emptyList();
    }

    var encodingBaseUrl = CONTENT_BASE_URL + manifestationEli.replace(".xml", "");
    var encodingZipBaseUrl =
        CONTENT_BASE_URL + manifestationEli.substring(0, manifestationEli.lastIndexOf('/'));

    return EncodingSchemaFactory.legislationEncodingSchemas(encodingBaseUrl, encodingZipBaseUrl);
  }

  /**
   * Builds the hasPart structure for a legislation expression.
   *
   * <p>The resulting list represents the hierarchical structure of the norm, where each entry
   * corresponds to a structural element derived from the structure of the table of contents. Each
   * part includes its identifiers and may contain nested subparts.
   *
   * @param tableOfContentsItems the table of contents entries describing the structure of the norm;
   *     may be {@code null}
   * @param idPrefix the prefix used to generate unique identifiers for each part in the hierarchy
   * @param articles the list of articles used to enrich or resolve the corresponding parts
   * @return a list of {@link LegislationExpressionPartSchema}
   */
  private static List<LegislationExpressionPartSchema> buildNestedHasPart(
      @Nullable List<TableOfContentsItem> tableOfContentsItems,
      String idPrefix,
      List<Article> articles) {
    if (tableOfContentsItems == null) {
      return List.of();
    }
    return tableOfContentsItems.stream()
        .map(item -> buildLegislationExpressionPart(articles, item, idPrefix))
        .toList();
  }

  /**
   * Builds a single legislation expression component. It combines the table of contents and the
   * articles array to construct the result, depending on whether the node is a leaf (present in the
   * articles array) or an internal node.
   *
   * @param articles articles array
   * @param tocItem tableOfContentsItem that is supposed to be mapped to a part
   * @param idPrefix the prefix used to generate unique identifiers for each part in the hierarchy
   * @return {@link LegislationExpressionPartSchema}
   */
  private static LegislationExpressionPartSchema buildLegislationExpressionPart(
      List<Article> articles, TableOfContentsItem tocItem, String idPrefix) {
    var article = articles.stream().filter(a -> tocItem.id().equals(a.eId())).findFirst();

    if (article.isPresent()) {
      return buildPart(article.get(), idPrefix, tocItem.marker(), tocItem.heading());
    } else {
      return new LegislationExpressionPartSchema(
          idPrefix + "#" + tocItem.id(),
          tocItem.id(),
          tocItem.marker(),
          tocItem.heading(),
          "",
          List.of(),
          buildNestedHasPart(tocItem.children(), idPrefix, articles));
    }
  }

  /**
   * This maps articles, preamble formula, conclusions formula and attachments of a norm. All are
   * currently part of the articles array.
   *
   * @param article Article or Attachment of a norm
   * @param idPrefix idPrefix referencing the corresponding norm
   * @return LegislationExpressionPartSchema object
   */
  private static LegislationExpressionPartSchema buildPart(
      Article article, String idPrefix, String marker, String heading) {
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
      encoding = List.of();
    }
    return LegislationExpressionPartSchema.builder()
        .id(idPrefix + "#" + article.eId())
        .name(marker)
        .eId(article.eId())
        .headline(heading)
        .temporalCoverage(
            DateUtils.toDateIntervalString(article.entryIntoForceDate(), article.expiryDate()))
        .encoding(encoding)
        .hasPart(List.of())
        .build();
  }
}
