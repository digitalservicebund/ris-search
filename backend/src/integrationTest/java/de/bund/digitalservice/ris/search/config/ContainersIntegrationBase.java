package de.bund.digitalservice.ris.search.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import de.bund.digitalservice.ris.TestJsonUtils;
import de.bund.digitalservice.ris.search.config.obs.CustomS3MockContainer;
import de.bund.digitalservice.ris.search.controller.api.testData.AdministrativeDirectiveTestData;
import de.bund.digitalservice.ris.search.controller.api.testData.CaseLawTestData;
import de.bund.digitalservice.ris.search.controller.api.testData.LiteratureTestData;
import de.bund.digitalservice.ris.search.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorageClient;
import de.bund.digitalservice.ris.search.repository.objectstorage.PublicFilesBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.AdministrativeDirectiveRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.ArticlesRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.DocumentRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.LiteratureRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import de.bund.digitalservice.ris.search.schema.TextMatchSchema;
import de.bund.digitalservice.ris.search.service.AdministrativeDirectiveService;
import de.bund.digitalservice.ris.search.service.AllDocumentsService;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.service.LiteratureService;
import de.bund.digitalservice.ris.search.service.NormsService;
import de.bund.digitalservice.ris.testmocks.PostHogTestConfig;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import org.apache.commons.collections4.IteratorUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

/**
 * Base class for integration tests that require OpenSearch and S3 buckets. It sets up the necessary
 * containers and provides methods to reset the state of the repositories and buckets before each
 * test run.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Import(PostHogTestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContainersIntegrationBase {

  // Autowire Persistence layer
  @Autowired protected AdministrativeDirectiveRepository administrativeDirectiveRepository;
  @MockitoSpyBean protected CaseLawRepository caseLawRepository;
  @Autowired protected LiteratureRepository literatureRepository;
  @Autowired protected NormsRepository normsRepository;
  @Autowired protected ArticlesRepository articlesRepository;
  @Autowired protected CaseLawBucket caseLawBucket;
  @Autowired protected NormsBucket normsBucket;
  @Autowired protected PublicFilesBucket publicFilesBucket;

  @Autowired
  @Qualifier("caseLawS3Client")
  private ObjectStorageClient caseLawS3Client;

  @Autowired
  @Qualifier("literatureS3Client")
  private ObjectStorageClient literatureS3Client;

  @Autowired
  @Qualifier("administrativeDirectiveS3Client")
  protected ObjectStorageClient administrativeDirectiveS3Client;

  @Autowired
  @Qualifier("normS3Client")
  private ObjectStorageClient normS3Client;

  @Autowired
  @Qualifier("portalS3Client")
  private ObjectStorageClient portalS3Client;

  @Autowired
  @Qualifier("publicFilesS3Client")
  private ObjectStorageClient publicFilesS3Client;

  // Autowire Service layer
  @Autowired protected AllDocumentsService allDocumentsService;
  @Autowired protected CaseLawService caseLawService;
  @Autowired protected NormsService normsService;
  @Autowired protected LiteratureService literatureService;
  @Autowired protected AdministrativeDirectiveService administrativeDirectiveService;

  // Setup Testcontainers
  public static final CustomOpensearchContainer openSearchContainer =
      new CustomOpensearchContainer();

  public static final CustomS3MockContainer s3Container = new CustomS3MockContainer();

  static {
    TestcontainersConfiguration.getInstance()
        .updateUserConfig("testcontainers.reuse.enable", "true");
    openSearchContainer.withReuse(true);
    openSearchContainer.start();
    s3Container.start();
  }

  @DynamicPropertySource
  static void registerDynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("opensearch.port", openSearchContainer::getFirstMappedPort);
    registry.add("s3mock.endpoint", s3Container::getHttpEndpoint);
  }

  // Shared cleanup

  @AfterAll
  protected void cleanup() {
    clearBuckets();
    clearRepositoryData();
  }

  protected void loadDefaultData() {
    loadDefaultEntites();
  }

  /** Clears all data from the buckets. */
  public void clearBuckets() {
    for (ObjectStorageClient client :
        List.of(
            caseLawS3Client,
            literatureS3Client,
            administrativeDirectiveS3Client,
            normS3Client,
            portalS3Client,
            publicFilesS3Client)) {
      client.listKeysByPrefix("").forEach(client::delete);
    }
  }

  /** Resets all repositories with the test data. */
  public void loadDefaultEntites() {
    caseLawRepository.saveAll(CaseLawTestData.allDocuments);
    literatureRepository.saveAll(LiteratureTestData.allDocuments);
    normsRepository.saveAll(NormsTestData.allNorms);
    articlesRepository.saveAll(NormsTestData.allArticles);
    administrativeDirectiveRepository.saveAll(AdministrativeDirectiveTestData.allDocuments);
  }

  /** Clears all data from the repositories. */
  public void clearRepositoryData() {
    caseLawRepository.deleteAll();
    literatureRepository.deleteAll();
    normsRepository.deleteAll();
    articlesRepository.deleteAll();
    administrativeDirectiveRepository.deleteAll();
  }

  // Shared helper methods

  /**
   * Adds the given norm XML files to the norms bucket.
   *
   * @param files A map where the key is the file name and the value is the file content.
   */
  public void addNormXmlFiles(Map<String, String> files) {
    for (var normFile : files.entrySet()) {
      normsBucket.save(normFile.getKey(), normFile.getValue());
    }
  }

  /**
   * Uploads a classpath resource to the given bucket.
   *
   * @param resourcePath absolute classpath location of the test file
   * @param objectKey key under which the file is stored in the bucket
   * @param storage the bucket to store the file in
   */
  public void addTestFileToBucket(String resourcePath, String objectKey, ObjectStorage storage)
      throws IOException {
    try (InputStream in = ContainersIntegrationBase.class.getResourceAsStream(resourcePath)) {
      byte[] content =
          Objects.requireNonNull(in, "Test resource not found: " + resourcePath).readAllBytes();
      storage.save(objectKey, content);
    }
  }

  protected List<String> getAllRepositoryEntityDates() {
    List<CaseLawDocumentationUnit> allCaseLaw =
        IteratorUtils.toList(caseLawRepository.findAll().iterator());
    List<Literature> allLiterature =
        IteratorUtils.toList(literatureRepository.findAll().iterator());
    List<Norm> allNorms = IteratorUtils.toList(normsRepository.findAll().iterator());
    List<AdministrativeDirective> directives =
        IteratorUtils.toList(administrativeDirectiveRepository.findAll().iterator());

    List<String> result = new ArrayList<>();
    result.addAll(allCaseLaw.stream().map(e -> e.decisionDate().toString()).toList());
    result.addAll(allLiterature.stream().map(e -> e.yearsOfPublication().getFirst()).toList());
    result.addAll(allNorms.stream().map(e -> e.getEntryIntoForceDate().toString()).toList());
    result.addAll(
        directives.stream()
            .map(
                e -> {
                  assert e.entryIntoEffectDate() != null;
                  return e.entryIntoEffectDate().toString();
                })
            .toList());
    return result;
  }

  protected <T> List<T> getAll(DocumentRepository<T> repo, Predicate<T> predicate) {
    return getAll(repo).stream().filter(predicate).toList();
  }

  protected <T> List<T> getAll(DocumentRepository<T> repo) {
    return repo.findAll(PageRequest.of(0, 1000)).get().toList();
  }

  protected List<CaseLawDocumentationUnit> searchCaseLaw(String searchTerm) {
    return searchCaseLawHit(searchTerm).get().map(SearchHit::getContent).toList();
  }

  protected SearchPage<CaseLawDocumentationUnit> searchCaseLawHit(String searchTerm) {
    return caseLawService.simpleSearchCaseLaw(
        UniversalSearchParams.builder().searchTerm(searchTerm).build(),
        null,
        Pageable.ofSize(10000));
  }

  protected SearchPage<Literature> searchLiteratureHit(String searchTerm) {
    return literatureService.simpleSearchLiterature(
        UniversalSearchParams.builder().searchTerm(searchTerm).build(),
        null,
        Pageable.ofSize(10000));
  }

  protected SearchPage<AdministrativeDirective> searchAdminHit(String searchTerm) {
    return administrativeDirectiveService.simpleSearch(
        UniversalSearchParams.builder().searchTerm(searchTerm).build(),
        null,
        Pageable.ofSize(10000));
  }

  protected List<TextMatchSchema> getTopHitTextMatches(MockMvc mockMvc, String url) {
    try {
      return TestJsonUtils.parseJsonResult(
              mockMvc
                  .perform(get(url).contentType(MediaType.APPLICATION_JSON))
                  .andReturn()
                  .getResponse()
                  .getContentAsString())
          .member()
          .getFirst()
          .textMatches();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected long allDocCount() {
    return administrativeDirectiveRepository.count()
        + caseLawRepository.count()
        + literatureRepository.count()
        + normsRepository.count();
  }
}
