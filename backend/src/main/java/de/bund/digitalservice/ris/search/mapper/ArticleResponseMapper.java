package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ServerConfig;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.LegislationPartType;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionPartSchema;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionPartType;
import de.bund.digitalservice.ris.search.schema.PartialCollectionViewSchema;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/** Maps Articles to Api response Objects */
@Service
public class ArticleResponseMapper {

  private final String jsonldContextPath;

  /**
   * @param serverConfig to configure jsonld context path
   */
  public ArticleResponseMapper(ServerConfig serverConfig) {
    this.jsonldContextPath = serverConfig.getBackEndUrl() + ApiConfig.Paths.JSONLD_CONTEXT;
  }

  /**
   * Maps an article to a LegislationExpressionPartSchema
   *
   * @param article Article to map
   * @return LegislationExpressionPartSchema
   */
  public LegislationExpressionPartSchema fromDomain(Article article) {
    return new LegislationExpressionPartSchema(
        "v1/article/" + article.getExpressionEli() + "#" + article.getEId(),
        article.getEId(),
        article.getName(),
        "",
        DateUtils.toDateIntervalString(article.getEntryIntoForceDate(), article.getExpiryDate()),
        mapLegislationPartType(article.getDocumentType()),
        List.of(),
        List.of());
  }

  /**
   * @param page Page of Article objects
   * @param path original path that executed that query
   * @return return Collection of LegislationExpressionPartSchema objects
   */
  public CollectionSchema<LegislationExpressionPartSchema> fromArticlePage(
      Page<Article> page, String path) {
    String id = String.format("%s?pageIndex=%d&size=%d", path, page.getNumber(), page.getSize());
    PartialCollectionViewSchema view = PartialCollectionViewMapper.fromPage(path, page);

    return CollectionSchema.<LegislationExpressionPartSchema>builder()
        .id(id)
        .totalItems(page.getTotalElements())
        .context(jsonldContextPath)
        .member(page.stream().map(this::fromDomain).toList())
        .view(view)
        .build();
  }

  /**
   * maps an opensearch LegislationPartType to an LegislationExpressionPartType
   *
   * @param type opensearch LegislationPartType
   * @return partType of the Api Schema
   */
  public static LegislationExpressionPartType mapLegislationPartType(LegislationPartType type) {
    return switch (type) {
      case null -> null;
      case ARTICLE -> LegislationExpressionPartType.ARTICLE;
      case ATTACHMENT -> LegislationExpressionPartType.ATTACHMENT;
      case CONCLUSION -> LegislationExpressionPartType.CONCLUSION;
      case PREAMBLE -> LegislationExpressionPartType.PREAMBLE;
    };
  }
}
