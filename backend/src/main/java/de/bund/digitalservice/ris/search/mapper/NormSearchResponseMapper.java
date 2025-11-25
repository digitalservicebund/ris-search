package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LegalForceStatus;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionSearchSchema;
import de.bund.digitalservice.ris.search.schema.LegislationWorkSearchSchema;
import de.bund.digitalservice.ris.search.schema.PartialCollectionViewSchema;
import de.bund.digitalservice.ris.search.schema.PublicationIssueSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.schema.TextMatchSchema;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;

public class NormSearchResponseMapper {
  private NormSearchResponseMapper() {}

  /**
   * Creates a {@link SearchMemberSchema} instance from a {@link SearchHit<Norm>} entity.
   *
   * @param searchHit The input {@link SearchHit<Norm>} entity to be converted.
   * @return A new {@link SearchMemberSchema} instance mapped from the input {@link Norm}.
   */
  public static <T> SearchMemberSchema<LegislationWorkSearchSchema> fromSearchHit(
      SearchHit<T> searchHit) {
    Norm norm = (Norm) searchHit.getContent();
    List<TextMatchSchema> textMatches = getTextMatches(searchHit);
    return SearchMemberSchema.<LegislationWorkSearchSchema>builder()
        .item(fromDomain(norm))
        .textMatches(textMatches)
        .build();
  }

  private static TextMatchSchema convertArticleHitToTextMatchSchema(SearchHit<?> articleHit) {
    var articleHitContent = articleHit.getContent();
    String articleMatchingName =
        articleHit.getHighlightFields().getOrDefault("articles.name", List.of()).stream()
            .findFirst()
            .orElse("");
    String articleMatchingText =
        articleHit.getHighlightFields().getOrDefault("articles.text", List.of()).stream()
            .findFirst()
            .orElse("");
    if (articleHitContent instanceof Article article) {
      return toTextMatchSchema(
          articleMatchingName.isEmpty() ? article.name() : articleMatchingName,
          articleMatchingText.isEmpty() ? article.text() : articleMatchingText,
          article.eId());
    }
    if (articleHitContent instanceof Map<?, ?> hit
        && hit.containsKey("name")
        && hit.containsKey("eid")) {
      return toTextMatchSchema(
          hit.get("name").toString(), articleMatchingText, hit.get("eid").toString());
    }
    return null;
  }

  public static <T> List<TextMatchSchema> getTextMatches(SearchHit<T> searchHit) {
    Optional<SearchHits<?>> matchingArticles =
        Optional.ofNullable(searchHit.getInnerHits().getOrDefault("articles", null));

    List<TextMatchSchema> articleHits =
        matchingArticles
            .map(
                hits ->
                    hits.stream()
                        .map(NormSearchResponseMapper::convertArticleHitToTextMatchSchema)
                        .filter(Objects::nonNull)
                        .toList())
            .orElse(List.of());

    List<TextMatchSchema> normHits =
        searchHit.getHighlightFields().entrySet().stream()
            .map(
                entrySet ->
                    new TextMatchSchema(
                        getTextMatchName(entrySet.getKey()), entrySet.getValue().getFirst(), null))
            .toList();
    return ListUtils.union(articleHits, normHits);
  }

  private static TextMatchSchema toTextMatchSchema(String name, String text, String location) {
    return TextMatchSchema.builder().name(name).text(text).location(location).build();
  }

  private static String getTextMatchName(String key) {
    String converted = PageUtils.snakeCaseToCamelCase(key);
    if (converted.equals("officialTitle")) return "name";
    return converted;
  }

  public static LegislationWorkSearchSchema fromDomain(Norm norm) {
    String contentBaseUrl = ApiConfig.Paths.LEGISLATION + "/";
    String expressionEli = norm.getExpressionEli();
    LegalForceStatus legislationLegalForce =
        DateUtils.isActive(norm.getEntryIntoForceDate(), norm.getExpiryDate())
            ? LegalForceStatus.IN_FORCE
            : LegalForceStatus.NOT_IN_FORCE;

    String temporalCoverage =
        DateUtils.toDateIntervalString(norm.getEntryIntoForceDate(), norm.getExpiryDate());

    String expressionId = contentBaseUrl + expressionEli;

    PublicationIssueSchema publicationIssue =
        norm.getPublishedIn() != null ? new PublicationIssueSchema(norm.getPublishedIn()) : null;

    return LegislationWorkSearchSchema.builder()
        .id(contentBaseUrl + norm.getWorkEli())
        .abbreviation(norm.getOfficialAbbreviation())
        .alternateName(norm.getOfficialShortTitle())
        .legislationIdentifier(norm.getWorkEli())
        .legislationDate(norm.getNormsDate())
        .datePublished(norm.getDatePublished())
        .name(norm.getOfficialTitle())
        .isPartOf(publicationIssue)
        .workExample(
            LegislationExpressionSearchSchema.builder()
                .legislationLegalForce(legislationLegalForce)
                .legislationIdentifier(expressionEli)
                .id(expressionId)
                .temporalCoverage(temporalCoverage)
                .build())
        .build();
  }

  public static CollectionSchema<SearchMemberSchema<LegislationWorkSearchSchema>> fromDomain(
      final SearchPage<Norm> page, String path) {
    String id = String.format("%s?pageIndex=%d&size=%d", path, page.getNumber(), page.getSize());
    PartialCollectionViewSchema view = PartialCollectionViewMapper.fromPage(path, page);

    return CollectionSchema.<SearchMemberSchema<LegislationWorkSearchSchema>>builder()
        .id(id)
        .totalItems(page.getTotalElements())
        .member(page.stream().map(NormSearchResponseMapper::fromSearchHit).toList())
        .view(view)
        .build();
  }
}
