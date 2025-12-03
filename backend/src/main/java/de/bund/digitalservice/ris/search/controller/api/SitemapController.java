package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.SitemapService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** Controller class for handling REST API requests for sitemaps. */
@RestController
@Hidden
@Tag(
    name = "Sitemap",
    description =
        """
        Retrieve sitemap files for norms and caselaw.
        The endpoints operates as a proxy for retrieving the sitemap files from the bucket, which were saved
        during the indexing time of norms and caselaw.
        """)
public class SitemapController {
  public final PortalBucket portalBucket;
  public final SitemapService sitemapService;

  /**
   * Constructs a SitemapController instance and initializes the required dependencies.
   *
   * @param sitemapService The service responsible for handling sitemap-related operations.
   * @param portalBucket The portal bucket interface used for retrieving sitemap files from the
   *     storage.
   */
  @Autowired
  public SitemapController(SitemapService sitemapService, PortalBucket portalBucket) {
    this.portalBucket = portalBucket;
    this.sitemapService = sitemapService;
  }

  /**
   * Retrieves a specific sitemap file for case law based on the provided type and filename.
   *
   * @param filename The name of the sitemap file (e.g., "index" for the index sitemap or a batch
   *     number filename).
   * @return A ResponseEntity containing the sitemap file as a byte array if found. Returns HTTP 200
   *     (OK) if the file is available, or HTTP 404 (Not Found) if the file does not exist.
   * @throws ObjectStoreServiceException If an error occurs while retrieving the sitemap file from
   *     the object store.
   */
  @GetMapping(
      path = ApiConfig.Paths.SITEMAP + "/case-law/{filename}.xml",
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Get case law sitemap files",
      description =
          """
                                          Get the index sitemap for case law.
                                          ## Example 1
                                          Get the sitemap file listing all the case law sitemap files:
                                          ```http request
                                          GET /v1/sitemaps/case-law/index.xml```

                                          ## Example 2
                                          Get the content of one sitemap file
                                          ```http request
                                          GET /v1/sitemaps/case-law/1.xml
                                          ```
                                          """)
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404")
  public ResponseEntity<byte[]> getCaseLawSitemapXml(
      @Parameter(description = "SitemapFile Filename", example = "index") @PathVariable
          String filename)
      throws ObjectStoreServiceException {

    return getSitemapXml(filename, DocumentKind.CASE_LAW);
  }

  /**
   * Retrieves a specific sitemap file for literature based on the provided type and filename.
   *
   * @param filename The name of the sitemap file (e.g., "index" for the index sitemap or a batch
   *     number filename).
   * @return A ResponseEntity containing the sitemap file as a byte array if found. Returns HTTP 200
   *     (OK) if the file is available, or HTTP 404 (Not Found) if the file does not exist.
   * @throws ObjectStoreServiceException If an error occurs while retrieving the sitemap file from
   *     the object store.
   */
  @GetMapping(
      path = ApiConfig.Paths.SITEMAP + "/literature/{filename}.xml",
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Get literature sitemap files",
      description =
          """
                                          Get the index sitemap for literature.
                                          ## Example 1
                                          Get the sitemap file listing all the literature sitemap files:
                                          ```http request
                                          GET /v1/sitemaps/literature/index.xml```

                                          ## Example 2
                                          Get the content of one sitemap file
                                          ```http request
                                          GET /v1/sitemaps/literature/1.xml
                                          ```
                                          """)
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404")
  public ResponseEntity<byte[]> getLiteratureSitemapXml(
      @Parameter(description = "SitemapFile Filename", example = "index") @PathVariable
          String filename)
      throws ObjectStoreServiceException {

    return getSitemapXml(filename, DocumentKind.LITERATURE);
  }

  /**
   * Retrieves a specific sitemap file for norms based on the provided type and filename.
   *
   * @param filename The name of the sitemap file (e.g., "index" for the index sitemap or a batch
   *     number filename).
   * @return A ResponseEntity containing the sitemap file as a byte array if found. Returns HTTP 200
   *     (OK) if the file is available, or HTTP 404 (Not Found) if the file does not exist.
   * @throws ObjectStoreServiceException If an error occurs while retrieving the sitemap file from
   *     the object store.
   */
  @GetMapping(
      path = ApiConfig.Paths.SITEMAP + "/norms/{filename}.xml",
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Get norm sitemap files",
      description =
          """
                                      Get the index sitemap for norms.
                                      ## Example 1
                                      Get the sitemap file listing all the norms sitemap files:
                                      ```http request
                                      GET /v1/sitemaps/norms/index.xml```

                                      ## Example 2
                                      Get the content of one sitemap file
                                      ```http request
                                      GET /v1/sitemaps/norms/1.xml
                                      ```
                                      """)
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404")
  public ResponseEntity<byte[]> getNormSitemapXml(
      @Parameter(description = "SitemapFile Filename", example = "index") @PathVariable
          String filename)
      throws ObjectStoreServiceException {

    return getSitemapXml(filename, DocumentKind.LEGISLATION);
  }

  private ResponseEntity<byte[]> getSitemapXml(String filename, DocumentKind documentKind)
      throws ObjectStoreServiceException {

    Optional<byte[]> file;

    if ("index".equals(filename)) {
      file = portalBucket.get(sitemapService.getIndexSitemapPath(documentKind));
    } else {
      try {
        int batchNumber = Integer.parseInt(filename);
        file = portalBucket.get(sitemapService.getBatchSitemapPath(batchNumber, documentKind));
      } catch (NumberFormatException exception) {
        return ResponseEntity.notFound().build();
      }
    }

    return file.map(
            body ->
                ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", MediaType.APPLICATION_XML_VALUE)
                    .body(body))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
