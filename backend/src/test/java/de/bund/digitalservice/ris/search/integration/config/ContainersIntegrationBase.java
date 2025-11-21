package de.bund.digitalservice.ris.search.integration.config;

import de.bund.digitalservice.ris.search.config.obs.TestMockS3Client;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.AdministrativeDirectiveTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.CaseLawTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.LiteratureTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.S3ObjectStorageClient;
import de.bund.digitalservice.ris.search.repository.opensearch.AdministrativeDirectiveRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.LiteratureRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.IteratorUtils;
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

  @Autowired protected CaseLawRepository caseLawRepository;
  @Autowired protected LiteratureRepository literatureRepository;
  @Autowired protected NormsRepository normsRepository;
  @Autowired protected CaseLawBucket caseLawBucket;
  @Autowired protected LiteratureBucket literatureBucket;
  @Autowired protected NormsBucket normsBucket;
  @Autowired protected AdministrativeDirectiveRepository administrativeDirectiveRepository;

  @Autowired
  @Qualifier("caseLawS3Client")
  private S3ObjectStorageClient caseLawS3Client;

  @Autowired
  @Qualifier("literatureS3Client")
  private S3ObjectStorageClient literatureS3Client;

  @Autowired
  @Qualifier("administrativeDirectiveS3Client")
  private S3ObjectStorageClient administrativeDirectiveS3Client;

  @Autowired
  @Qualifier("normS3Client")
  private S3ObjectStorageClient normS3Client;

  @Autowired
  @Qualifier("portalS3Client")
  private S3ObjectStorageClient portalS3Client;

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

  public void resetBuckets() {
    try {
      ((TestMockS3Client) caseLawS3Client.getS3Client()).loadDefaultFiles();
      ((TestMockS3Client) literatureS3Client.getS3Client()).loadDefaultFiles();
      ((TestMockS3Client) normS3Client.getS3Client()).loadDefaultFiles();
      ((TestMockS3Client) administrativeDirectiveS3Client.getS3Client()).loadDefaultFiles();
      for (var normFile : NormsTestData.allNormXml.entrySet()) {
        ((TestMockS3Client) normS3Client.getS3Client())
            .putFile(normFile.getKey(), normFile.getValue());
      }
      ((TestMockS3Client) portalS3Client.getS3Client()).loadDefaultFiles();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void resetRepositories() {
    clearRepositoryData();
    caseLawRepository.saveAll(CaseLawTestData.allDocuments);
    literatureRepository.saveAll(LiteratureTestData.allDocuments);
    normsRepository.saveAll(NormsTestData.allDocuments);
    administrativeDirectiveRepository.saveAll(AdministrativeDirectiveTestData.allDocuments);
  }

  public void clearRepositoryData() {
    caseLawRepository.deleteAll();
    literatureRepository.deleteAll();
    normsRepository.deleteAll();
    administrativeDirectiveRepository.deleteAll();
  }

  public void addNormXmlFiles(Map<String, String> files) {
    for (var normFile : files.entrySet()) {
      normsBucket.save(normFile.getKey(), normFile.getValue());
    }
  }

  protected List<String> getAllRepositoryEntityDates() {
    List<CaseLawDocumentationUnit> allCaseLaw =
        IteratorUtils.toList(caseLawRepository.findAll().iterator());
    List<Literature> allLiterature =
        IteratorUtils.toList(literatureRepository.findAll().iterator());
    List<Norm> allNorms = IteratorUtils.toList(normsRepository.findAll().iterator());

    List<String> result = new ArrayList<>();
    result.addAll(allCaseLaw.stream().map(e -> e.decisionDate().toString()).toList());
    result.addAll(allLiterature.stream().map(e -> e.firstPublicationDate().toString()).toList());
    result.addAll(allNorms.stream().map(e -> e.getEntryIntoForceDate().toString()).toList());
    return result;
  }
}
