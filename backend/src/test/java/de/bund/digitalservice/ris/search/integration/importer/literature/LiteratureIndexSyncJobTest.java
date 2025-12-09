package de.bund.digitalservice.ris.search.integration.importer.literature;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import de.bund.digitalservice.ris.LoadXmlUtils;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.LiteratureRepository;
import de.bund.digitalservice.ris.search.service.LiteratureIndexSyncJob;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LiteratureIndexSyncJobTest extends ContainersIntegrationBase {

  @Autowired LiteratureBucket bucket;
  @Autowired PortalBucket portalBucket;
  @Autowired LiteratureIndexSyncJob syncJob;
  @Autowired LiteratureRepository repository;

  @BeforeEach
  void reset() {
    bucket.getAllKeys().forEach(bucket::delete);
    portalBucket.getAllKeys().forEach(portalBucket::delete);
    literatureRepository.deleteAll();
  }

  @Test
  void itIndexesAdministrativeDirectivesOnFullReindex() throws ObjectStoreServiceException {
    String dependent = LoadXmlUtils.loadXmlAsString(Literature.class, "XXLS000000001.akn.xml");
    bucket.save("XXLS000000001.akn.xml", dependent);

    String independent = LoadXmlUtils.loadXmlAsString(Literature.class, "XXLU000000001.akn.xml");
    bucket.save("XXLU000000001.akn.xml", independent);

    syncJob.runJob();
    await()
        .atMost(500, TimeUnit.MILLISECONDS)
        .until(() -> literatureRepository.findAll().iterator().hasNext());
    assertThat(repository.findById("XXLU000000001")).isNotEmpty();
    assertThat(repository.findById("XXLS000000001")).isNotEmpty();

    assertThat(portalBucket.getFileAsString(LiteratureIndexSyncJob.LITERATURE_STATUS_FILENAME))
        .isNotEmpty();
  }
}
