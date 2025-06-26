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

public class NormResponseMapper {
  private NormResponseMapper() {}

  public static LegislationWorkSchema fromDomain(Norm norm) {
    String contentBaseUrl = ApiConfig.Paths.LEGISLATION + "/";
    String expressionEli = norm.getExpressionEli();
    String manifestationEliXml = norm.getManifestationEliExample();

    List<LegislationObjectSchema> encoding;
    if (manifestationEliXml != null) {
      String xmlContentUrl = contentBaseUrl + manifestationEliXml;
      String htmlContentUrl = norm.getHtmlContentUrl();
      String zipContentUrl = xmlContentUrl.substring(0, xmlContentUrl.lastIndexOf('/')) + ".zip";

      String htmlId = contentBaseUrl + manifestationEliXml.replace(".xml", "/html");
      String xmlId = contentBaseUrl + manifestationEliXml.replace(".xml", "/xml");
      String zipId = zipContentUrl.replace(".zip", "/zip");

      encoding =
          List.of(
              LegislationObjectSchema.builder()
                  .id(htmlId)
                  .contentUrl(htmlContentUrl)
                  .encodingFormat("text/html")
                  .inLanguage("de")
                  .build(),
              LegislationObjectSchema.builder()
                  .id(xmlId)
                  .contentUrl(xmlContentUrl)
                  .encodingFormat("application/xml")
                  .inLanguage("de")
                  .build(),
              LegislationObjectSchema.builder()
                  .id(zipId)
                  .contentUrl(zipContentUrl)
                  .encodingFormat("application/zip")
                  .inLanguage("de")
                  .build());
    } else {
      encoding = Collections.emptyList();
    }

    LegalForceStatus legislationLegalForce =
        DateUtils.isActive(norm.getEntryIntoForceDate(), norm.getExpiryDate())
            ? LegalForceStatus.IN_FORCE
            : LegalForceStatus.NOT_IN_FORCE;

    String temporalCoverage =
        DateUtils.toDateIntervalString(norm.getEntryIntoForceDate(), norm.getExpiryDate());

    String expressionId = contentBaseUrl + expressionEli;
    LegislationExpressionSchema expression =
        LegislationExpressionSchema.builder()
            .id(expressionId)
            .legislationIdentifier(expressionEli)
            .legislationLegalForce(legislationLegalForce)
            .temporalCoverage(temporalCoverage)
            .encoding(encoding)
            .tableOfContents(buildTableOfContents(norm.getTableOfContents()))
            .hasPart(buildPartList(norm, expressionId))
            .build();

    PublicationIssueSchema publicationIssue =
        norm.getPublishedIn() != null ? new PublicationIssueSchema(norm.getPublishedIn()) : null;

    return LegislationWorkSchema.builder()
        .id(contentBaseUrl + norm.getWorkEli())
        .abbreviation(norm.getOfficialAbbreviation())
        .alternateName(norm.getOfficialShortTitle())
        .legislationIdentifier(norm.getWorkEli())
        .legislationDate(norm.getNormsDate())
        .datePublished(norm.getDatePublished())
        .name(norm.getOfficialTitle())
        .workExample(expression)
        .isPartOf(publicationIssue)
        .build();
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

  private static LegislationExpressionPartSchema buildPart(Article article, String idPrefix) {
    List<LegislationObjectSchema> encoding;
    if (article.manifestationEli() != null) {
      final LegislationObjectSchema encodingItem =
          LegislationObjectSchema.builder()
              .encodingFormat(MediaType.APPLICATION_XML_VALUE)
              .contentUrl(article.manifestationEli())
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
