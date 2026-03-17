package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.config.opensearch.Configurations;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import java.time.LocalDate;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
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

  /** Constructor for AllDocumentsService. */
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
   * Search and filter all documents. The query syntax is documented in the <a
   * href="https://neuris-portal-api-docs-production.obs-website.eu-de.otc.t-systems.com/guides/filters/#date-filters">API
   * docs</a>.
   *
   * @param params Search parameters
   * @param pageable Page (offset) and size parameters.
   * @param mostRelevantOn Only applies to norms and determines which of multiple matching
   *     expressions is returned.
   * @return A new {@link SearchPage} of the containing {@link AbstractSearchEntity}.
   */
  public SearchPage<AbstractSearchEntity> simpleSearchAllDocuments(
      @NotNull UniversalSearchParams params,
      Pageable pageable,
      @Nullable LocalDate mostRelevantOn) {

    NormsSearchParams normsParams = new NormsSearchParams();
    normsParams.setMostRelevantOn(mostRelevantOn);

    List<SimpleSearchType> searchTypes =
        List.of(
            new NormSimpleSearchType(normsParams),
            new CaseLawSimpleSearchType(null),
            new LiteratureSimpleSearchType(null),
            new AdministrativeDirectiveSimpleSearchType(null));

    NativeSearchQuery query = simpleSearchQueryBuilder.buildQuery(searchTypes, params, pageable);

    SearchHits<Document> search = operations.search(query, Document.class, allDocumentsIndex);
    return pageUtils.unwrapMixedSearchHits(search, pageable);
  }
}
