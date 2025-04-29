package de.bund.digitalservice.ris.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import testnamespace.Request;
import testnamespace.RequestResponse;
import testnamespace.TestQuery;
import testnamespace.TestQueryResponse;
import testnamespace.VERSIONResponse;

@Endpoint
public class NlexEndpoint {
  private static final String NAMESPACE_URI = "testnamespace";

  @Autowired
  public NlexEndpoint() {}

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "VERSION")
  @ResponsePayload
  public String version() {
    VERSIONResponse response = new VERSIONResponse();
    response.setVERSIONResult("1.0");
    return "1.0";
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
