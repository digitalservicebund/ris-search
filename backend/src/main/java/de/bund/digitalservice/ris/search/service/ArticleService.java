package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.opensearch.ArticlesRepository;
import de.bund.digitalservice.ris.search.utils.LuceneQueryTools;
import de.bund.digitalservice.ris.search.utils.RisHighlightBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
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

  private SearchHits<Article> searchArticles(
      Set<String> expressionElis, String searchString, boolean isAdvancedSearch) {
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    boolQuery.filter(QueryBuilders.termsQuery("expression_eli", expressionElis));

    if (isAdvancedSearch) {
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

  public <T> void populateArticleTextMatches(
      List<SearchHit<T>> normSearchHits, String searchString, boolean isAdvancedSearch) {
    if (StringUtils.isEmpty(searchString)) {
      return;
    }
    Map<String, Map<String, SearchHits<?>>> normInnerHitsMap =
        normSearchHits.stream()
            .collect(Collectors.toMap(SearchHit::getId, SearchHit::getInnerHits));
    Set<String> expressionElis = normInnerHitsMap.keySet();
    if (isAdvancedSearch) {
      try {
        searchString = LuceneQueryTools.joinAllTermsWithOr(searchString);
      } catch (CustomValidationException e) {
        // This should never happen, but if it does happen we will get an error in the logs. It
        // could only happen if the lucene query was valid, but our transformation turned it
        // invalid.
        logger.error(
            "Error transforming lucene query for articles. Trying to fail gracefully by returning no articles.",
            e);
        return;
      }
    }
    SearchHits<Article> articles = searchArticles(expressionElis, searchString, isAdvancedSearch);

    for (SearchHit<Article> articleSearchHit : articles.getSearchHits()) {
      String expressionEli = articleSearchHit.getContent().getExpressionEli();
      normInnerHitsMap.get(expressionEli).putAll(articleSearchHit.getInnerHits());
    }
  }

  public void populateArticles(Norm norm, String expressionEli) {
    norm.setArticles(articlesRepository.findAllByExpressionEli(expressionEli));
  }
}
