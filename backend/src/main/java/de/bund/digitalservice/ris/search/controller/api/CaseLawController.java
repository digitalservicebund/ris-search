package de.bund.digitalservice.ris.search.controller.api;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.mapper.CaseLawSchemaMapper;
import de.bund.digitalservice.ris.search.models.api.parameters.ResourceReferenceMode;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.schema.CaseLawSchema;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.service.XsltTransformerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Tag(name = "Case Law")
@RestController
@Profile({"default", "staging", "test", "prototype"})
public class CaseLawController {

  private final CaseLawService caseLawService;
  private final XsltTransformerService xsltTransformerService;

  @Autowired
  public CaseLawController(
      CaseLawService caseLawService, XsltTransformerService xsltTransformerService) {
    this.caseLawService = caseLawService;
    this.xsltTransformerService = xsltTransformerService;
  }

  @GetMapping(
      path = ApiConfig.Paths.CASELAW + "/{documentNumber}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Decision metadata",
      description = "The endpoint returns a single decision from our database.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content)
  public ResponseEntity<CaseLawSchema> getCaseLaw(
      @Parameter(example = "STRE201770751") @PathVariable String documentNumber) {
    List<CaseLawDocumentationUnit> result = caseLawService.getByDocumentNumber(documentNumber);
    if (result.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    CaseLawDocumentationUnit unit = result.getFirst();
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(CaseLawSchemaMapper.fromDomain(unit));
  }

  @GetMapping(
      path = ApiConfig.Paths.CASELAW + "/{documentNumber}.html",
      produces = MediaType.TEXT_HTML_VALUE)
  @Operation(
      summary = "Decision HTML",
      description = "Renders and returns a case law decision as HTML.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content)
  public ResponseEntity<String> getCaseLawDocumentationUnitAsHtml(
      @Parameter(example = "STRE201770751") @PathVariable String documentNumber,
      @RequestHeader(
              name = ApiConfig.Headers.GET_RESOURCES_VIA,
              required = false,
              defaultValue = ResourceReferenceMode.DEFAULT_VALUE)
          @Parameter(
              description =
                  "Used to select a different prefix for referenced resources, like images. Selecting 'PROXY' will prepend `/api`. Otherwise, the API base URL will be used.")
          ResourceReferenceMode resourceReferenceMode)
      throws ObjectStoreServiceException {
    final String resourcePath = getResourceBasePath(resourceReferenceMode) + documentNumber + "/";
    Optional<byte[]> bytes = caseLawService.getFileByDocumentNumber(documentNumber);

    if (bytes.isPresent()) {
      String html = xsltTransformerService.transformCaseLaw(bytes.get(), resourcePath);
      return ResponseEntity.ok(html);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping(
      path = ApiConfig.Paths.CASELAW + "/{documentNumber}.xml",
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Decision XML",
      description =
          "Returns a case law decision as XML. This content is used as a source for the HTML endpoint.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content(schema = @Schema()))
  public ResponseEntity<byte[]> getCaseLawDocumentationUnitAsXml(
      @Parameter(example = "STRE201770751") @PathVariable String documentNumber)
      throws ObjectStoreServiceException {

    Optional<byte[]> bytes = caseLawService.getFileByDocumentNumber(documentNumber);
    return bytes.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping(
      path = ApiConfig.Paths.CASELAW + "/{documentNumber}.zip",
      produces = "application/zip")
  @Operation(
      summary = "Decision ZIP (XML and attachments)",
      description = "Returns a case law decision, including attachments, as a ZIP archive.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content(schema = @Schema()))
  public ResponseEntity<StreamingResponseBody> getCaseLawDocumentationUnitAsZip(
      @Parameter(example = "STRE201770751") @PathVariable String documentNumber) {

    String filename = documentNumber + ".zip";
    List<String> keys = caseLawService.getAllFilenamesByDocumentNumber(documentNumber);

    if (keys.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok()
        .header(CONTENT_DISPOSITION, "attachment;filename=\"%s\"".formatted(filename))
        .contentType(MediaType.valueOf("application/zip"))
        .body(outputStream -> caseLawService.writeZipArchive(keys, outputStream));
  }

  @GetMapping(path = ApiConfig.Paths.CASELAW + "/{documentNumber}/{name}.{extension}")
  @Operation(
      summary = "Caselaw resource",
      description = "Returns a specific resource of a particular caselaw.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content())
  public ResponseEntity<byte[]> getResource(
      @PathVariable @Schema(example = "BDRE000800001") String documentNumber,
      @Schema(example = "image") @PathVariable String name,
      @Schema(example = "jpg") @PathVariable String extension)
      throws ObjectStoreServiceException {
    Optional<byte[]> resource =
        caseLawService.getFileByPath(documentNumber + "/" + name + "." + extension);

    final String mimeType = URLConnection.guessContentTypeFromName(name + "." + extension);

    return resource
        .map(
            body ->
                ResponseEntity.status(HttpStatus.OK).header("Content-Type", mimeType).body(body))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Controls how resources like images will be referenced. For example, they might be accessed
   * through an API endpoint in local development, but served via a CDN in production.
   *
   * @param mode Controls which static prefix will be returned.
   * @return The prefix to use when returning references to resources.
   */
  private String getResourceBasePath(ResourceReferenceMode mode) {
    return switch (mode) {
      case API -> ApiConfig.Paths.CASELAW + "/";
      case PROXY -> "/api" + ApiConfig.Paths.CASELAW + "/";
    };
  }
}
