package de.bund.digitalservice.ris.search.unit.nlex.service;

import de.bund.digitalservice.ris.search.nlex.service.NlexWebService;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import nlex.AboutConnector;
import nlex.Request;
import nlex.RequestResponse;
import nlex.TestQuery;
import nlex.TestQueryResponse;
import nlex.VERSIONResponse;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class NlexWebserviceTest {
  private NlexWebService service;

  private NormsRepository repo;

  @BeforeEach
  void setup() throws JAXBException {
    this.service = new NlexWebService(repo);
    this.repo = Mockito.mock(NormsRepository.class);
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
  void onAboutConnectorItReturnsTheRisQuerySchemaDefinition() throws IOException {
    String configContent =
        this.service.aboutConnector(new AboutConnector()).getAboutConnectorResult();
    String expectedContent =
        IOUtils.toString(
            Objects.requireNonNull(
                NlexWebService.class.getResourceAsStream("/WEB_INF/nlex/schema/ris-query.xsd")),
            StandardCharsets.UTF_8);
    Assertions.assertEquals(expectedContent, configContent);
  }

  @Test
  void onRequestItReturnsAPlaceholderResult() throws JAXBException {
    RequestResponse response = this.service.request(new Request());
    Assertions.assertEquals("request_placeholder", response.getRequestResult());
  }
}
