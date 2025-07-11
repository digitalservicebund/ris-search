package de.bund.digitalservice.ris.search.unit.nlex.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.testcontainers.shaded.org.bouncycastle.util.Longs.valueOf;

import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.nlex.schema.query.BooleanAnd;
import de.bund.digitalservice.ris.search.nlex.schema.query.Criteria;
import de.bund.digitalservice.ris.search.nlex.schema.query.Navigation;
import de.bund.digitalservice.ris.search.nlex.schema.query.Page;
import de.bund.digitalservice.ris.search.nlex.schema.query.Query;
import de.bund.digitalservice.ris.search.nlex.schema.query.Words;
import de.bund.digitalservice.ris.search.nlex.schema.result.Content;
import de.bund.digitalservice.ris.search.nlex.schema.result.Document;
import de.bund.digitalservice.ris.search.nlex.schema.result.ExternUrl;
import de.bund.digitalservice.ris.search.nlex.schema.result.Para;
import de.bund.digitalservice.ris.search.nlex.schema.result.References;
import de.bund.digitalservice.ris.search.nlex.schema.result.RequestResult;
import de.bund.digitalservice.ris.search.nlex.schema.result.ResultList;
import de.bund.digitalservice.ris.search.nlex.schema.result.ResultStatus;
import de.bund.digitalservice.ris.search.nlex.service.NlexService;
import de.bund.digitalservice.ris.search.service.NormsService;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jose4j.base64url.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;

class NlexServiceTest {

  NormsService service;
  NlexService nlexService;

  @BeforeEach
  void setup() {
    this.service = Mockito.mock(NormsService.class);
    this.nlexService = new NlexService(service);
  }

  @Test
  void onRequestItReturnsTheProperResult() {
    String searchTerm = "test phrase";

    String manifestationExample = "example.xml";
    String title = "testTitle";
    String innerHitText = "inner hit";

    UniversalSearchParams expectedSearch = new UniversalSearchParams();
    expectedSearch.setSearchTerm(searchTerm);

    Query query = buildQuery(searchTerm);
    configureServiceMock(searchTerm, manifestationExample, title, innerHitText);

    RequestResult result = this.nlexService.runRequestQuery(query);

    RequestResult expectedResult =
        buildExpectedRequestResult("/v1/legislation/example.html", title, innerHitText, searchTerm);

    Assertions.assertTrue(EqualsBuilder.reflectionEquals(expectedResult, result));
  }

  @Test
  void itDefaultsToFirstPageOnMissingParameter() {
    UniversalSearchParams expectedSearch = new UniversalSearchParams();
    expectedSearch.setSearchTerm("test");

    Query query =
        new Query()
            .setNavigation(new Navigation())
            .setCriteria(new Criteria().setWords(new Words().setContains("test")));

    Norm expectedNorm = new Norm();
    expectedNorm.setManifestationEliExample("manifestationEliExample");
    expectedNorm.setOfficialTitle("title");

    Article expectedArticle = Article.builder().text("innerHitText").build();
    SearchPage<Norm> expectedPage = getMockedNormsServiceSearchPage(expectedNorm, expectedArticle);

    Mockito.when(service.searchAndFilterNorms(any(), any(), any())).thenReturn(expectedPage);

    nlexService.runRequestQuery(query);

    Mockito.verify(service, times(1))
        .searchAndFilterNorms(any(), any(), argThat(searchpage -> searchpage.getPageNumber() == 0));
  }

  private Query buildQuery(String searchTerm) {
    Query query =
        new Query().setCriteria(new Criteria().setWords(new Words().setContains(searchTerm)));
    query.setNavigation(new Navigation().setPage(new Page().setNumber(1)));
    return query;
  }

  private RequestResult buildExpectedRequestResult(
      String href, String title, String innerHitText, String searchTerm) {
    return new RequestResult()
        .setStatus(ResultStatus.OK)
        .setResultList(
            new ResultList()
                .setNavigation(
                    new de.bund.digitalservice.ris.search.nlex.schema.result.Navigation()
                        .setRequestId(Base64.encode(searchTerm.getBytes()))
                        .setHits(valueOf(1))
                        .setPage(
                            new de.bund.digitalservice.ris.search.nlex.schema.result.Page()
                                .setNumber(1)
                                .setSize(1)))
                .setDocuments(
                    List.of(
                        new Document()
                            .setContent(
                                new Content()
                                    .setTitle(title)
                                    .setLang("de-DE")
                                    .setParaList(
                                        List.of(
                                            new Para().setRoles("zoom").setValue(innerHitText))))
                            .setReferences(
                                new References().setExternUrl(new ExternUrl().setHref(href))))));
  }

  private void configureServiceMock(
      String searchTerm, String manifestationEliExample, String title, String innerHitText) {
    Norm expectedNorm = new Norm();
    expectedNorm.setManifestationEliExample(manifestationEliExample);
    expectedNorm.setOfficialTitle(title);

    Article expectedArticle = Article.builder().text(innerHitText).build();
    SearchPage<Norm> page = getMockedNormsServiceSearchPage(expectedNorm, expectedArticle);

    Mockito.when(
            service.searchAndFilterNorms(
                argThat(
                    universalSearchParams ->
                        universalSearchParams.getSearchTerm().equals(searchTerm)),
                eq(null),
                any()))
        .thenReturn(page);
  }

  private SearchPage<Norm> getMockedNormsServiceSearchPage(
      Norm expectedNorm, Article expectedArticle) {
    SearchHit<Article> innerHit =
        new SearchHit<>(null, null, null, 1, null, null, null, null, null, null, expectedArticle);
    SearchHits innerHits = Mockito.mock(SearchHits.class);
    Mockito.when(innerHits.stream()).thenReturn(Stream.of(innerHit));

    SearchHit<Norm> hit =
        new SearchHit<>(
            null,
            null,
            null,
            1,
            null,
            null,
            Collections.singletonMap("articles", innerHits),
            null,
            null,
            null,
            expectedNorm);

    SearchHits hits = Mockito.mock(SearchHits.class);
    Mockito.when(hits.stream()).thenReturn(Stream.of(hit));
    SearchPage<Norm> page = Mockito.mock(SearchPage.class);
    Mockito.when(page.getNumber()).thenReturn(1);
    Mockito.when(page.getNumberOfElements()).thenReturn(1);
    Mockito.when(page.getTotalElements()).thenReturn(valueOf(1));
    Mockito.when(page.getSearchHits()).thenReturn(hits);

    return page;
  }

  @Test
  void itReturnsTheProperErrorCodeOnEmptySearch() {
    Query emptyString =
        new Query()
            .setNavigation(new Navigation())
            .setCriteria(new Criteria().setWords(new Words().setContains("")));

    RequestResult result = this.nlexService.runRequestQuery(emptyString);
    Assertions.assertEquals("1", result.getErrors().getFirst().getCause());
  }

  @Test
  void itReturnsTheProperErrorCodeOnNullSearch() {
    Query nullSearchTerm =
        new Query()
            .setNavigation(new Navigation())
            .setCriteria(new Criteria().setAnd(new BooleanAnd().setWords(new Words())));
    RequestResult nullSearchTermResult = this.nlexService.runRequestQuery(nullSearchTerm);
    Assertions.assertEquals("1", nullSearchTermResult.getErrors().getFirst().getCause());
  }
}
