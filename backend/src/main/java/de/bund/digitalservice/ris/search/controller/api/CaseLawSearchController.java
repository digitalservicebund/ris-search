package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.mapper.CaseLawSearchSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.SortParamsConverter;
import de.bund.digitalservice.ris.search.models.CourtSearchResult;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSortParam;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.schema.CaseLawSearchSchema;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.utils.LuceneQueryTools;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling search REST API. This class is annotated with {@link
 * RestController} and {@link RequestMapping} to define the base URL for handling search in the API.
 */
@Tag(
    name = "Case Law",
    description =
        "This group of endpoints provides judgments and decisions of the Federal Constitutional Court, the supreme courts of the Federal Republic of Germany, the Federal Patent Court, and others that were documented by the documentation units of these courts. The documents are anonymized and published in full, and the database is updated daily.")
@RestController
@Profile({"default", "staging", "uat", "test", "prototype"})
public class CaseLawSearchController {
  private final CaseLawService caseLawService;

  @Autowired
  public CaseLawSearchController(CaseLawService caseLawService) {
    this.caseLawService = caseLawService;
  }

  /**
   * Search for documents in the OpenSearch index with filters. For more information on the
   * parameters, refer to the OpenAPI documentation.
   *
   * @param caseLawSearchParams Parameters to search with for case law.
   * @param universalSearchParams Parameters to search with for all document types.
   * @param paginationParams The number of entities and page index to request.
   * @param sortParams Parameters to sort the results.
   * @return The search results
   */
  @GetMapping(path = ApiConfig.Paths.CASELAW, produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "List and search decisions",
      description =
          "The endpoint returns a list of decisions from our database. The list is paginated and can be filtered and sorted.")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  public ResponseEntity<CollectionSchema<SearchMemberSchema<CaseLawSearchSchema>>> searchAndFilter(
      @ParameterObject() CaseLawSearchParams caseLawSearchParams,
      @ParameterObject UniversalSearchParams universalSearchParams,
      @ParameterObject() @Valid PaginationParams paginationParams,
      @ParameterObject @Valid CaseLawSortParam sortParams)
      throws CustomValidationException {

    var pageRequest = PageRequest.of(paginationParams.getPageIndex(), paginationParams.getSize());

    var sortedPageRequest =
        pageRequest.withSort(SortParamsConverter.buildSort(sortParams.getSort()));

    try {
      SearchPage<CaseLawDocumentationUnit> page =
          caseLawService.simpleSearchCaseLaw(
              universalSearchParams, caseLawSearchParams, sortedPageRequest);
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(CaseLawSearchSchemaMapper.fromSearchPage(page));
    } catch (UncategorizedElasticsearchException e) {
      LuceneQueryTools.checkForInvalidQuery(e);
      throw e;
    }
  }

  /**
   * Retrieves a list of courts with their long and short names and the number of associated
   * decisions. The list can be filtered using an optional prefix parameter. Only includes courts
   * whose decisions have been published in the database.
   *
   * @param prefix an optional parameter to filter courts by their name prefix; can be null
   * @return a ResponseEntity containing a list of CourtSearchResult objects
   */
  @GetMapping(
      path = ApiConfig.Paths.CASELAW + "/courts",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "List courts",
      description =
          "Lists courts with long and short name and number of associated decisions. The prefix parameter may be used to filter this list. Only includes courts whose decisions have been published in this database.")
  public ResponseEntity<List<CourtSearchResult>> getCourts(@Nullable String prefix) {
    var result = caseLawService.getCourts(prefix);
    return ResponseEntity.ok(result);
  }
}
