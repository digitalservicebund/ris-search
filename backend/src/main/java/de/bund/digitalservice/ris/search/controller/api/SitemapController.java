package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.SitemapService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
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
   * Retrieves a specific sitemap file for norms or caselaw based on the provided type and filename.
   *
   * @param docKind The type of the sitemap (e.g., "norms", "caselaw"). Determines the category of
   *     the sitemap.
   * @param filename The name of the sitemap file (e.g., "index" for the index sitemap or a batch
   *     number filename).
   * @return A ResponseEntity containing the sitemap file as a byte array if found. Returns HTTP 200
   *     (OK) if the file is available, or HTTP 404 (Not Found) if the file does not exist.
   * @throws ObjectStoreServiceException If an error occurs while retrieving the sitemap file from
   *     the object store.
   */
  @GetMapping(
      path = ApiConfig.Paths.SITEMAP + "/{docKind}/{filename}.xml",
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Get Norms and Caselaw Sitemap files",
      description =
          """
                              Return specific sitemap file for norms.

                              ## Example 1

                              Get the index sitemap for norms.
                              ```http request
                              GET /v1/sitemaps/norms/index.xml
                              ```
                              The API will return the index sitemap file for norms, which contains links to all
                              individual sitemap files for norms batched in 100 norms per file.

                              ## Example 2

                              Get a particular caselaw's batch sitemap file.
                              ```http request
                              GET /v1/sitemaps/caselaw/1.xml
                              ```
                              This call will return the first batch of caselaw, which contains the first 100 caselaw results
                              ```
                              """)
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404")
  public ResponseEntity<byte[]> getNormSitemapXml(
      @Parameter(
              description = "Type of sitemap files",
              example = "norms",
              schema = @Schema(allowableValues = {"case_law", "literature", "norms"}))
          @PathVariable
          String docKind,
      @Parameter(description = "SitemapFile Filename", example = "index") @PathVariable
          String filename)
      throws ObjectStoreServiceException {

    Optional<byte[]> file;

    DocumentKind sitemapType = DocumentKind.SITE_MAP_TO_DOC_KIND_MAP.get(docKind.toLowerCase());
    if (sitemapType == null) {
      return ResponseEntity.notFound().build();
    }
    if ("index".equals(filename)) {
      file = portalBucket.get(sitemapService.getIndexSitemapPath(sitemapType));
    } else {
      try {
        int batchNumber = Integer.parseInt(filename);
        file = portalBucket.get(sitemapService.getBatchSitemapPath(batchNumber, sitemapType));
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
