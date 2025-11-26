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

/**
 * This class represents a SOAP web service endpoint for handling various operations related to the
 * NLEX system. It uses JAXB for marshalling and unmarshalling request and response objects, and
 * integrates with the NlexService to execute queries and obtain results.
 *
 * <p>The endpoint provides implementations for several SOAP operations, including: - Processing
 * queries and returning results. - Handling version requests to provide the system version. -
 * Providing test queries for debugging or validation purposes. - Offering information about the
 * connector configuration.
 *
 * <p>This class is annotated with `@Endpoint` to signify its role in the Spring Web Services
 * framework as a SOAP endpoint.
 */
@Endpoint
public class NlexWebServiceEndpoint {
  private static final String NAMESPACE_URI = "nlex.search.ris.digitalservice.bund.de";

  JAXBContext queryCtx;
  JAXBContext resultCtx;
  NlexService nlexService;

  /**
   * Constructs an instance of the NlexWebServiceEndpoint class. This endpoint is responsible for
   * interfacing with the NlexService and handling web service requests by unmarshaling input
   * queries, executing them, and marshaling the results into appropriate response formats.
   *
   * @param nlexService the NlexService instance used for processing and executing queries
   * @throws JAXBException if an error occurs during the initialization of JAXBContexts for query
   *     and result processing
   */
  public NlexWebServiceEndpoint(NlexService nlexService) throws jakarta.xml.bind.JAXBException {
    this.nlexService = nlexService;
    this.queryCtx = JAXBContext.newInstance(Query.class);
    this.resultCtx = JAXBContext.newInstance(RequestResult.class);
  }

  /**
   * Processes the provided {@code Request} by unmarshalling its query, executing the query through
   * the service layer, and marshalling the result into a {@code RequestResponse}.
   *
   * @param request the input {@code Request} object containing a query to be processed
   * @return a {@code RequestResponse} object containing the result of the executed query
   * @throws JAXBException if an error occurs during marshaling or unmarshaling
   */
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "request")
  @ResponsePayload
  public RequestResponse request(@RequestPayload Request request) throws JAXBException {

    Query query = unmarshallQuery(request.getQuery());
    RequestResult result = nlexService.runRequestQuery(query);
    return this.marshallRequestResponse(result);
  }

  private Query unmarshallQuery(String query) throws JAXBException {
    Unmarshaller um = queryCtx.createUnmarshaller();
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

  /**
   * Handles the "VERSION" request and provides the current version of the system.
   *
   * @return a {@code VERSIONResponse} object containing the version information of the system
   */
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "VERSION")
  @ResponsePayload
  public VERSIONResponse version() {
    VERSIONResponse response = new VERSIONResponse();
    response.setVERSIONResult("0.1");
    return response;
  }

  /**
   * Handles the "test_query" operation by processing the provided request and returning a response
   * containing a query string.
   *
   * @param request the input {@code TestQuery} object containing request details
   * @return a {@code TestQueryResponse} object containing the query string retrieved from the
   *     predefined resource file
   * @throws IOException if an error occurs while reading the query resource
   */
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "test_query")
  @ResponsePayload
  public TestQueryResponse testQuery(@RequestPayload TestQuery request) throws IOException {
    TestQueryResponse response = new TestQueryResponse();
    String query =
        IOUtils.toString(
            Objects.requireNonNull(
                this.getClass().getResourceAsStream("/WEB_INF/nlex/test-query.xml")),
            StandardCharsets.UTF_8);
    response.setQuery(query);

    return response;
  }

  /**
   * Handles the "about_connector" request and returns information about the connector
   * configuration.
   *
   * @param connector the input object containing details about the connector
   * @return an {@code AboutConnectorResponse} object containing the connector configuration details
   * @throws IOException if an error occurs while reading the configuration file
   */
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
