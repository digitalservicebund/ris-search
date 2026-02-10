package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.mapper.AdministrativeDirectiveSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.AdministrativeDirectiveSearchSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.SortParamsConverter;
import de.bund.digitalservice.ris.search.models.api.parameters.AdministrativeDirectiveSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.AdministrativeDirectiveSortParam;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.schema.AdministrativeDirectiveSchema;
import de.bund.digitalservice.ris.search.schema.AdministrativeDirectiveSearchSchema;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.service.AdministrativeDirectiveService;
import de.bund.digitalservice.ris.search.service.xslt.AdministrativeDirectiveXsltTransformerService;
import de.bund.digitalservice.ris.search.utils.LuceneQueryTools;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing administrative directives. It provides endpoints to retrieve, search,
 * filter, and access administrative directive information in various formats such as JSON and XML.
 */
@Tag(name = "AdministrativeDirective")
@RestController
@Profile({"default", "staging", "uat", "test", "prototype"})
public class AdministrativeDirectiveController {

  private final AdministrativeDirectiveService service;
  private final AdministrativeDirectiveXsltTransformerService transformerService;

  /**
   * Constructor for the AdministrativeDirectiveController, used to initialize the controller with
   * the required services for handling administrative directives and transformations.
   *
   * @param service the service responsible for handling administrative directive operations
   * @param transformerService the service responsible for transforming administrative directives
   *     using XSLT
   */
  @Autowired
  public AdministrativeDirectiveController(
      AdministrativeDirectiveService service,
      AdministrativeDirectiveXsltTransformerService transformerService) {
    this.service = service;
    this.transformerService = transformerService;
  }

  /**
   * Retrieves the metadata of a single administrative directive by its document number.
   *
   * @param documentNumber the unique identifier of the administrative directive to retrieve
   *     metadata for
   * @return a {@code ResponseEntity} containing the metadata of the administrative directive
   *     wrapped in an {@code AdministrativeDirectiveSchema} if found, or a 404 response if no such
   *     directive exists
   */
  @GetMapping(
      path = ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE + "/{documentNumber}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Administrative directive metadata",
      description =
          "The endpoint returns the metadata of a single administrative directive from our database.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content)
  public ResponseEntity<AdministrativeDirectiveSchema> getAdministrativeDirectiveMetadata(
      @Parameter(example = "KSNR00000") @PathVariable String documentNumber) {
    List<AdministrativeDirective> result = service.getByDocumentNumber(documentNumber);
    if (result.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    AdministrativeDirective unit = result.getFirst();
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(AdministrativeDirectiveSchemaMapper.fromDomain(unit));
  }

  /**
   * Search for administrative directives in the OpenSearch index with filters. For more information
   * on the parameters, refer to the OpenAPI documentation.
   *
   * @param searchParams Parameters to search with for administrative directives.
   * @param universalSearchParams Parameters to search with for all document types.
   * @param paginationParams The number of entities and page index to request.
   * @param sortParams The sorting parameters
   * @return The search results
   */
  @GetMapping(
      path = ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "List and search administrative directives",
      description =
          "The endpoint returns a list of administrative directives from our database. The list is paginated and can be filtered and sorted.")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  public ResponseEntity<CollectionSchema<SearchMemberSchema<AdministrativeDirectiveSearchSchema>>>
      searchAndFilter(
          @ParameterObject() AdministrativeDirectiveSearchParams searchParams,
          @ParameterObject UniversalSearchParams universalSearchParams,
          @ParameterObject() @Valid PaginationParams paginationParams,
          @ParameterObject @Valid AdministrativeDirectiveSortParam sortParams)
          throws CustomValidationException {

    var pageRequest = PageRequest.of(paginationParams.getPageIndex(), paginationParams.getSize());

    var sortedPageRequest =
        pageRequest.withSort(
            SortParamsConverter.buildSortWithNullHandlingLast(sortParams.getSort()));

    try {
      SearchPage<AdministrativeDirective> page =
          service.simpleSearch(universalSearchParams, searchParams, sortedPageRequest);
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(AdministrativeDirectiveSearchSchemaMapper.fromSearchPage(page));
    } catch (UncategorizedElasticsearchException e) {
      LuceneQueryTools.checkForInvalidQuery(e);
      throw e;
    }
  }

  /**
   * Retrieves an administrative directive in XML format based on the provided document number. This
   * XML content is used as a source for the HTML endpoint.
   *
   * @param documentNumber The unique identifier of the administrative directive to retrieve.
   *     Example: "KSNR00000".
   * @return A ResponseEntity containing the XML representation of the administrative directive as a
   *     byte array with HTTP status 200 if the document is found, or an HTTP status 404 if the
   *     document is not found.
   */
  @GetMapping(
      path = ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE + "/{documentNumber}.xml",
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Administrative directive XML",
      description =
          "Returns an administrative directive item as XML. This content is used as a source for the HTML endpoint.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content(schema = @Schema()))
  public ResponseEntity<byte[]> getAdministrativeDirectiveAsXml(
      @Parameter(example = "KSNR00000") @PathVariable String documentNumber) {

    Optional<byte[]> bytes = service.getFileByDocumentNumber(documentNumber);
    return bytes.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Renders and returns an administrative directive as an HTML document.
   *
   * @param documentNumber the document number of the administrative directive to be retrieved
   * @return a ResponseEntity containing the HTML representation of the administrative directive if
   *     found, or a 404 Not Found response if the corresponding directive does not exist
   */
  @GetMapping(
      path = ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE + "/{documentNumber}.html",
      produces = MediaType.TEXT_HTML_VALUE)
  @Operation(
      summary = "Administrative directive HTML",
      description = "Renders and returns an administrative directive as HTML.")
  @ApiResponse(
      responseCode = "200",
      content = @Content(mediaType = MediaType.TEXT_HTML_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "404", content = @Content)
  public ResponseEntity<String> getAdministrativeDirectiveAsHtml(
      @Parameter(example = "KSNR00000") @PathVariable String documentNumber) {
    return service
        .getFileByDocumentNumber(documentNumber)
        .map(transformerService::transform)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
