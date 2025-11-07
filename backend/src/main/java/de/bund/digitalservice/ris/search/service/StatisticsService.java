package de.bund.digitalservice.ris.search.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.exception.OpenSearchFetchException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {
  private final List<String> indexNames = Arrays.asList("norms", "literature", "caselaws");

  protected static final Logger logger = LogManager.getLogger(StatisticsService.class);
  private final RestHighLevelClient client;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public StatisticsService(RestHighLevelClient client) {
    this.client = client;
  }

  public Map<String, Long> getAllCounts() throws IOException {

    try {
      List<String> aliases = fetchAliases();
      Map<String, Long> counts = new HashMap<>();

      for (String alias : aliases) {
        if (indexNames.contains(alias)) {
          counts.put(alias, fetchCountWithLogging(alias));
        }
      }
      return counts;
    } catch (IOException e) {
      logger.info(String.format("Failed to fetch Elasticsearch counts: %s", e.getMessage()));
      throw new OpenSearchFetchException("Failed to fetch aliases from Opensearch", e);
    }
  }

  private List<String> fetchAliases() throws IOException {
    Request aliasRequest = new Request("GET", "/_cat/aliases?format=json");
    Response aliasResponse = client.getLowLevelClient().performRequest(aliasRequest);
    JsonNode root = objectMapper.readTree(aliasResponse.getEntity().getContent());

    List<String> aliases = new ArrayList<>();
    for (JsonNode aliasNode : root) {
      aliases.add(aliasNode.get("alias").asText());
    }
    return aliases;
  }

  private long fetchCountWithLogging(String alias) throws IOException {
    try {
      return fetchCount(alias);
    } catch (IOException e) {
      logger.warn("Failed to fetch count for index '{}': {}", alias, e.getMessage());
      throw new OpenSearchFetchException(
          String.format("Failed to fetch count for index %s", alias), e);
    }
  }

  private long fetchCount(String alias) throws IOException {
    Request countRequest = new Request("GET", "/" + alias + "/_count");
    Response countResponse = client.getLowLevelClient().performRequest(countRequest);
    JsonNode countNode = objectMapper.readTree(countResponse.getEntity().getContent());
    return countNode.get("count").asLong();
  }
}
