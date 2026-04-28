package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import java.util.List;
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
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Service
public class ArticleSearchQueryBuilder {

  private final ElasticsearchOperations operations;

  public ArticleSearchQueryBuilder(ElasticsearchOperations operations) {
    this.operations = operations;
  }

  public SearchHits<Article> getArticleTextMatches(List<String> expressionElis, String searchTerm) {
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    boolQuery.filter(QueryBuilders.termsQuery("expression_eli", expressionElis));

    boolQuery.should(
        new MultiMatchQueryBuilder(searchTerm)
            .zeroTermsQuery(MatchQuery.ZeroTermsQuery.ALL)
            .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
            .operator(Operator.OR));

    InnerHitBuilder innerHitBuilder =
        new InnerHitBuilder()
            .setName("top_three_articles")
            .setSize(3); // This retrieves the top 3 per group

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
}
