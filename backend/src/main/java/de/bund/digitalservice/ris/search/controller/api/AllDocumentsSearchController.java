package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.mapper.DocumentResponseMapper;
import de.bund.digitalservice.ris.search.mapper.MappingDefinitions;
import de.bund.digitalservice.ris.search.mapper.SortParamsConverter;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSortParam;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
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
import org.apache.commons.lang3.StringUtils;
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

  /** Search for documents in the OpenSearch index with filters */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Global search / list",
      description =
          """
          This endpoint can be used to search for documents across different document kinds. Currently we support case law and legislation document kinds. The endpoint provides a paginated response with up to 10,000 results with at most 100 results per page.

          The searchTerm parameter searches across multiple fields of a document at the same time. The fields searched depend on the document kind. See the filters guide for more information.

          Default sorting is by relevance from most relevant to least relevant. Multiple factors are combined to boost the most relevant documents to the top of the result list. Additionally, sorting by date is possible by setting the sort query parameter to date.
          """)
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  public ResponseEntity<CollectionSchema<SearchMemberSchema>> searchAndFilter(
      @ParameterObject UniversalSearchParams request,
      @ParameterObject NormsSearchParams normsSearchParams,
      @ParameterObject CaseLawSearchParams caseLawSearchParams,
      @ParameterObject UniversalSortParam sortParams,
      @RequestParam("documentKind")
          @Schema(
              description =
                  "Filter by document kind. Specify R for case law (<u>R</u>echtsprechung), or N for legislation (<u>N</u>ormen).")
          Optional<DocumentKind> documentKind,
      @ParameterObject @Valid PaginationParams paginationParams)
      throws CustomValidationException {
    normsSearchParams.validate();

    boolean defaultToUnsorted = StringUtils.isNotBlank(request.getSearchTerm());
    var pageRequest = PageRequest.of(paginationParams.getPageIndex(), paginationParams.getSize());

    var sortedPageRequest =
        pageRequest.withSort(
            SortParamsConverter.buildSort(
                sortParams.getSort(), MappingDefinitions.ResolutionMode.ALL, defaultToUnsorted));

    try {
      SearchPage<AbstractSearchEntity> entitiesPage =
          allDocumentsService.searchAndFilterAllDocuments(
              request,
              normsSearchParams,
              caseLawSearchParams,
              documentKind.orElse(null),
              sortedPageRequest);

      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body((DocumentResponseMapper.fromDomain(entitiesPage, ApiConfig.Paths.DOCUMENT)));
    } catch (UncategorizedElasticsearchException e) {
      LuceneQueryTools.checkForInvalidQuery(e);
      throw e;
    }
  }
}
