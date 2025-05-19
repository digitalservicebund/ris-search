package de.bund.digitalservice.ris.search.controller;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.NormIndexSyncJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReindexingController {

  private final NormIndexSyncJob normIndexSyncJob;
  private final CaseLawIndexSyncJob caseLawIndexSyncJob;

  @Autowired
  public ReindexingController(
      NormIndexSyncJob normIndexSyncJob, CaseLawIndexSyncJob caseLawIndexSyncJob) {
    this.normIndexSyncJob = normIndexSyncJob;
    this.caseLawIndexSyncJob = caseLawIndexSyncJob;
  }

  @PostMapping(value = ApiConfig.Paths.SYNC_CASELAW)
  public ResponseEntity<Void> syncCaselawIndex() throws ObjectStoreServiceException {
    caseLawIndexSyncJob.runJobAsync();
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = ApiConfig.Paths.SYNC_NORMS)
  public ResponseEntity<Void> syncNormIndex() throws ObjectStoreServiceException {
    normIndexSyncJob.runJobAsync();
    return ResponseEntity.ok().build();
  }
}
