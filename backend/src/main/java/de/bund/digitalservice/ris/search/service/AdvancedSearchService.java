package de.bund.digitalservice.ris.search.service;

import static org.opensearch.index.query.QueryBuilders.matchAllQuery;
import static org.opensearch.index.query.QueryBuilders.queryStringQuery;

import de.bund.digitalservice.ris.search.config.opensearch.Configurations;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import de.bund.digitalservice.ris.search.utils.RisHighlightBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opensearch.action.search.SearchType;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.stereotype.Service;

/**
 * Service class providing advanced search functionalities for multiple entity types using
 * OpenSearch. This service facilitates performing search operations with pagination and
 * highlighting support.
 *
 * <p>The service allows search across various indices, handling different types of entities like
 * documents, case law, literature, and norms, leveraging the ElasticsearchOperations and custom
 * utility classes for configurable and efficient operations.
 */
@Service
public class AdvancedSearchService {

  private final ElasticsearchOperations operations;
  private final PageUtils pageUtils;
  private final IndexCoordinates allDocumentsIndex;

  /**
   * Constructs an instance of AdvancedSearchService which provides search capabilities for multiple
   * entity types using OpenSearch. This service delegates operations to ElasticsearchOperations and
   * utilizes configurations for determining index settings.
   *
   * @param operations the ElasticsearchOperations instance used for search operations.
   * @param pageUtils utility class for handling pagination and search result mapping.
   * @param configurations configuration provider for OpenSearch settings, including indices and
   *     alias names.
   */
  @Autowired
  public AdvancedSearchService(
      ElasticsearchOperations operations, PageUtils pageUtils, Configurations configurations) {
    this.operations = operations;
    this.pageUtils = pageUtils;
    this.allDocumentsIndex = IndexCoordinates.of(configurations.getDocumentsAliasName());
  }

  /**
   * Executes a search query across multiple entity types in an OpenSearch index. The search results
   * are paginated based on the provided Pageable object, and fields in the results may be
   * highlighted based on the configuration.
   *
   * @param search the search query string. If null or empty, a match-all query is executed.
   * @param pageable the pagination information defining the page size and index.
   * @return a paginated list of search results containing a mix of different entity types.
   */
  public SearchPage<AbstractSearchEntity> searchAll(String search, Pageable pageable) {

    HighlightBuilder highlightBuilder = RisHighlightBuilder.baseHighlighter();

    Set<HighlightBuilder.Field> highlightfields =
        Stream.of(
                CaseLawSimpleSearchType.getHighlightedFieldsStatic(),
                LiteratureSimpleSearchType.getHighlightedFieldsStatic(),
                NormSimpleSearchType.getHighlightedFieldsStatic(),
                AdministrativeDirectiveSimpleSearchType.getHighlightedFieldsStatic())
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());

    highlightfields.forEach(highlightBuilder::field);
    var searchResults = callOpenSearch(search, highlightBuilder, null, pageable, Document.class);
    return pageUtils.unwrapMixedSearchHits(searchResults, pageable);
  }

  /**
   * Executes a search query for CaseLawDocumentationUnit objects in an OpenSearch index. The search
   * results are paginated based on the provided Pageable object, and fields in the results may be
   * highlighted based on the configuration.
   *
   * @param search the search query string. If null or empty, a match-all query is executed.
   * @param pageable the pagination information defining the page size and index.
   * @return a paginated list of search results containing CaseLawDocumentationUnit objects.
   */
  public SearchPage<CaseLawDocumentationUnit> searchCaseLaw(String search, Pageable pageable) {

    HighlightBuilder highlightBuilder = RisHighlightBuilder.baseHighlighter();

    CaseLawSimpleSearchType.getHighlightedFieldsStatic().forEach(highlightBuilder::field);
    return SearchHitSupport.searchPageFor(
        callOpenSearch(search, highlightBuilder, null, pageable, CaseLawDocumentationUnit.class),
        pageable);
  }

  /**
   * Executes a search query for Literature objects in an OpenSearch index. The search results are
   * paginated based on the provided Pageable object, and fields in the results may be highlighted
   * based on the configuration.
   *
   * @param search the search query string. If null or empty, a match-all query is executed.
   * @param pageable the pagination information defining the page size and index.
   * @return a paginated list of search results containing Literature objects.
   */
  public SearchPage<Literature> searchLiterature(String search, Pageable pageable) {

    HighlightBuilder highlightBuilder = RisHighlightBuilder.baseHighlighter();
    LiteratureSimpleSearchType.getHighlightedFieldsStatic().forEach(highlightBuilder::field);
    return SearchHitSupport.searchPageFor(
        callOpenSearch(search, highlightBuilder, null, pageable, Literature.class), pageable);
  }

  /**
   * Executes a search query for administrative directive objects in an OpenSearch index. The search
   * results are paginated based on the provided Pageable object, and fields in the results may be
   * highlighted based on the configuration.
   *
   * @param search lucene query string
   * @param pageable {@link Pageable} pageable to sort and paginate request
   * @return {@link SearchPage} searchpage of administrative directives
   */
  public SearchPage<AdministrativeDirective> searchAdministrativeDirective(
      String search, Pageable pageable) {

    HighlightBuilder highlightBuilder = RisHighlightBuilder.baseHighlighter();
    AdministrativeDirectiveSimpleSearchType.getHighlightedFieldsStatic()
        .forEach(highlightBuilder::field);
    return SearchHitSupport.searchPageFor(
        callOpenSearch(search, highlightBuilder, null, pageable, AdministrativeDirective.class),
        pageable);
  }

  /**
   * Executes a search query for Norm objects in an OpenSearch index. The search results are
   * paginated based on the provided Pageable object, and fields in the results may be highlighted
   * based on the configuration.
   *
   * @param search the search query string. If null or empty, a match-all query is executed.
   * @param pageable the pagination information defining the page size and index.
   * @return a paginated list of search results containing Norm objects.
   */
  public SearchPage<Norm> searchNorm(String search, Pageable pageable) {

    HighlightBuilder highlightBuilder = RisHighlightBuilder.baseHighlighter();
    NormSimpleSearchType.getHighlightedFieldsStatic().forEach(highlightBuilder::field);
    return SearchHitSupport.searchPageFor(
        callOpenSearch(
            search,
            highlightBuilder,
            NormSimpleSearchType.NORMS_FETCH_EXCLUDED_FIELDS,
            pageable,
            Norm.class),
        pageable);
  }

  private <T> SearchHits<T> callOpenSearch(
      @Nullable String search,
      @Nullable HighlightBuilder highlightBuilder,
      @Nullable List<String> excludedFields,
      @NotNull Pageable pageable,
      Class<T> type) {

    var searchQuery =
        new NativeSearchQueryBuilder()
            .withSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .withPageable(pageable)
            .withHighlightBuilder(highlightBuilder);

    if (excludedFields != null && !excludedFields.isEmpty()) {
      searchQuery.withSourceFilter(
          new FetchSourceFilter(false, null, excludedFields.toArray(String[]::new)));
    }

    if (StringUtils.isNotBlank(search)) {
      searchQuery.withQuery(queryStringQuery(search));
    } else {
      searchQuery.withQuery(matchAllQuery());
    }

    if (type == Document.class) {
      return operations.search(searchQuery.build(), type, allDocumentsIndex);
    } else {
      return operations.search(searchQuery.build(), type);
    }
  }
}
