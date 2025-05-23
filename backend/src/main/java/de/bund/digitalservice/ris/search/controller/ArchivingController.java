package de.bund.digitalservice.ris.search.controller;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.BulkExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("!test")
public class ArchivingController {

  private final NormsBucket normsBucket;
  private final BulkExportService bulkExportService;
  private final PortalBucket portalBucket;

  @Autowired
  public ArchivingController(
      NormsBucket normsBucket, BulkExportService bulkExportService, PortalBucket portalBucket) {
    this.normsBucket = normsBucket;
    this.bulkExportService = bulkExportService;
    this.portalBucket = portalBucket;
  }

  @PostMapping(value = ApiConfig.Paths.GENERATE_ARCHIVE)
  public ResponseEntity<Void> generateArchive() {
    bulkExportService.updateExportAsync(normsBucket, portalBucket, "norms", "eli/");
    return ResponseEntity.noContent().build();
  }
}
