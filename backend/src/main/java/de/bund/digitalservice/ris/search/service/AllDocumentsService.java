package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.config.opensearch.Configurations;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.models.api.parameters.AdministrativeDirectiveSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.LiteratureSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.utils.PageUtils;
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
      @Nullable AdministrativeDirectiveSearchParams administrativeDirectiveSearchParams,
      @Nullable DocumentKind documentKind,
      Pageable pageable) {

    List<SimpleSearchType> searchTypes =
        switch (documentKind) {
          case LEGISLATION -> List.of(new NormSimpleSearchType(normsParams));
          case CASELAW -> List.of(new CaseLawSimpleSearchType(caseLawParams));
          case LITERATURE -> List.of(new LiteratureSimpleSearchType(literatureSearchParams));
          case ADMINISTRATIVE_DIRECTIVE ->
              List.of(
                  new AdministrativeDirectiveSimpleSearchType(administrativeDirectiveSearchParams));
          case null ->
              List.of(
                  new NormSimpleSearchType(normsParams),
                  new CaseLawSimpleSearchType(caseLawParams),
                  new LiteratureSimpleSearchType(literatureSearchParams),
                  new AdministrativeDirectiveSimpleSearchType(administrativeDirectiveSearchParams));
        };

    NativeSearchQuery query = simpleSearchQueryBuilder.buildQuery(searchTypes, params, pageable);

    if (documentKind == null) {
      SearchHits<Document> search = operations.search(query, Document.class, allDocumentsIndex);
      return pageUtils.unwrapMixedSearchHits(search, pageable);
    } else {

      var searchHits =
          switch (documentKind) {
            case LEGISLATION -> operations.search(query, Norm.class);
            case CASELAW -> operations.search(query, CaseLawDocumentationUnit.class);
            case LITERATURE -> operations.search(query, Literature.class);
            case ADMINISTRATIVE_DIRECTIVE ->
                operations.search(query, AdministrativeDirective.class);
          };
      @SuppressWarnings("unchecked")
      SearchHits<AbstractSearchEntity> castSearchHits =
          (SearchHits<AbstractSearchEntity>) searchHits;
      return PageUtils.unwrapSearchHits(castSearchHits, pageable);
    }
  }
}
