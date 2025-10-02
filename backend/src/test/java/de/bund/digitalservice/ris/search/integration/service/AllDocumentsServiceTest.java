package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.CaseLawTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.TestDataGenerator;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import de.bund.digitalservice.ris.search.service.AllDocumentsService;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
@WithJwt("jwtTokens/ValidAccessToken.json")
class AllDocumentsServiceTest extends ContainersIntegrationBase {

  @Autowired private CaseLawRepository caseLawRepository;
  @Autowired private NormsRepository normsRepository;
  @Autowired private AllDocumentsService allDocumentsService;

  Boolean initialized = false;

  @BeforeEach
  void setUpSearchControllerApiTest() throws IOException {
    if (initialized) return; // replacement for @BeforeAll setup, which causes errors
    initialized = true;

    assertTrue(openSearchContainer.isRunning());

    super.recreateIndex();
    super.updateMapping();
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
}
