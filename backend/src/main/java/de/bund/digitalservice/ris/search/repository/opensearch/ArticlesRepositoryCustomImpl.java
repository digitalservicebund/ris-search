package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.ArticleWithExpressions;
import de.bund.digitalservice.ris.search.models.opensearch.LegislationPartType;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import java.util.List;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.data.client.orhlc.OpenSearchAggregations;
import org.opensearch.index.query.InnerHitBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.opensearch.search.aggregations.metrics.ParsedCardinality;
import org.opensearch.search.collapse.CollapseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;

/** Repository with custom Article operations */
public class ArticlesRepositoryCustomImpl implements ArticlesRepositoryCustom {

  private static final String EXPRESSIONS_INNER_HIT_NAME = "expressions";

  private static final String DISTINCT_DOCUMENT_NUMBERS_AGGREGATION_NAME =
      "distinct_document_numbers";

  private static final int MAX_EXPRESSIONS_PER_ARTICLE = 100;

  private final ElasticsearchOperations operations;

  // the first 21 characters of the document number identify an article across expressions
  private static final int DOC_NUMBER_PREFIX_LENGTH = 21;

  public ArticlesRepositoryCustomImpl(ElasticsearchOperations operations) {
    this.operations = operations;
  }

  /**
   * Query all Articles and their associated expressions grouped by their documentNumber.
   *
   * <p>OpenSearch's field collapsing reports {@code hits.total} as the number of matching documents
   * before collapsing, not the number of distinct collapse groups. A cardinality aggregation on the
   * collapse field is used instead to report the correct total number of distinct document numbers.
   *
   * @param documentNumber the document number prefix
   * @param type the legislation part type to filter on
   * @param preferredExpressionEli if this expressionEli occurs in a collapse group, that group's
   *     Article is returned as the representative instead of whichever one OpenSearch's collapsing
   *     would otherwise pick; may be {@code null} to leave the default selection untouched
   * @param pageable the pagination parameters defining page size and index
   * @return Page of ArticleWithExpressions
   */
  @Override
  public Page<ArticleWithExpressions> findAllVersionsByDocumentNumber(
      String documentNumber,
      LegislationPartType type,
      String preferredExpressionEli,
      Pageable pageable) {

    if (documentNumber.length() <= DOC_NUMBER_PREFIX_LENGTH) {
      throw new IllegalArgumentException("document number is too short");
    }
    String prefix = documentNumber.substring(0, DOC_NUMBER_PREFIX_LENGTH);

    CollapseBuilder collapseBuilder =
        new CollapseBuilder(Article.Fields.DOCUMENT_NUMBER)
            .setInnerHits(
                new InnerHitBuilder()
                    .setName(EXPRESSIONS_INNER_HIT_NAME)
                    .setSize(MAX_EXPRESSIONS_PER_ARTICLE));

    // query cardinality across document numbers to get proper total hits after collapsing
    CardinalityAggregationBuilder distinctDocumentNumbersAggregation =
        new CardinalityAggregationBuilder(DISTINCT_DOCUMENT_NUMBERS_AGGREGATION_NAME)
            .field(Article.Fields.DOCUMENT_NUMBER);

    NativeSearchQuery query =
        new NativeSearchQueryBuilder()
            .withQuery(
                QueryBuilders.boolQuery()
                    .filter(QueryBuilders.prefixQuery(Article.Fields.DOCUMENT_NUMBER, prefix))
                    .filter(QueryBuilders.termQuery(Article.Fields.DOCUMENT_TYPE, type.name())))
            .withCollapseBuilder(collapseBuilder)
            .withAggregations(distinctDocumentNumbersAggregation)
            .withPageable(pageable)
            .build();

    SearchHits<Article> hits = operations.search(query, Article.class);
    SearchPage<Article> searchPage = PageUtils.unwrapSearchHits(hits, pageable);
    List<ArticleWithExpressions> content =
        searchPage.stream()
            .map(hit -> toArticleWithExpressions(hit, preferredExpressionEli, documentNumber))
            .toList();
    return new PageImpl<>(content, pageable, getDistinctDocumentNumberCount(hits));
  }

  private long getDistinctDocumentNumberCount(SearchHits<Article> hits) {
    if (!(hits.getAggregations() instanceof OpenSearchAggregations aggregationsWrapper)) {
      return hits.getTotalHits();
    }
    ParsedCardinality cardinality =
        (ParsedCardinality)
            aggregationsWrapper
                .aggregations()
                .getAsMap()
                .get(DISTINCT_DOCUMENT_NUMBERS_AGGREGATION_NAME);
    return cardinality.getValue();
  }

  private ArticleWithExpressions toArticleWithExpressions(
      SearchHit<Article> hit, String preferredExpressionEli, String documentNumber) {
    SearchHits<?> innerHits = hit.getInnerHits().get(EXPRESSIONS_INNER_HIT_NAME);
    List<Article> versions =
        innerHits == null
            ? List.of(hit.getContent())
            : innerHits.stream().map(innerHit -> (Article) innerHit.getContent()).toList();

    Article representative = hit.getContent();
    if (representative.getDocumentNumber().equals(documentNumber)) {
      representative =
          versions.stream()
              .filter(article -> article.getExpressionEli().equals(preferredExpressionEli))
              .findFirst()
              .orElse(representative);
    }

    List<String> expressionElis =
        versions.stream().map(Article::getExpressionEli).distinct().toList();

    return new ArticleWithExpressions(representative, expressionElis);
  }
}
