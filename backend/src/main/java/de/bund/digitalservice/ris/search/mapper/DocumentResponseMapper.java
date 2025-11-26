package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.schema.AbstractDocumentSchema;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.PartialCollectionViewSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.schema.TextMatchSchema;
import java.util.List;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;

/**
 * Utility class for mapping search hits and search pages into structured schemas that represent
 * search results. The class provides methods to convert individual search hits into specific
 * document schema representations with associated text matches, and to convert paginated search
 * results into a comprehensive collection schema.
 *
 * <p>This class serves as a mapper to transform domain-specific search entities into a format
 * suitable for API responses or data interchange.
 *
 * <p>Note: This class cannot be instantiated as it only provides static utility methods.
 */
public class DocumentResponseMapper {
  private DocumentResponseMapper() {}

  /**
   * Converts a given search hit into a SearchMemberSchema object containing the document schema and
   * associated text matches. This method dynamically adapts to the content type of the search hit
   * and maps it to the corresponding document schema and text matches.
   *
   * @param <T> The type of the content within the provided search hit, representing the domain
   *     model.
   * @param searchHit The search hit containing the content and metadata that needs to be converted
   *     into the corresponding SearchMemberSchema.
   * @return A SearchMemberSchema object containing the mapped document schema and associated text
   *     matches, based on the content type of the provided search hit.
   * @throws IllegalArgumentException If the content type of the given search hit is unknown or
   *     unsupported.
   */
  public static <T> SearchMemberSchema<AbstractDocumentSchema> convertSingle(
      SearchHit<T> searchHit) {
    List<TextMatchSchema> textMatches;
    AbstractDocumentSchema document;
    if (searchHit.getContent() instanceof CaseLawDocumentationUnit cldu) {
      textMatches = CaseLawSearchSchemaMapper.getTextMatches(searchHit);
      document = CaseLawSearchSchemaMapper.fromDomain(cldu);
    } else if (searchHit.getContent() instanceof Literature literature) {
      textMatches = LiteratureSearchSchemaMapper.getTextMatches(searchHit);
      document = LiteratureSearchSchemaMapper.fromDomain(literature);
    } else if (searchHit.getContent() instanceof Norm norm) {
      textMatches = NormSearchResponseMapper.getTextMatches(searchHit);
      document = NormSearchResponseMapper.fromDomain(norm);
    } else {
      throw new IllegalArgumentException(
          "Unknown entity type: " + searchHit.getContent().getClass().getSimpleName());
    }

    return SearchMemberSchema.builder().item(document).textMatches(textMatches).build();
  }

  /**
   * Converts a given search page into a collection schema for search member schemas, wrapping the
   * search results, pagination metadata, and related information in a structured format.
   *
   * @param <T> The type of the content in the provided search page, representing the domain model.
   * @param page The search page containing the current subset of results, total elements, and
   *     pagination details.
   * @param path The base path used to generate the identifier and links for pagination in the
   *     resulting collection schema.
   * @return A CollectionSchema containing a list of search member schemas, the total number of
   *     elements, and the pagination metadata for the represented collection.
   */
  public static <T> CollectionSchema<SearchMemberSchema<AbstractDocumentSchema>> fromDomain(
      final SearchPage<T> page, String path) {
    String id = String.format("%s?pageIndex=%d&size=%d", path, page.getNumber(), page.getSize());
    PartialCollectionViewSchema view = PartialCollectionViewMapper.fromPage(path, page);

    return CollectionSchema.<SearchMemberSchema<AbstractDocumentSchema>>builder()
        .id(id)
        .totalItems(page.getTotalElements())
        .member(page.stream().map(DocumentResponseMapper::convertSingle).toList())
        .view(view)
        .build();
  }
}
