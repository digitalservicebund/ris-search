package de.bund.digitalservice.ris.search.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.service.StatisticsService;
import java.util.Map;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensearch.client.Request;
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
    Response aliasResponse =
        mockResponse(
            "[{\"alias\":\"norms\"},{\"alias\":\"literature\"},{\"alias\":\"caselaws\"},{\"alias\":\"test\"}]");
    Response normsResponse = mockResponse("{\"count\":123}");
    Response literatureResponse = mockResponse("{\"count\":456}");
    Response caseLawResponse = mockResponse("{\"count\":500}");

    when(lowLevelClient.performRequest(any(Request.class)))
        .thenReturn(aliasResponse)
        .thenReturn(normsResponse)
        .thenReturn(literatureResponse)
        .thenReturn(caseLawResponse);

    Map<String, Long> counts = statisticsService.getAllCounts();

    assertEquals(Map.of("norms", 123L, "literature", 456L, "caselaws", 500L), counts);
  }

  private Response mockResponse(String json) throws Exception {
    Response response = mock(Response.class);
    when(response.getEntity()).thenReturn(new StringEntity(json, ContentType.APPLICATION_JSON));
    return response;
  }
}
