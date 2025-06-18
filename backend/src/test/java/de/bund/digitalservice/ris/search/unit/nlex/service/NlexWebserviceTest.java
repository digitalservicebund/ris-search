package de.bund.digitalservice.ris.search.unit.nlex.service;

import de.bund.digitalservice.ris.search.nlex.AboutConnector;
import de.bund.digitalservice.ris.search.nlex.AboutConnectorResponse;
import de.bund.digitalservice.ris.search.nlex.Request;
import de.bund.digitalservice.ris.search.nlex.RequestResponse;
import de.bund.digitalservice.ris.search.nlex.TestQuery;
import de.bund.digitalservice.ris.search.nlex.TestQueryResponse;
import de.bund.digitalservice.ris.search.nlex.VERSIONResponse;
import de.bund.digitalservice.ris.search.nlex.result.ObjectFactory;
import de.bund.digitalservice.ris.search.nlex.result.Result;
import de.bund.digitalservice.ris.search.nlex.service.NlexWebService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import javax.xml.transform.stream.StreamSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NlexWebserviceTest {
  private NlexWebService service;
  private JAXBContext resultJaxbContext;

  @BeforeEach
  void setup() throws JAXBException {
    this.service = new NlexWebService();
    this.resultJaxbContext = JAXBContext.newInstance(ObjectFactory.class);
  }

  @Test
  void onVersionItReturnsItsVersion() {
    VERSIONResponse response = this.service.version();
    Assertions.assertEquals("0.1", response.getVERSIONResult());
  }

  @Test
  void onTestQueryItReturnsThePlaceholder() {
    TestQueryResponse response = this.service.testQuery(new TestQuery());
    Assertions.assertEquals("test_query_placeholder", response.getQuery());
  }

  @Test
  void onAboutConnectorItReturnsThePlaceholder() {
    AboutConnectorResponse response = this.service.aboutConnector(new AboutConnector());
    Assertions.assertEquals("query.xsd", response.getAboutConnectorResult());
  }

  @Test
  void onRequestItReturnsAnEmptyResultWithStatusOk() throws JAXBException {
    RequestResponse response = this.service.request(new Request());
    JAXBElement<Result> element =
        resultJaxbContext
            .createUnmarshaller()
            .unmarshal(
                new StreamSource(new ByteArrayInputStream(response.getRequestResult().getBytes())),
                Result.class);

    Assertions.assertEquals("OK", element.getValue().getStatus());
  }
}
