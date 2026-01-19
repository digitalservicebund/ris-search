package de.bund.digitalservice.ris.search.controller.api;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.mapper.LegislationExpressionSearchSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.NormSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.NormSearchResponseMapper;
import de.bund.digitalservice.ris.search.mapper.SortParamsConverter;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSortParam;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionSearchSchema;
import de.bund.digitalservice.ris.search.schema.LegislationWorkSchema;
import de.bund.digitalservice.ris.search.schema.LegislationWorkSearchSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.service.NormsService;
import de.bund.digitalservice.ris.search.service.xslt.NormXsltTransformerService;
import de.bund.digitalservice.ris.search.utils.LuceneQueryTools;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import de.bund.digitalservice.ris.search.utils.eli.ManifestationEli;
import de.bund.digitalservice.ris.search.utils.eli.WorkEli;
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
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/** Controller class for handling REST API requests for legislation. */
@RestController
@Tag(
    name = "Legislation",
    description =
        """
        Retrieve current and historical versions of laws and decrees.
        The endpoints operate on the levels of "work", "expression", and "manifestation". See
        <a href="https://en.wikipedia.org/wiki/Functional_Requirements_for_Bibliographic_Records">Functional
        Requirements for Bibliographic Records (FRBR)</a> for more information.
        """)
public class NormsController {
  private static final String HTML_FILE_NOT_FOUND = "<div>LegalDocML file not found</div>";

  public static final String BUND_DESCRIPTION = "Country or regional code for the jurisdiction";
  public static final String BUND_EXAMPLE = "bund";
  public static final String AGENT_DESCRIPTION =
      "Agent or authority issuing the legislation, e.g., 'bgbl-1' for Bundesgesetzblatt Teil I (Federal Law Gazette part I)";
  public static final String AGENT_EXAMPLE = "bgbl-1";
  public static final String YEAR_DESCRIPTION = "Year the legislation was enacted or published";
  public static final String YEAR_EXAMPLE = "1979";
  public static final String NATURAL_IDENTIFIER_DESCRIPTION =
      "Unique natural identifier for the legislation, specific to the jurisdiction and agent";
  public static final String NATURAL_IDENTIFIER_EXAMPLE = "s1325";

  private final NormsService normsService;
  private final NormXsltTransformerService xsltTransformerService;

  /**
   * Constructor for the NormsController class.
   *
   * @param normsService the service responsible for handling norms-related operations
   * @param xsltTransformerService the service responsible for transforming norms using XSLT
   */
  @Autowired
  public NormsController(
      NormsService normsService, NormXsltTransformerService xsltTransformerService) {
    this.normsService = normsService;
    this.xsltTransformerService = xsltTransformerService;
  }

  /**
   * Searches and filters legislation records based on provided parameters, such as search terms,
   * temporal coverage, and sorting and pagination options. It returns the filtered and paginated
   * collection of legislation records.
   *
   * @param normsSearchParams the search parameters specific to norms
   * @param universalSearchParams general search parameters applicable to all document kinds
   * @param pagination the pagination parameters defining page size and index
   * @param sortParams the sorting parameters for ordering the results
   * @return a filtered and paginated collection of legislation records encapsulated as {@code
   *     CollectionSchema<SearchMemberSchema<LegislationWorkSearchSchema>>}
   * @throws CustomValidationException if the provided search parameters fail validation
   */
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

                      Get all of the legislation that belong to the work eli `eli/bund/bgbl-1/1979/s1325`
                      ```http request
                      GET /v1/legislation?eli=eli/bund/bgbl-1/1979/s1325
                      ```
                      """)
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "422")
  public CollectionSchema<SearchMemberSchema<LegislationWorkSearchSchema>> searchAndFilter(
      @ParameterObject NormsSearchParams normsSearchParams,
      @ParameterObject UniversalSearchParams universalSearchParams,
      @ParameterObject @Valid PaginationParams pagination,
      @ParameterObject @Valid NormsSortParam sortParams)
      throws CustomValidationException {

    normsSearchParams.validate();

    var pageRequest = PageRequest.of(pagination.getPageIndex(), pagination.getSize());
    var sortedPageRequest =
        pageRequest.withSort(SortParamsConverter.buildSort(sortParams.getSort()));

    try {
      SearchPage<Norm> resultPage =
          normsService.simpleSearchNorms(
              universalSearchParams, normsSearchParams, sortedPageRequest);
      return NormSearchResponseMapper.fromDomain(resultPage, ApiConfig.Paths.LEGISLATION);
    } catch (UncategorizedElasticsearchException e) {
      LuceneQueryTools.checkForInvalidQuery(e);
      throw e;
    }
  }

  /**
   * Retrieves the work and expression-level metadata of a legislation item.
   *
   * @param jurisdiction the jurisdiction to which the legal document belongs
   * @param agent the agent responsible for the legal document
   * @param year the year of issuance for the legal document
   * @param naturalIdentifier an identifier for the legal document
   * @param pointInTime the point in time representing the start of the validity of the document
   * @param version the version of the document
   * @param language the language of the document
   * @return A {@code ResponseEntity} containing the {@code LegislationWorkSchema} with metadata if
   *     found, or a {@code ResponseEntity} with a 404 status if no matching legislation is found.
   */
  @GetMapping(
      path =
          ApiConfig.Paths.LEGISLATION_SINGLE
              + "/{jurisdiction}/{agent}/{year}/{naturalIdentifier}/{pointInTime}/{version}/{language}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Work and expression-level metadata",
      description =
          "Returns the work and expression-level (\"workExample\") metadata of a legislation item.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content)
  public ResponseEntity<LegislationWorkSchema> getLegislation(
      @Parameter(description = BUND_DESCRIPTION, schema = @Schema(allowableValues = {BUND_EXAMPLE}))
          @PathVariable
          String jurisdiction,
      @Parameter(description = AGENT_DESCRIPTION, example = AGENT_EXAMPLE) @PathVariable
          String agent,
      @Parameter(description = YEAR_DESCRIPTION, example = YEAR_EXAMPLE) @PathVariable String year,
      @Parameter(description = NATURAL_IDENTIFIER_DESCRIPTION, example = NATURAL_IDENTIFIER_EXAMPLE)
          @PathVariable
          String naturalIdentifier,
      @Parameter(example = "2020-06-19") @PathVariable LocalDate pointInTime,
      @Parameter(example = "2") @PathVariable Integer version,
      @Parameter(example = "deu") @PathVariable String language) {

    var eli =
        new ExpressionEli(
            jurisdiction, agent, year, naturalIdentifier, pointInTime, version, language);
    Optional<Norm> result = normsService.getByExpressionEli(eli);

    return result
        .map(r -> ResponseEntity.ok(NormSchemaMapper.fromDomain(r)))
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Retrieves all expression level metadata for a given workEli
   *
   * @param jurisdiction the jurisdiction to which the legal document belongs
   * @param agent the agent responsible for the legal document
   * @param year the year of issuance for the legal document
   * @param naturalIdentifier an identifier for the legal document
   * @param pagination the pagination parameters defining page size and index
   * @return a paginated collection {@link CollectionSchema} of expression level metadata {@link
   *     LegislationExpressionSearchSchema}
   */
  @GetMapping(
      path =
          ApiConfig.Paths.LEGISLATION_WORK_EXAMPLE
              + "/{jurisdiction}/{agent}/{year}/{naturalIdentifier}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Retrieves expression level metadata for a given work eli",
      description =
          "Returns the expression-level (\"workExample\") metadata of a legislation item.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content)
  public CollectionSchema<LegislationExpressionSearchSchema> getWorkExamples(
      @Parameter(description = BUND_DESCRIPTION, schema = @Schema(allowableValues = {BUND_EXAMPLE}))
          @PathVariable
          String jurisdiction,
      @Parameter(description = AGENT_DESCRIPTION, example = AGENT_EXAMPLE) @PathVariable
          String agent,
      @Parameter(description = YEAR_DESCRIPTION, example = YEAR_EXAMPLE) @PathVariable String year,
      @Parameter(description = NATURAL_IDENTIFIER_DESCRIPTION, example = NATURAL_IDENTIFIER_EXAMPLE)
          @PathVariable
          String naturalIdentifier,
      @ParameterObject @Valid PaginationParams pagination) {
    WorkEli eli = new WorkEli(jurisdiction, agent, year, naturalIdentifier);
    Page<Norm> expressions =
        normsService.getWorkExpressions(
            eli, PageRequest.of(pagination.getPageIndex(), pagination.getSize()));

    return LegislationExpressionSearchSchemaMapper.fromNormsPage(
        expressions, ApiConfig.Paths.LEGISLATION_WORK_EXAMPLE);
  }

  /**
   * Retrieves a specific manifestation of a piece of legislation as HTML.
   *
   * <p>This method fetches and converts the requested legislation data into an HTML representation
   * based on the specified parameters.
   *
   * @param jurisdiction the jurisdiction to which the legal document belongs
   * @param agent the agent responsible for the legal document
   * @param year the year of issuance for the legal document
   * @param naturalIdentifier an identifier for the legal document
   * @param pointInTime the point in time representing the start of the validity of the document
   * @param version the version of the document
   * @param language the language of the document
   * @param pointInTimeManifestation the point in time the manifestation was generated
   * @param subtype the subtype of the document
   * @return A {@link ResponseEntity} containing the HTML representation of the requested
   *     legislation if found, or a 404 error if not found.
   * @throws ObjectStoreServiceException If an error occurs during the process of retrieving or
   *     transforming the legislation.
   */
  @GetMapping(
      path =
          ApiConfig.Paths.LEGISLATION_SINGLE
              + "/{jurisdiction}/{agent}/{year}/{naturalIdentifier}/{pointInTime}/{version}/{language}/{pointInTimeManifestation}/{subtype}.html",
      produces = MediaType.TEXT_HTML_VALUE)
  @Operation(
      summary = "Manifestation HTML",
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
      @Parameter(
              description = BUND_DESCRIPTION,
              example = BUND_EXAMPLE,
              schema = @Schema(allowableValues = {BUND_EXAMPLE}))
          @PathVariable
          String jurisdiction,
      @Parameter(description = AGENT_DESCRIPTION, example = AGENT_EXAMPLE) @PathVariable
          String agent,
      @Parameter(description = YEAR_DESCRIPTION, example = YEAR_EXAMPLE) @PathVariable String year,
      @Parameter(description = NATURAL_IDENTIFIER_DESCRIPTION, example = NATURAL_IDENTIFIER_EXAMPLE)
          @PathVariable
          String naturalIdentifier,
      @Parameter(example = "2020-06-19") @PathVariable LocalDate pointInTime,
      @Parameter(example = "2") @PathVariable Integer version,
      @Parameter(example = "deu") @PathVariable String language,
      @Parameter(example = "2020-06-19") @PathVariable LocalDate pointInTimeManifestation,
      @Parameter(example = "regelungstext-1") @PathVariable String subtype)
      throws ObjectStoreServiceException {
    final String resourceBasePath = getResourceBasePath();
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
            subtype,
            "xml");
    final Optional<byte[]> normFileByEli = normsService.getNormFileByEli(eli);
    if (normFileByEli.isPresent()) {
      final String body =
          xsltTransformerService.transformNorm(normFileByEli.get(), language, resourceBasePath);
      return ResponseEntity.ok(body);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HTML_FILE_NOT_FOUND);
    }
  }

  /**
   * Retrieves a specific manifestation of legislation in XML format based on the provided
   * parameters.
   *
   * @param jurisdiction the jurisdiction to which the legal document belongs
   * @param agent the agent responsible for the legal document
   * @param year the year of issuance for the legal document
   * @param naturalIdentifier an identifier for the legal document
   * @param pointInTime the point in time representing the start of the validity of the document
   * @param version the version of the document
   * @param language the language of the document
   * @param pointInTimeManifestation the point in time the manifestation was generated
   * @param subtype the subtype of the document
   * @return a ResponseEntity containing the XML representation of the requested legal document, or
   *     a 404 response if not found
   * @throws ObjectStoreServiceException if there is an issue retrieving the document from storage
   */
  @GetMapping(
      path =
          ApiConfig.Paths.LEGISLATION_SINGLE
              + "/{jurisdiction}/{agent}/{year}/{naturalIdentifier}/{pointInTime}/{version}/{language}/{pointInTimeManifestation}/{subtype}.xml",
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Manifestation XML",
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
      @Parameter(
              description = BUND_DESCRIPTION,
              example = BUND_EXAMPLE,
              schema = @Schema(allowableValues = {BUND_EXAMPLE}))
          @PathVariable
          String jurisdiction,
      @Parameter(description = AGENT_DESCRIPTION, example = AGENT_EXAMPLE) @PathVariable
          String agent,
      @PathVariable @Parameter(description = YEAR_DESCRIPTION, example = YEAR_EXAMPLE) String year,
      @Parameter(description = NATURAL_IDENTIFIER_DESCRIPTION, example = NATURAL_IDENTIFIER_EXAMPLE)
          @PathVariable
          String naturalIdentifier,
      @Parameter(example = "2020-06-19") @PathVariable LocalDate pointInTime,
      @Parameter(example = "2") @PathVariable Integer version,
      @Parameter(example = "deu") @PathVariable String language,
      @Parameter(example = "2020-06-19") @PathVariable LocalDate pointInTimeManifestation,
      @Parameter(example = "regelungstext-1") @PathVariable String subtype)
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
            subtype,
            "xml");

    return normsService
        .getNormFileByEli(eli)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Returns a particular manifestation of a piece of legislation, including attachments, as a ZIP
   * archive.
   *
   * @param jurisdiction the jurisdiction to which the legal document belongs
   * @param agent the agent responsible for the legal document
   * @param year the year of issuance for the legal document
   * @param naturalIdentifier an identifier for the legal document
   * @param pointInTime the point in time representing the start of the validity of the document
   * @param version the version of the document
   * @param language the language of the document
   * @param pointInTimeManifestation the point in time the manifestation was generated
   * @return a {@link ResponseEntity} containing a {@link StreamingResponseBody} with the ZIP file
   *     if the relevant data exists; otherwise, a 404 NOT FOUND response is returned
   */
  @GetMapping(
      path =
          ApiConfig.Paths.LEGISLATION_SINGLE
              + "/{jurisdiction}/{agent}/{year}/{naturalIdentifier}/{pointInTime}/{version}/{language}/{pointInTimeManifestation}.zip",
      produces = "application/zip")
  @Operation(
      summary = "Manifestation ZIP (XML and attachments)",
      description =
          """
            Returns a particular manifestation of a piece of legislation, including attachments, as a ZIP archive.
            Note the omission of the subtype path parameter.
            """)
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content(schema = @Schema()))
  public ResponseEntity<StreamingResponseBody> getLegislationSubtypeAsZip(
      @Parameter(
              description = BUND_DESCRIPTION,
              example = BUND_EXAMPLE,
              schema = @Schema(allowableValues = {BUND_EXAMPLE}))
          @PathVariable
          String jurisdiction,
      @Parameter(description = AGENT_DESCRIPTION, example = AGENT_EXAMPLE) @PathVariable
          String agent,
      @PathVariable @Parameter(description = YEAR_DESCRIPTION, example = YEAR_EXAMPLE) String year,
      @Parameter(description = NATURAL_IDENTIFIER_DESCRIPTION, example = NATURAL_IDENTIFIER_EXAMPLE)
          @PathVariable
          String naturalIdentifier,
      @Parameter(example = "2020-06-19") @PathVariable LocalDate pointInTime,
      @Parameter(example = "2") @PathVariable Integer version,
      @Parameter(example = "deu") @PathVariable String language,
      @Parameter(example = "2020-06-19") @PathVariable LocalDate pointInTimeManifestation) {
    String prefix =
        String.join(
            "/",
            "eli",
            jurisdiction,
            agent,
            year,
            naturalIdentifier,
            pointInTime.toString(),
            version.toString(),
            language,
            pointInTimeManifestation.toString());

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

  /**
   * Retrieves a specific article (§) of a particular manifestation of a piece of legislation and
   * returns it as an HTML representation.
   *
   * @param jurisdiction the jurisdiction to which the legal document belongs
   * @param agent the agent responsible for the legal document
   * @param year the year of issuance for the legal document
   * @param naturalIdentifier an identifier for the legal document
   * @param pointInTime the point in time representing the start of the validity of the document
   * @param version the version of the document
   * @param language the language of the document
   * @param pointInTimeManifestation the point in time the manifestation was generated
   * @param subtype the subtype of the document
   * @param articleEid The identifier that denotes the specific article (§) within the legislation.
   * @return A {@link ResponseEntity} containing the HTML representation of the requested article
   *     (§), or a "404 Not Found" response if the article cannot be found.
   * @throws ObjectStoreServiceException If an error occurs while accessing the legislation data or
   *     transforming it.
   */
  @GetMapping(
      path =
          ApiConfig.Paths.LEGISLATION_SINGLE
              + "/{jurisdiction}/{agent}/{year}/{naturalIdentifier}/{pointInTime}/{version}/{language}/{pointInTimeManifestation}/{subtype}/{articleEid}.html",
      produces = MediaType.TEXT_HTML_VALUE)
  @Operation(
      summary = "Manifestation article (§) HTML",
      description =
          """
              Returns a specific article (§) of particular manifestation of a piece of legislation, converted to HTML.
              """)
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content())
  public ResponseEntity<String> getLegislationArticleAsHtml(
      @Parameter(
              description = BUND_DESCRIPTION,
              example = BUND_EXAMPLE,
              schema = @Schema(allowableValues = {BUND_EXAMPLE}))
          @PathVariable
          String jurisdiction,
      @Parameter(description = AGENT_DESCRIPTION, example = AGENT_EXAMPLE) @PathVariable
          String agent,
      @Parameter(description = YEAR_DESCRIPTION, example = YEAR_EXAMPLE) @PathVariable String year,
      @Parameter(description = NATURAL_IDENTIFIER_DESCRIPTION, example = NATURAL_IDENTIFIER_EXAMPLE)
          @PathVariable
          String naturalIdentifier,
      @Parameter(example = "2020-06-19") @PathVariable LocalDate pointInTime,
      @PathVariable @Parameter(example = "2") Integer version,
      @PathVariable @Parameter(example = "deu") String language,
      @PathVariable @Parameter(example = "2020-06-19") LocalDate pointInTimeManifestation,
      @Parameter(example = "regelungstext-1") @PathVariable String subtype,
      @Parameter(
              description = "The expression identifier, denoting elements inside an expression",
              example = "art-z1")
          @PathVariable
          String articleEid)
      throws ObjectStoreServiceException {
    final String resourceBasePath = getResourceBasePath();

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
            subtype,
            "xml");
    final Optional<byte[]> normFileByEli = normsService.getNormFileByEli(eli);
    return normFileByEli
        .map(
            bytes ->
                ResponseEntity.ok(
                    xsltTransformerService.transformArticle(bytes, articleEid, resourceBasePath)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Retrieves a specific resource related to a particular manifestation of a piece of legislation.
   * This resource is identified by several path parameters, including jurisdiction, agent, year,
   * natural identifier, point in time, version, language, and extension, among others.
   *
   * @param jurisdiction the jurisdiction to which the legal document belongs
   * @param agent the agent responsible for the legal document
   * @param year the year of issuance for the legal document
   * @param naturalIdentifier an identifier for the legal document
   * @param pointInTime the point in time representing the start of the validity of the document
   * @param version the version of the document
   * @param language the language of the document
   * @param pointInTimeManifestation the point in time the manifestation was generated
   * @param name the name of the target resource
   * @param extension the file extension of the resource (e.g., pdf, xml, jpg, gif)
   * @return a ResponseEntity containing the file as a byte array if found, with the appropriate
   *     content type set in the headers. If the resource is not found or the extension is invalid,
   *     returns a 404 (not found) response.
   * @throws ObjectStoreServiceException if an error occurs while accessing the object store service
   */
  @GetMapping(
      path =
          ApiConfig.Paths.LEGISLATION_SINGLE
              + "/{jurisdiction}/{agent}/{year}/{naturalIdentifier}/{pointInTime}/{version}/{language}/{pointInTimeManifestation}/{name}.{extension}")
  @Operation(
      summary = "Manifestation resource",
      description =
          """
                            Returns a specific resource of a particular manifestation of a piece of legislation.
                            """)
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content())
  public ResponseEntity<byte[]> getFile(
      @Parameter(
              description = BUND_DESCRIPTION,
              example = BUND_EXAMPLE,
              schema = @Schema(allowableValues = {BUND_EXAMPLE}))
          @PathVariable
          String jurisdiction,
      @Parameter(description = AGENT_DESCRIPTION, example = AGENT_EXAMPLE) @PathVariable
          String agent,
      @Parameter(description = YEAR_DESCRIPTION, example = YEAR_EXAMPLE) @PathVariable String year,
      @Parameter(description = NATURAL_IDENTIFIER_DESCRIPTION, example = NATURAL_IDENTIFIER_EXAMPLE)
          @PathVariable
          String naturalIdentifier,
      @Parameter(example = "2020-06-19") @PathVariable LocalDate pointInTime,
      @Parameter(example = "2") @PathVariable Integer version,
      @Parameter(example = "deu") @PathVariable String language,
      @Parameter(example = "2020-06-19") @PathVariable LocalDate pointInTimeManifestation,
      @Parameter(example = "image") @PathVariable String name,
      @Parameter(example = "jpg", schema = @Schema(allowableValues = {"pdf", "xml", "jpg", "gif"}))
          @PathVariable
          String extension)
      throws ObjectStoreServiceException {

    final List<String> allowedExtensions = List.of("pdf", "xml", "jpg", "gif");
    if (!allowedExtensions.contains(extension.toLowerCase())) {
      return ResponseEntity.notFound().build();
    }

    final ManifestationEli eli =
        new ManifestationEli(
            jurisdiction,
            agent,
            year,
            naturalIdentifier,
            pointInTime,
            version,
            language,
            pointInTimeManifestation,
            name,
            extension);
    Optional<byte[]> file = normsService.getNormFileByEli(eli);

    final String mimeType = URLConnection.guessContentTypeFromName(name + "." + extension);

    return file.map(
            body ->
                ResponseEntity.status(HttpStatus.OK).header("Content-Type", mimeType).body(body))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Controls how resources like images will be referenced.
   *
   * @return The prefix to use when returning references to resources.
   */
  private String getResourceBasePath() {
    return ApiConfig.Paths.LEGISLATION + "/";
  }
}
