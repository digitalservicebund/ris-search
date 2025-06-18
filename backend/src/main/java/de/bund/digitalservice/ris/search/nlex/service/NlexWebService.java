package de.bund.digitalservice.ris.search.nlex.service;

import de.bund.digitalservice.ris.search.nlex.AboutConnector;
import de.bund.digitalservice.ris.search.nlex.AboutConnectorResponse;
import de.bund.digitalservice.ris.search.nlex.Request;
import de.bund.digitalservice.ris.search.nlex.RequestResponse;
import de.bund.digitalservice.ris.search.nlex.TestQuery;
import de.bund.digitalservice.ris.search.nlex.TestQueryResponse;
import de.bund.digitalservice.ris.search.nlex.VERSIONResponse;
import de.bund.digitalservice.ris.search.nlex.result.ObjectFactory;
import de.bund.digitalservice.ris.search.nlex.result.Result;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class NlexWebService {
  private static final String NAMESPACE_URI = "nlex.search.ris.digitalservice.bund.de";
  private final JAXBContext resultJaxbContext;

  @Autowired
  public NlexWebService() throws JAXBException {
    this.resultJaxbContext = JAXBContext.newInstance(ObjectFactory.class);
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "request")
  @ResponsePayload
  public RequestResponse request(@RequestPayload Request query) throws JAXBException {

    RequestResponse resp = new RequestResponse();
    Result result = new Result();
    result.setStatus("OK");
    StringWriter sw = new StringWriter();
    Marshaller m = this.resultJaxbContext.createMarshaller();
    m.setProperty(Marshaller.JAXB_FRAGMENT, true);
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.marshal(new ObjectFactory().createResult(result), sw);

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
  public AboutConnectorResponse aboutConnector(@RequestPayload AboutConnector connector) {
    AboutConnectorResponse resp = new AboutConnectorResponse();
    resp.setAboutConnectorResult("query.xsd");
    return resp;
  }
}
