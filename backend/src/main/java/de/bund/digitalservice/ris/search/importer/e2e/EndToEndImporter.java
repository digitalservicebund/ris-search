package de.bund.digitalservice.ris.search.importer.e2e;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.service.IndexAdministrativeDirectiveService;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.service.IndexLiteratureService;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Component that triggers end-to-end import jobs when the application starts in the e2e profile.
 *
 * <p>It invokes caselaw, literature and norms reindex operations using the corresponding services.
 */
@Profile("e2e")
@Component
public class EndToEndImporter {

  private static final Logger logger = LogManager.getLogger(EndToEndImporter.class);

  private final IndexCaselawService indexCaselawService;
  private final IndexLiteratureService indexLiteratureService;
  private final IndexAdministrativeDirectiveService indexAdministrativeDirectiveService;
  private final IndexNormsService indexNormsService;

  /**
   * Construct an EndToEndImporter with required index services.
   *
   * @param indexCaselawService service used to reindex caselaw documents
   * @param indexLiteratureService service used to reindex literature documents
   * @param indexNormsService service used to reindex norms documents
   */
  @Autowired
  public EndToEndImporter(
      IndexCaselawService indexCaselawService,
      IndexLiteratureService indexLiteratureService,
      IndexAdministrativeDirectiveService indexAdministrativeDirectiveService,
      IndexNormsService indexNormsService) {
    this.indexCaselawService = indexCaselawService;
    this.indexLiteratureService = indexLiteratureService;
    this.indexAdministrativeDirectiveService = indexAdministrativeDirectiveService;
    this.indexNormsService = indexNormsService;
  }

  /**
   * Triggers the end-to-end data import and reindexing process when the application is ready.
   *
   * <p>This method is automatically executed when the Spring application fires the {@code
   * ApplicationReadyEvent}, ensuring the initiation of reindexing operations for caselaw,
   * literature, and norms. It invokes corresponding services to reindex all documents and logs the
   * progress and completion of each step.
   *
   * <p>The method performs the following tasks sequentially: 1. Reindexes all caselaw data using
   * the {@code IndexCaselawService}. 2. Reindexes all literature data using the {@code
   * IndexLiteratureService}. 3. Reindexes all norms data using the {@code IndexNormsService}.
   *
   * @throws ObjectStoreServiceException if an error occurs during the reindexing process.
   */
  @Async
  @EventListener(value = ApplicationReadyEvent.class)
  public void endToEndTrigger() throws ObjectStoreServiceException {
    logger.info("Import E2E caselaw data: started");
    indexCaselawService.reindexAll(Instant.now().toString());
    logger.info("Import E2E caselaw data: done");

    logger.info("Import E2E literature data: started");
    indexLiteratureService.reindexAll(Instant.now().toString());
    logger.info("Import E2E literature data: done");

    logger.info("Import E2E administrative directive data: started");
    indexAdministrativeDirectiveService.reindexAll(Instant.now().toString());
    logger.info("Import E2E administrative directive data: done");

    logger.info("Import E2E norms data: started");
    indexNormsService.reindexAll(
        ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
    logger.info("Import E2E norms data: done");
  }
}
