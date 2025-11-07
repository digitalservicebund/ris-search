package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.OpenSearchFetchException;
import de.bund.digitalservice.ris.search.schema.StatisticsApiSchema;
import de.bund.digitalservice.ris.search.schema.StatisticsCountSchema;
import de.bund.digitalservice.ris.search.service.StatisticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Statistics",
    description =
        "Use this endpoint to get insides of the number of documents provided by this API.")
@RestController
@RequestMapping(ApiConfig.Paths.STATISTICS)
public class StatisticsController {

  private final StatisticsService statisticsService;

  public StatisticsController(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public StatisticsApiSchema getStatisticsData() throws OpenSearchFetchException {

    Map<String, Long> indexCount = statisticsService.getAllCounts();

    return new StatisticsApiSchema(
        new StatisticsCountSchema(indexCount.get("norms")),
        new StatisticsCountSchema(indexCount.get("caselaws")),
        new StatisticsCountSchema(indexCount.get("literature")));
  }
}
