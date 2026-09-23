package de.bund.digitalservice.ris.search.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.ArticleWithExpressions;
import de.bund.digitalservice.ris.search.models.opensearch.LegislationPartType;
import de.bund.digitalservice.ris.search.schema.ArticleVersionSchema;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionPartType;
import de.bund.digitalservice.ris.search.schema.LegislationObjectSchema;
import de.bund.digitalservice.ris.search.schema.PartialCollectionViewSchema;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class ArticleVersionSchemaMapperTest {

  @Test
  void itMapsAPageOfArticlesToACollectionSchema() {
    Article article =
        Article.builder()
            .eId("art-z1")
            .expressionEli("eli/bund/bgbl-1/1975/s1000/1975-01-01/1/deu")
            .name("§ 1")
            .entryIntoForceDate(LocalDate.of(1975, Month.JANUARY, 1))
            .expiryDate(null)
            .manifestationEli("eli/bund/bgbl-1/1975/s1000/1975-01-01/1/deu/regelungstext-1.xml")
            .documentType(LegislationPartType.ARTICLE)
            .build();
    ArticleWithExpressions articleWithExpressions =
        new ArticleWithExpressions(article, List.of(article.getExpressionEli()));

    var page = new PageImpl<>(List.of(articleWithExpressions), PageRequest.of(0, 10), 1);
    String path = "/v1/legislation/work/eli/bund/bgbl-1/1975/s1000/art-z1";
    String remoteJsonContext = "/v1/jsonld/context";

    CollectionSchema<ArticleVersionSchema> result =
        ArticleVersionSchemaMapper.fromArticlePage(page, path, remoteJsonContext);

    ArticleVersionSchema expectedMember =
        ArticleVersionSchema.builder()
            .id("/v1/legislation/eli/bund/bgbl-1/1975/s1000/1975-01-01/1/deu#art-z1")
            .eId("art-z1")
            .name("§ 1")
            .temporalCoverage("1975-01-01/..")
            .partType(LegislationExpressionPartType.ARTICLE)
            .encoding(
                List.of(
                    LegislationObjectSchema.builder()
                        .id(
                            "/v1/legislation/eli/bund/bgbl-1/1975/s1000/1975-01-01/1/deu/regelungstext-1/art-z1/html")
                        .contentUrl(
                            "/v1/legislation/eli/bund/bgbl-1/1975/s1000/1975-01-01/1/deu/regelungstext-1/art-z1.html")
                        .encodingFormat("text/html")
                        .inLanguage("de")
                        .build()))
            .isPartOf(
                List.of(
                    new ArticleVersionSchema.IsPartOfReference(
                        "/v1/legislation/eli/bund/bgbl-1/1975/s1000/1975-01-01/1/deu")))
            .build();

    assertEquals(remoteJsonContext, result.context());
    assertEquals(path + "?pageIndex=0&size=10", result.id());
    assertEquals(1, result.totalItems());
    assertEquals(List.of(expectedMember), result.member());
    assertEquals(
        PartialCollectionViewSchema.builder()
            .first(path + "?pageIndex=0&size=10")
            .last(path + "?pageIndex=0&size=10")
            .build(),
        result.view());
  }
}
