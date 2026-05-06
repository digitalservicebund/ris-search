package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.opensearch.ArticlesRepository;
import de.bund.digitalservice.ris.search.utils.RisHighlightBuilder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.opensearch.action.search.SearchType;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.InnerHitBuilder;
import org.opensearch.index.query.MultiMatchQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.collapse.CollapseBuilder;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

  private final ElasticsearchOperations operations;
  private final ArticlesRepository articlesRepository;

  public ArticleService(ElasticsearchOperations operations, ArticlesRepository articlesRepository) {
    this.operations = operations;
    this.articlesRepository = articlesRepository;
  }

  public <T extends AbstractSearchEntity> SearchHits<Article> searchArticles(
      SearchPage<T> searchPage, String searchTerm, boolean isLuceneQuery) {
    if (searchPage.isEmpty() || searchTerm == null || searchTerm.isBlank()) {
      return getEmptySearchHitsImpl();
    }

    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    boolQuery.filter(QueryBuilders.termsQuery("expression_eli", getExpressionElis(searchPage)));

    if (isLuceneQuery) {
      boolQuery.should(QueryBuilders.queryStringQuery(searchTerm));
    } else {
      boolQuery.should(
          new MultiMatchQueryBuilder(searchTerm)
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

  private SearchHits<Article> getEmptySearchHitsImpl() {
    return new SearchHitsImpl<>(
        0, TotalHitsRelation.EQUAL_TO, 0, Duration.ZERO, null, null, List.of(), null, null, null);
  }

  private <T extends AbstractSearchEntity> List<String> getExpressionElis(
      SearchPage<T> searchPage) {
    List<String> expressionElis = new ArrayList<>();
    searchPage
        .getSearchHits()
        .getSearchHits()
        .forEach(
            hit -> {
              if (hit.getContent() instanceof Norm) {
                expressionElis.add(((Norm) hit.getContent()).getExpressionEli());
              }
            });
    return expressionElis;
  }

  public <T extends AbstractSearchEntity> void populateArticleTextMatches(
      SearchPage<T> searchHits, SearchHits<Article> articles) {

    List<SearchHit<T>> normSearchHits =
        searchHits.getSearchHits().stream()
            .filter(e -> e.getContent().getClass().equals(Norm.class))
            .toList();

    Map<String, Map<String, SearchHits<?>>> normInnerHitsMap =
        normSearchHits.stream()
            .collect(Collectors.toMap(SearchHit::getId, SearchHit::getInnerHits));

    for (SearchHit<Article> articleSearchHit : articles.getSearchHits()) {
      String expressionEli = articleSearchHit.getContent().getExpressionEli();
      normInnerHitsMap.get(expressionEli).putAll(articleSearchHit.getInnerHits());
    }
  }

  public void populateArticles(Norm norm, String expressionEli) {
    norm.setArticles(articlesRepository.findAllByExpressionEli(expressionEli));
  }
}
