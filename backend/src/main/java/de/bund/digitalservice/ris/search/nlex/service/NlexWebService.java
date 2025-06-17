package de.bund.digitalservice.ris.search.nlex.service;

import de.bund.digitalservice.ris.search.nlex.Request;
import de.bund.digitalservice.ris.search.nlex.RequestResponse;
import de.bund.digitalservice.ris.search.nlex.TestQuery;
import de.bund.digitalservice.ris.search.nlex.TestQueryResponse;
import de.bund.digitalservice.ris.search.nlex.VERSIONResponse;
import de.bund.digitalservice.ris.search.nlex.result.DocumentSpecification;
import de.bund.digitalservice.ris.search.nlex.result.ObjectFactory;
import de.bund.digitalservice.ris.search.nlex.result.Result;
import de.bund.digitalservice.ris.search.nlex.result.ResultList;
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

  @Autowired
  public NlexWebService() {}

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "request")
  @ResponsePayload
  public RequestResponse request(@RequestPayload Request query) throws JAXBException {

    RequestResponse resp = new RequestResponse();
    ResultList resultList = new ResultList();
    Result result = new Result();
    Class[] classes = new Class[1];
    classes[0] = Result.class;
    JAXBContext jc = JAXBContext.newInstance(classes);

    Marshaller m = jc.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.setProperty(Marshaller.JAXB_FRAGMENT, true);

    ResultList.Documents docs = new ResultList.Documents();
    docs.getDocument().add(new DocumentSpecification());
    resultList.setDocuments(docs);

    result.setStatus("OK");
    result.setResultList(resultList);
    StringWriter sw = new StringWriter();
    m.marshal(new ObjectFactory().createResult(result), sw);
    resp.setRequestResult(sw.toString());
    return resp;
  }

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

  public String aboutConnector(String type) {
    return "";
  }
}
