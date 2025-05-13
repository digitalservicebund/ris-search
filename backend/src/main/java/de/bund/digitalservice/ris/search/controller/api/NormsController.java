package de.bund.digitalservice.ris.search.controller.api;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.mapper.MappingDefinitions;
import de.bund.digitalservice.ris.search.mapper.NormResponseMapper;
import de.bund.digitalservice.ris.search.mapper.NormSearchResponseMapper;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.api.parameters.ResourceReferenceMode;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LegislationWorkSchema;
import de.bund.digitalservice.ris.search.schema.LegislationWorkSearchSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.service.NormsService;
import de.bund.digitalservice.ris.search.service.XsltTransformerService;
import de.bund.digitalservice.ris.search.service.helper.PaginationParamsConverter;
import de.bund.digitalservice.ris.search.utils.LuceneQueryTools;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import de.bund.digitalservice.ris.search.utils.eli.ManifestationEli;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/** Controller class for handling REST API requests for legislation. */
@RestController
@Tag(
    name = "Legislation",
    description = "Retrieve current and historical versions of laws and decrees.")
public class NormsController {
  private static final String HTML_FILE_NOT_FOUND = "<div>LegalDocML file not found</div>";
  private final NormsService normsService;
  private final XsltTransformerService xsltTransformerService;

  @Autowired
  public NormsController(NormsService normsService, XsltTransformerService xsltTransformerService) {
    this.normsService = normsService;
    this.xsltTransformerService = xsltTransformerService;
  }

  @GetMapping(value = ApiConfig.Paths.LEGISLATION, produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "List and search legislation",
      description =
          """
                      List all legislation in our database with support for filtering and pagination.

                      ## Example 1

                      Get all legislation containing the tokens :` Gesetz`, `über`,`das`, `Verfahren`, `bei`, `sonstigen` and `Änderungen`.
                      ```http request
                      GET /v1/legislation?searchTerm=Gesetz%20über%20das%20Verfahren%20bei%20sonstigen%20Änderungen
                      ```
                      The API will return only the legislation that contains all these terms. Please note that in practice very common tokens such as `das` are ignored
                      (please see <a href="https://en.wikipedia.org/wiki/Stop_word">https://en.wikipedia.org/wiki/Stop_word</a> for more details).

                      ## Example 2

                      Get all legislation containing the tokens :` Gesetz` and `über` and were valid on 2020-01-01
                      ```http request
                      GET /v1/legislation?temporalCoverageFrom=2020-01-01&temporalCoverageTo=2020-01-01&searchTerm=Gesetz%20über
                      ```
                      This example can be used to only return currently valid legislation by replacing 2020-01-01 with today's date.

                      ## Example 3

                      Get all legislation that belong to the work eli `eli/bund/bgbl-1/1979/s1325`
                      ```http request
                      GET /v1/legislation?eli=eli/bund/bgbl-1/1979/s1325
                      ```
                      """)
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "422")
  public CollectionSchema<SearchMemberSchema<LegislationWorkSearchSchema>> searchAndFilter(
      @ParameterObject NormsSearchParams normsSearchParams,
      @ParameterObject UniversalSearchParams universalSearchParams,
      @ParameterObject @Valid PaginationParams pagination)
      throws CustomValidationException {

    normsSearchParams.validate();

    boolean defaultToUnsorted = StringUtils.isNotBlank(universalSearchParams.getSearchTerm());
    Pageable pageable =
        PaginationParamsConverter.convert(
            pagination, MappingDefinitions.ResolutionMode.NORMS, defaultToUnsorted);

    try {
      SearchPage<Norm> resultPage =
          normsService.searchAndFilterNorms(universalSearchParams, normsSearchParams, pageable);
      return NormSearchResponseMapper.fromDomain(resultPage, ApiConfig.Paths.LEGISLATION);
    } catch (UncategorizedElasticsearchException e) {
      LuceneQueryTools.checkForInvalidQuery(e);
      throw e;
    }
  }

  @GetMapping(
      path =
          ApiConfig.Paths.LEGISLATION_SINGLE
              + "/{jurisdiction}/{agent}/{year}/{naturalIdentifier}/{pointInTime}/{version}/{language}/{subtype}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Get the metadata for a legislation expression, by its expression-level ELI.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content)
  public ResponseEntity<LegislationWorkSchema> getLegislation(
      @Parameter(description = "Country or regional code for the jurisdiction", example = "bund")
          @PathVariable
          @Schema(allowableValues = {"bund"})
          String jurisdiction,
      @Parameter(
              description =
                  "Agent or authority issuing the legislation, e.g., 'bgbl-1' for Bundesgesetzblatt Teil I (Federal Law Gazette part I)",
              example = "bgbl-1")
          @PathVariable
          @Schema(example = "bgbl-1")
          String agent,
      @Parameter(description = "Year the legislation was enacted or published", example = "1979")
          @PathVariable
          String year,
      @Parameter(
              description =
                  "Unique natural identifier for the legislation, specific to the jurisdiction and agent",
              example = "s1325")
          @PathVariable
          String naturalIdentifier,
      @Parameter(example = "2020-06-19") @PathVariable LocalDate pointInTime,
      @PathVariable @Schema(example = "2") Integer version,
      @PathVariable @Schema(example = "deu") String language,
      @PathVariable @Schema(example = "regelungstext-1") String subtype) {

    var eli =
        new ExpressionEli(
            jurisdiction, agent, year, naturalIdentifier, pointInTime, version, language, subtype);
    Optional<Norm> result = normsService.getByExpressionEli(eli);

    return result
        .map(r -> ResponseEntity.ok(NormResponseMapper.fromDomain(r)))
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping(
      path =
          ApiConfig.Paths.LEGISLATION_SINGLE
              + "/{jurisdiction}/{agent}/{year}/{naturalIdentifier}/{pointInTime}/{version}/{language}/{pointInTimeManifestation}/{subtype}.html",
      produces = MediaType.TEXT_HTML_VALUE)
  @Operation(
      summary = "Get a particular manifestation of a piece of legislation as HTML",
      description =
          """
              Returns a particular manifestation of a piece of legislation, converted to HTML.
              """)
  @ApiResponse(responseCode = "200")
  @ApiResponse(
      responseCode = "404",
      content =
          @Content(
              mediaType = MediaType.TEXT_HTML_VALUE,
              schema = @Schema(example = HTML_FILE_NOT_FOUND)))
  public ResponseEntity<String> getLegislationSubtypeAsHtml(
      @Parameter(description = "Country or regional code for the jurisdiction", example = "bund")
          @PathVariable
          @Schema(allowableValues = {"bund"})
          String jurisdiction,
      @Parameter(
              description =
                  "Agent or authority issuing the legislation, e.g., 'bgbl-1' for Bundesgesetzblatt Teil I (Federal Law Gazette part I)",
              example = "bgbl-1")
          @PathVariable
          String agent,
      @PathVariable
          @Parameter(
              description = "Year the legislation was enacted or published",
              example = "1979")
          String year,
      @Parameter(
              description =
                  "Unique natural identifier for the legislation, specific to the jurisdiction and agent",
              example = "s1325")
          @PathVariable
          String naturalIdentifier,
      @Parameter(example = "2020-06-19") @PathVariable LocalDate pointInTime,
      @PathVariable @Schema(example = "2") Integer version,
      @PathVariable @Schema(example = "deu") String language,
      @PathVariable @Schema(example = "2020-06-19") LocalDate pointInTimeManifestation,
      @Schema(example = "regelungstext-1") @PathVariable String subtype,
      @RequestHeader(
              name = ApiConfig.Headers.GET_RESOURCES_VIA,
              required = false,
              defaultValue = ResourceReferenceMode.DEFAULT_VALUE)
          @Parameter(
              description =
                  "Used to select a different prefix for referenced resources, like images. Selecting 'PROXY' will prepend `/api`. Otherwise, the API base URL will be used.")
          ResourceReferenceMode resourceReferenceMode)
      throws ObjectStoreServiceException {
    final String resourceBasePath = getResourceBasePath(resourceReferenceMode);
    var eli =
        new ManifestationEli(
            jurisdiction,
            agent,
            year,
            naturalIdentifier,
            pointInTime,
            version,
            language,
            pointInTimeManifestation,
            subtype);
    final Optional<byte[]> normFileByEli = normsService.getNormFileByEli(eli);
    if (normFileByEli.isPresent()) {
      final String body =
          xsltTransformerService.transformNorm(normFileByEli.get(), subtype, resourceBasePath);
      return ResponseEntity.ok(body);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HTML_FILE_NOT_FOUND);
    }
  }

  @GetMapping(
      path =
          ApiConfig.Paths.LEGISLATION_SINGLE
              + "/{jurisdiction}/{agent}/{year}/{naturalIdentifier}/{pointInTime}/{version}/{language}/{pointInTimeManifestation}/{subtype}.xml",
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Get a particular manifestation of a piece of legislation as XML",
      description =
          """
              Returns a particular manifestation of a piece of legislation in XML format.

              ## Example

              Download the LegalDocML format for the piece of legislation with a manifestation eli of `eli/bund/bgbl-1/1979/s1325/2020-06-19/2/deu/2020-06-19/regelungstext-1.xml`

              ```http request
              GET /v1/eli/bund/bgbl-1/1979/s1325/2020-06-19/2/deu/2020-06-19/regelungstext-1.xml
              ```
              """)
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content(schema = @Schema()))
  public ResponseEntity<byte[]> getLegislationSubtypeAsXml(
      @Parameter(description = "Country or regional code for the jurisdiction", example = "bund")
          @PathVariable
          @Schema(allowableValues = {"bund"})
          String jurisdiction,
      @Parameter(
              description =
                  "Agent or authority issuing the legislation, e.g., 'bgbl-1' for Bundesgesetzblatt Teil I (Federal Law Gazette part I)",
              example = "bgbl-1")
          @PathVariable
          String agent,
      @PathVariable
          @Parameter(
              description = "Year the legislation was enacted or published",
              example = "1979")
          String year,
      @Parameter(
              description =
                  "Unique natural identifier for the legislation, specific to the jurisdiction and agent",
              example = "s1325")
          @PathVariable
          String naturalIdentifier,
      @Parameter(example = "2020-06-19") @PathVariable LocalDate pointInTime,
      @PathVariable @Schema(example = "2") Integer version,
      @PathVariable @Schema(example = "deu") String language,
      @PathVariable @Schema(example = "2020-06-19") LocalDate pointInTimeManifestation,
      @Schema(example = "regelungstext-1") @PathVariable String subtype)
      throws ObjectStoreServiceException {
    var eli =
        new ManifestationEli(
            jurisdiction,
            agent,
            year,
            naturalIdentifier,
            pointInTime,
            version,
            language,
            pointInTimeManifestation,
            subtype);

    return normsService
        .getNormFileByEli(eli)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping(
      path =
          ApiConfig.Paths.LEGISLATION_SINGLE
              + "/{jurisdiction}/{agent}/{year}/{naturalIdentifier}/{pointInTime}/{version}/{language}/{pointInTimeManifestation}.zip",
      produces = "application/zip")
  @Operation(
      summary =
          "Get a particular manifestation of a piece of legislation, including attachments, as ZIP",
      description =
          "Returns a particular manifestation of a piece of legislation, including attachments, as a ZIP archive.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content(schema = @Schema()))
  public ResponseEntity<StreamingResponseBody> getLegislationSubtypeAsZip(
      @Parameter(description = "Country or regional code for the jurisdiction", example = "bund")
          @PathVariable
          @Schema(allowableValues = {"bund"})
          String jurisdiction,
      @Parameter(
              description =
                  "Agent or authority issuing the legislation, e.g., 'bgbl-1' for Bundesgesetzblatt Teil I (Federal Law Gazette part I)",
              example = "bgbl-1")
          @PathVariable
          String agent,
      @PathVariable
          @Parameter(
              description = "Year the legislation was enacted or published",
              example = "1979")
          String year,
      @Parameter(
              description =
                  "Unique natural identifier for the legislation, specific to the jurisdiction and agent",
              example = "s1325")
          @PathVariable
          String naturalIdentifier,
      @Parameter(example = "2020-06-19") @PathVariable LocalDate pointInTime,
      @PathVariable @Schema(example = "2") Integer version,
      @PathVariable @Schema(example = "deu") String language,
      @PathVariable @Schema(example = "2020-06-19") LocalDate pointInTimeManifestation) {
    String prefix =
        Stream.of(
                "eli",
                jurisdiction,
                agent,
                year,
                naturalIdentifier,
                pointInTime.toString(),
                version.toString(),
                language,
                pointInTimeManifestation.toString())
            .collect(Collectors.joining("/"));

    String fileName = prefix + ".zip";

    List<String> keys = normsService.getAllFilenamesByPath(prefix);

    if (keys.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok()
        .header(CONTENT_DISPOSITION, "attachment;filename=\"%s\"".formatted(fileName))
        .contentType(MediaType.valueOf("application/zip"))
        .body(outputStream -> normsService.writeZipArchive(keys, outputStream));
  }

  @GetMapping(
      path =
          ApiConfig.Paths.LEGISLATION_SINGLE
              + "/{jurisdiction}/{agent}/{year}/{naturalIdentifier}/{pointInTime}/{version}/{language}/{pointInTimeManifestation}/{subtype}/{articleEid}.{format}",
      produces = MediaType.TEXT_HTML_VALUE)
  @Operation(
      summary =
          "Get an article (§) of a particular manifestation of a piece of legislation as HTML",
      description =
          """
              Returns a specific article (§) of particular manifestation of a piece of legislation, converted to HTML.
              """)
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content())
  public ResponseEntity<String> getLegislationArticleAsHtml(
      @PathVariable
          @Parameter(
              description = "Country or regional code for the jurisdiction",
              example = "bund")
          @Schema(allowableValues = {"bund"})
          String jurisdiction,
      @Parameter(
              description =
                  "Agent or authority issuing the legislation, e.g., 'bgbl-1' for Bundesgesetzblatt Teil I (Federal Law Gazette part I)",
              example = "bgbl-1")
          @PathVariable
          String agent,
      @PathVariable
          @Parameter(
              description = "Year the legislation was enacted or published",
              example = "1979")
          String year,
      @Parameter(
              description =
                  "Unique natural identifier for the legislation, specific to the jurisdiction and agent",
              example = "s1325")
          @PathVariable
          String naturalIdentifier,
      @Parameter(example = "2020-06-19") @PathVariable LocalDate pointInTime,
      @PathVariable @Parameter(example = "2") Integer version,
      @PathVariable @Parameter(example = "deu") String language,
      @PathVariable @Parameter(example = "2020-06-19") LocalDate pointInTimeManifestation,
      @Parameter(example = "regelungstext-1") @PathVariable String subtype,
      @Parameter(
              description = "The expression identifier, denoting elements inside an expression",
              example = "hauptteil-1_art-1")
          @PathVariable
          String articleEid,
      @PathVariable @Schema(allowableValues = "html") String format,
      @RequestHeader(
              name = ApiConfig.Headers.GET_RESOURCES_VIA,
              required = false,
              defaultValue = ResourceReferenceMode.DEFAULT_VALUE)
          @Parameter(
              description =
                  "Used to select a different prefix for referenced resources, like images. Selecting 'PROXY' will prepend `/api`. Otherwise, the API base URL will be used.")
          ResourceReferenceMode resourceReferenceMode)
      throws ObjectStoreServiceException {
    final String resourceBasePath = getResourceBasePath(resourceReferenceMode);

    if (!Objects.equals(format, "html")) {
      return ResponseEntity.badRequest().body("only html is supported for format");
    }
    var eli =
        new ManifestationEli(
            jurisdiction,
            agent,
            year,
            naturalIdentifier,
            pointInTime,
            version,
            language,
            pointInTimeManifestation,
            subtype);
    final Optional<byte[]> normFileByEli = normsService.getNormFileByEli(eli);
    return normFileByEli
        .map(
            bytes ->
                ResponseEntity.ok(
                    xsltTransformerService.transformArticle(bytes, articleEid, resourceBasePath)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping(
      path =
          ApiConfig.Paths.LEGISLATION_SINGLE
              + "/{jurisdiction}/{agent}/{year}/{naturalIdentifier}/{pointInTime}/{version}/{language}/{pointInTimeManifestation}/{name}.{extension}")
  @Operation(
      summary = "Get a resource of a particular manifestation of a piece of legislation",
      description =
          """
                            Returns a specific resource of a particular manifestation of a piece of legislation.
                            """)
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content())
  public ResponseEntity<byte[]> getImage(
      @Parameter(description = "Country or regional code for the jurisdiction", example = "bund")
          @PathVariable
          @Schema(allowableValues = {"bund"})
          String jurisdiction,
      @Parameter(
              description =
                  "Agent or authority issuing the legislation, e.g., 'bgbl-1' for Bundesgesetzblatt Teil I (Federal Law Gazette part I)",
              example = "bgbl-1")
          @PathVariable
          String agent,
      @PathVariable
          @Parameter(
              description = "Year the legislation was enacted or published",
              example = "1979")
          String year,
      @Parameter(
              description =
                  "Unique natural identifier for the legislation, specific to the jurisdiction and agent",
              example = "s1325")
          @PathVariable
          String naturalIdentifier,
      @Parameter(example = "2020-06-19") @PathVariable LocalDate pointInTime,
      @PathVariable @Schema(example = "2") Integer version,
      @PathVariable @Schema(example = "deu") String language,
      @PathVariable @Schema(example = "2020-06-19") LocalDate pointInTimeManifestation,
      @Schema(example = "image") @PathVariable String name,
      @Schema(example = "jpg") @PathVariable String extension)
      throws ObjectStoreServiceException {

    final ManifestationEli baseEli =
        new ManifestationEli(
            jurisdiction,
            agent,
            year,
            naturalIdentifier,
            pointInTime,
            version,
            language,
            pointInTimeManifestation,
            name);
    Optional<byte[]> image = normsService.getAttachment(baseEli, extension);

    final String mimeType = URLConnection.guessContentTypeFromName(name + "." + extension);

    return image
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
      case API -> ApiConfig.Paths.LEGISLATION + "/";
      case PROXY -> "/api" + ApiConfig.Paths.LEGISLATION + "/";
    };
  }
}
