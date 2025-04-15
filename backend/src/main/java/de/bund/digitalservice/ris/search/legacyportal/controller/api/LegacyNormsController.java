package de.bund.digitalservice.ris.search.legacyportal.controller.api;

import de.bund.digitalservice.ris.search.legacyportal.config.ApiConfig;
import de.bund.digitalservice.ris.search.legacyportal.controller.helper.BuildLegislationDocumentURI;
import de.bund.digitalservice.ris.search.legacyportal.dto.api.norms.NormsApiListDTO;
import de.bund.digitalservice.ris.search.legacyportal.enums.LegalDocumentVersion;
import de.bund.digitalservice.ris.search.legacyportal.exceptions.BadRequestException;
import de.bund.digitalservice.ris.search.legacyportal.service.LegacyNormsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Legacy", description = "Legacy API for E-Gesetzgebung")
@RestController
@RequestMapping(ApiConfig.API_OPEN_NORMS)
@ConditionalOnProperty(value = "feature-flags.legacy-endpoints", havingValue = "true")
public class LegacyNormsController {

  private final LegacyNormsService normsService;
  private static final String ERROR_MESSAGE_WRONG_LEGAL_DOCUMENT_VERSION =
      "The norm version is wrong. It must be 1.4 or 1.6";
  private static final String ERROR_MESSAGE_PRINT_ANNOUNCEMENT_YEAR =
      "Print announcement year parameter should be a numeric or positive value";

  @Autowired
  public LegacyNormsController(LegacyNormsService normsService) {
    this.normsService = normsService;
  }

  @GetMapping(produces = {"application/json"})
  @Operation(summary = "Get all norms", description = "Get a list of norms filtered by a query")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved")
  @ApiResponse(responseCode = "400", description = "Bad request")
  @ApiResponse(responseCode = "404", description = "Norms could not be found")
  @ApiResponse(responseCode = "500", description = "Internal Server Error")
  public ResponseEntity<NormsApiListDTO> getAllLegislations(
      @Parameter(
              name = "q",
              description =
                  """
                Searches for a substring in the following properties of a norm:
                - officialLongTitle
                - officialShortTitle
                - unofficialLongTitle
                - unofficialShortTitle
                    The search term is used as is without any postprocessing and is case sensitive
                """)
          @RequestParam(value = "q", required = false)
          String searchQuery,
      @Parameter(
              name = "version",
              description = "Searches for norms by the version. Possible values: 1.4|1.6")
          @RequestParam(value = "version", required = false, defaultValue = "1.4")
          String version) {

    var legalDocumentVersion = LegalDocumentVersion.fromString(version);

    if (legalDocumentVersion == null) {
      throw new BadRequestException(ERROR_MESSAGE_WRONG_LEGAL_DOCUMENT_VERSION);
    }

    var result = normsService.getAllNormsBySearchQuery(searchQuery, legalDocumentVersion);

    return result.isPresent() ? ResponseEntity.ok(result.get()) : ResponseEntity.notFound().build();
  }

  @GetMapping(
      path =
          ApiConfig.API_OPEN_NORMS_ELI_XML
              + "/{printAnnouncementGazette}/{printAnnouncementYear}/{printAnnouncementPage}",
      produces = {"application/xml"})
  @Operation(
      summary = "Load a single as XML in LegalDocML.de format by eli",
      description = "Retrieves a single norm in LegalDocML.de xml format using its ELI")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved")
  @ApiResponse(responseCode = "400", description = "Bad request")
  @ApiResponse(responseCode = "404", description = "Norm could not be found")
  @ApiResponse(responseCode = "500", description = "Internal Server Error")
  public ResponseEntity<byte[]> downloadNormXMLDocument(
      HttpServletResponse response,
      @Parameter(
              name = "printAnnouncementGazette",
              description = "First part of the ELI, the gazette of the print announcement version",
              required = true,
              example = "bgbl-1")
          @PathVariable
          String printAnnouncementGazette,
      @Parameter(
              name = "printAnnouncementYear",
              description = "Second part of the ELI, the publication year",
              required = true,
              example = "1976")
          @PathVariable
          String printAnnouncementYear,
      @Parameter(
              name = "printAnnouncementPage",
              description = "Third part of the ELI, the page of the print announcement version",
              required = true,
              example = "s3341")
          @PathVariable
          String printAnnouncementPage) {

    if (!StringUtils.isNumeric(printAnnouncementYear)
        || NumberUtils.toInt(printAnnouncementYear) < NumberUtils.INTEGER_ZERO) {
      throw new BadRequestException(ERROR_MESSAGE_PRINT_ANNOUNCEMENT_YEAR);
    }

    BuildLegislationDocumentURI buildLegislationDocumentURI =
        new BuildLegislationDocumentURI(
            printAnnouncementGazette,
            NumberUtils.toInt(printAnnouncementYear),
            printAnnouncementPage);

    var normDocumentResult =
        this.normsService.getNormDocumentByAnnouncementParameters(buildLegislationDocumentURI);

    if (normDocumentResult.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    var fileName = buildLegislationDocumentURI.getDocumentURI().replace("/", "_") + ".xml";

    response.setContentType(MediaType.APPLICATION_XML_VALUE);
    response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
    response.setContentLength(normDocumentResult.get().length);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    return ResponseEntity.ok(normDocumentResult.get());
  }
}
