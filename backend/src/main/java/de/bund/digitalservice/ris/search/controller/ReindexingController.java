package de.bund.digitalservice.ris.search.controller;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.ImportService;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import de.bund.digitalservice.ris.search.service.IndexingState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
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

  @PostMapping(value = ApiConfig.Paths.SYNC_CASELAW)
  public ResponseEntity<Void> syncCaselawIndex() throws ObjectStoreServiceException {
    IndexingState state =
        new IndexingState(
            caselawBucket, ImportService.CASELAW_STATUS_FILENAME, indexCaselawService);
    importService.lockAndProcessChangelogsAsync(state);
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = ApiConfig.Paths.SYNC_NORMS)
  public ResponseEntity<Void> syncNormIndex() throws ObjectStoreServiceException {
    IndexingState state =
        new IndexingState(normsBucket, ImportService.NORM_STATUS_FILENAME, indexNormsService);
    importService.lockAndProcessChangelogsAsync(state);
    return ResponseEntity.ok().build();
  }
}
