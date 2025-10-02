package de.bund.digitalservice.ris.search.controller.api;

import static de.bund.digitalservice.ris.search.utils.LuceneQueryTools.validateLuceneQuery;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.mapper.CaseLawSearchSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.DocumentResponseMapper;
import de.bund.digitalservice.ris.search.mapper.NormSearchResponseMapper;
import de.bund.digitalservice.ris.search.mapper.SortParamsConverter;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSortParam;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSortParam;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSortParam;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.schema.CaseLawSearchSchema;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LegislationWorkSearchSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.service.AllDocumentsService;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.service.NormsService;
import de.bund.digitalservice.ris.search.utils.LuceneQueryTools;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(value = "feature-flags.advanced-search", havingValue = "true")
public class AdvancedSearchController {
  private final AllDocumentsService allDocumentsService;
  private final NormsService normsService;
  private final CaseLawService caseLawService;

  @Autowired
  public AdvancedSearchController(
      AllDocumentsService allDocumentsService,
      NormsService normsService,
      CaseLawService caseLawService) {
    this.allDocumentsService = allDocumentsService;
    this.normsService = normsService;
    this.caseLawService = caseLawService;
  }

  /**
   * Search for documents in the OpenSearch index
   *
   * @param query The query filter based on Lucene query syntax
   * @param pagination Pagination parameters
   * @return The search results
   * @throws CustomValidationException If the query is invalid
   */
  @GetMapping(ApiConfig.Paths.DOCUMENT_ADVANCED_SEARCH)
  @Operation(summary = "Advanced search using Lucene query syntax", tags = "All documents")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  public ResponseEntity<CollectionSchema<SearchMemberSchema>> search(
      @Parameter(name = "query", description = "The query filter based on Lucene query")
          @RequestParam
          String query,
      @ParameterObject @Valid PaginationParams pagination,
      @ParameterObject @Valid UniversalSortParam sortParams)
      throws CustomValidationException {

    String decodedQuery = validateLuceneQuery(query);

    PageRequest pageable = PageRequest.of(pagination.getPageIndex(), pagination.getSize());
    PageRequest sortedPageable =
        pageable.withSort(SortParamsConverter.buildSort(sortParams.getSort()));

    try {
      SearchPage<AbstractSearchEntity> resultPage =
          allDocumentsService.advancedSearchAllDocuments(decodedQuery, sortedPageable);

      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(
              DocumentResponseMapper.fromDomain(
                  resultPage, ApiConfig.Paths.DOCUMENT_ADVANCED_SEARCH));
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
   * @return A JsonLd string of {@link CollectionSchema<SearchMemberSchema>} containing the
   *     retrieved search results. Returns HTTP 200 (OK) and the search results data if found.
   *     Returns HTTP 422 (Unprocessable Entity) if any request data information is wrong.
   */
  @GetMapping(ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH)
  @Operation(summary = "Advanced search using Lucene query syntax", tags = "Legislation")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  public ResponseEntity<CollectionSchema<SearchMemberSchema<LegislationWorkSearchSchema>>>
      searchLegislation(
          @Parameter(name = "query", description = "The query filter based on Lucene query")
              @RequestParam
              String query,
          @ParameterObject @Valid PaginationParams pagination,
          @ParameterObject @Valid NormsSortParam sortParams)
          throws CustomValidationException {

    String decodedQuery = validateLuceneQuery(query);

    PageRequest pageable = PageRequest.of(pagination.getPageIndex(), pagination.getSize());
    PageRequest sortedPageable =
        pageable.withSort(SortParamsConverter.buildSort(sortParams.getSort()));

    try {
      SearchPage<Norm> page = normsService.advancedSearchNorms(decodedQuery, sortedPageable);
      return ResponseEntity.ok(
          NormSearchResponseMapper.fromDomain(page, ApiConfig.Paths.LEGISLATION_ADVANCED_SEARCH));
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
   * @return A JsonLd String of {@link CollectionSchema<SearchMemberSchema>} containing the
   *     retrieved search results. Returns HTTP 200 (OK) and the search results data if found.
   *     Returns HTTP 422 (Unprocessable Entity) if any request data information is wrong.
   */
  @GetMapping(ApiConfig.Paths.CASELAW_ADVANCED_SEARCH)
  @Operation(summary = "Advanced search using Lucene query syntax", tags = "Case Law")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  public ResponseEntity<CollectionSchema<SearchMemberSchema<CaseLawSearchSchema>>> caseLawSearch(
      @Parameter(name = "query", description = "The query filter based on Lucene query syntax")
          @RequestParam
          String query,
      @ParameterObject @Valid PaginationParams pagination,
      @ParameterObject @Valid CaseLawSortParam sortParams)
      throws CustomValidationException {

    String decodedQuery = validateLuceneQuery(query);

    PageRequest pageable = PageRequest.of(pagination.getPageIndex(), pagination.getSize());
    PageRequest sortedPageable =
        pageable.withSort(SortParamsConverter.buildSort(sortParams.getSort()));

    try {
      SearchPage<CaseLawDocumentationUnit> page =
          caseLawService.advancedSearchCaseLaw(decodedQuery, sortedPageable);

      return ResponseEntity.ok(CaseLawSearchSchemaMapper.fromSearchPage(page));
    } catch (UncategorizedElasticsearchException e) {
      LuceneQueryTools.checkForInvalidQuery(e);
      throw e;
    }
  }
}
