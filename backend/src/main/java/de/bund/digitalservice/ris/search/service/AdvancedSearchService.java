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

@Service
public class AdvancedSearchService {

  private final ElasticsearchOperations operations;
  private final PageUtils pageUtils;
  private final IndexCoordinates allDocumentsIndex;

  @Autowired
  public AdvancedSearchService(
      ElasticsearchOperations operations, PageUtils pageUtils, Configurations configurations) {
    this.operations = operations;
    this.pageUtils = pageUtils;
    this.allDocumentsIndex = IndexCoordinates.of(configurations.getDocumentsAliasName());
  }

  public SearchPage<AbstractSearchEntity> searchAll(String search, Pageable pageable) {

    HighlightBuilder highlightBuilder = RisHighlightBuilder.baseHighlighter();

    Set<HighlightBuilder.Field> highlightfields =
        Stream.of(
                CaseLawSimpleSearchType.getHighlightedFieldsStatic(),
                LiteratureSimpleSearchType.getHighlightedFieldsStatic(),
                NormSimpleSearchType.getHighlightedFieldsStatic())
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());

    highlightfields.forEach(highlightBuilder::field);
    var searchResults = callOpenSearch(search, highlightBuilder, null, pageable, Document.class);
    return pageUtils.unwrapMixedSearchHits(searchResults, pageable);
  }

  public SearchPage<CaseLawDocumentationUnit> searchCaseLaw(String search, Pageable pageable) {

    HighlightBuilder highlightBuilder = RisHighlightBuilder.baseHighlighter();

    CaseLawSimpleSearchType.getHighlightedFieldsStatic().forEach(highlightBuilder::field);
    return SearchHitSupport.searchPageFor(
        callOpenSearch(search, highlightBuilder, null, pageable, CaseLawDocumentationUnit.class),
        pageable);
  }

  public SearchPage<Literature> searchLiterature(String search, Pageable pageable) {

    HighlightBuilder highlightBuilder = RisHighlightBuilder.baseHighlighter();
    LiteratureSimpleSearchType.getHighlightedFieldsStatic().forEach(highlightBuilder::field);
    return SearchHitSupport.searchPageFor(
        callOpenSearch(search, highlightBuilder, null, pageable, Literature.class), pageable);
  }

  public SearchPage<AdministrativeDirective> searchAdministrativeDirective(
      String search, Pageable pageable) {

    HighlightBuilder highlightBuilder = RisHighlightBuilder.baseHighlighter();
    AdministrativeDirectiveSimpleSearchType.getHighlightedFieldsStatic()
        .forEach(highlightBuilder::field);
    return SearchHitSupport.searchPageFor(
        callOpenSearch(search, highlightBuilder, null, pageable, AdministrativeDirective.class),
        pageable);
  }

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
