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

@Tag(
    name = "Statistics",
    description = "Returns document count statistics for all document kinds exposed by the API.")
@RestController
@RequestMapping(ApiConfig.Paths.STATISTICS)
public class StatisticsController {

  private final Map<String, String> indexNames;

  private final StatisticsService statisticsService;

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

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public StatisticsApiSchema getStatisticsData() throws OpenSearchFetchException {

    Map<String, Long> indexCount = statisticsService.getAllCounts();

    return new StatisticsApiSchema(
        new StatisticsCountSchema(indexCount.get(this.indexNames.get("norms"))),
        new StatisticsCountSchema(indexCount.get(this.indexNames.get("caselaws"))),
        new StatisticsCountSchema(indexCount.get(this.indexNames.get("literature"))));
  }
}
