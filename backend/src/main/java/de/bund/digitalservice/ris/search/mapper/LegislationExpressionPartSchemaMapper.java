package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.ArticleWithExpressions;
import de.bund.digitalservice.ris.search.models.opensearch.LegislationPartType;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionPartSchema;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionPartType;
import de.bund.digitalservice.ris.search.schema.LegislationObjectSchema;
import de.bund.digitalservice.ris.search.schema.PartialCollectionViewSchema;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/** Maps Articles to Api response Objects */
@Service
public class LegislationExpressionPartSchemaMapper {

  /**
   * Maps an article to a LegislationExpressionPartSchema
   *
   * @param articleWithExpressions ArticleWithExpressions to map
   * @return LegislationExpressionPartSchema
   */
  public LegislationExpressionPartSchema fromDomain(ArticleWithExpressions articleWithExpressions) {
    Article article = articleWithExpressions.article();

    return new LegislationExpressionPartSchema(
        "v1/article/" + article.getExpressionEli() + "#" + article.getEId(),
        article.getEId(),
        article.getName(),
        "",
        DateUtils.toDateIntervalString(article.getEntryIntoForceDate(), article.getExpiryDate()),
        mapLegislationPartType(article.getDocumentType()),
        getEncoding(article),
        List.of());
  }

  /**
   * @param page Page of Article objects
   * @param path original path that executed that query
   * @param remoteJsonContext remoteJsonContext url to retrieve jsonld context
   * @return return Collection of LegislationExpressionPartSchema objects
   */
  public CollectionSchema<LegislationExpressionPartSchema> fromArticlePage(
      Page<ArticleWithExpressions> page, String path, String remoteJsonContext) {
    String id = String.format("%s?pageIndex=%d&size=%d", path, page.getNumber(), page.getSize());
    PartialCollectionViewSchema view = PartialCollectionViewMapper.fromPage(path, page);

    return CollectionSchema.<LegislationExpressionPartSchema>builder()
        .id(id)
        .totalItems(page.getTotalElements())
        .context(remoteJsonContext)
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

  private List<LegislationObjectSchema> getEncoding(Article article) {
    List<LegislationObjectSchema> encoding = new ArrayList<>();
    if (Objects.nonNull(article.getManifestationEli())) {
      if (article.getDocumentType().equals(LegislationPartType.ATTACHMENT)) {
        //
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
}
