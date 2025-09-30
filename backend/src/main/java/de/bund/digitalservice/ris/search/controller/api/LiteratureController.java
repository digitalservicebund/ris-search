package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.mapper.LiteratureSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.LiteratureSearchSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.SortParamsConverter;
import de.bund.digitalservice.ris.search.models.api.parameters.LiteratureSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.LiteratureSortParam;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.api.parameters.ResourceReferenceMode;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LiteratureSchema;
import de.bund.digitalservice.ris.search.schema.LiteratureSearchSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.service.LiteratureService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Literature")
@RestController
@Profile({"default", "staging", "uat", "test", "prototype"})
public class LiteratureController {

  private final LiteratureService literatureService;

  @Autowired
  public LiteratureController(LiteratureService literatureService) {
    this.literatureService = literatureService;
  }

  @GetMapping(
      path = ApiConfig.Paths.LITERATURE + "/{documentNumber}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Literature metadata",
      description = "The endpoint returns the metadata of a single literature from our database.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content)
  public ResponseEntity<LiteratureSchema> getLiteratureMetadata(
      @Parameter(example = "STRE201770751") @PathVariable String documentNumber) {
    List<Literature> result = literatureService.getByDocumentNumber(documentNumber);
    if (result.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    Literature unit = result.getFirst();
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(LiteratureSchemaMapper.fromDomain(unit));
  }

  @GetMapping(
      path = ApiConfig.Paths.LITERATURE + "/{documentNumber}.html",
      produces = MediaType.TEXT_HTML_VALUE)
  @Operation(
      summary = "Literature HTML",
      description = "Renders and returns a literature item as HTML.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content)
  public ResponseEntity<String> getLiteratureAsHtml(
      @Parameter(example = "BJLU075748788") @PathVariable String documentNumber,
      @RequestHeader(
              name = ApiConfig.Headers.GET_RESOURCES_VIA,
              required = false,
              defaultValue = ResourceReferenceMode.DEFAULT_VALUE)
          @Parameter(
              description =
                  "Used to select a different prefix for referenced resources, like images. Selecting 'PROXY' will prepend `/api`. Otherwise, the API base URL will be used.")
          ResourceReferenceMode resourceReferenceMode) {
    // implement this correctly when the literature xslt transformer is available
    throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Not implemented yet");
  }

  @GetMapping(
      path = ApiConfig.Paths.LITERATURE + "/{documentNumber}.xml",
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Literature XML",
      description =
          "Returns a literature item as XML. This content is used as a source for the HTML endpoint.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content(schema = @Schema()))
  public ResponseEntity<byte[]> getLiteratureAsXml(
      @Parameter(example = "BJLU075748788") @PathVariable String documentNumber)
      throws ObjectStoreServiceException {

    Optional<byte[]> bytes = literatureService.getFileByDocumentNumber(documentNumber);
    return bytes.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Search for literature in the OpenSearch index with filters. For more information on the
   * parameters, refer to the OpenAPI documentation.
   *
   * @param literatureSearchParams Parameters to search with for literature.
   * @param universalSearchParams Parameters to search with for all document types.
   * @param paginationParams The number of entities and page index to request.
   * @param sortParams The sorting parameters
   * @return The search results
   */
  @GetMapping(path = ApiConfig.Paths.LITERATURE, produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "List and search literature",
      description =
          "The endpoint returns a list of literature from our database. The list is paginated and can be filtered and sorted.")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  public ResponseEntity<CollectionSchema<SearchMemberSchema<LiteratureSearchSchema>>>
      searchAndFilter(
          @ParameterObject() LiteratureSearchParams literatureSearchParams,
          @ParameterObject UniversalSearchParams universalSearchParams,
          @ParameterObject() @Valid PaginationParams paginationParams,
          @ParameterObject @Valid LiteratureSortParam sortParams)
          throws CustomValidationException {

    var pageRequest = PageRequest.of(paginationParams.getPageIndex(), paginationParams.getSize());

    var sortedPageRequest =
        pageRequest.withSort(SortParamsConverter.buildSort(sortParams.getSort()));

    try {
      SearchPage<Literature> page =
          literatureService.searchAndFilterLiterature(
              universalSearchParams, literatureSearchParams, sortedPageRequest);
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(LiteratureSearchSchemaMapper.fromSearchPage(page));
    } catch (UncategorizedElasticsearchException e) {
      LuceneQueryTools.checkForInvalidQuery(e);
      throw e;
    }
  }
}
