package de.bund.digitalservice.ris.search.integration.service;

import static de.bund.digitalservice.ris.search.nlex.schema.result.Error.PAGE_SMALLER_THAN_ONE;
import static de.bund.digitalservice.ris.search.nlex.schema.result.Error.STANDARD_ERROR_NO_SEARCHTERM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.nlex.schema.query.BooleanAnd;
import de.bund.digitalservice.ris.search.nlex.schema.query.Criteria;
import de.bund.digitalservice.ris.search.nlex.schema.query.Query;
import de.bund.digitalservice.ris.search.nlex.schema.query.Words;
import de.bund.digitalservice.ris.search.nlex.schema.result.Content;
import de.bund.digitalservice.ris.search.nlex.schema.result.Navigation;
import de.bund.digitalservice.ris.search.nlex.schema.result.Page;
import de.bund.digitalservice.ris.search.nlex.schema.result.Para;
import de.bund.digitalservice.ris.search.nlex.schema.result.RequestResult;
import de.bund.digitalservice.ris.search.nlex.service.NlexService;
import de.bund.digitalservice.ris.search.service.NormsService;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("integration")
class NlexServiceTest extends ContainersIntegrationBase {

  @Autowired() NormsService service;
  NlexService nlexService;
  private static final String FRONTEND_URL = "http://testurl.de/";

  @BeforeEach
  void setup() {
    this.nlexService = new NlexService(service, FRONTEND_URL);
  }

  @Test
  void onRequestItReturnsTheProperResult() {
    String searchTerm = "test";

    Query query = buildQuery(searchTerm);

    RequestResult result = this.nlexService.runRequestQuery(query);

    var expectedNavigation = new Navigation();
    expectedNavigation.setPage(new Page().setNumber(1).setSize(20));
    expectedNavigation.setHits(3L);
    expectedNavigation.setRequestId("dGVzdA==");
    assertThat(result.getResultList().getNavigation()).isEqualTo(expectedNavigation);

    var firstDocument = result.getResultList().getDocuments().getFirst();

    assertThat(firstDocument.getContent())
        .extracting(Content::getTitle, Content::getLang)
        .isEqualTo(List.of("Test Gesetz", "de-DE"));

    assertThat(firstDocument.getContent().getTitle()).isEqualTo("Test Gesetz");
    assertThat(firstDocument.getContent().getLang()).isEqualTo("de-DE");

    assertThat(firstDocument.getReferences().getExternUrl().getHref())
        .isEqualTo("http://testurl.de/norms/eli/bund/bgbl-1/1000/test/2000-10-06/2/deu");

    assertThat(firstDocument.getContent().getParaList())
        .extracting(Para::getRoles, Para::getValue)
        .containsExactlyInAnyOrder(tuple("zoom", "<mark>Test</mark> Gesetz"));
  }

  @Test
  void itDefaultsToFirstPageOnMissingParameter() {
    Query query =
        new Query()
            .setNavigation(new de.bund.digitalservice.ris.search.nlex.schema.query.Navigation())
            .setCriteria(new Criteria().setWords(new Words().setContains("test")));

    Norm expectedNorm = new Norm();
    expectedNorm.setManifestationEliExample("manifestationEliExample");
    expectedNorm.setOfficialTitle("title");

    var response = nlexService.runRequestQuery(query);

    assertThat(response.getResultList().getNavigation().getPage().getNumber()).isEqualTo(1);
  }

  private Query buildQuery(String searchTerm) {
    Query query =
        new Query().setCriteria(new Criteria().setWords(new Words().setContains(searchTerm)));
    query.setNavigation(
        new de.bund.digitalservice.ris.search.nlex.schema.query.Navigation()
            .setPage(new de.bund.digitalservice.ris.search.nlex.schema.query.Page().setNumber(1)));
    return query;
  }

  @Test
  void itReturnsTheProperErrorCodeOnEmptySearch() {
    Query emptyString =
        new Query()
            .setNavigation(new de.bund.digitalservice.ris.search.nlex.schema.query.Navigation())
            .setCriteria(new Criteria().setWords(new Words().setContains("")));

    RequestResult result = this.nlexService.runRequestQuery(emptyString);
    Assertions.assertEquals(STANDARD_ERROR_NO_SEARCHTERM, result.getErrors().getFirst().getCause());
  }

  @Test
  void itReturnsTheProperErrorCodeOnNullSearch() {
    Query nullSearchTerm =
        new Query()
            .setNavigation(new de.bund.digitalservice.ris.search.nlex.schema.query.Navigation())
            .setCriteria(new Criteria().setAnd(new BooleanAnd().setWords(new Words())));
    RequestResult nullSearchTermResult = this.nlexService.runRequestQuery(nullSearchTerm);
    Assertions.assertEquals(
        STANDARD_ERROR_NO_SEARCHTERM, nullSearchTermResult.getErrors().getFirst().getCause());
  }

  @Test
  void itReturnsTheProperErrorOnInvalidPage() {
    Query nullSearchTerm =
        new Query()
            .setNavigation(
                new de.bund.digitalservice.ris.search.nlex.schema.query.Navigation()
                    .setPage(
                        new de.bund.digitalservice.ris.search.nlex.schema.query.Page()
                            .setNumber(0)))
            .setCriteria(
                new Criteria()
                    .setAnd(new BooleanAnd().setWords(new Words().setContains("searchTerm"))));
    RequestResult nullSearchTermResult = this.nlexService.runRequestQuery(nullSearchTerm);
    Assertions.assertEquals(
        PAGE_SMALLER_THAN_ONE, nullSearchTermResult.getErrors().getFirst().getCause());
  }
}
