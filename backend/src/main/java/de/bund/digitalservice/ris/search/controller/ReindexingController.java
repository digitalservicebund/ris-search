package de.bund.digitalservice.ris.search.controller;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.LiteratureIndexSyncJob;
import de.bund.digitalservice.ris.search.service.NormIndexSyncJob;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReindexingController {

  private final NormIndexSyncJob normIndexSyncJob;
  private final CaseLawIndexSyncJob caseLawIndexSyncJob;
  private final LiteratureIndexSyncJob literatureIndexSyncJob;

  @Autowired
  public ReindexingController(
      NormIndexSyncJob normIndexSyncJob,
      CaseLawIndexSyncJob caseLawIndexSyncJob,
      LiteratureIndexSyncJob literatureIndexSyncJob) {
    this.normIndexSyncJob = normIndexSyncJob;
    this.caseLawIndexSyncJob = caseLawIndexSyncJob;
    this.literatureIndexSyncJob = literatureIndexSyncJob;
  }

  @PostMapping(value = ApiConfig.Paths.SYNC_CASELAW)
  @Hidden
  public ResponseEntity<Void> syncCaselawIndex() {
    caseLawIndexSyncJob.runJobAsync();
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = ApiConfig.Paths.SYNC_NORMS)
  @Hidden
  public ResponseEntity<Void> syncNormIndex() {
    normIndexSyncJob.runJobAsync();
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = ApiConfig.Paths.SYNC_LITERATURE)
  @Hidden
  public ResponseEntity<Void> syncLiteratureIndex() {
    literatureIndexSyncJob.runJobAsync();
    return ResponseEntity.ok().build();
  }
}
