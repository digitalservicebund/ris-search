package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.mapper.CaseLawSchemaMapper;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.schema.CaseLawSchema;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.service.XsltTransformerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Case Law")
@RestController
@Profile({"default", "staging", "test", "prototype"})
public class CaseLawController {

  private final CaseLawService caseLawService;
  private final XsltTransformerService xsltTransformerService;
  private final CaseLawBucket caseLawBucket;
  private final Environment environment;

  @Autowired
  public CaseLawController(
      CaseLawService caseLawService,
      XsltTransformerService xsltTransformerService,
      CaseLawBucket caseLawBucket,
      Environment environment) {
    this.caseLawService = caseLawService;
    this.xsltTransformerService = xsltTransformerService;
    this.caseLawBucket = caseLawBucket;
    this.environment = environment;
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
      @Parameter(example = "STRE201770751") @PathVariable String documentNumber)
      throws ObjectStoreServiceException {
    Optional<byte[]> bytes;
    if (environment.acceptsProfiles(Profiles.of("default", "test", "staging"))) {
      bytes = caseLawBucket.get(String.format("%s/%s.xml", documentNumber, documentNumber));
    } else {
      bytes = caseLawBucket.get(String.format("%s.xml", documentNumber));
    }

    if (bytes.isPresent()) {
      String html = xsltTransformerService.transformCaseLaw(bytes.get());
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
    Optional<byte[]> bytes;
    if (environment.acceptsProfiles(Profiles.of("default", "test", "staging"))) {
      bytes = caseLawBucket.get(String.format("%s/%s.xml", documentNumber, documentNumber));
    } else {
      bytes = caseLawBucket.get(String.format("%s.xml", documentNumber));
    }

    return bytes.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }
}
