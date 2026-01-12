package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.CaseLawTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.LiteratureTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.TestDataGenerator;
import de.bund.digitalservice.ris.search.mapper.DocumentResponseMapper;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.service.AllDocumentsService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class AllDocumentsServiceTest extends ContainersIntegrationBase {

  @Autowired private AllDocumentsService allDocumentsService;

  @BeforeEach
  void setUpSearchControllerApiTest() {
    clearRepositoryData();
  }

  @Test
  @DisplayName("Two different court names match each other")
  void courtSynonymTest() {
    caseLawRepository.save(CaseLawTestData.simple("caselaw1", "Bundessozialgericht"));
    caseLawRepository.save(CaseLawTestData.simple("caselaw2", "no match"));
    normsRepository.save(NormsTestData.simple("norm1", "Bundessozialgericht"));
    normsRepository.save(NormsTestData.simple("norm2", "other no match"));
    List<AbstractSearchEntity> searchResults =
        TestDataGenerator.searchAll(allDocumentsService, "BSG");
    List<String> caselaws = TestDataGenerator.getCaseLawIds(searchResults);
    List<String> norms = TestDataGenerator.getNormIds(searchResults);
    assertThat(caselaws).containsExactly("caselaw1");
    assertThat(norms).containsExactly("norm1");
  }

  @Test
  @DisplayName("Synonyms in the golem thesaurus match each other")
  void golemThesaurusSynonymTest() {
    caseLawRepository.save(CaseLawTestData.simple("caselaw1", "Abgasreduzierend"));
    caseLawRepository.save(CaseLawTestData.simple("caselaw2", "no match"));
    normsRepository.save(NormsTestData.simple("norm1", "Abgasreduzierend"));
    normsRepository.save(NormsTestData.simple("norm2", "other no match"));
    List<AbstractSearchEntity> searchResults =
        TestDataGenerator.searchAll(allDocumentsService, "Abgas reduzierend");
    List<String> caselaws = TestDataGenerator.getCaseLawIds(searchResults);
    List<String> norms = TestDataGenerator.getNormIds(searchResults);
    assertThat(caselaws).containsExactly("caselaw1");
    assertThat(norms).containsExactly("norm1");
  }

  @Test
  @DisplayName("Synonyms should match even with different standard suffixes")
  void synonymWithStemmingMatchTest() {
    // expected matches
    caseLawRepository.save(CaseLawTestData.simple("caselaw1", "Abgasreduzierend"));
    caseLawRepository.save(CaseLawTestData.simple("caselaw2", "Abgasreduzierende"));
    // eser is not a standard suffix and is not expected to match
    caseLawRepository.save(CaseLawTestData.simple("caselaw3", "Abgasreduzierendeser"));
    // es is a standard suffix
    List<AbstractSearchEntity> searchResults =
        TestDataGenerator.searchAll(allDocumentsService, "Abgas reduzierendes");
    List<String> caselawIds = TestDataGenerator.getCaseLawIds(searchResults);
    assertThat(caselawIds).containsExactlyInAnyOrder("caselaw1", "caselaw2");
  }

  @Test
  @DisplayName("Three Different Document kinds are all found")
  void threeDifferentDocumentKindsAreAllFoundTest() {
    caseLawRepository.save(CaseLawTestData.simple("caselaw1", "urlaub"));
    literatureRepository.save(LiteratureTestData.simple("literature1", "urlaub"));
    normsRepository.save(NormsTestData.simple("norm1", "urlaub"));
    List<AbstractSearchEntity> searchResults =
        TestDataGenerator.searchAll(allDocumentsService, "urlaub");
    List<String> caselawIds = TestDataGenerator.getCaseLawIds(searchResults);
    assertThat(caselawIds).containsExactlyInAnyOrder("caselaw1");
    List<String> literatureIds = TestDataGenerator.getLiteratureIds(searchResults);
    assertThat(literatureIds).containsExactlyInAnyOrder("literature1");
    List<String> normIds = TestDataGenerator.getNormIds(searchResults);
    assertThat(normIds).containsExactlyInAnyOrder("norm1");
  }

  @Test
  @DisplayName("titles are not fragmented in the Highlighter")
  void titlesAreNotFragmentedInTheHighlighter() {
    String expectedHeadline = "this headline. Should not - be fragmented.";
    CaseLawDocumentationUnit unit =
        CaseLawDocumentationUnit.builder()
            .id("IDXXX")
            .caseFacts("Test")
            .headline(expectedHeadline)
            .build();
    caseLawRepository.save(unit);

    UniversalSearchParams searchParams = new UniversalSearchParams();
    searchParams.setSearchTerm("be fragmented");

    SearchPage<AbstractSearchEntity> searchResult =
        allDocumentsService.simpleSearchAllDocuments(
            searchParams, null, null, null, null, null, Pageable.ofSize(10));
    var collection = DocumentResponseMapper.fromDomain(searchResult, ApiConfig.Paths.DOCUMENT);

    String expectedheadline =
        "this headline. Should not - <mark>be</mark> <mark>fragmented</mark>.";
    assertThat(collection.member().getFirst().textMatches())
        .anyMatch(m -> expectedheadline.equals(m.text()));
  }
}
