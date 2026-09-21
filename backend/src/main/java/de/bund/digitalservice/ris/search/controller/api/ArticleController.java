package de.bund.digitalservice.ris.search.controller.api;

import static de.bund.digitalservice.ris.search.controller.api.NormsController.AGENT_DESCRIPTION;
import static de.bund.digitalservice.ris.search.controller.api.NormsController.AGENT_EXAMPLE;
import static de.bund.digitalservice.ris.search.controller.api.NormsController.BUND_DESCRIPTION;
import static de.bund.digitalservice.ris.search.controller.api.NormsController.BUND_EXAMPLE;
import static de.bund.digitalservice.ris.search.controller.api.NormsController.NATURAL_IDENTIFIER_DESCRIPTION;
import static de.bund.digitalservice.ris.search.controller.api.NormsController.NATURAL_IDENTIFIER_EXAMPLE;
import static de.bund.digitalservice.ris.search.controller.api.NormsController.YEAR_DESCRIPTION;
import static de.bund.digitalservice.ris.search.controller.api.NormsController.YEAR_EXAMPLE;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ServerConfig;
import de.bund.digitalservice.ris.search.mapper.LegislationExpressionPartSchemaMapper;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.opensearch.ArticleWithExpressions;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionPartSchema;
import de.bund.digitalservice.ris.search.service.ArticleService;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** Controller class for handling REST API requests for articles. */
@RestController()
public class ArticleController {

  private final ArticleService articleService;

  private final LegislationExpressionPartSchemaMapper articleMapper;

  private final String jsonldContextPath;

  ArticleController(
      ArticleService articleService,
      LegislationExpressionPartSchemaMapper articleMapper,
      ServerConfig serverConfig) {
    this.articleService = articleService;
    this.articleMapper = articleMapper;
    this.jsonldContextPath = serverConfig.getBackEndUrl() + ApiConfig.Paths.JSONLD_CONTEXT;
  }

  /**
   * Retrieve all work examples of a given Article
   *
   * @param jurisdiction the jurisdiction to which the legal document belongs
   * @param agent the agent responsible for the legal document
   * @param year the year of issuance for the legal document
   * @param naturalIdentifier an identifier for the legal document
   * @param pointInTime the point in time representing the start of the validity of the document
   * @param version the version of the document
   * @param language the language of the document
   * @param eId The identifier that denotes the specific article (§) within the legislation.
   * @param pagination the pagination parameters defining page size and index
   * @return response object of a Collection of LegislationExpressionPartSchema
   */
  @GetMapping(
      path =
          ApiConfig.Paths.ARTICLE_WORK_EXAMPLE
              + "/{jurisdiction}/{agent}/{year}/{naturalIdentifier}/{pointInTime}/{version}/{language}/{eId}")
  public ResponseEntity<CollectionSchema<LegislationExpressionPartSchema>> getArticleVersions(
      @Parameter(description = BUND_DESCRIPTION, schema = @Schema(allowableValues = {BUND_EXAMPLE}))
          @PathVariable
          String jurisdiction,
      @Parameter(description = AGENT_DESCRIPTION, example = AGENT_EXAMPLE) @PathVariable
          String agent,
      @Parameter(description = YEAR_DESCRIPTION, example = YEAR_EXAMPLE) @PathVariable String year,
      @Parameter(description = NATURAL_IDENTIFIER_DESCRIPTION, example = NATURAL_IDENTIFIER_EXAMPLE)
          @PathVariable
          String naturalIdentifier,
      @Parameter(example = "2020-06-19") @PathVariable LocalDate pointInTime,
      @Parameter(example = "2") @PathVariable Integer version,
      @Parameter(example = "deu") @PathVariable String language,
      @Parameter(example = "art-z1") @PathVariable String eId,
      @ParameterObject @Valid PaginationParams pagination) {

    ExpressionEli eli =
        new ExpressionEli(
            jurisdiction, agent, year, naturalIdentifier, pointInTime, version, language);

    Page<ArticleWithExpressions> articles =
        articleService.getAllArticleVersions(
            eli, eId, PageRequest.of(pagination.getPageIndex(), pagination.getSize()));

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            articleMapper.fromArticlePage(
                articles, ApiConfig.Paths.ARTICLE_WORK_EXAMPLE, jsonldContextPath));
  }
}
