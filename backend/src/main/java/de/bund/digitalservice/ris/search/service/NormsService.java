package de.bund.digitalservice.ris.search.service;

import static org.opensearch.index.query.QueryBuilders.queryStringQuery;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.models.ParsedSearchTerm;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import de.bund.digitalservice.ris.search.service.helper.FetchSourceFilterDefinitions;
import de.bund.digitalservice.ris.search.service.helper.NormQueryBuilder;
import de.bund.digitalservice.ris.search.service.helper.PortalQueryBuilder;
import de.bund.digitalservice.ris.search.service.helper.ZipManager;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import de.bund.digitalservice.ris.search.utils.RisHighlightBuilder;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import de.bund.digitalservice.ris.search.utils.eli.ManifestationEli;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opensearch.action.search.SearchType;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.stereotype.Service;

/**
 * Service class for interacting with the database and return the search results. This class is
 * annotated with {@link Service} to indicate that it's a service component in the Spring context.
 */
@Service
public class NormsService {

  private final NormsRepository normsRepository;
  private final ElasticsearchOperations operations;
  private final SearchTermParser searchTermParser;
  private final NormsBucket normsBucket;

  @Autowired
  public NormsService(
      NormsRepository normsRepository,
      NormsBucket normsBucket,
      ElasticsearchOperations operations,
      SearchTermParser searchTermParser) {
    this.normsRepository = normsRepository;
    this.normsBucket = normsBucket;
    this.operations = operations;
    this.searchTermParser = searchTermParser;
  }

  /**
   * Search and filter norm documents. The query syntax is documented in the <a
   * href="https://neuris-portal-api-docs-production.obs-website.eu-de.otc.t-systems.com/guides/filters/#date-filters">API
   * docs</a>.
   *
   * @param params Search parameters
   * @param normsSearchParams Norms search parameters
   * @param pageable Page (offset) and size parameters.
   * @return A new {@link SearchPage} of the containing {@link Norm}.
   */
  public SearchPage<Norm> searchAndFilterNorms(
      @NotNull UniversalSearchParams params,
      @Nullable NormsSearchParams normsSearchParams,
      Pageable pageable) {

    // Transform the request parameters into a BoolQuery
    ParsedSearchTerm searchTerm = searchTermParser.parse(params.getSearchTerm());
    PortalQueryBuilder builder =
        new PortalQueryBuilder(searchTerm, params.getDateFrom(), params.getDateTo());
    NormQueryBuilder.addNormsLogic(searchTerm, normsSearchParams, builder.getQuery());

    // Add pagination and other parameters
    NativeSearchQuery nativeQuery =
        new NativeSearchQueryBuilder()
            .withSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .withPageable(pageable)
            .withQuery(builder.getQuery())
            .withHighlightBuilder(RisHighlightBuilder.getNormsHighlighter())
            .build();
    // articles are highlighted using the HighlightBuilder added in the inner query

    // Exclude fields with long text from search results
    nativeQuery.addSourceFilter(
        new FetchSourceFilter(
            null, FetchSourceFilterDefinitions.NORMS_FETCH_EXCLUDED_FIELDS.toArray(String[]::new)));

    SearchHits<Norm> searchHits = operations.search(nativeQuery, Norm.class);

    return PageUtils.unwrapSearchHits(searchHits, pageable);
  }

  /**
   * Search and retrieve norms with highlights.
   *
   * @param search The input {@link String} of Lucene query values.
   */
  public SearchPage<Norm> searchNorms(final String search, final Pageable pageable) {
    var searchQuery =
        new NativeSearchQueryBuilder()
            .withSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .withPageable(pageable)
            .withQuery(queryStringQuery(search))
            .withHighlightBuilder(RisHighlightBuilder.getNormsHighlighter())
            .build();

    SearchHits<Norm> searchHits = operations.search(searchQuery, Norm.class);
    return PageUtils.unwrapSearchHits(searchHits, pageable);
  }

  /**
   * Returns a {@link Norm} by its expression-level ELI.
   *
   * @param expressionEli the expression-level ELI of the Norm to return
   */
  public Optional<Norm> getByExpressionEli(final ExpressionEli expressionEli) {
    Norm result = normsRepository.getByExpressionEli(expressionEli.toString());
    return Optional.ofNullable(result);
  }

  public Optional<byte[]> getNormFileByEli(ManifestationEli eli)
      throws ObjectStoreServiceException {
    return normsBucket.get(eli.toString());
  }

  public void writeZipArchive(List<String> keys, OutputStream outputStream) throws IOException {
    ZipManager.writeZipArchive(normsBucket, keys, outputStream);
  }

  public List<String> getAllFilenamesByPath(String prefix) {
    return normsBucket.getAllKeysByPrefix(prefix);
  }
}
