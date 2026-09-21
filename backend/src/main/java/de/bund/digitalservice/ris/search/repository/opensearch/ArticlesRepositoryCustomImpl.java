package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.ArticleWithExpressions;
import de.bund.digitalservice.ris.search.models.opensearch.LegislationPartType;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import java.util.List;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.index.query.InnerHitBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.collapse.CollapseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;

/** Repository with custom Article operations */
public class ArticlesRepositoryCustomImpl implements ArticlesRepositoryCustom {

  private static final String EXPRESSIONS_INNER_HIT_NAME = "expressions";

  private static final int MAX_EXPRESSIONS_PER_ARTICLE = 100;

  private final ElasticsearchOperations operations;

  public ArticlesRepositoryCustomImpl(ElasticsearchOperations operations) {
    this.operations = operations;
  }

  /**
   * Query all Articles and their associated expressions grouped by their documentNumber.
   *
   * @param documentNumber the document number prefix
   * @param type the legislation part type to filter on
   * @param pageable the pagination parameters defining page size and index
   * @return Page of ArticleWithExpressions
   */
  @Override
  public Page<ArticleWithExpressions> findAllByDocumentNumberStartingWithAndDocumentType(
      String documentNumber, LegislationPartType type, Pageable pageable) {

    CollapseBuilder collapseBuilder =
        new CollapseBuilder(Article.Fields.DOCUMENT_NUMBER)
            .setInnerHits(
                new InnerHitBuilder()
                    .setName(EXPRESSIONS_INNER_HIT_NAME)
                    .setSize(MAX_EXPRESSIONS_PER_ARTICLE));

    NativeSearchQuery query =
        new NativeSearchQueryBuilder()
            .withQuery(
                QueryBuilders.boolQuery()
                    .filter(
                        QueryBuilders.prefixQuery(Article.Fields.DOCUMENT_NUMBER, documentNumber))
                    .filter(QueryBuilders.termQuery(Article.Fields.DOCUMENT_TYPE, type.name())))
            .withCollapseBuilder(collapseBuilder)
            .withPageable(pageable)
            .build();

    SearchHits<Article> hits = operations.search(query, Article.class);
    SearchPage<Article> searchPage = PageUtils.unwrapSearchHits(hits, pageable);
    return searchPage.map(this::toArticleWithExpressions);
  }

  private ArticleWithExpressions toArticleWithExpressions(SearchHit<Article> hit) {
    SearchHits<?> innerHits = hit.getInnerHits().get(EXPRESSIONS_INNER_HIT_NAME);
    List<String> expressionElis =
        innerHits == null
            ? List.of(hit.getContent().getExpressionEli())
            : innerHits.stream()
                .map(innerHit -> ((Article) innerHit.getContent()).getExpressionEli())
                .distinct()
                .toList();
    return new ArticleWithExpressions(hit.getContent(), expressionElis);
  }
}
