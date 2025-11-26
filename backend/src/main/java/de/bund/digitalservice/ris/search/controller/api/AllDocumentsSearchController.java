package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.mapper.DocumentResponseMapper;
import de.bund.digitalservice.ris.search.mapper.SortParamsConverter;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.LiteratureSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSortParam;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.schema.AbstractDocumentSchema;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.service.AllDocumentsService;
import de.bund.digitalservice.ris.search.utils.LuceneQueryTools;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Controller for global search */
@Tag(
    name = "All documents",
    description = "Use this endpoint to search across all document types in our database.")
@RestController
@RequestMapping(ApiConfig.Paths.DOCUMENT)
public class AllDocumentsSearchController {

  private final AllDocumentsService allDocumentsService;

  public AllDocumentsSearchController(AllDocumentsService allDocumentsService) {
    this.allDocumentsService = allDocumentsService;
  }

  /**
   * Performs a global search and filtering operation on documents of various kinds (case law,
   * legislation, and literature). This endpoint supports paginated, filtered, and sorted search
   * results, allowing clients to query specific document types and apply sorting and pagination
   * parameters.
   *
   * <p>Default sorting is by relevance, but it can be customized to sort by date. The endpoint
   * supports multiple search parameters depending on the document kind.
   *
   * @param request Universal search parameters that apply to all document kinds.
   * @param normsSearchParams Specific search parameters related to legislation documents.
   * @param caseLawSearchParams Specific search parameters related to case law documents.
   * @param literatureSearchParams Specific search parameters related to literature documents.
   * @param sortParams Sorting parameters for ordering the search results.
   * @param documentKind Optional filter indicating the kind of document to search for. Supported
   *     values: R (case law), N (legislation), L (literature).
   * @param paginationParams Pagination parameters such as page index and size.
   * @return A ResponseEntity containing a paginated collection of search results.
   * @throws CustomValidationException if there are validation errors with the provided input
   *     parameters.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Global search / list",
      description =
          """
          This endpoint can be used to search for documents across different document kinds. Currently we support case law, legislation and literature document kinds. The endpoint provides a paginated response with up to 10,000 results with at most 100 results per page.

          The searchTerm parameter searches across multiple fields of a document at the same time. The fields searched depend on the document kind. See the filters guide for more information.

          Default sorting is by relevance from most relevant to least relevant. Multiple factors are combined to boost the most relevant documents to the top of the result list. Additionally, sorting by date is possible by setting the sort query parameter to date.
          """)
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  public ResponseEntity<CollectionSchema<SearchMemberSchema<AbstractDocumentSchema>>>
      searchAndFilter(
          @ParameterObject UniversalSearchParams request,
          @ParameterObject NormsSearchParams normsSearchParams,
          @ParameterObject CaseLawSearchParams caseLawSearchParams,
          @ParameterObject LiteratureSearchParams literatureSearchParams,
          @ParameterObject @Valid UniversalSortParam sortParams,
          @RequestParam("documentKind")
              @Schema(
                  description =
                      "Filter by document kind. Specify R for case law (<u>R</u>echtsprechung), N for legislation (<u>N</u>ormen) or L for literature (<u>L</u>iteratur).")
              Optional<DocumentKind> documentKind,
          @ParameterObject @Valid PaginationParams paginationParams)
          throws CustomValidationException {
    normsSearchParams.validate();

    var pageRequest = PageRequest.of(paginationParams.getPageIndex(), paginationParams.getSize());

    var sortedPageRequest =
        pageRequest.withSort(SortParamsConverter.buildSort(sortParams.getSort()));

    try {
      SearchPage<AbstractSearchEntity> searchResult =
          allDocumentsService.simpleSearchAllDocuments(
              request,
              normsSearchParams,
              caseLawSearchParams,
              literatureSearchParams,
              documentKind.orElse(null),
              sortedPageRequest);

      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body((DocumentResponseMapper.fromDomain(searchResult, ApiConfig.Paths.DOCUMENT)));
    } catch (UncategorizedElasticsearchException e) {
      LuceneQueryTools.checkForInvalidQuery(e);
      throw e;
    }
  }
}
