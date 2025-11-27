package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.mapper.AdministrativeDirectiveSearchSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.CaseLawSearchSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.DocumentResponseMapper;
import de.bund.digitalservice.ris.search.mapper.LiteratureSearchSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.NormSearchResponseMapper;
import de.bund.digitalservice.ris.search.mapper.SortParamsConverter;
import de.bund.digitalservice.ris.search.models.api.parameters.AdministrativeDirectiveSortParam;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSortParam;
import de.bund.digitalservice.ris.search.models.api.parameters.LiteratureSortParam;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSortParam;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSortParam;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.schema.AbstractDocumentSchema;
import de.bund.digitalservice.ris.search.schema.AdministrativeDirectiveSearchSchema;
import de.bund.digitalservice.ris.search.schema.CaseLawSearchSchema;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LegislationWorkSearchSchema;
import de.bund.digitalservice.ris.search.schema.LiteratureSearchSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.service.AdvancedSearchService;
import de.bund.digitalservice.ris.search.utils.LuceneQueryTools;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller providing advanced search functionalities for multiple document types.
 *
 * <p>It includes endpoints for searching all documents, legislation, and case law using Lucene
 * query syntax. The controller utilizes services to execute the searches and handle pagination and
 * sorting.
 */
@RestController
public class AdvancedSearchController {
  private final AdvancedSearchService advancedSearchService;

  /**
   * Constructs an instance of AdvancedSearchController with the provided services.
   *
   * @param advancedSearchService a service to advanced search
   */
  @Autowired
  public AdvancedSearchController(AdvancedSearchService advancedSearchService) {
    this.advancedSearchService = advancedSearchService;
  }

  /**
   * Search for documents in the OpenSearch index
   *
   * @param query The query filter based on Lucene query syntax
   * @param pagination Pagination parameters
   * @param sortParams Sorting parameters
   * @return The search results
   * @throws CustomValidationException If the query is invalid
   */
  @GetMapping(ApiConfig.Paths.DOCUMENT_ADVANCED_SEARCH)
  @Operation(summary = "Advanced search using Lucene query syntax", tags = "All documents")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  public ResponseEntity<CollectionSchema<SearchMemberSchema<AbstractDocumentSchema>>> search(
      @Parameter(name = "query", description = "The query filter based on Lucene query")
          @RequestParam
          @Nullable
          String query,
      @ParameterObject @Valid PaginationParams pagination,
      @ParameterObject @Valid UniversalSortParam sortParams)
      throws CustomValidationException {

    LuceneQueryTools.validateLuceneQuery(query);
    PageRequest pageable = PageRequest.of(pagination.getPageIndex(), pagination.getSize());
    PageRequest sortedPageable =
        pageable.withSort(SortParamsConverter.buildSort(sortParams.getSort()));

    try {
      SearchPage<AbstractSearchEntity> page =
          advancedSearchService.searchAll(query, sortedPageable);
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(DocumentResponseMapper.fromDomain(page, ApiConfig.Paths.DOCUMENT_ADVANCED_SEARCH));
    } catch (UncategorizedElasticsearchException e) {
      LuceneQueryTools.checkForInvalidQuery(e);
      throw e;
    }
  }

  /**
   * Legislation Search Endpoint retrieves the JSON representation of a search query. The search
   * query should follow Lucene syntax. Unlike the caselaw search method, this endpoint does not
   * support custom sorting.
   *
   * @param query The query filter based on Lucene query
   * @param pagination Pagination parameters
   * @param sortParams Sorting parameters
   * @return A JsonLd string containing the retrieved search results. Returns HTTP 200 (OK) and the
   *     search results data if found. Returns HTTP 422 (Unprocessable Entity) if any request data
   *     information is wrong.
   */
  @GetMapping(ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH)
  @Operation(summary = "Advanced search using Lucene query syntax", tags = "Legislation")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  public ResponseEntity<CollectionSchema<SearchMemberSchema<LegislationWorkSearchSchema>>>
      searchLegislation(
          @Parameter(name = "query", description = "The query filter based on Lucene query")
              @RequestParam
              @Nullable
              String query,
          @ParameterObject @Valid PaginationParams pagination,
          @ParameterObject @Valid NormsSortParam sortParams)
          throws CustomValidationException {

    LuceneQueryTools.validateLuceneQuery(query);
    PageRequest pageable = PageRequest.of(pagination.getPageIndex(), pagination.getSize());
    PageRequest sortedPageable =
        pageable.withSort(SortParamsConverter.buildSort(sortParams.getSort()));

    try {
      SearchPage<Norm> page = advancedSearchService.searchNorm(query, sortedPageable);
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(
              NormSearchResponseMapper.fromDomain(
                  page, ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH));
    } catch (UncategorizedElasticsearchException e) {
      LuceneQueryTools.checkForInvalidQuery(e);
      throw e;
    }
  }

  /**
   * Case Law Search Endpoint retrieves the JSON representation of a search query. The search query
   * as Lucene query parameter.
   *
   * @param query The query filter based on Lucene query
   * @param pagination Pagination parameters
   * @param sortParams Sorting parameters
   * @return A JsonLd String containing the retrieved search results. Returns HTTP 200 (OK) and the
   *     search results data if found. Returns HTTP 422 (Unprocessable Entity) if any request data
   *     information is wrong.
   */
  @GetMapping(ApiConfig.Paths.CASELAW_ADVANCED_SEARCH)
  @Operation(summary = "Advanced search using Lucene query syntax", tags = "Case Law")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  public ResponseEntity<CollectionSchema<SearchMemberSchema<CaseLawSearchSchema>>> caseLawSearch(
      @Parameter(name = "query", description = "The query filter based on Lucene query syntax")
          @RequestParam
          @Nullable
          String query,
      @ParameterObject @Valid PaginationParams pagination,
      @ParameterObject @Valid CaseLawSortParam sortParams)
      throws CustomValidationException {

    LuceneQueryTools.validateLuceneQuery(query);
    PageRequest pageable = PageRequest.of(pagination.getPageIndex(), pagination.getSize());
    PageRequest sortedPageable =
        pageable.withSort(SortParamsConverter.buildSort(sortParams.getSort()));

    try {
      SearchPage<CaseLawDocumentationUnit> page =
          advancedSearchService.searchCaseLaw(query, sortedPageable);
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(CaseLawSearchSchemaMapper.fromSearchPage(page));
    } catch (UncategorizedElasticsearchException e) {
      LuceneQueryTools.checkForInvalidQuery(e);
      throw e;
    }
  }

  /**
   * Literature Search Endpoint retrieves the JSON representation of a search query. The search
   * query as Lucene query parameter.
   *
   * @param query The query filter based on Lucene query
   * @param pagination Pagination parameters
   * @param sortParams Sorting parameters
   * @return A JsonLd String of {@link CollectionSchema} of {@code SearchMemberSchema} containing
   *     the retrieved search results. Returns HTTP 200 (OK) and the search results data if found.
   *     Returns HTTP 422 (Unprocessable Entity) if any request data information is wrong.
   */
  @GetMapping(ApiConfig.Paths.LITERATURE_ADVANCED_SEARCH)
  @Operation(summary = "Advanced search using Lucene query syntax", tags = "Literature")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  public ResponseEntity<CollectionSchema<SearchMemberSchema<LiteratureSearchSchema>>>
      literatureSearch(
          @Parameter(name = "query", description = "The query filter based on Lucene query syntax")
              @RequestParam
              @Nullable
              String query,
          @ParameterObject @Valid PaginationParams pagination,
          @ParameterObject @Valid LiteratureSortParam sortParams)
          throws CustomValidationException {

    LuceneQueryTools.validateLuceneQuery(query);
    PageRequest pageable = PageRequest.of(pagination.getPageIndex(), pagination.getSize());
    PageRequest sortedPageable =
        pageable.withSort(SortParamsConverter.buildSort(sortParams.getSort()));

    try {
      SearchPage<Literature> page = advancedSearchService.searchLiterature(query, sortedPageable);
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(LiteratureSearchSchemaMapper.fromSearchPage(page));
    } catch (UncategorizedElasticsearchException e) {
      LuceneQueryTools.checkForInvalidQuery(e);
      throw e;
    }
  }

  /**
   * Administrative Directive Search Endpoint retrieves the JSON representation of a search query.
   * The search query as Lucene query parameter.
   *
   * @param query The query filter based on Lucene query
   * @param pagination Pagination parameters
   * @return A JsonLd String of {@link CollectionSchema<SearchMemberSchema>} containing the
   *     retrieved search results. Returns HTTP 200 (OK) and the search results data if found.
   *     Returns HTTP 422 (Unprocessable Entity) if any request data information is wrong.
   */
  @GetMapping(ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE_ADVANCED_SEARCH)
  @Operation(
      summary = "Advanced search using Lucene query syntax",
      tags = "AdministrativeDirective")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  public ResponseEntity<CollectionSchema<SearchMemberSchema<AdministrativeDirectiveSearchSchema>>>
      literatureSearch(
          @Parameter(name = "query", description = "The query filter based on Lucene query syntax")
              @RequestParam
              @Nullable
              String query,
          @ParameterObject @Valid PaginationParams pagination,
          @ParameterObject @Valid AdministrativeDirectiveSortParam sortParams)
          throws CustomValidationException {

    LuceneQueryTools.validateLuceneQuery(query);
    PageRequest pageable = PageRequest.of(pagination.getPageIndex(), pagination.getSize());
    PageRequest sortedPageable =
        pageable.withSort(SortParamsConverter.buildSort(sortParams.getSort()));

    try {
      SearchPage<AdministrativeDirective> page =
          advancedSearchService.searchAdministrativeDirective(query, sortedPageable);
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(AdministrativeDirectiveSearchSchemaMapper.fromSearchPage(page));
    } catch (UncategorizedElasticsearchException e) {
      LuceneQueryTools.checkForInvalidQuery(e);
      throw e;
    }
  }
}
