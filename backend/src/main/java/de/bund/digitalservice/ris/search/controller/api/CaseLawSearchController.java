package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.mapper.CaseLawSearchSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.MappingDefinitions;
import de.bund.digitalservice.ris.search.models.CourtSearchResult;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.schema.CaseLawSearchSchema;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.service.helper.PaginationParamsConverter;
import de.bund.digitalservice.ris.search.utils.LuceneQueryTools;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
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
      @ParameterObject() @Valid PaginationParams paginationParams)
      throws CustomValidationException {

    boolean defaultToUnsorted = StringUtils.isNotBlank(universalSearchParams.getSearchTerm());
    Pageable pageable =
        PaginationParamsConverter.convert(
            paginationParams, MappingDefinitions.ResolutionMode.CASE_LAW, defaultToUnsorted);

    try {
      SearchPage<CaseLawDocumentationUnit> page =
          caseLawService.searchAndFilterCaseLaw(
              universalSearchParams, caseLawSearchParams, pageable);
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(CaseLawSearchSchemaMapper.fromSearchPage(page));
    } catch (UncategorizedElasticsearchException e) {
      LuceneQueryTools.checkForInvalidQuery(e);
      throw e;
    }
  }

  @GetMapping(
      path = ApiConfig.Paths.CASELAW + "/courts",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "List courts whose decisions are published in this database",
      description =
          "Lists courts with long and short name and number of associated decisions. The prefix parameter may be used to filter this list.")
  public ResponseEntity<List<CourtSearchResult>> getCourts(@Nullable String prefix) {
    var result = caseLawService.getCourts(prefix);
    return ResponseEntity.ok(result);
  }
}
