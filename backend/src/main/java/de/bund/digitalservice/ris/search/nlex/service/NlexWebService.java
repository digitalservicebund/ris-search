package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.Request;
import de.bund.digitalservice.ris.search.RequestResponse;
import de.bund.digitalservice.ris.search.TestQuery;
import de.bund.digitalservice.ris.search.TestQueryResponse;
import de.bund.digitalservice.ris.search.VERSIONResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class NlexWebService {
  private static final String NAMESPACE_URI = "de.bund.digitalservice.ris.search";

  @Autowired
  public NlexWebService() {}

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "VERSION")
  @ResponsePayload
  public VERSIONResponse version() {
    VERSIONResponse response = new VERSIONResponse();
    response.setVERSIONResult("1.0");
    return response;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "test_query")
  @ResponsePayload
  public TestQueryResponse testQuery(@RequestPayload TestQuery request) {
    TestQueryResponse response = new TestQueryResponse();
    response.setQuery(request.getType());

    return response;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "request")
  @ResponsePayload
  public RequestResponse request(@RequestPayload Request query) {
    RequestResponse resp = new RequestResponse();
    resp.setRequestResult("TEST");
    return resp;
  }

  public String aboutConnector(String type) {
    return "";
  }
}
