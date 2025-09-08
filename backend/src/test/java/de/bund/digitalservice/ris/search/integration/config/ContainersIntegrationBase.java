package de.bund.digitalservice.ris.search.integration.config;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import java.io.IOException;
import java.io.InputStreamReader;
import org.opensearch.data.client.orhlc.OpenSearchRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.index.AliasAction;
import org.springframework.data.elasticsearch.core.index.AliasActionParameters;
import org.springframework.data.elasticsearch.core.index.AliasActions;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.FileCopyUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.TestcontainersConfiguration;

public class ContainersIntegrationBase {

  public static final CustomOpensearchContainer openSearchContainer =
      new CustomOpensearchContainer();

  public static final PostgreSQLContainer<?> postgresContainer =
      new PostgreSQLContainer<>("postgres:14-alpine");

  static {
    TestcontainersConfiguration.getInstance()
        .updateUserConfig("testcontainers.reuse.enable", "true");
    openSearchContainer.withReuse(true);
    openSearchContainer.start();
    postgresContainer.withDatabaseName("neuris").withPassword("test").withUsername("test").start();
  }

  @DynamicPropertySource
  static void registerDynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("opensearch.port", openSearchContainer::getFirstMappedPort);

    registry.add("db.user", postgresContainer::getUsername);
    registry.add("db.password", postgresContainer::getPassword);
    registry.add("db.host", postgresContainer::getHost);
    registry.add("db.port", postgresContainer::getFirstMappedPort);
    registry.add("db.database", postgresContainer::getDatabaseName);
  }

  @Autowired private OpenSearchRestTemplate openSearchRestTemplate;

  public void recreateIndex() {

    // delete all indices (side effect of deleting all aliases)
    openSearchRestTemplate.indexOps(IndexCoordinates.of("*")).delete();

    // recreate indices
    openSearchRestTemplate.indexOps(CaseLawDocumentationUnit.class).create();
    openSearchRestTemplate.indexOps(CaseLawDocumentationUnit.class).refresh();
    openSearchRestTemplate.indexOps(Norm.class).create();
    openSearchRestTemplate.indexOps(Norm.class).refresh();

    // recreate documents alias
    openSearchRestTemplate
        .indexOps(IndexCoordinates.of("*"))
        .alias(
            new AliasActions(
                new AliasAction.Add(
                    AliasActionParameters.builder()
                        .withIndices("caselaws", "norms")
                        .withAliases("documents")
                        .build())));
  }

  public void updateMapping() throws IOException {

    // Case law mapping
    ClassPathResource resource = new ClassPathResource("/openSearch/caselaw_mappings.json");
    InputStreamReader reader = new InputStreamReader(resource.getInputStream());
    String mappingJson = FileCopyUtils.copyToString(reader);

    Document mappingDocument = Document.parse(mappingJson);
    openSearchRestTemplate.indexOps(IndexCoordinates.of("caselaws")).putMapping(mappingDocument);

    // norms mapping
    ClassPathResource resourceNorms = new ClassPathResource("/openSearch/norms_mapping.json");
    InputStreamReader readerNorms = new InputStreamReader(resourceNorms.getInputStream());
    String mappingJsonNorms = FileCopyUtils.copyToString(readerNorms);

    Document mappingDocumentNorms = Document.parse(mappingJsonNorms);
    openSearchRestTemplate.indexOps(IndexCoordinates.of("norms")).putMapping(mappingDocumentNorms);
  }
}
