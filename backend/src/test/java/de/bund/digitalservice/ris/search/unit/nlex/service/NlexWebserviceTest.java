package de.bund.digitalservice.ris.search.unit.nlex.service;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.nlex.schema.query.Criteria;
import de.bund.digitalservice.ris.search.nlex.schema.query.Query;
import de.bund.digitalservice.ris.search.nlex.schema.query.Words;
import de.bund.digitalservice.ris.search.nlex.schema.result.Content;
import de.bund.digitalservice.ris.search.nlex.schema.result.Document;
import de.bund.digitalservice.ris.search.nlex.schema.result.ExternUrl;
import de.bund.digitalservice.ris.search.nlex.schema.result.Para;
import de.bund.digitalservice.ris.search.nlex.schema.result.References;
import de.bund.digitalservice.ris.search.nlex.schema.result.RequestResult;
import de.bund.digitalservice.ris.search.nlex.schema.result.ResultList;
import de.bund.digitalservice.ris.search.nlex.service.NlexWebService;
import de.bund.digitalservice.ris.search.service.NormsService;
import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import nlex.AboutConnector;
import nlex.Request;
import nlex.RequestResponse;
import nlex.TestQuery;
import nlex.TestQueryResponse;
import nlex.VERSIONResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;

class NlexWebserviceTest {
  private NlexWebService nlexService;

  private NormsService service;

  @BeforeEach
  void setup() throws JAXBException {
    this.service = Mockito.mock(NormsService.class);
    this.nlexService = new NlexWebService(service);
  }

  @Test
  void onVersionItReturnsItsVersion() {
    VERSIONResponse response = this.nlexService.version();
    Assertions.assertEquals("0.1", response.getVERSIONResult());
  }

  @Test
  void onTestQueryItReturnsThePlaceholder() {
    TestQueryResponse response = this.nlexService.testQuery(new TestQuery());
    Assertions.assertEquals("test_query_placeholder", response.getQuery());
  }

  @Test
  void onAboutConnectorItReturnsTheRisQuerySchemaDefinition() throws IOException {
    String configContent =
        this.nlexService.aboutConnector(new AboutConnector()).getAboutConnectorResult();
    String expectedContent =
        IOUtils.toString(
            Objects.requireNonNull(
                NlexWebService.class.getResourceAsStream("/WEB_INF/nlex/schema/ris-query.xsd")),
            StandardCharsets.UTF_8);
    Assertions.assertEquals(expectedContent, configContent);
  }

  @Test
  void onRequestItReturnsTheExpectedResult() throws JAXBException {
    String searchTerm = "test phrase";

    String manifestationExample = "example.xml";
    String title = "testTitle";
    String innerHitText = "inner hit";

    UniversalSearchParams expectedSearch = new UniversalSearchParams();
    expectedSearch.setSearchTerm(searchTerm);

    Request request = buildRequest(searchTerm);
    configureServiceMock(searchTerm, manifestationExample, title, innerHitText);

    RequestResponse resp = this.nlexService.request(request);

    RequestResult expectedResult =
        buildExpectedRequestResult("/v1/legislation/example.html", title, innerHitText);

    RequestResult reqResult =
        JAXB.unmarshal(new StringReader(resp.getRequestResult()), RequestResult.class);

    Assertions.assertTrue(EqualsBuilder.reflectionEquals(expectedResult, reqResult));
  }

  private Request buildRequest(String searchTerm) {
    Query query =
        new Query().setCriteria(new Criteria().setWords(new Words().setContains(searchTerm)));

    Request request = new Request();
    StringWriter sw = new StringWriter();
    JAXB.marshal(query, sw);
    request.setQuery(sw.toString());

    return request;
  }

  private RequestResult buildExpectedRequestResult(String href, String title, String innerHitText) {
    return new RequestResult()
        .setResultList(
            new ResultList()
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
    Mockito.when(page.getSearchHits()).thenReturn(hits);

    Mockito.when(
            service.searchAndFilterNorms(
                argThat(
                    universalSearchParams ->
                        universalSearchParams.getSearchTerm().equals(searchTerm)),
                eq(null),
                eq(null)))
        .thenReturn(page);
  }
}
