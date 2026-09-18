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
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.service.ArticleService;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** Controller class for handling REST API requests for articles. */
@RestController()
public class ArticleController {

  private final ArticleService articleService;

  ArticleController(ArticleService articleService) {
    this.articleService = articleService;
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
   */
  @GetMapping(
      path =
          ApiConfig.Paths.ARTICLE_WORK_EXAMPLE
              + "/{jurisdiction}/{agent}/{year}/{naturalIdentifier}/{pointInTime}/{version}/{language}/{eId}")
  public void getArticleVersions(
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
      @Parameter(example = "art-z1") @PathVariable String eId) {

    ExpressionEli eli =
        new ExpressionEli(
            jurisdiction, agent, year, naturalIdentifier, pointInTime, version, language);

    List<Article> articles = articleService.getAllArticleVersions(eli, eId);

    return;
  }
}
