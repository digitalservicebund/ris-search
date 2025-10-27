package de.bund.digitalservice.ris.search.integration.config;

import de.bund.digitalservice.ris.search.config.obs.TestMockS3Client;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.CaseLawTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.LiteratureTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.repository.objectstorage.S3ObjectStorageClient;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.LiteratureRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.utility.TestcontainersConfiguration;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContainersIntegrationBase {

  @Autowired private CaseLawRepository caseLawRepository;
  @Autowired private LiteratureRepository literatureRepository;
  @Autowired private NormsRepository normsRepository;

  @Autowired
  @Qualifier("normS3Client")
  private S3ObjectStorageClient normS3Client;

  public static final CustomOpensearchContainer openSearchContainer =
      new CustomOpensearchContainer();

  static {
    TestcontainersConfiguration.getInstance()
        .updateUserConfig("testcontainers.reuse.enable", "true");
    openSearchContainer.withReuse(true);
    openSearchContainer.start();
  }

  @DynamicPropertySource
  static void registerDynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("opensearch.port", openSearchContainer::getFirstMappedPort);
  }

  @BeforeAll
  void reset() {
    resetBuckets();
    resetRepositories();
  }

  public void clearBuckets() {
    ((TestMockS3Client) normS3Client.getS3Client()).removeAllFiles();
  }

  public void resetBuckets() {
    try {
      ((TestMockS3Client) normS3Client.getS3Client()).loadDefaultFiles();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void resetRepositories() {
    clearRepositoryData();
    caseLawRepository.saveAll(CaseLawTestData.allDocuments);
    literatureRepository.saveAll(LiteratureTestData.allDocuments);
    normsRepository.saveAll(NormsTestData.allDocuments);
  }

  public void clearRepositoryData() {
    caseLawRepository.deleteAll();
    literatureRepository.deleteAll();
    normsRepository.deleteAll();
  }
}
