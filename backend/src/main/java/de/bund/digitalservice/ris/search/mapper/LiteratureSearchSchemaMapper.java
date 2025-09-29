package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LiteratureSearchSchema;
import de.bund.digitalservice.ris.search.schema.PartialCollectionViewSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.schema.TextMatchSchema;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;

public class LiteratureSearchSchemaMapper {
  private LiteratureSearchSchemaMapper() {}

  public static <T> SearchMemberSchema<LiteratureSearchSchema> fromSearchHit(
      SearchHit<T> searchHit) {
    Literature document = (Literature) searchHit.getContent();
    List<TextMatchSchema> textMatches =
        searchHit.getHighlightFields().entrySet().stream()
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

    return SearchMemberSchema.<LiteratureSearchSchema>builder()
        .item(fromDomain(document))
        .textMatches(textMatches)
        .build();
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

  public static LiteratureSearchSchema fromDomain(Literature doc) {
    String entityURI = ApiConfig.Paths.LITERATURE + "/" + doc.documentNumber();
    return LiteratureSearchSchema.builder()
        .id(entityURI)
        .inLanguage("de")
        .documentNumber(doc.documentNumber())
        .recordingDate(doc.recordingDate())
        .yearsOfPublication(doc.yearsOfPublication())
        .documentTypes(doc.documentTypes())
        .dependentReferences(doc.dependentReferences())
        .independentReferences(doc.independentReferences())
        .headline(doc.mainTitle())
        .alternativeTitle(doc.documentaryTitle())
        .authors(doc.authors())
        .collaborators(doc.collaborators())
        .shortReport(doc.shortReport())
        .outline(doc.outline())
        .build();
  }

  public static <T> CollectionSchema<SearchMemberSchema<LiteratureSearchSchema>> fromSearchPage(
      final SearchPage<T> page) {
    String collectionBasePath = ApiConfig.Paths.LITERATURE;
    PartialCollectionViewSchema view =
        PartialCollectionViewMapper.fromPage(collectionBasePath, page);

    String id =
        String.format("%s?page=%d&size=%d", collectionBasePath, page.getNumber(), page.getSize());

    return CollectionSchema.<SearchMemberSchema<LiteratureSearchSchema>>builder()
        .id(id)
        .totalItems(page.getTotalElements())
        .member(page.stream().map(LiteratureSearchSchemaMapper::fromSearchHit).toList())
        .view(view)
        .build();
  }
}
