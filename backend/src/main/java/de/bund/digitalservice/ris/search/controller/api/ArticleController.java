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
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class ArticleController {

  @GetMapping(path = ApiConfig.Paths.ARTICLE + "")
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
      @Parameter(example = "deu") @PathVariable String language) {

    ExpressionEli eli =
        new ExpressionEli(
            jurisdiction, agent, year, naturalIdentifier, pointInTime, version, language);
  }
}
