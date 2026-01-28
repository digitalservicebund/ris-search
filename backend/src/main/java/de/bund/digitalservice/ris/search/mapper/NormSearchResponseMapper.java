package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LegalForceStatus;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionSearchSchema;
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

/**
 * A utility class responsible for mapping search results of type {@link Norm} to specific response
 * schemas used in the application. This mapper facilitates the transformation of search results and
 * related metadata into structures compatible with API response formats.
 *
 * <p>The mappings include: - Conversion of {@link SearchHit} instances containing {@link Norm} into
 * {@link SearchMemberSchema}. - Extraction and conversion of text matches from search results into
 * {@link TextMatchSchema}. - Transformation of search pages into {@link CollectionSchema}
 * containing structured response items.
 *
 * <p>This class is not intended to be instantiated; all utility methods are static.
 */
public class NormSearchResponseMapper {
  private NormSearchResponseMapper() {}

  /**
   * Creates a {@link SearchMemberSchema} instance from a {@link SearchPage} of {@code Norm} entity.
   *
   * @param <T> The type of the entity contained within the {@link SearchHit}.
   * @param searchHit The input {@link SearchHit} of {@code Norm} entity to be converted.
   * @return A new {@link SearchMemberSchema} instance mapped from the input {@link Norm}.
   */
  public static <T> SearchMemberSchema<LegislationExpressionSearchSchema> fromSearchHit(
      SearchHit<T> searchHit) {
    Norm norm = (Norm) searchHit.getContent();
    List<TextMatchSchema> textMatches = getTextMatches(searchHit);
    return SearchMemberSchema.<LegislationExpressionSearchSchema>builder()
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

  /**
   * Extracts text matches from a given {@link SearchHit} object. This includes matches found in
   * inner hits (e.g., articles) and highlight fields. The method combines results from both sources
   * into a unified list of {@link TextMatchSchema}.
   *
   * @param searchHit The {@link SearchHit} object from which text matches are to be retrieved. This
   *     may include inner hits such as "articles" and highlight fields.
   * @param <T> The type of the entity contained within the {@link SearchHit}.
   * @return A list of {@link TextMatchSchema} objects representing the text matches found in the
   *     {@link SearchHit}, including matches from inner hits and highlighted fields.
   */
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

  /**
   * Maps the given {@link Norm} entity to a {@link LegislationExpressionSearchSchema} instance.
   *
   * @param norm The {@link Norm} entity containing legislative data to be converted.
   * @return A {@link LegislationExpressionSearchSchema} instance constructed from the provided
   *     {@link Norm}.
   */
  public static LegislationExpressionSearchSchema fromDomain(Norm norm) {
    String contentBaseUrl = ApiConfig.Paths.LEGISLATION + "/";

    PublicationIssueSchema publicationIssue =
        norm.getPublishedIn() != null ? new PublicationIssueSchema(norm.getPublishedIn()) : null;

    LegalForceStatus legislationLegalForce =
        DateUtils.isActive(norm.getEntryIntoForceDate(), norm.getExpiryDate())
            ? LegalForceStatus.IN_FORCE
            : LegalForceStatus.NOT_IN_FORCE;

    String expressionEli = norm.getExpressionEli();
    String expressionId = contentBaseUrl + expressionEli;

    String temporalCoverage =
        DateUtils.toDateIntervalString(norm.getEntryIntoForceDate(), norm.getExpiryDate());

    return LegislationExpressionSearchSchema.builder()
        .id(expressionId)
        .abbreviation(norm.getOfficialAbbreviation())
        .alternateName(norm.getOfficialShortTitle())
        .legislationIdentifier(expressionEli)
        .legislationDate(norm.getNormsDate())
        .datePublished(norm.getDatePublished())
        .name(norm.getOfficialTitle())
        .isPartOf(publicationIssue)
        .legislationLegalForce(legislationLegalForce)
        .legislationIdentifier(expressionEli)
        .temporalCoverage(temporalCoverage)
        .build();
  }

  /**
   * Converts the given {@link SearchPage} of {@code Norm} into a {@link CollectionSchema} of {@link
   * SearchMemberSchema} containing {@link LegislationExpressionSearchSchema}.
   *
   * @param page The {@link SearchPage} containing {@link Norm} elements to be transformed.
   * @param path The base path used for constructing the collection's ID and view metadata.
   * @return A {@link CollectionSchema} initialized with members mapped from the input page, the
   *     total number of items, and the corresponding view metadata.
   */
  public static CollectionSchema<SearchMemberSchema<LegislationExpressionSearchSchema>> fromDomain(
      final SearchPage<Norm> page, String path) {
    String id = String.format("%s?pageIndex=%d&size=%d", path, page.getNumber(), page.getSize());
    PartialCollectionViewSchema view = PartialCollectionViewMapper.fromPage(path, page);

    return CollectionSchema.<SearchMemberSchema<LegislationExpressionSearchSchema>>builder()
        .id(id)
        .totalItems(page.getTotalElements())
        .member(page.stream().map(NormSearchResponseMapper::fromSearchHit).toList())
        .view(view)
        .build();
  }
}
