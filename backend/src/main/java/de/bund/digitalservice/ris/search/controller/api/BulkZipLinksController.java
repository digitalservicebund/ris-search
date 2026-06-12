package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.repository.objectstorage.PublicFilesBucket;
import de.bund.digitalservice.ris.search.schema.ZipDataCatalogSchema;
import de.bund.digitalservice.ris.search.service.BulkExportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
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

  private final PublicFilesBucket publicFilesBucket;
  private final String zipPrefix;
  private final String bucketUrl;

  /** Constructs a new instance of {@code BulkZipLinksController} */
  public BulkZipLinksController(
      PublicFilesBucket publicFilesBucket,
      @Value("${s3.file-storage.public-files.bucket-name}") String publicFilesBucketName,
      @Value("${s3.file-storage.public-files.endpoint}") String publicFilesBucketEndpoint) {
    this.publicFilesBucket = publicFilesBucket;
    this.zipPrefix = BulkExportService.BULK_ZIP_PREFIX;
    this.bucketUrl = publicFilesBucketEndpoint + "/" + publicFilesBucketName + "/";
  }

  /**
   * @return an instance of {@link ZipDataCatalogSchema} containing the bulk zip links.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ZipDataCatalogSchema getBulkZipLinks() {

    List<String> files =
        publicFilesBucket.getAllKeysByPrefix(zipPrefix).stream()
            .map(e -> e.substring(zipPrefix.length()))
            .toList();

    return new ZipDataCatalogSchema(
        getLatestWithPrefix(files, DocumentKind.ADMINISTRATIVE_DIRECTIVE.getBulkZipPath()),
        getLatestWithPrefix(files, DocumentKind.CASE_LAW.getBulkZipPath()),
        getLatestWithPrefix(files, DocumentKind.LEGISLATION.getBulkZipPath()),
        getLatestWithPrefix(files, DocumentKind.LITERATURE.getBulkZipPath()));
  }

  private String getLatestWithPrefix(List<String> toFilter, String prefix) {
    return toFilter.stream()
        .filter(e -> e.startsWith(prefix))
        .max(Comparator.naturalOrder())
        .map(e -> bucketUrl + zipPrefix + e)
        .orElse(null);
  }
}
