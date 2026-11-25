package de.bund.digitalservice.ris.search.controller.api;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import de.bund.digitalservice.ris.html.service.xslt.SliLiteratureV1XsltTransformer;
import de.bund.digitalservice.ris.html.service.xslt.UliLiteratureV1XsltTransformer;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ServerConfig;
import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.mapper.ChangelogResponseMapper;
import de.bund.digitalservice.ris.search.mapper.LiteratureSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.LiteratureSearchSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.SortParamsConverter;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.models.api.parameters.ChangelogParams;
import de.bund.digitalservice.ris.search.models.api.parameters.LiteratureSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.LiteratureSortParam;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureType;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import de.bund.digitalservice.ris.search.schema.ChangelogResponse;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LiteratureSchema;
import de.bund.digitalservice.ris.search.schema.LiteratureSearchSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import de.bund.digitalservice.ris.search.service.LiteratureService;
import de.bund.digitalservice.ris.search.utils.LuceneQueryTools;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/** Controller serving literature documents under {@code /v1/literaturnachweise}. */
@Tag(name = "Literaturnachweise")
@RestController
@Profile({"dev", "e2e", "staging", "uat", "test", "prototype"})
public class LiteraturnachweiseController {

  private final LiteratureService literatureService;
  private final UliLiteratureV1XsltTransformer uliXsltTransformer;
  private final SliLiteratureV1XsltTransformer sliXsltTransformer;
  private final ChangelogService<LiteratureBucket> changelogService;
  private final String jsonldContextPath;

  /**
   * Constructor for LiteraturnachweiseController.
   *
   * @param literatureService the service responsible for handling literature-related operations
   * @param uliXsltTransformer the XSLT transformer for unselbstständige literature
   * @param sliXsltTransformer the XSLT transformer for selbstständige literature
   * @param changelogService the service responsible for changelog operations
   * @param serverConfig serverconfig of the application
   */
  public LiteraturnachweiseController(
      LiteratureService literatureService,
      UliLiteratureV1XsltTransformer uliXsltTransformer,
      SliLiteratureV1XsltTransformer sliXsltTransformer,
      ChangelogService<LiteratureBucket> changelogService,
      ServerConfig serverConfig) {
    this.literatureService = literatureService;
    this.uliXsltTransformer = uliXsltTransformer;
    this.sliXsltTransformer = sliXsltTransformer;
    this.changelogService = changelogService;
    this.jsonldContextPath = serverConfig.getBackEndUrl() + ApiConfig.Paths.JSONLD_CONTEXT;
  }

  /**
   * Retrieves the metadata of a single Literaturnachweis by its document number.
   *
   * @param documentNumber the unique document number of the Literaturnachweis
   * @return {@code 200 OK} with the metadata when found, otherwise {@code 404 Not Found}
   */
  @GetMapping(
      path = ApiConfig.Paths.LITERATURNACHWEISE + "/{documentNumber}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Metadaten eines Literaturnachweises",
      description = "The endpoint returns the metadata of a single Literaturnachweis.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content)
  public ResponseEntity<LiteratureSchema> getLiteraturnachweis(
      @Parameter(example = "KALU000000002") @PathVariable String documentNumber) {
    List<Literature> result = literatureService.getByDocumentNumber(documentNumber);
    if (result.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(LiteratureSchemaMapper.fromDomain(result.getFirst(), jsonldContextPath));
  }

  /**
   * Renders and returns a Literaturnachweis as HTML.
   *
   * @param documentNumber the unique document number of the Literaturnachweis
   * @return {@code 200 OK} with the HTML when found, otherwise {@code 404 Not Found}
   * @throws ObjectStoreServiceException if an error occurs while retrieving the file
   */
  @GetMapping(
      path = ApiConfig.Paths.LITERATURNACHWEISE + "/{documentNumber}.html",
      produces = MediaType.TEXT_HTML_VALUE)
  @Operation(
      summary = "Literaturnachweis HTML",
      description = "Renders and returns a Literaturnachweis as HTML.")
  @ApiResponse(
      responseCode = "200",
      content = @Content(mediaType = MediaType.TEXT_HTML_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "404", content = @Content)
  public ResponseEntity<String> getLiteraturnachweisAsHtml(
      @Parameter(example = "BJLU075748788") @PathVariable String documentNumber)
      throws ObjectStoreServiceException {
    return literatureService
        .getFileByDocumentNumber(documentNumber)
        .map(file ->
            switch (LiteratureType.getByDocumentNumber(documentNumber)) {
              case SLI -> sliXsltTransformer.transform(file);
              case ULI -> uliXsltTransformer.transform(file);
              default -> null;
            })
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Returns a Literaturnachweis as XML. This content is the source for the HTML endpoint.
   *
   * @param documentNumber the unique document number of the Literaturnachweis
   * @return {@code 200 OK} with the XML when found, otherwise {@code 404 Not Found}
   * @throws ObjectStoreServiceException if an error occurs while retrieving the file
   */
  @GetMapping(
      path = ApiConfig.Paths.LITERATURNACHWEISE + "/{documentNumber}.xml",
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Literaturnachweis XML",
      description =
          "Returns a Literaturnachweis as XML. This content is used as a source for the HTML endpoint.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content(schema = @Schema))
  public ResponseEntity<byte[]> getLiteraturnachweisAsXml(
      @Parameter(example = "BJLU075748788") @PathVariable String documentNumber)
      throws ObjectStoreServiceException {
    return literatureService
        .getFileByDocumentNumber(documentNumber)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Search for Literaturnachweise in the OpenSearch index with filters. For more information on the
   * parameters, refer to the OpenAPI documentation.
   *
   * @param literatureSearchParams Parameters to search with for literature.
   * @param universalSearchParams Parameters to search with for all document types.
   * @param paginationParams The number of entities and page index to request.
   * @param sortParams The sorting parameters
   * @return The search results
   * @throws CustomValidationException if the search parameters are invalid
   */
  @GetMapping(
      path = ApiConfig.Paths.LITERATURNACHWEISE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      operationId = "searchLiteraturnachweise",
      summary = "List and search Literaturnachweise",
      description =
          "The endpoint returns a list of Literaturnachweise from our database. The list is paginated and can be filtered and sorted.")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  public ResponseEntity<CollectionSchema<SearchMemberSchema<LiteratureSearchSchema>>>
      searchAndFilter(
          @ParameterObject LiteratureSearchParams literatureSearchParams,
          @ParameterObject UniversalSearchParams universalSearchParams,
          @ParameterObject @Valid PaginationParams paginationParams,
          @ParameterObject @Valid LiteratureSortParam sortParams)
          throws CustomValidationException {
    var pageRequest =
        PageRequest.of(paginationParams.getPageIndex(), paginationParams.getSize())
            .withSort(SortParamsConverter.buildSort(sortParams.getSort()));

    try {
      SearchPage<Literature> page =
          literatureService.simpleSearchLiterature(
              universalSearchParams, literatureSearchParams, pageRequest);
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(LiteratureSearchSchemaMapper.fromSearchPage(page, jsonldContextPath));
    } catch (UncategorizedElasticsearchException e) {
      LuceneQueryTools.checkForInvalidQuery(e);
      throw e;
    }
  }

  /**
   * Retrieves an aggregated changelog, listing all indexed fileChanges between the given timestamps
   * (inclusive). If a full reindex occurred inside the given bounds, the allChanged flag is set to
   * true, otherwise changes are given based on the document root in the changed/deleted
   * collections.
   *
   * @param params {@link ChangelogParams} to determine upper and lower boundary to retrieve changes
   * @return Changelog response with aggregated changes across the given time range
   */
  @GetMapping(path = ApiConfig.Paths.LITERATURNACHWEISE_CHANGELOGS)
  @Operation(
      summary = "Literaturnachweise changelog",
      description =
          "Returns references of document changes that occurred in between two points in time.")
  public ResponseEntity<ChangelogResponse> getChangelogs(
      @ParameterObject @Valid ChangelogParams params) {
    var changelog =
        changelogService.getChangesBetween(
            params.getFrom().toInstant(), params.getTo().toInstant());

    return ResponseEntity.ok(
        ChangelogResponseMapper.mapChangelog(
            changelog, DocumentKind.LITERATURE, jsonldContextPath));
  }

  /**
   * Retrieves all files associated with a Literaturnachweis as a ZIP archive.
   *
   * @param documentNumber the unique document number of the Literaturnachweis
   * @return {@code 200 OK} with the ZIP file as a streaming response body when found, otherwise
   *     {@code 404 Not Found}
   */
  @GetMapping(
      path = ApiConfig.Paths.LITERATURNACHWEISE + "/{documentNumber}.zip",
      produces = "application/zip")
  @Operation(
      summary = "Literaturnachweis ZIP (XML and attachments)",
      description = "Returns all files of a Literaturnachweis as a ZIP archive.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content(schema = @Schema))
  public ResponseEntity<StreamingResponseBody> getLiteraturnachweisAsZip(
      @Pattern(regexp = "^[a-zA-Z]{4}\\d{9}$", message = "Invalid document number format")
          @Parameter(example = "XXLS201770751")
          @PathVariable
          String documentNumber) {
    List<String> keys = literatureService.getAllFilenamesByDocumentNumber(documentNumber);
    if (keys.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok()
        .header(CONTENT_DISPOSITION, "attachment;filename=\"%s.zip\"".formatted(documentNumber))
        .contentType(MediaType.valueOf("application/zip"))
        .body(outputStream -> literatureService.writeZipArchive(keys, outputStream));
  }
}
