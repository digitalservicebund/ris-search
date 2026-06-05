package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.schema.BulkZipLinkSchema;
import de.bund.digitalservice.ris.search.schema.BulkZipLinksSchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** This controller provides an endpoint to get the name of the bulk zip for each document kind */
@Tag(
    name = "Document bulk download links",
    description = "Returns a link that can be used to download a bulk zip for each document kind.")
@RestController
@RequestMapping(ApiConfig.Paths.BULK_ZIP_LINKS)
public class BulkZipLinksController {

  /** Constructs a new instance of {@code BulkZipLinksController} */
  public BulkZipLinksController() {}

  /**
   * @return an instance of {@link BulkZipLinksSchema} containing the bulk zip links.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public BulkZipLinksSchema getBulkZipLinks() {

    return new BulkZipLinksSchema(
        new BulkZipLinkSchema(""),
        new BulkZipLinkSchema(""),
        new BulkZipLinkSchema(""),
        new BulkZipLinkSchema(""));
  }
}
