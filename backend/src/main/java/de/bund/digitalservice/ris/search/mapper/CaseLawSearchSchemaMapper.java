package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.schema.CaseLawSearchSchema;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.PartialCollectionViewSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.schema.TextMatchSchema;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.Strings;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;

/**
 * This class provides methods for mapping search hits and search pages to specific schema
 * representations used in the context of legal case law search results. The main purpose is to
 * transform raw search results into structured types like {@link CaseLawSearchSchema} and {@link
 * SearchMemberSchema}, ensuring the data adheres to the intended output format and structure. The
 * methods also handle normalization of text match keys and the extraction of text matches for
 * enhanced search result visibility.
 *
 * <p>The class is designed to be a utility and therefore has a private constructor to prevent
 * instantiation.
 */
public class CaseLawSearchSchemaMapper {
  private CaseLawSearchSchemaMapper() {}

  /**
   * Maps a {@link SearchHit} to a {@link SearchMemberSchema} containing a {@link
   * CaseLawSearchSchema} item and associated text matches. This method extracts the primary content
   * from the {@link SearchHit}, transforms it into a {@link CaseLawSearchSchema}, and processes
   * highlighted fields into a structure of {@link TextMatchSchema} instances.
   *
   * @param <T> The type of the content inside the {@link SearchHit}.
   * @param searchHit The search hit containing the content and highlighted fields to be
   *     transformed.
   * @return A {@link SearchMemberSchema} containing the mapped {@link CaseLawSearchSchema} item and
   *     a list of text matches.
   */
  public static <T> SearchMemberSchema<CaseLawSearchSchema> fromSearchHit(SearchHit<T> searchHit) {
    CaseLawDocumentationUnit document = (CaseLawDocumentationUnit) searchHit.getContent();
    List<TextMatchSchema> textMatches = getTextMatches(searchHit);

    return SearchMemberSchema.<CaseLawSearchSchema>builder()
        .item(fromDomain(document))
        .textMatches(textMatches)
        .build();
  }

  /**
   * Extracts and processes text matches from a given {@link SearchHit} object into a list of {@link
   * TextMatchSchema} instances. This method takes the highlight fields from the search hit,
   * constructs text match entries for each highlighted field, and normalizes their keys to fit a
   * specific schema.
   *
   * @param <T> The type of the content inside the {@link SearchHit}.
   * @param searchHit The search hit object containing the highlight fields to be transformed into
   *     text match schemas.
   * @return A list of {@link TextMatchSchema} instances representing the text matches from the
   *     search hit's highlight fields.
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
      return PageUtils.snakeCaseToCamelCase(Strings.CS.removeEnd(key, ".text"));
    }
    return PageUtils.snakeCaseToCamelCase(key);
  }

  /**
   * Maps a {@link CaseLawDocumentationUnit} into a {@link CaseLawSearchSchema}. This method
   * transfers relevant fields from the source domain object into a schema object that adheres to a
   * specific data representation format.
   *
   * @param doc the {@link CaseLawDocumentationUnit} instance containing the source data
   * @return a {@link CaseLawSearchSchema} instance populated with the mapped fields
   */
  public static CaseLawSearchSchema fromDomain(CaseLawDocumentationUnit doc) {
    String entityURI = ApiConfig.Paths.CASELAW + "/" + doc.documentNumber();
    return CaseLawSearchSchema.builder()
        // JSON-LD-specific fields
        .id(entityURI)
        .inLanguage("de")
        // links to other resource representations
        // equivalent fields
        .documentNumber(doc.documentNumber())
        .ecli(doc.ecli())
        .headline(doc.headline())
        .otherLongText(doc.otherLongText())
        .decisionDate(doc.decisionDate())
        .fileNumbers(doc.fileNumbers())
        .courtType(doc.courtType())
        .location(doc.location())
        .documentType(doc.documentType())
        .outline(doc.outline())
        .judicialBody(doc.judicialBody())
        .decisionName(doc.decisionName())
        .deviatingDocumentNumber(doc.deviatingDocumentNumber())
        // .publicationStatus(doc.publicationStatus())
        // fields with different name
        .courtName(doc.courtKeyword())
        // end
        .build();
  }

  /**
   * Transforms a SearchPage object into a CollectionSchema containing SearchMemberSchema items for
   * CaseLawSearchSchema. This method generates a structured response based on the input search
   * page, including pagination information and search result members.
   *
   * @param <T> The type of the elements in the input SearchPage.
   * @param page The search page containing the results and pagination details to be mapped.
   * @return A CollectionSchema containing SearchMemberSchema items for CaseLawSearchSchema, along
   *     with pagination and metadata information.
   */
  public static <T> CollectionSchema<SearchMemberSchema<CaseLawSearchSchema>> fromSearchPage(
      final SearchPage<T> page) {
    String collectionBasePath = ApiConfig.Paths.CASELAW;
    PartialCollectionViewSchema view =
        PartialCollectionViewMapper.fromPage(collectionBasePath, page);

    String id =
        String.format(
            "%s?pageIndex=%d&size=%d", collectionBasePath, page.getNumber(), page.getSize());

    return CollectionSchema.<SearchMemberSchema<CaseLawSearchSchema>>builder()
        .id(id)
        .totalItems(page.getTotalElements())
        .member(page.stream().map(CaseLawSearchSchemaMapper::fromSearchHit).toList())
        .view(view)
        .build();
  }
}
