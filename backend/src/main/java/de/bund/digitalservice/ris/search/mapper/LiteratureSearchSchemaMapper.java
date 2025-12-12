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

/**
 * The LiteratureSearchSchemaMapper class provides static methods to map opensearch results to the
 * literature objects returned by the api
 */
public class LiteratureSearchSchemaMapper {
  private LiteratureSearchSchemaMapper() {}

  /**
   * Converts a {@link SearchHit} object into a {@link SearchMemberSchema} containing a {@link
   * LiteratureSearchSchema} representation. This method transforms the domain-level representation
   * of a document within the search hit into a structured search schema and extracts text match
   * highlights.
   *
   * @param <T> The type of content contained in the given {@link SearchHit}.
   * @param searchHit The {@link SearchHit} object containing the document content and associated
   *     highlight fields, representing the matching search result.
   * @return A {@link SearchMemberSchema} instance containing the transformed {@link
   *     LiteratureSearchSchema} document and a list of {@link TextMatchSchema} objects representing
   *     highlighted text matches.
   */
  public static <T> SearchMemberSchema<LiteratureSearchSchema> fromSearchHit(
      SearchHit<T> searchHit) {
    Literature document = (Literature) searchHit.getContent();
    List<TextMatchSchema> textMatches = getTextMatches(searchHit);

    return SearchMemberSchema.<LiteratureSearchSchema>builder()
        .item(fromDomain(document))
        .textMatches(textMatches)
        .build();
  }

  /**
   * Extracts a list of {@link TextMatchSchema} objects representing the highlighted text matches
   * from the given {@link SearchHit} object.
   *
   * <p>The method processes the highlight fields in the search hit, generating a structured result
   * that maps each field's normalized key to its associated matched text snippets.
   *
   * @param <T> The type of the content contained in the given {@link SearchHit}.
   * @param searchHit The {@link SearchHit} object containing the document data and highlight
   *     fields, which represents the matches returned by the search query.
   * @return A list of {@link TextMatchSchema} objects representing the fields and snippets of
   *     matched text.
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
   * Converts a {@link Literature} domain object into a {@link LiteratureSearchSchema}
   * representation. This method maps the properties of the given domain object to equivalent
   * properties in the literature search schema for use in document search contexts.
   *
   * @param doc The {@link Literature} object containing the domain-level representation of a
   *     literature document, including metadata and detailed information such as publication
   *     details, titles, authorship, and references.
   * @return A {@link LiteratureSearchSchema} instance populated with the mapped data from the given
   *     domain object.
   */
  public static LiteratureSearchSchema fromDomain(Literature doc) {
    String entityURI = ApiConfig.Paths.LITERATURE + "/" + doc.documentNumber();
    return LiteratureSearchSchema.builder()
        .id(entityURI)
        .inLanguage("de")
        .documentNumber(doc.documentNumber())
        .yearsOfPublication(doc.yearsOfPublication())
        .firstPublicationDate(doc.firstPublicationDate())
        .documentTypes(doc.documentTypes())
        .dependentReferences(doc.dependentReferences())
        .independentReferences(doc.independentReferences())
        .headline(doc.mainTitle())
        .alternativeHeadline(doc.documentaryTitle())
        .authors(doc.authors())
        .collaborators(doc.collaborators())
        .shortReport(doc.shortReport())
        .outline(doc.outline())
        .literatureType(LiteratureTypeMapper.mapLiteratureType(doc.documentNumber()))
        .build();
  }

  /**
   * Maps a {@link SearchPage} object into a {@link CollectionSchema} containing {@link
   * SearchMemberSchema} instances of {@link LiteratureSearchSchema}. This method processes search
   * results by transforming them into a structured collection schema, including metadata such as
   * pagination details and total items.
   *
   * @param <T> The type of content contained in the given {@link SearchPage}.
   * @param page The {@link SearchPage} object containing the search results and pagination data
   *     that will be transformed into a collection schema.
   * @return A {@link CollectionSchema} containing a collection of {@link SearchMemberSchema}
   *     objects with transformed search results and metadata.
   */
  public static <T> CollectionSchema<SearchMemberSchema<LiteratureSearchSchema>> fromSearchPage(
      final SearchPage<T> page) {
    String collectionBasePath = ApiConfig.Paths.LITERATURE;
    PartialCollectionViewSchema view =
        PartialCollectionViewMapper.fromPage(collectionBasePath, page);

    String id =
        String.format(
            "%s?pageIndex=%d&size=%d", collectionBasePath, page.getNumber(), page.getSize());

    return CollectionSchema.<SearchMemberSchema<LiteratureSearchSchema>>builder()
        .id(id)
        .totalItems(page.getTotalElements())
        .member(page.stream().map(LiteratureSearchSchemaMapper::fromSearchHit).toList())
        .view(view)
        .build();
  }
}
