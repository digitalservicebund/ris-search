package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.schema.AdministrativeDirectiveSearchSchema;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.PartialCollectionViewSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.schema.TextMatchSchema;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;

/**
 * The AdministrativeDirectiveSearchSchemaMapper class provides utility methods for mapping and
 * transforming administrative directive search results between domain models, schemas, and
 * paginated search contexts.
 *
 * <p>This class is designed to convert raw search data or domain objects into structured schemas
 * compatible with JSON-LD and other standardized representations.
 *
 * <p>Note: This class contains only static utility methods and should not be instantiated.
 */
public class AdministrativeDirectiveSearchSchemaMapper {
  private AdministrativeDirectiveSearchSchemaMapper() {}

  /**
   * Transforms a search hit into a SearchMemberSchema containing an item of type
   * AdministrativeDirectiveSearchSchema and associated text matches.
   *
   * @param <T> The type parameter of the search hit content.
   * @param searchHit The search hit containing the content and highlight fields. The content is
   *     expected to be of type AdministrativeDirective.
   * @return A SearchMemberSchema containing the transformed AdministrativeDirectiveSearchSchema and
   *     corresponding text matches.
   */
  public static <T> SearchMemberSchema<AdministrativeDirectiveSearchSchema> fromSearchHit(
      SearchHit<T> searchHit) {
    AdministrativeDirective document = (AdministrativeDirective) searchHit.getContent();
    List<TextMatchSchema> textMatches = getTextMatches(searchHit);

    return SearchMemberSchema.<AdministrativeDirectiveSearchSchema>builder()
        .item(fromDomain(document))
        .textMatches(textMatches)
        .build();
  }

  /**
   * A static method to build a list of text matched from a searchHit
   *
   * @param searchHit the search hit to extract the text matches from
   * @param <T> the type of content inside the searchHit
   * @return a list of the text matches
   */
  public static <T> List<TextMatchSchema> getTextMatches(SearchHit<T> searchHit) {
    return searchHit.getHighlightFields().entrySet().stream()
        .flatMap(
            textMatch ->
                textMatch.getValue().stream()
                    .map(
                        valueMatch ->
                            TextMatchSchema.builder()
                                .name(getTextMatchKey(textMatch))
                                .text(valueMatch)
                                .build()))
        .toList();
  }

  /**
   * Case-convert text match keys (field names) to match the result schema.
   *
   * @param textMatch The text match to take the name from.
   * @return A normalized key
   */
  private static String getTextMatchKey(Map.Entry<String, List<String>> textMatch) {
    final String key = textMatch.getKey();
    if (key.endsWith(".text")) {
      return PageUtils.snakeCaseToCamelCase(StringUtils.removeEnd(key, ".text"));
    }
    return PageUtils.snakeCaseToCamelCase(key);
  }

  /**
   * Transforms an AdministrativeDirective domain object into its corresponding
   * AdministrativeDirectiveSearchSchema representation.
   *
   * @param doc The AdministrativeDirective object containing the information to be mapped. This
   *     includes details like document number, headline, type, entry into force date, and reference
   *     numbers.
   * @return An AdministrativeDirectiveSearchSchema instance that encapsulates the transformed data
   *     from the provided domain object.
   */
  public static AdministrativeDirectiveSearchSchema fromDomain(AdministrativeDirective doc) {
    String entityURI = ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE + "/" + doc.documentNumber();
    return AdministrativeDirectiveSearchSchema.builder()
        .id(entityURI)
        .documentNumber(doc.documentNumber())
        .headline(doc.headline())
        .shortReport(doc.shortReport())
        .entryIntoForceDate(doc.entryIntoEffectDate())
        .referenceNumbers(doc.referenceNumbers())
        .legislationAuthority(doc.legislationAuthority())
        .documentType(doc.documentType())
        .build();
  }

  /**
   * Converts a paginated search result into a collection schema of administrative directive search
   * results.
   *
   * @param <T> The type parameter of the paginated search result.
   * @param page The paginated search result containing the data to be transformed. This includes
   *     the page information and the content.
   * @return A collection schema representing the administrative directive search results, including
   *     metadata such as total items, a unique identifier, and a list of member schemas.
   */
  public static <T>
      CollectionSchema<SearchMemberSchema<AdministrativeDirectiveSearchSchema>> fromSearchPage(
          final SearchPage<T> page) {
    String collectionBasePath = ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE;
    PartialCollectionViewSchema view =
        PartialCollectionViewMapper.fromPage(collectionBasePath, page);

    String id =
        String.format(
            "%s?pageIndex=%d&size=%d", collectionBasePath, page.getNumber(), page.getSize());

    return CollectionSchema.<SearchMemberSchema<AdministrativeDirectiveSearchSchema>>builder()
        .id(id)
        .totalItems(page.getTotalElements())
        .member(
            page.stream().map(AdministrativeDirectiveSearchSchemaMapper::fromSearchHit).toList())
        .view(view)
        .build();
  }
}
