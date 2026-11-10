package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ServerConfig;
import de.bund.digitalservice.ris.search.mapper.ArticleVersionSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.NormSearchResponseMapper;
import de.bund.digitalservice.ris.search.models.opensearch.ArticleWithExpressions;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.schema.ArticleVersionSchema;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionSearchSchema;
import de.bund.digitalservice.ris.search.service.ArticleService;
import de.bund.digitalservice.ris.search.service.NormsService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** Controller class for handling REST API requests for articles. */
@RestController
public class ArticleController {

  private final ArticleService articleService;
  private final NormsService normsService;

  private final String jsonldContextPath;

  ArticleController(
      ArticleService articleService, NormsService normsService, ServerConfig serverConfig) {
    this.articleService = articleService;
    this.normsService = normsService;
    this.jsonldContextPath = serverConfig.getBackEndUrl() + ApiConfig.Paths.JSONLD_CONTEXT;
  }

  /**
   * Retrieve all work examples of a given Article
   *
   * @param revision The identifier of the article revision
   * @return response object of a Collection of ArticleVersionSchema
   */
  @GetMapping(path = ApiConfig.Paths.ARTICLE_WORK_EXAMPLE + "/{revision}")
  public ResponseEntity<CollectionSchema<ArticleVersionSchema>> getArticleVersionsByRevision(
      @Parameter @PathVariable String revision) {

    Page<ArticleWithExpressions> articles = articleService.getAllArticleVersions(revision);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            ArticleVersionSchemaMapper.fromArticlePage(
                articles,
                ApiConfig.Paths.ARTICLE_WORK_EXAMPLE + "/" + revision,
                jsonldContextPath));
  }

  /**
   * Retrieve all LegislationExpressions that a given article revision is part of
   *
   * @param revision The identifier of the article revision
   * @return a paginated collection {@link CollectionSchema} of expression level metadata {@link
   *     LegislationExpressionSearchSchema}
   */
  @GetMapping(path = ApiConfig.Paths.ARTICLE + "/{revision}/legislations")
  public ResponseEntity<CollectionSchema<LegislationExpressionSearchSchema>>
      getLegislationExpressionsByArticleRevision(@Parameter @PathVariable String revision) {

    Page<Norm> norms = normsService.getAllNormsContainingArticle(revision);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            NormSearchResponseMapper.fromNormsPage(
                norms,
                ApiConfig.Paths.ARTICLE + "/" + revision + "/legislations",
                jsonldContextPath));
  }
}
