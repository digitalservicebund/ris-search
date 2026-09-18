package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ServerConfig;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
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

  private final String jsonldContextPath;

  /**
   * @param serverConfig to configure jsonld context path
   */
  public LegislationExpressionPartSchemaMapper(ServerConfig serverConfig) {
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
        getEncoding(article),
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

  /**
   * construct the baseUrl for encoding objects from the manifestationEli
   *
   * @param manifestationEli of a legislation object
   * @return contentBaseUrl of a legislation object
   */
  private static String getEncodingBaseUrlFromManifestationEli(String manifestationEli) {
    return ApiConfig.Paths.LEGISLATION + "/" + manifestationEli.replace(".xml", "");
  }
}
