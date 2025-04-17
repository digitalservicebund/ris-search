package de.bund.digitalservice.ris.search.controller;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.ImportService;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("!test")
public class ReindexingController {

  private final ImportService importService;
  private final IndexNormsService indexNormsService;
  private final IndexCaselawService indexCaselawService;
  private final NormsBucket normsBucket;
  private final CaseLawBucket caselawBucket;

  @Autowired
  public ReindexingController(
      ImportService importService,
      IndexNormsService indexNormsService,
      NormsBucket normsBucket,
      IndexCaselawService indexCaselawService,
      CaseLawBucket caselawBucket) {
    this.importService = importService;
    this.indexNormsService = indexNormsService;
    this.normsBucket = normsBucket;
    this.indexCaselawService = indexCaselawService;
    this.caselawBucket = caselawBucket;
  }

  @PostMapping(value = ApiConfig.Paths.REINDEX_CASELAW)
  public ResponseEntity<Void> reindexCaselaw() {
    importService.lockAndProcessChangelogsAsync(
        indexCaselawService,
        ImportService.CASELAW_LOCK_FILENAME,
        ImportService.CASELAW_LAST_SUCCESS_FILENAME,
        caselawBucket);
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = ApiConfig.Paths.REINDEX_NORMS)
  public ResponseEntity<Void> reindexNorms() {
    importService.lockAndProcessChangelogsAsync(
        indexNormsService,
        ImportService.NORM_LOCK_FILENAME,
        ImportService.NORM_LAST_SUCCESS_FILENAME,
        normsBucket);
    return ResponseEntity.ok().build();
  }
}
