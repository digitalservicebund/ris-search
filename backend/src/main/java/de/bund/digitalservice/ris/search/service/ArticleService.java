package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.ArticleWithExpressions;
import de.bund.digitalservice.ris.search.models.opensearch.LegislationPartType;
import de.bund.digitalservice.ris.search.repository.opensearch.ArticlesRepository;
import de.bund.digitalservice.ris.search.utils.RisHighlightBuilder;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEliPath;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
import org.opensearch.search.collapse.CollapseBuilder;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.opensearch.search.sort.SortBuilders;
import org.opensearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

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
    boolQuery.filter(QueryBuilders.termsQuery(Article.Fields.EXPRESSION_ELI, expressionElis));

    if (isLuceneQuery) {
      boolQuery.must(QueryBuilders.queryStringQuery(searchString));
    } else {
      boolQuery.must(
          new MultiMatchQueryBuilder(searchString)
              .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
              .operator(Operator.OR));

      boolQuery.should(
          QueryBuilders.matchQuery(Article.Fields.ARTICLE_FINGERPRINT, searchString).boost(10.0f));
    }

    HighlightBuilder highlightBuilder =
        RisHighlightBuilder.baseHighlighter().field(Article.Fields.NAME).field(Article.Fields.TEXT);

    InnerHitBuilder innerHitBuilder =
        new InnerHitBuilder()
            .setName("top_three_articles")
            .setSize(3)
            .addSort(SortBuilders.scoreSort().order(SortOrder.DESC))
            // Secondary tie-breaker sort
            .addSort(SortBuilders.fieldSort("_id").order(SortOrder.ASC))
            .setHighlightBuilder(highlightBuilder);

    CollapseBuilder collapseBuilder =
        new CollapseBuilder(Article.Fields.EXPRESSION_ELI).setInnerHits(innerHitBuilder);

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

  /**
   * This method takes a possible eid and returns the best matching eid if one exists. An eid can
   * contain % and therefore can't be directly used as a path variable. When it does contain %, all
   * known cases are the result of a uri encoding, but not all eids are uri encoded. For example an
   * eid could be präambel-n1 (not uri encoded) or art-za%20b (is a uri encoding of "artz-a b"). The
   * xslt provides a partial workaround by skipping the normal uri encoding when building the url
   * containing an eid. We still need to check both possibilities here because it's unclear which
   * case we are dealing with.
   *
   * @param expressionEli expressionEli of the norm
   * @param eidGiven the possible eid
   * @return the real eid if it exists, or empty if it can't be found.
   */
  public Optional<String> getActualEid(String expressionEli, String eidGiven) {
    if (articleExist(expressionEli, eidGiven)) {
      return Optional.of(eidGiven);
    }
    String encodedEid = UriUtils.encode(eidGiven, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
    if (articleExist(expressionEli, encodedEid)) {
      return Optional.of(encodedEid);
    }
    return Optional.empty();
  }

  /**
   * Retrieves a List of all versions of an Article across the whole work it belongs to. A
   * combination of expressionEli and eId is is used to retrieve the dokNr from the ris metadata.
   * The eId is not guaranteed to be a stable identifier across all expressions.
   *
   * @param expressionEliPath expressionEli of the norm
   * @param eidGiven the possible eid
   * @return List of version of that article across the whole work
   */
  public Page<ArticleWithExpressions> getAllArticleVersions(
      ExpressionEliPath expressionEliPath, String eidGiven) {
    String expressionEliString = expressionEliPath.toString();

    Sort sort = Sort.by(Sort.Direction.DESC, "entryIntoForceDate");
    Pageable sortedPageable = Pageable.unpaged(sort);

    return getActualEid(expressionEliString, eidGiven)
        .flatMap(
            actualEid ->
                articlesRepository.findById(Article.buildId(expressionEliString, actualEid)))
        .map(Article::getDocumentNumber)
        .map(
            docNr ->
                this.getAllArticleVersionsByDocumentNumberPrefix(
                    docNr, expressionEliString, sortedPageable))
        .orElseGet(Page::empty);
  }

  /**
   * Retrieves a List of all versions of an Article across the whole work it belongs to, together
   * with the expressionElis every version occurs in. The documentNumber is used as the article
   * identifier. Restricts prefix lookup to minimum-length document numbers to avoid unintended
   * matches.
   *
   * @param documentNumber of a given article
   * @param preferredExpressionEli the expressionEli that was originally queried; if it occurs among
   *     a version group's expressions, that group's returned Article is forced to be the one
   *     belonging to this expressionEli, instead of an arbitrary one
   * @return List of Article objects of the same article across all its expressions, with their
   *     expressionElis
   */
  private Page<ArticleWithExpressions> getAllArticleVersionsByDocumentNumberPrefix(
      String documentNumber, String preferredExpressionEli, Pageable page) {

    try {
      return articlesRepository.findAllVersionsByDocumentNumber(
          documentNumber, LegislationPartType.ARTICLE, preferredExpressionEli, page);
    } catch (IllegalArgumentException _) {
      return Page.empty();
    }
  }

  private boolean articleExist(String expressionEli, String eid) {
    return articlesRepository.existsById(Article.buildId(expressionEli, eid));
  }
}
