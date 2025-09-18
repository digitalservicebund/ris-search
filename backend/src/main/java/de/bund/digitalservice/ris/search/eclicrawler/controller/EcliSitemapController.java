package de.bund.digitalservice.ris.search.eclicrawler.controller;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.eclicrawler.service.EcliSitemapService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
public class EcliSitemapController {
  public final EcliSitemapService sitemapService;

  @Autowired
  public EcliSitemapController(EcliSitemapService sitemapService) {
    this.sitemapService = sitemapService;
  }

  @GetMapping(
      path = ApiConfig.Paths.ECLICRAWLER + "/{year}/{month}/{day}/{filename}",
      produces = MediaType.APPLICATION_XML_VALUE)
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404")
  public ResponseEntity<byte[]> getEcliSitemapfiles(
      @PathVariable() String year,
      @PathVariable() String month,
      @PathVariable() String day,
      @PathVariable() String filename) {

    var file =
        sitemapService.getSitemapFile(String.format("%s/%s/%s/%s", year, month, day, filename));

    return file.map(
            body ->
                ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", MediaType.APPLICATION_XML_VALUE)
                    .body(body))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
