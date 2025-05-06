package de.bund.digitalservice.ris.search.importer.e2e;

import de.bund.digitalservice.ris.search.exception.ObjectStoreException;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
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

@Profile("e2e")
@Component
public class EndToEndImporter {

  private static final Logger logger = LogManager.getLogger(EndToEndImporter.class);

  private final IndexCaselawService indexCaselawService;
  private final IndexNormsService normsImporter;

  @Autowired
  public EndToEndImporter(
      IndexCaselawService indexCaselawService, IndexNormsService normsImporter) {
    this.indexCaselawService = indexCaselawService;
    this.normsImporter = normsImporter;
  }

  @Async
  @EventListener(value = ApplicationReadyEvent.class)
  public void endToEndTrigger() throws ObjectStoreException {
    logger.info("Import E2E caselaw data: started");
    indexCaselawService.reindexAll(Instant.now().toString());
    logger.info("Import E2E caselaw data: done");

    logger.info("Import E2E norms data: started");
    normsImporter.reindexAll(
        ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
    logger.info("Import E2E norms data: done");
  }
}
