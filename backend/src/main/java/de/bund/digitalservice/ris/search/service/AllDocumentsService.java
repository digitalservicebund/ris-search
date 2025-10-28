package de.bund.digitalservice.ris.search.service;

import static org.opensearch.index.query.QueryBuilders.queryStringQuery;

import de.bund.digitalservice.ris.search.config.opensearch.Configurations;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.LiteratureSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import de.bund.digitalservice.ris.search.utils.RisHighlightBuilder;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opensearch.action.search.SearchType;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

/** Service for searching all documents. */
@Service
public class AllDocumentsService {

  private final ElasticsearchOperations operations;
  private final SimpleSearchQueryBuilder simpleSearchQueryBuilder;
  private final IndexCoordinates allDocumentsIndex;
  private final PageUtils pageUtils;

  public AllDocumentsService(
      ElasticsearchOperations operations,
      Configurations configurations,
      PageUtils pageUtils,
      SimpleSearchQueryBuilder simpleSearchQueryBuilder) {
    this.operations = operations;
    allDocumentsIndex = IndexCoordinates.of(configurations.getDocumentsAliasName());
    this.pageUtils = pageUtils;
    this.simpleSearchQueryBuilder = simpleSearchQueryBuilder;
  }

  /**
   * Search and retrieve a {@link SearchPage} of a {@link AbstractSearchEntity} DTO.
   *
   * @param search The input {@link String} of lucene query values.
   * @param pageable Page (offset) and size parameters.
   * @return A new {@link SearchPage} of the containing {@link AbstractSearchEntity}.
   */
  public SearchPage<AbstractSearchEntity> advancedSearchAllDocuments(
      String search, Pageable pageable) {
    HighlightBuilder highlightBuilder = RisHighlightBuilder.baseHighlighter();
    NormSimpleSearchType.addHighlightedFieldsStatic(highlightBuilder);
    CaseLawSimpleSearchType.addHighlightedFieldsStatic(highlightBuilder);

    var searchQuery =
        new NativeSearchQueryBuilder()
            .withSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .withPageable(pageable)
            .withQuery(queryStringQuery(search))
            .withHighlightBuilder(highlightBuilder)
            .build();

    SearchHits<Document> searchHits =
        operations.search(searchQuery, Document.class, allDocumentsIndex);
    return pageUtils.unwrapMixedSearchHits(
        searchHits, pageable, operations.getElasticsearchConverter());
  }

  /**
   * Search and filter all documents. The query syntax is documented in the <a
   * href="https://neuris-portal-api-docs-production.obs-website.eu-de.otc.t-systems.com/guides/filters/#date-filters">API
   * docs</a>.
   *
   * @param params Search parameters
   * @param normsParams Norms search parameters
   * @param caseLawParams Case law search parameters
   * @param literatureSearchParams Literature search parameters
   * @param documentKind The kind of document to search for.
   * @param pageable Page (offset) and size parameters.
   * @return A new {@link SearchPage} of the containing {@link AbstractSearchEntity}.
   */
  public SearchPage<AbstractSearchEntity> simpleSearchAllDocuments(
      @NotNull UniversalSearchParams params,
      @Nullable NormsSearchParams normsParams,
      @Nullable CaseLawSearchParams caseLawParams,
      @Nullable LiteratureSearchParams literatureSearchParams,
      @Nullable DocumentKind documentKind,
      Pageable pageable) {

    NativeSearchQuery query =
        simpleSearchQueryBuilder.buildQuery(
            List.of(
                new NormSimpleSearchType(normsParams),
                new CaseLawSimpleSearchType(caseLawParams),
                new LiteratureSimpleSearchType(literatureSearchParams)),
            params,
            pageable);

    if (documentKind == null) {
      return searchAllIndices(query, pageable);
    } else {
      return searchSpecificIndex(query, pageable, documentKind);
    }
  }

  private SearchPage<AbstractSearchEntity> searchAllIndices(
      NativeSearchQuery nativeQuery, Pageable pageable) {
    var searchHits = operations.search(nativeQuery, Document.class, allDocumentsIndex);
    return pageUtils.unwrapMixedSearchHits(
        searchHits, pageable, operations.getElasticsearchConverter());
  }

  private SearchPage<AbstractSearchEntity> searchSpecificIndex(
      NativeSearchQuery nativeQuery, Pageable pageable, @NotNull DocumentKind documentKind) {
    var searchHits =
        switch (documentKind) {
          case LEGISLATION -> operations.search(nativeQuery, Norm.class);
          case CASELAW -> operations.search(nativeQuery, CaseLawDocumentationUnit.class);
          case LITERATURE -> operations.search(nativeQuery, Literature.class);
        };
    @SuppressWarnings("unchecked")
    SearchHits<AbstractSearchEntity> castSearchHits = (SearchHits<AbstractSearchEntity>) searchHits;
    return PageUtils.unwrapSearchHits(castSearchHits, pageable);
  }
}
