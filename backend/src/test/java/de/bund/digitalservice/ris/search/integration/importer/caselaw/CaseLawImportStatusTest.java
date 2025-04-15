package de.bund.digitalservice.ris.search.integration.importer.caselaw;

import de.bund.digitalservice.ris.search.caselawhandover.shared.CaseLawBucket;
import de.bund.digitalservice.ris.search.exception.RetryableObjectStoreException;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawSynthesizedRepository;
import de.bund.digitalservice.ris.search.service.ImportService;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.utils.CaseLawLdmlTemplateUtils;
import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import org.junit.AfterClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Tag("integration")
class CaseLawImportStatusTest extends ContainersIntegrationBase {
  @Autowired CaseLawSynthesizedRepository caseLawSynthesizedRepository;

  @Autowired CaseLawBucket caseLawBucket;
  @Autowired PortalBucket portalBucket;
  @Autowired IndexCaselawService indexCaselawService;
  @Autowired ImportService importService;
  @Autowired IndexStatusService indexStatusService;
  private final CaseLawLdmlTemplateUtils caseLawLdmlTemplateUtils = new CaseLawLdmlTemplateUtils();

  private final String oldTimestamp = "2000-01-01T00:00:00Z";

  @AfterEach
  void cleanUp() {
    portalBucket.delete(ImportService.CASELAW_LOCK_FILENAME);
  }

  @AfterClass
  public void cleanUpClass() {
    caseLawBucket.close();
    portalBucket.close();
  }

  @BeforeEach
  public void setUp() {
    for (int i = 0; i < 5; i++) {
      try {
        caseLawBucket.save(
            "caseLawTestLdml" + i + ".xml", caseLawLdmlTemplateUtils.getXmlFromTemplate(null));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    portalBucket.save(ImportService.CASELAW_LOCK_FILENAME, oldTimestamp);
    portalBucket.save(ImportService.CASELAW_LAST_SUCCESS_FILENAME, oldTimestamp);
  }

  @Test
  void createsLastSuccessFileProperly() throws RetryableObjectStoreException {
    Assertions.assertEquals(5, caseLawBucket.getAllFilenames().size());
    importService.importChangelogs(
        indexCaselawService,
        caseLawBucket,
        Instant.now(),
        ImportService.CASELAW_LAST_SUCCESS_FILENAME);
    Assertions.assertEquals(5, caseLawBucket.getAllFilenames().size());
    Assertions.assertTrue(
        portalBucket.getAllFilenames().contains(ImportService.CASELAW_LAST_SUCCESS_FILENAME));
    String lastSuccess = readValueFromFile(ImportService.CASELAW_LAST_SUCCESS_FILENAME);
    Assertions.assertTrue(isUTCDate(lastSuccess));
  }

  @Test
  void testLocking() throws RetryableObjectStoreException {
    Instant currentTime = Instant.now();
    boolean locked = indexStatusService.lockIndex(ImportService.CASELAW_LOCK_FILENAME, currentTime);
    Assertions.assertTrue(locked);
    String lockedAt = readValueFromFile(ImportService.CASELAW_LOCK_FILENAME);
    Assertions.assertEquals(currentTime.toString(), lockedAt);
    String lastSuccess = readValueFromFile(ImportService.CASELAW_LAST_SUCCESS_FILENAME);
    Assertions.assertEquals(oldTimestamp, lastSuccess);
  }

  private Boolean isUTCDate(String date) {
    try {
      String zone = ZonedDateTime.parse(date).getZone().toString();
      return "Z".equals(zone);
    } catch (DateTimeParseException e) {
      return false;
    }
  }

  private String readValueFromFile(String fileName) throws RetryableObjectStoreException {
    return portalBucket.getFileAsString(fileName).orElse(null);
  }
}
