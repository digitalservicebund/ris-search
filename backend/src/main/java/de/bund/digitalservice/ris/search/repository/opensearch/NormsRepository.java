package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.service.helper.FetchSourceFilterDefinitions;
import de.bund.digitalservice.ris.search.service.helper.UniversalDocumentQueryBuilder;
import de.bund.digitalservice.ris.search.utils.RisHighlightBuilder;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.opensearch.action.search.SearchType;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.index.query.BoolQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.stereotype.Component;

/** Repository for interacting with the persisted norms in OpenSearchIndex */
@Component
public class NormsRepository {
  private final NormsSynthesizedRepository normsSynthesizedRepository;
  private final ElasticsearchOperations operations;

  /**
   * Constructor to inject the dependencies
   *
   * @param normsSynthesizedRepository The synthesized repository to retrieve data from OpenSearch
   *     via synthesized queries
   */
  @Autowired
  public NormsRepository(
      NormsSynthesizedRepository normsSynthesizedRepository, ElasticsearchOperations operations) {
    this.normsSynthesizedRepository = normsSynthesizedRepository;
    this.operations = operations;
  }

  /**
   * Returns a {@link Norm} by its work-level ELI.
   *
   * @return A {@link Norm}
   */
  public Norm getByExpressionEli(final String expressionEli) {
    return normsSynthesizedRepository.getByExpressionEli(expressionEli);
  }

  /**
   * Saves a list of {@link Norm}.
   *
   * @param norms The list of {@link Norm}.
   */
  public void saveAll(List<Norm> norms) {
    normsSynthesizedRepository.saveAll(norms);
  }

  public SearchHits<Norm> searchAndFilterNorms(
      @Nullable UniversalSearchParams params,
      @Nullable NormsSearchParams normsSearchParams,
      Pageable pageable) {

    // Transform the request parameters into a BoolQuery
    BoolQueryBuilder query =
        new UniversalDocumentQueryBuilder()
            .withUniversalSearchParams(params)
            .withNormsParams(normsSearchParams)
            .getQuery();

    // Add pagination and other parameters
    NativeSearchQuery nativeQuery =
        new NativeSearchQueryBuilder()
            .withSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .withPageable(pageable)
            .withQuery(query)
            .withHighlightBuilder(RisHighlightBuilder.getNormsHighlighter())
            .build();
    // articles are highlighted using the HighlightBuilder added in the inner query

    // Exclude fields with long text from search results
    nativeQuery.addSourceFilter(
        new FetchSourceFilter(
            null, FetchSourceFilterDefinitions.NORMS_FETCH_EXCLUDED_FIELDS.toArray(String[]::new)));

    return operations.search(nativeQuery, Norm.class);
  }
}
