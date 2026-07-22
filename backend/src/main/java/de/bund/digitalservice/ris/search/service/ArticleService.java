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
import org.opensearch.index.query.MatchPhraseQueryBuilder;
import org.opensearch.index.query.MultiMatchQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.collapse.CollapseBuilder;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.opensearch.search.sort.SortBuilders;
import org.opensearch.search.sort.SortOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;
import org.springframework.stereotype.Service;

/**
 * Service class for interacting with the database and return the search results. This class is
 * annotated with {@link Service} to indicate that it's a service component in the Spring context.
 */
@Service
public class ArticleService {
  protected static final Logger logger = LogManager.getLogger(ArticleService.class);

  private final ElasticsearchOperations operations;
  private final ArticlesRepository articlesRepository;

  /**
   * Constructs a new instance of {@code ArticleService}.
   *
   * @param operations the ElasticsearchOperations instance to interact with Elasticsearch.
   * @param articlesRepository The repository for interacting with the OpenSearch articles.
   */
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
      boolQuery.must(QueryBuilders.queryStringQuery(searchString));
    } else {
      boolQuery.must(
          new MultiMatchQueryBuilder(searchString)
              .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
              .operator(Operator.OR));

      // Allow searching articles by a combined "search keyword" (e.g., article number and norm
      // abbreviation).
      // Slop is added to account for re-ordering of the components.
      boolQuery.should(new MatchPhraseQueryBuilder("search_keyword", searchString).slop(3));
    }

    HighlightBuilder highlightBuilder =
        RisHighlightBuilder.baseHighlighter().field("name").field("text");

    InnerHitBuilder innerHitBuilder =
        new InnerHitBuilder()
            .setName("top_three_articles")
            .setSize(3)
            .addSort(SortBuilders.scoreSort().order(SortOrder.DESC))
            // Secondary tie-breaker sort (e.g., by eid)
            .addSort(SortBuilders.fieldSort("eid").order(SortOrder.ASC))
            .setHighlightBuilder(highlightBuilder);

    CollapseBuilder collapseBuilder =
        new CollapseBuilder("expression_eli").setInnerHits(innerHitBuilder);

    NativeSearchQuery articleQuery =
        new NativeSearchQueryBuilder()
            .withSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .withPageable(Pageable.ofSize(expressionElis.size()))
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
