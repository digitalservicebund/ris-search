package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.OpenSearchFetchException;
import de.bund.digitalservice.ris.search.schema.StatisticsApiSchema;
import de.bund.digitalservice.ris.search.schema.StatisticsCountSchema;
import de.bund.digitalservice.ris.search.service.StatisticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller provides endpoints for retrieving statistical data about the document records
 * stored in the system. It collects document counts for various categories such as norms, case
 * laws, and literature from the underlying OpenSearch system and exposes the data via a REST API.
 */
@Tag(
    name = "Statistics",
    description = "Returns document count statistics for all document kinds exposed by the API.")
@RestController
@RequestMapping(ApiConfig.Paths.STATISTICS)
public class StatisticsController {

  private final Map<String, String> indexNames;

  private final StatisticsService statisticsService;

  /**
   * Constructs a new instance of {@code StatisticsController} with the specified dependencies and
   * configuration properties required for document statistics functionality.
   *
   * @param statisticsService the service responsible for fetching statistical data.
   * @param normsIndexName the name of the index containing norms documents in OpenSearch.
   * @param literatureIndexName the name of the index containing literature documents in OpenSearch.
   * @param caselawsIndexName the name of the index containing caselaws documents in OpenSearch.
   */
  public StatisticsController(
      StatisticsService statisticsService,
      @Value("${opensearch.norms-index-name}") String normsIndexName,
      @Value("${opensearch.literature-index-name}") String literatureIndexName,
      @Value("${opensearch.caselaws-index-name}") String caselawsIndexName) {
    this.statisticsService = statisticsService;
    this.indexNames =
        Map.of(
            "norms", normsIndexName,
            "literature", literatureIndexName,
            "caselaws", caselawsIndexName);
  }

  /**
   * Retrieves statistical data for different types of documents, including norms, case laws, and
   * literature. The method fetches the count of documents available in each category and organizes
   * the data into {@link StatisticsApiSchema} for API responses.
   *
   * @return an instance of {@link StatisticsApiSchema} containing the counts of norms, case laws,
   *     and literature documents.
   * @throws OpenSearchFetchException if there is an issue while fetching data from OpenSearch.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public StatisticsApiSchema getStatisticsData() throws OpenSearchFetchException {

    Map<String, Long> indexCount = statisticsService.getAllCounts();

    return new StatisticsApiSchema(
        new StatisticsCountSchema(indexCount.get(this.indexNames.get("norms"))),
        new StatisticsCountSchema(indexCount.get(this.indexNames.get("caselaws"))),
        new StatisticsCountSchema(indexCount.get(this.indexNames.get("literature"))));
  }
}
