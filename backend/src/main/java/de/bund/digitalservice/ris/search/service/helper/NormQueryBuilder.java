package de.bund.digitalservice.ris.search.service.helper;

import static org.opensearch.index.query.QueryBuilders.matchQuery;

import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import de.bund.digitalservice.ris.search.utils.RisHighlightBuilder;
import java.util.List;
import org.apache.lucene.search.join.ScoreMode;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.InnerHitBuilder;
import org.opensearch.index.query.MatchAllQueryBuilder;
import org.opensearch.index.query.MatchPhraseQueryBuilder;
import org.opensearch.index.query.MultiMatchQueryBuilder;
import org.opensearch.index.query.NestedQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.search.MatchQuery;
import org.opensearch.search.fetch.subphase.FetchSourceContext;

public class NormQueryBuilder {

  private NormQueryBuilder() {}

  static final int ARTICLE_INNER_HITS_SIZE = 3;

  public static void addSearchTerm(
      String searchTerm,
      List<String> unquotedSearchTerms,
      List<String> quotedSearchPhrases,
      BoolQueryBuilder query) {
    // --- Article Search within Nested Documents ---
    // Norm articles are stored as nested documents and require a dedicated nested query.
    // This query aims to find relevant articles, even if only a single term within the article
    // matches.
    BoolQueryBuilder articleNestedQuery = QueryBuilders.boolQuery();
    articleNestedQuery.minimumShouldMatch(1); // At least one clause must match within an article

    for (String term : unquotedSearchTerms) {
      // Use a Multi-Match query to search across multiple fields.
      // ZeroTermsQuery.ALL ensures that if the analyzer removes all terms (e.g., stop words),
      // the query still matches all documents instead of returning an empty result set.
      MultiMatchQueryBuilder nestedMatchQuery =
          new MultiMatchQueryBuilder(term)
              .zeroTermsQuery(MatchQuery.ZeroTermsQuery.ALL)
              .operator(Operator.OR)
              .type(MultiMatchQueryBuilder.Type.BEST_FIELDS);
      articleNestedQuery.should(nestedMatchQuery);
    }

    for (String phrase : quotedSearchPhrases) {
      // Quoted terms are treated as phrases using a Multi-Match query with type PHRASE.
      MultiMatchQueryBuilder quotedQuery =
          new MultiMatchQueryBuilder(phrase).type(MultiMatchQueryBuilder.Type.PHRASE);
      articleNestedQuery.should(quotedQuery);
    }

    // Allow searching articles by a combined "search keyword" (e.g., article number and norm
    // abbreviation).
    // Slop is added to account for re-ordering of the components, and a boost prioritizes these
    // matches.
    articleNestedQuery.should(
        new MatchPhraseQueryBuilder("articles.search_keyword", searchTerm).slop(3).boost(3));

    // Include a MatchAllQuery with boost 0 to ensure all articles are considered for display,
    // even if they don't explicitly match the query, but without influencing their ranking.
    // This is useful for filling in results if few highly relevant articles are found.
    articleNestedQuery.should(new MatchAllQueryBuilder().boost(0));

    // Construct the nested query for articles.
    // ScoreMode.Max ensures that documents are ranked by their highest-scoring matching article,
    // preventing documents with many low-scoring articles from outranking those with fewer,
    // highly relevant ones.
    NestedQueryBuilder articleQueryBuilder =
        QueryBuilders.nestedQuery("articles", articleNestedQuery, ScoreMode.Max);

    // Configure inner hits for the nested article query.
    // Inner hits enable highlighting and extraction of specific article context (name, eId)
    // to facilitate direct linking in the UI.
    InnerHitBuilder articleInnerHitBuilder =
        new InnerHitBuilder()
            .setSize(ARTICLE_INNER_HITS_SIZE)
            .setHighlightBuilder(RisHighlightBuilder.getArticleFieldsHighlighter())
            .setFetchSourceContext(
                new FetchSourceContext(true, new String[] {"articles.name", "articles.eid"}, null));
    articleQueryBuilder.innerHit(articleInnerHitBuilder);

    query.should(articleQueryBuilder);
  }

  public static void addNormFilters(NormsSearchParams params, BoolQueryBuilder query) {
    if (params == null) {
      return;
    }
    if (params.getEli() != null) {
      query.must(matchQuery("work_eli", params.getEli()).operator(Operator.AND));
    }
    DateUtils.buildQueryForTemporalCoverage(
            params.getTemporalCoverageFrom(), params.getTemporalCoverageTo())
        .ifPresent(query::filter);
  }
}
