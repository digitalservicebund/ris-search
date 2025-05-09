package de.bund.digitalservice.ris.search.controller;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.service.CaseLawImportService;
import de.bund.digitalservice.ris.search.service.NormImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReindexingController {

  private final NormImportService normsImportService;
  private final CaseLawImportService caseLawImportService;

  @Autowired
  public ReindexingController(
      NormImportService normImportService, CaseLawImportService caseLawImportService) {
    this.normsImportService = normImportService;
    this.caseLawImportService = caseLawImportService;
  }

  @PostMapping(value = ApiConfig.Paths.SYNC_CASELAW)
  public ResponseEntity<Void> syncCaselawIndex() throws ObjectStoreServiceException {
    caseLawImportService.lockAndProcessChangelogsAsync();
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = ApiConfig.Paths.SYNC_NORMS)
  public ResponseEntity<Void> syncNormIndex() throws ObjectStoreServiceException {
    normsImportService.lockAndProcessChangelogsAsync();
    return ResponseEntity.ok().build();
  }
}
