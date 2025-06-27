package de.bund.digitalservice.ris.search.nlex.service;

import de.bund.digitalservice.ris.search.nlex.schema.query.Query;
import de.bund.digitalservice.ris.search.nlex.schema.result.RequestResult;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import nlex.AboutConnector;
import nlex.AboutConnectorResponse;
import nlex.Request;
import nlex.RequestResponse;
import nlex.TestQuery;
import nlex.TestQueryResponse;
import nlex.VERSIONResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class NlexWebServiceEndpoint {
  private static final String NAMESPACE_URI = "nlex.search.ris.digitalservice.bund.de";

  JAXBContext queryCtx;
  JAXBContext resultCtx;
  NlexService nlexService;

  public NlexWebServiceEndpoint(NlexService nlexService) throws jakarta.xml.bind.JAXBException {
    this.nlexService = nlexService;
    this.queryCtx = JAXBContext.newInstance(Query.class);
    this.resultCtx = JAXBContext.newInstance(RequestResult.class);
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "request")
  @ResponsePayload
  public RequestResponse request(@RequestPayload Request request) throws JAXBException {

    Query query = this.unmarshallQuery(request.getQuery());
    RequestResult result = this.nlexService.runRequestQuery(query);
    return this.marshallRequestResponse(result);
  }

  private Query unmarshallQuery(String query) throws JAXBException {
    Unmarshaller um = this.queryCtx.createUnmarshaller();
    return (Query) um.unmarshal(new StringReader(query));
  }

  private RequestResponse marshallRequestResponse(RequestResult result) throws JAXBException {
    Marshaller m = resultCtx.createMarshaller();
    StringWriter sw = new StringWriter();
    m.marshal(result, sw);
    RequestResponse resp = new RequestResponse();
    resp.setRequestResult(sw.toString());
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
    response.setQuery("test_query_placeholder");

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
            StandardCharsets.UTF_8);
    resp.setAboutConnectorResult(configContent);
    return resp;
  }
}
