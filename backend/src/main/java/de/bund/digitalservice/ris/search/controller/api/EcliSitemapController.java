package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapWriter;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** Controller class for handling REST API requests for sitemaps. */
@RestController
@Validated
@Hidden
public class EcliSitemapController {
  public final EcliSitemapWriter sitemapService;

  public EcliSitemapController(EcliSitemapWriter sitemapService) {
    this.sitemapService = sitemapService;
  }

  @GetMapping(
      path = ApiConfig.Paths.ECLICRAWLER + "/robots.txt",
      produces = MediaType.TEXT_PLAIN_VALUE)
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404")
  public ResponseEntity<byte[]> getRobots() {

    return sitemapService
        .getRobots()
        .map(
            body ->
                ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", MediaType.TEXT_PLAIN_VALUE)
                    .body(body))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping(
      path = ApiConfig.Paths.ECLICRAWLER + "/{year}/{month}/{day}/{filename}",
      produces = MediaType.APPLICATION_XML_VALUE)
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404")
  public ResponseEntity<byte[]> getEcliSitemapfiles(
      @PathVariable() @Pattern(regexp = "\\d{4}") String year,
      @PathVariable() @Pattern(regexp = "\\d{2}") String month,
      @PathVariable() @Pattern(regexp = "\\d{2}") String day,
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
