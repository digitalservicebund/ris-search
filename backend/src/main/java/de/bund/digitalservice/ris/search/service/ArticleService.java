package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.repository.opensearch.ArticlesRepository;
import de.bund.digitalservice.ris.search.utils.RisHighlightBuilder;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.search.SearchType;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.InnerHitBuilder;
import org.opensearch.index.query.MultiMatchQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.search.MatchQuery;
import org.opensearch.search.collapse.CollapseBuilder;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {
  protected static final Logger logger = LogManager.getLogger(ArticleService.class);

  private final ElasticsearchOperations operations;
  private final ArticlesRepository articlesRepository;

  public ArticleService(ElasticsearchOperations operations, ArticlesRepository articlesRepository) {
    this.operations = operations;
    this.articlesRepository = articlesRepository;
  }

  /**
   * Accepts a Set of expressionElis and retrieves the top 3 article hits for every expressionEli.
   *
   * @param expressionElis List of Norm expression elis
   * @param searchString the searchTerm or query used to collect article hits
   * @param isLuceneQuery determine if the searchString is a lucene query or a term
   * @return SearchHits of articles
   */
  public SearchHits<Article> searchTopThreeArticlesByExpressionELi(
      Set<String> expressionElis, String searchString, boolean isLuceneQuery) {

    if (expressionElis.isEmpty()) {
      return emptyArticleHits();
    }

    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    boolQuery.filter(QueryBuilders.termsQuery("expression_eli", expressionElis));

    if (isLuceneQuery) {
      boolQuery.should(QueryBuilders.queryStringQuery(searchString));
    } else {
      boolQuery.should(
          new MultiMatchQueryBuilder(searchString)
              .zeroTermsQuery(MatchQuery.ZeroTermsQuery.ALL)
              .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
              .operator(Operator.OR));
    }

    HighlightBuilder highlightBuilder =
        RisHighlightBuilder.baseHighlighter().field("name").field("text");

    InnerHitBuilder innerHitBuilder =
        new InnerHitBuilder()
            .setName("top_three_articles")
            .setSize(3)
            .setHighlightBuilder(highlightBuilder);

    CollapseBuilder collapseBuilder =
        new CollapseBuilder("expression_eli").setInnerHits(innerHitBuilder);

    NativeSearchQuery articleQuery =
        new NativeSearchQueryBuilder()
            .withSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .withQuery(boolQuery)
            .withCollapseBuilder(collapseBuilder)
            .build();

    return operations.search(articleQuery, Article.class);
  }

  private SearchHits<Article> emptyArticleHits() {
    return new SearchHitsImpl<>(
        0, TotalHitsRelation.EQUAL_TO, 0f, Duration.ZERO, null, null, List.of(), null, null, null);
  }

  /**
   * Retrieves a List of Articles belonging to a specific norm Expression
   *
   * @param expressionEli expressionEli of the norm
   * @return List of Articles belonging to the given expressionEli
   */
  public List<Article> findAllByExpressionEli(String expressionEli) {
    return articlesRepository.findAllByExpressionEli(expressionEli);
  }
}
