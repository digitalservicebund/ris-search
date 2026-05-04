package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.utils.RisHighlightBuilder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

  private final ElasticsearchOperations operations;

  public ArticleService(ElasticsearchOperations operations) {
    this.operations = operations;
  }

  private SearchHits<Article> searchArticles(List<String> expressionElis, String searchTerm) {
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    boolQuery.filter(QueryBuilders.termsQuery("expression_eli", expressionElis));

    boolQuery.should(
        new MultiMatchQueryBuilder(searchTerm)
            .zeroTermsQuery(MatchQuery.ZeroTermsQuery.ALL)
            .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
            .operator(Operator.OR));

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

  public void populateArticleTextMatches(SearchHits<Norm> normSearchHits, String searchTerm) {
    if (StringUtils.isEmpty(searchTerm)) {
      return;
    }
    List<String> expressionElis =
        normSearchHits.getSearchHits().stream().map(SearchHit::getId).toList();

    SearchHits<Article> articles = searchArticles(expressionElis, searchTerm);

    Map<String, Map<String, SearchHits<?>>> normInnerHitsMap =
        normSearchHits.getSearchHits().stream()
            .collect(Collectors.toMap(SearchHit::getId, SearchHit::getInnerHits));

    for (SearchHit<Article> articleSearchHit : articles.getSearchHits()) {
      String expressionEli = articleSearchHit.getContent().getExpressionEli();
      normInnerHitsMap.get(expressionEli).putAll(articleSearchHit.getInnerHits());
    }
  }

  public void populateArticleTextMatches(
      SearchPage<AbstractSearchEntity> searchHits, String searchTerm) {
    if (StringUtils.isEmpty(searchTerm)) {
      return;
    }
    List<SearchHit<AbstractSearchEntity>> normSearchHits =
        searchHits.getSearchHits().stream()
            .filter(e -> e.getContent().getClass().equals(Norm.class))
            .toList();
    List<String> expressionElis = normSearchHits.stream().map(SearchHit::getId).toList();

    SearchHits<Article> articles = searchArticles(expressionElis, searchTerm);

    Map<String, Map<String, SearchHits<?>>> normInnerHitsMap =
        normSearchHits.stream()
            .collect(Collectors.toMap(SearchHit::getId, SearchHit::getInnerHits));

    for (SearchHit<Article> articleSearchHit : articles.getSearchHits()) {
      String expressionEli = articleSearchHit.getContent().getExpressionEli();
      normInnerHitsMap.get(expressionEli).putAll(articleSearchHit.getInnerHits());
    }
  }
}
