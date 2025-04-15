package de.bund.digitalservice.ris.search.service;

import static org.opensearch.index.query.QueryBuilders.queryStringQuery;

import de.bund.digitalservice.ris.search.config.opensearch.Configurations;
import de.bund.digitalservice.ris.search.models.CourtSearchResult;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.service.helper.CourtNameAbbreviationExpander;
import de.bund.digitalservice.ris.search.service.helper.FetchSourceFilterDefinitions;
import de.bund.digitalservice.ris.search.service.helper.UniversalDocumentQueryBuilder;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import de.bund.digitalservice.ris.search.utils.RisHighlightBuilder;
import java.util.List;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.opensearch.action.search.SearchType;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.data.client.orhlc.OpenSearchAggregations;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.opensearch.search.aggregations.Aggregations;
import org.opensearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.opensearch.search.aggregations.bucket.terms.Terms;
import org.opensearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.stereotype.Service;

/**
 * Service class for interacting with the database and return the search results. This class is
 * annotated with {@link Service} to indicate that it's a service component in the Spring context.
 */
@Service
public class CaseLawService {
  private final CaseLawRepository caseLawRepository;
  private final ElasticsearchOperations operations;
  private final CourtNameAbbreviationExpander courtNameAbbreviationExpander;
  private final Configurations configurations;

  @SneakyThrows
  @Autowired
  public CaseLawService(
      CaseLawRepository caseLawRepository,
      ElasticsearchOperations operations,
      Configurations configurations) {
    this.caseLawRepository = caseLawRepository;
    this.operations = operations;
    this.configurations = configurations;
    this.courtNameAbbreviationExpander = new CourtNameAbbreviationExpander();
  }

  /**
   * Search and filter case law documents. The query syntax is documented in the <a
   * href="https://neuris-portal-api-docs-production.obs-website.eu-de.otc.t-systems.com/guides/filters/#date-filters">API
   * docs</a>.
   *
   * @param params Search parameters
   * @param caseLawParams Case law search parameters
   * @param pageable Page (offset) and size parameters.
   * @return A new {@link SearchPage} of the containing {@link CaseLawDocumentationUnit}.
   */
  public SearchPage<CaseLawDocumentationUnit> searchAndFilterCaseLaw(
      @Nullable UniversalSearchParams params,
      @Nullable CaseLawSearchParams caseLawParams,
      Pageable pageable) {

    // transform the request parameters into a BoolQuery
    BoolQueryBuilder query =
        new UniversalDocumentQueryBuilder()
            .withUniversalSearchParams(params)
            .withCaseLawSearchParams(caseLawParams)
            .getQuery();

    // add pagination and other parameters
    NativeSearchQuery nativeQuery =
        new NativeSearchQueryBuilder()
            .withSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .withPageable(pageable)
            .withQuery(query)
            .withHighlightBuilder(RisHighlightBuilder.getCaseLawHighlighter())
            .build();

    // Exclude fields with long text from search results
    nativeQuery.addSourceFilter(
        new FetchSourceFilter(
            null,
            FetchSourceFilterDefinitions.CASE_LAW_FETCH_EXCLUDED_FIELDS.toArray(String[]::new)));

    SearchHits<CaseLawDocumentationUnit> searchHits =
        operations.search(nativeQuery, CaseLawDocumentationUnit.class);

    return PageUtils.unwrapSearchHits(searchHits, pageable);
  }

  /**
   * Search and retrieve items.
   *
   * @param search The input {@link String} of lucene query values.
   * @param pageable Pagination parameters
   * @return A new {@link SearchPage} of the containing {@link CaseLawDocumentationUnit}.
   */
  public SearchPage<CaseLawDocumentationUnit> searchCaseLaws(
      final String search, Pageable pageable) {
    var searchQuery =
        new NativeSearchQueryBuilder()
            .withSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .withPageable(pageable)
            .withQuery(queryStringQuery(search))
            .withHighlightBuilder(RisHighlightBuilder.getCaseLawHighlighter())
            .build();

    SearchHits<CaseLawDocumentationUnit> searchHits =
        operations.search(searchQuery, CaseLawDocumentationUnit.class);
    return PageUtils.unwrapSearchHits(searchHits, pageable);
  }

  public List<CourtSearchResult> getCourts(String searchPrefix) {

    var filterQuery =
        searchPrefix == null
            ? null
            : new MatchPhrasePrefixQueryBuilder("court_keyword", searchPrefix.toLowerCase());
    final String aggregationName = "t";
    var termsAggregation =
        new TermsAggregationBuilder(aggregationName).field("court_keyword.keyword");

    var mainQuery =
        new NativeSearchQueryBuilder()
            .withQuery(filterQuery)
            .withAggregations(termsAggregation)
            .withMaxResults(
                0); // we only care about the aggregation buckets, not the underlying query data

    SearchHits<Void> searchHits =
        operations.search(
            mainQuery.build(),
            Void.class,
            IndexCoordinates.of(configurations.getCaseLawsIndexName()));

    var buckets = getBuckets(searchHits, aggregationName);
    var firstToken = CourtNameAbbreviationExpander.extractFirstToken(searchPrefix);
    return buckets.stream()
        .map(
            item -> {
              String key = item.getKeyAsString();
              long count = item.getDocCount();
              String label =
                  courtNameAbbreviationExpander.getLabelExpandingSynonyms(key, firstToken);
              return new CourtSearchResult(key, count, label);
            })
        .toList();
  }

  private static List<? extends Terms.Bucket> getBuckets(
      SearchHits<Void> searchHits, String aggregationName) {
    OpenSearchAggregations aggregationsWrapper =
        (OpenSearchAggregations) searchHits.getAggregations();
    assert aggregationsWrapper != null;
    Aggregations aggregations = aggregationsWrapper.aggregations();
    ParsedStringTerms counts = (ParsedStringTerms) aggregations.getAsMap().get(aggregationName);
    assert counts != null;
    return counts.getBuckets();
  }

  public List<CaseLawDocumentationUnit> getByDocumentNumber(String documentNumber) {
    return caseLawRepository.getByDocumentNumber(documentNumber);
  }
}
