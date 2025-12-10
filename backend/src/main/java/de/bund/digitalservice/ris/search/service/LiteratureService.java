package de.bund.digitalservice.ris.search.service;

import static org.opensearch.index.query.QueryBuilders.queryStringQuery;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.models.api.parameters.LiteratureSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.LiteratureRepository;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import de.bund.digitalservice.ris.search.utils.RisHighlightBuilder;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opensearch.action.search.SearchType;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

/**
 * Service class for interacting with the database and return the search results. This class is
 * annotated with {@link Service} to indicate that it's a service component in the Spring context.
 */
@Service
public class LiteratureService {
  private final LiteratureRepository literatureRepository;
  private final LiteratureBucket literatureBucket;
  private final ElasticsearchOperations operations;
  private final SimpleSearchQueryBuilder simpleSearchQueryBuilder;

  /**
   * Constructor for LiteratureService.
   *
   * @param literatureRepository Repository for literature data
   * @param literatureBucket Bucket for literature files
   * @param operations Elasticsearch operations for querying
   * @param simpleSearchQueryBuilder Builder for simple search queries
   */
  @SneakyThrows
  @Autowired
  public LiteratureService(
      LiteratureRepository literatureRepository,
      LiteratureBucket literatureBucket,
      ElasticsearchOperations operations,
      SimpleSearchQueryBuilder simpleSearchQueryBuilder) {
    this.literatureRepository = literatureRepository;
    this.literatureBucket = literatureBucket;
    this.operations = operations;
    this.simpleSearchQueryBuilder = simpleSearchQueryBuilder;
  }

  /**
   * Search and filter literature.
   *
   * @param params Search parameters
   * @param literatureParams Literature search parameters
   * @param pageable Page (offset) and size parameters.
   * @return A new {@link SearchPage} of the containing {@link Literature}.
   */
  public SearchPage<Literature> simpleSearchLiterature(
      @NotNull UniversalSearchParams params,
      @Nullable LiteratureSearchParams literatureParams,
      Pageable pageable) {

    NativeSearchQuery query =
        simpleSearchQueryBuilder.buildQuery(
            List.of(new LiteratureSimpleSearchType(literatureParams)), params, pageable);
    SearchHits<Literature> searchHits = operations.search(query, Literature.class);

    return PageUtils.unwrapSearchHits(searchHits, pageable);
  }

  /**
   * Search and retrieve literature items.
   *
   * @param search The input {@link String} of lucene query values.
   * @param pageable Pagination parameters
   * @return A new {@link SearchPage} of the containing {@link Literature}.
   */
  public SearchPage<Literature> searchLiterature(final String search, Pageable pageable) {

    HighlightBuilder highlightBuilder = RisHighlightBuilder.baseHighlighter();
    LiteratureSimpleSearchType.getHighlightedFieldsStatic().forEach(highlightBuilder::field);

    var searchQuery =
        new NativeSearchQueryBuilder()
            .withSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .withPageable(pageable)
            .withQuery(queryStringQuery(search))
            .withHighlightBuilder(highlightBuilder)
            .build();

    SearchHits<Literature> searchHits = operations.search(searchQuery, Literature.class);
    return PageUtils.unwrapSearchHits(searchHits, pageable);
  }

  /**
   * Retrieves a list of literature items filtered by the document number
   *
   * @param documentNumber the given document number
   * @return the list of items
   */
  public List<Literature> getByDocumentNumber(String documentNumber) {
    return literatureRepository.findByDocumentNumber(documentNumber);
  }

  /**
   * Retrives the file of a literature item by its document number
   *
   * @param documentNumber the given document number
   * @return the file
   * @throws ObjectStoreServiceException if an issue is encountered with the bucket
   */
  public Optional<byte[]> getFileByDocumentNumber(String documentNumber)
      throws ObjectStoreServiceException {
    return literatureBucket.get(String.format("%s.akn.xml", documentNumber));
  }

  /**
   * Determines the literaturetype of a literature ldml based on its documentNumber
   *
   * @param documentNumber documentNumber of a literature document
   * @return {@link de.bund.digitalservice.ris.search.service.LiteratureService.LiteratureType}
   */
  public static LiteratureType getLiteratureType(String documentNumber) {
    switch (documentNumber.substring(2, 4)) {
      case "LU" -> {
        return LiteratureType.ULI;
      }
      case "LS" -> {
        return LiteratureType.SLI;
      }
      default -> {
        return LiteratureType.UNKNOWN;
      }
    }
  }

  /** possible LiteratureTypes */
  public enum LiteratureType {
    SLI,
    ULI,
    UNKNOWN
  }
}
