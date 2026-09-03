package de.bund.digitalservice.ris.search.integration.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import de.bund.digitalservice.ris.TestJsonUtils;
import de.bund.digitalservice.ris.search.config.obs.TestMockS3Client;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.AdministrativeDirectiveTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.CaseLawTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.LiteratureTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PublicFilesBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.S3ObjectStorageClient;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.commons.collections4.IteratorUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

/**
 * Base class for integration tests that require OpenSearch and S3 buckets. It sets up the necessary
 * containers and provides methods to reset the state of the repositories and buckets before each
 * test run.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContainersIntegrationBase {

  @Autowired protected CaseLawRepository caseLawRepository;
  @Autowired protected LiteratureRepository literatureRepository;
  @Autowired protected NormsRepository normsRepository;
  @Autowired protected ArticlesRepository articlesRepository;
  @Autowired protected CaseLawBucket caseLawBucket;
  @Autowired protected NormsBucket normsBucket;
  @Autowired protected PublicFilesBucket publicFilesBucket;
  @Autowired protected AdministrativeDirectiveRepository administrativeDirectiveRepository;
  @Autowired protected AllDocumentsService allDocumentsService;
  @Autowired protected CaseLawService caseLawService;
  @Autowired protected NormsService normsService;
  @Autowired protected LiteratureService literatureService;
  @Autowired protected AdministrativeDirectiveService administrativeDirectiveService;

  @Autowired
  @Qualifier("caseLawS3Client")
  private S3ObjectStorageClient caseLawS3Client;

  @Autowired
  @Qualifier("literatureS3Client")
  private S3ObjectStorageClient literatureS3Client;

  @Autowired
  @Qualifier("administrativeDirectiveS3Client")
  protected S3ObjectStorageClient administrativeDirectiveS3Client;

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
  void beforeAllWrapper() {
    reset();
  }

  protected void reset() {
    resetBuckets();
    resetRepositories();
  }

  /** Resets all S3 buckets with the test data. */
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

  /** Resets all repositories with the test data. */
  public void resetRepositories() {
    clearRepositoryData();
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
    administrativeDirectiveRepository.deleteAll();
  }

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
    result.addAll(allLiterature.stream().map(e -> e.firstPublicationDate().toString()).toList());
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

  protected void saveSimpleNorm(String id, String content) {
    String expressionEli = "ExpressionPrefix" + id;
    String workEli = "WorkPrefix" + expressionEli;
    String articleName = "Article 1";
    normsRepository.save(
        Norm.builder()
            .id(expressionEli)
            .workEli(workEli)
            .expressionEli(expressionEli)
            .articleTexts(List.of(content))
            .articles(List.of(Article.builder().name(articleName).text(content).build()))
            .build());
    articlesRepository.save(
        Article.builder()
            .id(expressionEli + "/" + "eid1")
            .workEli(workEli)
            .expressionEli(expressionEli)
            .name(articleName)
            .text(content)
            .build());
  }

  protected <T> List<T> getAll(DocumentRepository<T> repo, Predicate<T> predicate) {
    return getAll(repo).stream().filter(predicate).toList();
  }

  protected <T> List<T> getAll(DocumentRepository<T> repo) {
    return repo.findAll(PageRequest.of(0, 1000)).get().toList();
  }

  protected List<AbstractSearchEntity> searchAll(String searchTerm) {
    return searchAllHit(searchTerm).get().map(SearchHit::getContent).toList();
  }

  protected SearchPage<AbstractSearchEntity> searchAllHit(String searchTerm) {
    return allDocumentsService.simpleSearchAllDocuments(
        UniversalSearchParams.builder().searchTerm(searchTerm).build(),
        Pageable.ofSize(10000),
        null);
  }

  protected List<Norm> searchNorms(String searchTerm) {
    return searchNormsHit(searchTerm).get().map(SearchHit::getContent).toList();
  }

  protected SearchPage<Norm> searchNormsHit(String searchTerm) {
    return searchNormsHit(searchTerm, null);
  }

  protected SearchPage<Norm> searchNormsHit(String searchTerm, NormsSearchParams normParams) {
    return normsService.simpleSearchNorms(
        UniversalSearchParams.builder().searchTerm(searchTerm).build(),
        normParams,
        Pageable.ofSize(10000));
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

  protected List<Literature> searchLiterature(String searchTerm) {
    return searchLiteratureHit(searchTerm).get().map(SearchHit::getContent).toList();
  }

  protected SearchPage<Literature> searchLiteratureHit(String searchTerm) {
    return literatureService.simpleSearchLiterature(
        UniversalSearchParams.builder().searchTerm(searchTerm).build(),
        null,
        Pageable.ofSize(10000));
  }

  protected List<AdministrativeDirective> searchAdmin(String searchTerm) {
    return searchAdminHit(searchTerm).get().map(SearchHit::getContent).toList();
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
}
