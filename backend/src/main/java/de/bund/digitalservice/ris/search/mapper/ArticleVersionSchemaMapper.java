package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.ArticleWithExpressions;
import de.bund.digitalservice.ris.search.models.opensearch.LegislationPartType;
import de.bund.digitalservice.ris.search.schema.ArticleVersionSchema;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LegislationObjectSchema;
import de.bund.digitalservice.ris.search.schema.PartialCollectionViewSchema;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Page;

/** */
public class ArticleVersionSchemaMapper {

  private ArticleVersionSchemaMapper() {}

  /**
   * Maps an article to a ArticleVersionSchema
   *
   * @param articleWithExpressions ArticleWithExpressions to map
   * @return ArticleVersionSchema
   */
  public static ArticleVersionSchema fromArticleWithExpressions(
      ArticleWithExpressions articleWithExpressions) {
    Article article = articleWithExpressions.article();

    return new ArticleVersionSchema(
        ApiConfig.Paths.LEGISLATION + "/" + article.getExpressionEli() + "#" + article.getEId(),
        article.getEId(),
        article.getName(),
        DateUtils.toDateIntervalString(article.getEntryIntoForceDate(), article.getExpiryDate()),
        getEncoding(article),
        getIsPartOf(articleWithExpressions));
  }

  /**
   * @param page Page of Article objects
   * @param path original path that executed that query
   * @param remoteJsonContext remoteJsonContext url to retrieve jsonld context
   * @return return Collection of ArticleVersionSchema objects
   */
  public static CollectionSchema<ArticleVersionSchema> fromArticlePage(
      Page<ArticleWithExpressions> page, String path, String remoteJsonContext) {
    String id = String.format("%s?pageIndex=%d&size=%d", path, page.getNumber(), page.getSize());
    PartialCollectionViewSchema view = PartialCollectionViewMapper.fromPage(path, page);

    return CollectionSchema.<ArticleVersionSchema>builder()
        .id(id)
        .totalItems(page.getTotalElements())
        .context(remoteJsonContext)
        .member(page.stream().map(ArticleVersionSchemaMapper::fromArticleWithExpressions).toList())
        .view(view)
        .build();
  }

  private static List<LegislationObjectSchema> getEncoding(Article article) {
    List<LegislationObjectSchema> encoding = new ArrayList<>();
    if (Objects.nonNull(article.getManifestationEli())) {
      if (article.getDocumentType().equals(LegislationPartType.ATTACHMENT)) {
        encoding.add(
            EncodingSchemaFactory.legislationEncodingSchema(
                EncodingSchemaFactory.SchemaType.XML,
                ApiConfig.Paths.LEGISLATION
                    + "/"
                    + article.getManifestationEli().replace(".xml", "")));
      } else {
        encoding.add(
            EncodingSchemaFactory.legislationEncodingSchema(
                EncodingSchemaFactory.SchemaType.HTML,
                ApiConfig.Paths.LEGISLATION
                    + "/"
                    + article.getManifestationEli().replace(".xml", "")
                    + "/"
                    + article.getEId()));
      }
    }
    return encoding;
  }

  private static List<ArticleVersionSchema.IsPartOfReference> getIsPartOf(
      ArticleWithExpressions articleWithExpressions) {
    return articleWithExpressions.expressionElis().stream()
        .map(ArticleVersionSchema.IsPartOfReference::fromExpressionEli)
        .toList();
  }
}
