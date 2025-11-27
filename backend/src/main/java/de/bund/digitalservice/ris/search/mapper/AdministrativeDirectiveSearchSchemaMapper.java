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

public class AdministrativeDirectiveSearchSchemaMapper {
  private AdministrativeDirectiveSearchSchemaMapper() {}

  public static <T> SearchMemberSchema<AdministrativeDirectiveSearchSchema> fromSearchHit(
      SearchHit<T> searchHit) {
    AdministrativeDirective document = (AdministrativeDirective) searchHit.getContent();
    List<TextMatchSchema> textMatches = getTextMatches(searchHit);

    return SearchMemberSchema.<AdministrativeDirectiveSearchSchema>builder()
        .item(fromDomain(document))
        .textMatches(textMatches)
        .build();
  }

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

  public static AdministrativeDirectiveSearchSchema fromDomain(AdministrativeDirective doc) {
    String entityURI = ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE + "/" + doc.documentNumber();
    return AdministrativeDirectiveSearchSchema.builder()
        .id(entityURI)
        .documentNumber(doc.documentNumber())
        .headline(doc.headline())
        .entryIntoForceDate(doc.entryIntoEffectDate())
        .referenceNumbers(doc.referenceNumbers())
        .documentType(doc.documentType())
        .build();
  }

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
