package de.bund.digitalservice.ris.search.nlex.service;

import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Objects;
import nlex.AboutConnector;
import nlex.AboutConnectorResponse;
import nlex.Request;
import nlex.RequestResponse;
import nlex.TestQuery;
import nlex.TestQueryResponse;
import nlex.VERSIONResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class NlexWebService {
  private static final String NAMESPACE_URI = "nlex.search.ris.digitalservice.bund.de";

  @Autowired
  public NlexWebService() {}

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "request")
  @ResponsePayload
  public RequestResponse request(@RequestPayload Request request) throws JAXBException {
    RequestResponse resp = new RequestResponse();
    resp.setRequestResult("placeholder");
    return resp;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "VERSION")
  @ResponsePayload
  public VERSIONResponse version() {
    VERSIONResponse response = new VERSIONResponse();
    response.setVERSIONResult("0.1");
    return response;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "test_query")
  @ResponsePayload
  public TestQueryResponse testQuery(@RequestPayload TestQuery request) {
    TestQueryResponse response = new TestQueryResponse();
    response.setQuery("placeholder");

    return response;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "about_connector")
  @ResponsePayload
  public AboutConnectorResponse aboutConnector(@RequestPayload AboutConnector connector)
      throws IOException {
    AboutConnectorResponse resp = new AboutConnectorResponse();
    String configContent =
        IOUtils.toString(
            Objects.requireNonNull(
                this.getClass().getResourceAsStream("/WEB_INF/nlex/schema/ris-query.xsd")),
            "UTF-8");
    resp.setAboutConnectorResult(configContent);
    return resp;
  }
}
