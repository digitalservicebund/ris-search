package de.bund.digitalservice.ris.search.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.service.StatisticsService;
import java.util.Map;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensearch.client.Response;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;

class StatisticsServiceTest {

  private RestHighLevelClient client;
  private RestClient lowLevelClient;
  private StatisticsService statisticsService;

  @BeforeEach
  void setUp() {
    client = mock(RestHighLevelClient.class);
    lowLevelClient = mock(RestClient.class);
    when(client.getLowLevelClient()).thenReturn(lowLevelClient);

    statisticsService = new StatisticsService(client);
  }

  @Test
  void testGetAllCounts_success() throws Exception {
    Response aliasResponse = mock(Response.class);

    // Mock alias request
    HttpEntity aliasEntity =
        new StringEntity(
            "[{\"alias\":\"norms\"}, {\"alias\":\"literature\"}, {\"alias\":\"caselaws\"}, {\"alias\":\"test\"}]",
            ContentType.APPLICATION_JSON);
    when(aliasResponse.getEntity()).thenReturn(aliasEntity);
    when(lowLevelClient.performRequest(
            argThat(req -> req != null && "/_cat/aliases?format=json".equals(req.getEndpoint()))))
        .thenReturn(aliasResponse);

    // Mock norms count request
    Response normsResponse = mock(Response.class);
    when(normsResponse.getEntity())
        .thenReturn(new StringEntity("{\"count\":123}", ContentType.APPLICATION_JSON));
    when(lowLevelClient.performRequest(
            argThat(req -> req != null && req.getEndpoint().equals("/norms/_count"))))
        .thenReturn(normsResponse);

    // Mock literature count request
    Response literatureResponse = mock(Response.class);
    when(literatureResponse.getEntity())
        .thenReturn(new StringEntity("{\"count\":456}", ContentType.APPLICATION_JSON));
    when(lowLevelClient.performRequest(
            argThat(req -> req != null && req.getEndpoint().equals("/literature/_count"))))
        .thenReturn(literatureResponse);

    // Mock caselaw count request
    Response caseLawResponse = mock(Response.class);
    when(caseLawResponse.getEntity())
        .thenReturn(new StringEntity("{\"count\":500}", ContentType.APPLICATION_JSON));
    when(lowLevelClient.performRequest(
            argThat(req -> req != null && req.getEndpoint().equals("/caselaws/_count"))))
        .thenReturn(caseLawResponse);

    // Execute
    Map<String, Long> counts = statisticsService.getAllCounts();

    // Assertions
    assertEquals(3, counts.size());
    assertEquals(123L, counts.get("norms"));
    assertEquals(456L, counts.get("literature"));
    assertEquals(500L, counts.get("caselaws"));
  }
}
