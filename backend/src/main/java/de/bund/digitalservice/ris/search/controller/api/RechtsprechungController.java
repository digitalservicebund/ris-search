package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.mapper.RechtsprechungSchemaMapper;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.schema.RechtsprechungSchema;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for retrieving metadata of individual Rechtsprechung (case law) documents. */
@Tag(name = "Rechtsprechung")
@RestController
@Profile({"default", "staging", "uat", "test", "prototype"})
public class RechtsprechungController {

  private final CaseLawService caseLawService;

  @Autowired
  public RechtsprechungController(CaseLawService caseLawService) {
    this.caseLawService = caseLawService;
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
}
