package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
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
  public final NormsBucket normsBucket;
  public final SitemapService sitemapService;

  @Autowired
  public SitemapController(SitemapService sitemapService, NormsBucket normsBucket) {
    this.normsBucket = normsBucket;
    this.sitemapService = sitemapService;
  }

  @GetMapping(
      path = ApiConfig.Paths.SITEMAP_NORMS + "/{filename}.xml",
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Get Norms Sitemap files",
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

                              Get a particular norms' batch sitemap file.
                              ```http request
                              GET /v1/sitemaps/norms/1.xml
                              ```
                              This call will return the first batch of norms, which contains the first 100 norms
                              ```
                              """)
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404")
  public ResponseEntity<byte[]> getNormSitemapXml(
      @Parameter(description = "Sitemap Filename", example = "index") @PathVariable String filename)
      throws ObjectStoreServiceException {

    Optional<byte[]> file;

    if (filename.equals("index")) {
      file = normsBucket.get(sitemapService.getNormsIndexSitemapPath());
    } else {
      try {
        int batchNumber = Integer.parseInt(filename);
        file = normsBucket.get(sitemapService.getNormsBatchSitemapPath(batchNumber));
      } catch (NumberFormatException e) {
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
