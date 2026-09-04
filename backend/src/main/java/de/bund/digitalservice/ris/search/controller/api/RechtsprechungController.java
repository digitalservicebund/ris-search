package de.bund.digitalservice.ris.search.controller.api;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import de.bund.digitalservice.ris.html.service.xslt.CaselawXsltTransformer;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.FileNotFoundException;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.mapper.ChangelogResponseMapper;
import de.bund.digitalservice.ris.search.mapper.RechtsprechungSchemaMapper;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.models.api.parameters.ChangelogParams;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.schema.ChangelogResponse;
import de.bund.digitalservice.ris.search.schema.RechtsprechungSchema;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * REST controller for retrieving Rechtsprechung (case law) documents.
 *
 * <p>The JSON metadata endpoint below is intentionally kept separate from {@link
 * CaseLawController}, returning the richer, German-named {@link RechtsprechungSchema}. All other
 * endpoints mirror their {@code /v1/case-law/**} counterparts exactly (same {@link CaseLawService}
 * calls, same response bodies), just reachable under {@code /v1/rechtsprechung/**} as well, so the
 * frontend can be migrated to the new base path in a follow-up PR before {@code /v1/case-law/**} is
 * eventually removed.
 */
@Tag(name = "Rechtsprechung")
@RestController
@Profile({"dev", "e2e", "default", "staging", "uat", "test", "prototype"})
public class RechtsprechungController {

  private final CaseLawService caseLawService;
  private final CaselawXsltTransformer caselawXsltTransformer;
  private final ChangelogService<CaseLawBucket> changelogService;

  /**
   * Constructor for the RechtsprechungController class.
   *
   * @param caseLawService the service layer responsible for case law operations
   * @param caselawXsltTransformer the case law xslt transformer
   * @param changelogService the service layer responsible for changelog operations
   */
  @Autowired
  public RechtsprechungController(
      CaseLawService caseLawService,
      CaselawXsltTransformer caselawXsltTransformer,
      ChangelogService<CaseLawBucket> changelogService) {
    this.caseLawService = caseLawService;
    this.caselawXsltTransformer = caselawXsltTransformer;
    this.changelogService = changelogService;
  }

  /**
   * Returns metadata for a single Rechtsprechung (caselaw) document by document number.
   *
   * @param documentNumber unique document number of the case law document
   * @return {@code 200 OK} with metadata when found, otherwise {@code 404 Not Found}
   */
  @GetMapping(
      path = ApiConfig.Paths.RECHTSPRECHUNG + "/{documentNumber}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Judgment metadata (Metadaten eines Rechtsrechungsdokuments)",
      description =
          "The endpoint returns a single judgment (Rechtsrechungsdokument) from our database.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content)
  public ResponseEntity<RechtsprechungSchema> getCaseLaw(
      @Parameter(example = "STRE201770751") @PathVariable String documentNumber) {
    List<CaseLawDocumentationUnit> result = caseLawService.getByDocumentNumber(documentNumber);
    if (result.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    CaseLawDocumentationUnit unit = result.getFirst();
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(RechtsprechungSchemaMapper.fromDomain(unit));
  }

  /**
   * Renders and returns a case law decision as an HTML document.
   *
   * @param documentNumber the unique identifier for the case law document to be retrieved and
   *     rendered as HTML
   * @return a ResponseEntity containing the rendered HTML content if the document is found, or a
   *     404 status if not found
   * @throws ObjectStoreServiceException if an error occurs while accessing the object store
   */
  @GetMapping(
      path = ApiConfig.Paths.RECHTSPRECHUNG + "/{documentNumber}.html",
      produces = MediaType.TEXT_HTML_VALUE)
  @Operation(
      summary = "Decision HTML",
      description = "Renders and returns a case law decision as HTML.")
  @ApiResponse(
      responseCode = "200",
      content = @Content(mediaType = MediaType.TEXT_HTML_VALUE, schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "404", content = @Content)
  public ResponseEntity<String> getCaseLawDocumentationUnitAsHtml(
      @Parameter(example = "STRE201770751") @PathVariable String documentNumber)
      throws ObjectStoreServiceException {
    final String resourcePath = getResourceBasePath(documentNumber);
    Optional<byte[]> bytes = caseLawService.getFileByDocumentNumber(documentNumber);

    if (bytes.isPresent()) {
      String html = caselawXsltTransformer.transform(bytes.get(), resourcePath);
      return ResponseEntity.ok(html);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Retrieves a case law decision as an XML document using the provided document number.
   *
   * @param documentNumber The unique identifier for the case law decision, used to locate and
   *     retrieve the corresponding XML content. For example, "STRE201770751".
   * @return A ResponseEntity containing the XML content as a byte array if the document is found,
   *     or a 404 Not Found response if the document does not exist.
   * @throws ObjectStoreServiceException If an error occurs during the retrieval process from the
   *     underlying storage service.
   */
  @GetMapping(
      path = ApiConfig.Paths.RECHTSPRECHUNG + "/{documentNumber}.xml",
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

  /**
   * Retrieves a case law decision and its associated attachments as a ZIP archive.
   *
   * @param documentNumber the unique identifier of the case law document to retrieve, e.g., a
   *     document number like "STRE201770751"
   * @return a ResponseEntity containing the ZIP file as a streaming response body if the document
   *     is found, or a 404 Not Found response if no document matches the provided identifier
   */
  @GetMapping(
      path = ApiConfig.Paths.RECHTSPRECHUNG + "/{documentNumber}.zip",
      produces = "application/zip")
  @Operation(
      summary = "Decision ZIP (XML and attachments)",
      description = "Returns a case law decision, including attachments, as a ZIP archive.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content(schema = @Schema()))
  public ResponseEntity<StreamingResponseBody> getCaseLawDocumentationUnitAsZip(
      @Pattern(regexp = "^[a-zA-Z]{4}\\d{9}$", message = "Invalid document number format")
          @Parameter(example = "STRE201770751")
          @PathVariable
          String documentNumber) {

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

  /**
   * Retrieves a specific image resource for a particular caselaw based on the provided parameters.
   * *
   *
   * @param documentNumber the unique document number identifying the caselaw resource (e.g.,
   *     "BDRE000800001")
   * @param name the name of the image file (e.g., "image")
   * @param extension the file extension of the image (e.g., "png", "jpg", "jpeg", "gif", "wmf",
   *     "emf", or "bitmap")
   * @return a {@code ResponseEntity} containing the requested image resource as a byte array and a
   *     "Content-Type" header specifying the MIME type of the file; if the file is not found or the
   *     extension is invalid, a {@code ResponseEntity} with a 404 status is returned
   * @throws ObjectStoreServiceException if an error occurs while accessing the object storage
   *     service
   * @throws FileNotFoundException if the requested file is not found and the placeholder image
   *     cannot be loaded
   */
  @GetMapping(path = ApiConfig.Paths.RECHTSPRECHUNG + "/{documentNumber}/{name}.{extension}")
  @Operation(
      summary = "Caselaw resource",
      description = "Returns a specific resource of a particular caselaw.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content())
  public ResponseEntity<byte[]> getImage(
      @Parameter(example = "BDRE000800001") @PathVariable String documentNumber,
      @Parameter(example = "image") @PathVariable String name,
      @Parameter(
              example = "jpg",
              schema =
                  @Schema(allowableValues = {"png", "jpg", "jpeg", "gif", "wmf", "emf", "bitmap"}))
          @PathVariable
          String extension)
      throws ObjectStoreServiceException, FileNotFoundException {
    if (!List.of("png", "jpg", "jpeg", "gif", "wmf", "emf", "bitmap")
        .contains(extension.toLowerCase())) {
      return ResponseEntity.notFound().build();
    }

    byte[] resource =
        caseLawService
            .getFileByPath(documentNumber + "/" + name + "." + extension)
            .orElseGet(
                () -> {
                  try {
                    return new ClassPathResource("placeholder.png").getInputStream().readAllBytes();
                  } catch (IOException exception) {
                    throw new FileNotFoundException(exception.getMessage());
                  }
                });

    final String mimeType = URLConnection.guessContentTypeFromName(name + "." + extension);

    return ResponseEntity.status(HttpStatus.OK).header("Content-Type", mimeType).body(resource);
  }

  /**
   * Controls how resources like images will be referenced.
   *
   * @param documentNumber parent Document that resources belong to
   * @return The prefix to use when returning references to resources.
   */
  private String getResourceBasePath(String documentNumber) {
    return ApiConfig.Paths.RECHTSPRECHUNG + "/" + documentNumber + "/";
  }

  /**
   * Retrieves an aggregated changelog, listing all indexed fileChanges between the given timestamps
   * (inclusive).
   *
   * @param params {@link ChangelogParams} to determine upper and lower boundary to retrieve changes
   * @return Changelog response with aggregated changes across the given time range
   */
  @GetMapping(path = ApiConfig.Paths.RECHTSPRECHUNG_CHANGELOGS)
  @Operation(
      summary = "Caselaw changelog",
      description =
          "Returns references of document changes that occurred in between two points in time.")
  @ApiResponse(responseCode = "200")
  public ResponseEntity<ChangelogResponse> getChangelogs(
      @ParameterObject @Valid ChangelogParams params) {

    var changelog =
        changelogService.getChangesBetween(
            params.getFrom().toInstant(), params.getTo().toInstant());
    return ResponseEntity.ok(
        ChangelogResponseMapper.mapChangelog(changelog, DocumentKind.CASE_LAW));
  }
}
