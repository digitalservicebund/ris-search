package de.bund.digitalservice.ris.search.unit.nlex.service;

import de.bund.digitalservice.ris.search.nlex.service.NlexWebService;
import nlex.AboutConnector;
import nlex.AboutConnectorResponse;
import nlex.Request;
import nlex.RequestResponse;
import nlex.TestQuery;
import nlex.TestQueryResponse;
import nlex.VERSIONResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NlexWebserviceTest {
  private NlexWebService service;

  @BeforeEach
  void setup() {
    this.service = new NlexWebService();
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
    Assertions.assertEquals("about_connector_placeholder", response.getAboutConnectorResult());
  }

  @Test
  void onRequestItReturnsAPlaceholderResult() {
    RequestResponse response = this.service.request(new Request());
    Assertions.assertEquals("request_placeholder", response.getRequestResult());
  }
}
