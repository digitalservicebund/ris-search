package de.bund.digitalservice.ris.search.controller;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReindexingController {

  private final ImportService normsImportService;
  private final ImportService caselawImportService;

  @Autowired
  public ReindexingController(
      @Qualifier("normImportService") ImportService normImportService,
      @Qualifier("caselawImportService") ImportService caselawImportService) {
    this.normsImportService = normImportService;
    this.caselawImportService = caselawImportService;
  }

  @PostMapping(value = ApiConfig.Paths.SYNC_CASELAW)
  public ResponseEntity<Void> syncCaselawIndex() throws ObjectStoreServiceException {
    caselawImportService.lockAndProcessChangelogsAsync();
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = ApiConfig.Paths.SYNC_NORMS)
  public ResponseEntity<Void> syncNormIndex() throws ObjectStoreServiceException {
    normsImportService.lockAndProcessChangelogsAsync();
    return ResponseEntity.ok().build();
  }
}
