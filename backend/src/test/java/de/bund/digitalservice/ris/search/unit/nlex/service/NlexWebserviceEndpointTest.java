package de.bund.digitalservice.ris.search.unit.nlex.service;

import static org.mockito.ArgumentMatchers.argThat;

import de.bund.digitalservice.ris.search.nlex.schema.query.Query;
import de.bund.digitalservice.ris.search.nlex.schema.result.RequestResult;
import de.bund.digitalservice.ris.search.nlex.service.NlexService;
import de.bund.digitalservice.ris.search.nlex.service.NlexWebServiceEndpoint;
import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
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
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

class NlexWebserviceEndpointTest {
  private NlexWebServiceEndpoint nlexServiceEndpoint;
  private NlexService service;

  @BeforeEach
  void setup() throws JAXBException {
    this.service = Mockito.mock(NlexService.class);
    this.nlexServiceEndpoint = new NlexWebServiceEndpoint(service);
  }

  @Test
  void onVersionItReturnsItsVersion() {
    VERSIONResponse response = this.nlexServiceEndpoint.version();
    Assertions.assertEquals("0.1", response.getVERSIONResult());
  }

  @Test
  void onTestQueryItReturnsThePlaceholder() {
    TestQueryResponse response = this.nlexServiceEndpoint.testQuery(new TestQuery());
    Assertions.assertEquals("test_query_placeholder", response.getQuery());
  }

  @Test
  void onAboutConnectorItReturnsTheRisQuerySchemaDefinition() throws IOException {
    String configContent =
        this.nlexServiceEndpoint.aboutConnector(new AboutConnector()).getAboutConnectorResult();
    String expectedContent =
        IOUtils.toString(
            Objects.requireNonNull(
                NlexWebServiceEndpoint.class.getResourceAsStream(
                    "/WEB_INF/nlex/schema/ris-query.xsd")),
            StandardCharsets.UTF_8);
    Assertions.assertEquals(expectedContent, configContent);
  }

  @Test
  void onRequestItReturnsTheExpectedResult() throws JAXBException {
    String exampleRequest =
        """
                        <request output_lang="de-DE">
                            <navigation>
                                <page number="1" />
                            </navigation>
                            <criteria encoding="utf8">
                               <and>
                                    <words idx-name="nlex:fulltext">
                                        <contains>Sample expression</contains>
                                    </words>
                               </and>
                            </criteria>
                        </request>
                    """;
    Request request = new Request();
    request.setQuery(exampleRequest);

    String exampleResultString =
        """
                        <result status="OK">
                            <navigation>
                                <request-id>123-456</request-id>
                                <page number="1" size="10" />
                            </navigation>
                            <result-list>
                            <documents>
                              <document>
                                <references>
                                  <extern-url format="text/html" display="HTML extern" href="http://example.html" />
                                </references>
                              </document>
                            </documents>
                            </result-list>
                        </result>
                    """;

    RequestResult exampleResult =
        JAXB.unmarshal(new StringReader(exampleResultString), RequestResult.class);
    RequestResponse expectedResponse = new RequestResponse();
    expectedResponse.setRequestResult(exampleResultString);

    Mockito.when(
            this.service.runRequestQuery(
                argThat(
                    new ArgumentMatcher<Query>() {
                      @Override
                      public boolean matches(Query query) {
                        return query.getNavigation().getPage().getNumber() == 1
                            && query
                                .getCriteria()
                                .getAnd()
                                .getWords()
                                .getContains()
                                .equals("Sample expression");
                      }
                    })))
        .thenReturn(exampleResult);

    RequestResponse response = this.nlexServiceEndpoint.request(request);

    JAXB.marshal(response.getRequestResult(), new StringWriter());
    Assertions.assertTrue(
        EqualsBuilder.reflectionEquals(
            JAXB.unmarshal(new StringReader(response.getRequestResult()), RequestResult.class),
            exampleResult));
  }
}
