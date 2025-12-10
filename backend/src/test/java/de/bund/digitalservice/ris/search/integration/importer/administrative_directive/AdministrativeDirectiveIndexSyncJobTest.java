package de.bund.digitalservice.ris.search.integration.importer.administrative_directive;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.LoadXmlUtils;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.repository.objectstorage.AdministrativeDirectiveBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.AdministrativeDirectiveRepository;
import de.bund.digitalservice.ris.search.service.AdministrativeDirectiveIndexSyncJob;
import de.bund.digitalservice.ris.search.service.IndexingState;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Tag("integration")
class AdministrativeDirectiveIndexSyncJobTest extends ContainersIntegrationBase {
  @Autowired AdministrativeDirectiveBucket bucket;
  @Autowired PortalBucket portalBucket;
  @Autowired AdministrativeDirectiveIndexSyncJob syncJob;
  @Autowired AdministrativeDirectiveRepository repository;

  @BeforeEach
  void reset() {
    bucket.getAllKeys().forEach(bucket::delete);
    portalBucket.getAllKeys().forEach(portalBucket::delete);
    administrativeDirectiveRepository.deleteAll();
  }

  @Test
  void itIndexesAdministrativeDirectivesOnFullReindex() throws ObjectStoreServiceException {
    String content =
        LoadXmlUtils.loadXmlAsString(AdministrativeDirective.class, "KSNR0000.akn.xml");

    bucket.save("KSNR0000.akn.xml", content);
    syncJob.runJob();
    await()
        .atMost(500, TimeUnit.MILLISECONDS)
        .until(() -> administrativeDirectiveRepository.findAll().iterator().hasNext());
    AdministrativeDirective expected = repository.findAll().iterator().next();
    assertThat(expected.documentNumber()).isEqualTo("KSNR0000");
    assertThat(portalBucket.getFileAsString(AdministrativeDirectiveIndexSyncJob.STATUS_FILENAME))
        .isNotEmpty();
  }

  @Test
  void itIndexesAdministrativeDirectivesOnChangelog()
      throws JsonProcessingException, ObjectStoreServiceException {
    // data
    String content =
        LoadXmlUtils.loadXmlAsString(AdministrativeDirective.class, "KSNR0000.akn.xml");
    bucket.save("KSNR0000.akn.xml", content);

    // changelog
    Changelog log = new Changelog();
    log.setChanged(new HashSet<>(List.of("KSNR0000.akn.xml")));
    ObjectMapper mapper = new ObjectMapper();
    bucket.save("changelogs/2025-07-12T13:34:34.392285Z.json", mapper.writeValueAsString(log));

    // previous statefile
    IndexingState state = new IndexingState("2025-07-12T13:34:34.392285Z.json", null, null);
    portalBucket.save(
        AdministrativeDirectiveIndexSyncJob.STATUS_FILENAME, mapper.writeValueAsString(state));

    assertThat(repository.findAll().iterator().hasNext()).isFalse();

    syncJob.runJob();
    await()
        .atMost(500, TimeUnit.MILLISECONDS)
        .until(() -> administrativeDirectiveRepository.findAll().iterator().hasNext());

    AdministrativeDirective expected = repository.findAll().iterator().next();
    assertThat(expected.documentNumber()).isEqualTo("KSNR0000");
    assertThat(portalBucket.getFileAsString(AdministrativeDirectiveIndexSyncJob.STATUS_FILENAME))
        .isNotEmpty();
  }
}
