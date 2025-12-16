package de.bund.digitalservice.ris.search.service;

import static org.opensearch.index.query.QueryBuilders.termQuery;

import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import de.bund.digitalservice.ris.search.utils.RisHighlightBuilder;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
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
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;

/** Service class for interacting with the database and return the search results. */
public class NormSimpleSearchType implements SimpleSearchType {

  private static final int ARTICLE_INNER_HITS_SIZE = 3;

  private static final List<String> NORMS_HIGHLIGHT_CONTENT_FIELDS =
      List.of(Norm.Fields.OFFICIAL_TITLE);

  public static final List<String> NORMS_FETCH_EXCLUDED_FIELDS =
      List.of(
          Norm.Fields.ARTICLE_NAMES,
          Norm.Fields.ARTICLE_TEXTS,
          Norm.Fields.ARTICLES,
          Norm.Fields.TABLE_OF_CONTENTS);

  private final NormsSearchParams normsSearchParams;

  public NormSimpleSearchType(NormsSearchParams normsSearchParams) {
    this.normsSearchParams = normsSearchParams;
  }

  @Override
  public List<String> getExcludedFields() {
    return NORMS_FETCH_EXCLUDED_FIELDS;
  }

  @Override
  public List<HighlightBuilder.Field> getHighlightedFields() {
    return getHighlightedFieldsStatic();
  }

  public static List<HighlightBuilder.Field> getHighlightedFieldsStatic() {
    return NORMS_HIGHLIGHT_CONTENT_FIELDS.stream().map(HighlightBuilder.Field::new).toList();
  }

  @Override
  public void addExtraLogic(String searchTerm, BoolQueryBuilder query) {
    if (StringUtils.isNotEmpty(searchTerm)) {
      addSearchTerm(searchTerm, query);
    }

    if (normsSearchParams == null) {
      return;
    }
    if (normsSearchParams.getEli() != null) {
      // for eli we do an exact match on work eli
      // *termQuery is used for exact matching the whole field
      // *filter is used because exact matching the whole field makes ranking redundant
      query.filter(termQuery(Norm.Fields.WORK_ELI_KEYWORD, normsSearchParams.getEli()));
    }
    if (normsSearchParams.getMostRelevantOn() != null) {
      BoolQueryBuilder isNotNorm =
          QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(Norm.Fields.EXPRESSION_ELI));

      BoolQueryBuilder isMostRelevant = QueryBuilders.boolQuery();
      isMostRelevant.filter(
          QueryBuilders.rangeQuery(Norm.Fields.TIME_RELEVANCE_START_DATE)
              .lte(normsSearchParams.getMostRelevantOn()));
      isMostRelevant.filter(
          QueryBuilders.rangeQuery(Norm.Fields.TIME_RELEVANCE_END_DATE)
              .gte(normsSearchParams.getMostRelevantOn()));

      BoolQueryBuilder either = QueryBuilders.boolQuery().minimumShouldMatch(1);
      either.should(isMostRelevant);
      either.should(isNotNorm);
      query.must(either);
    }
    DateUtils.buildQueryForTemporalCoverage(
            normsSearchParams.getTemporalCoverageFrom(), normsSearchParams.getTemporalCoverageTo())
        .ifPresent(query::filter);
  }

  /**
   * Adds a search term to the provided query for searching within nested documents of articles.
   * This method sets up a BoolQueryBuilder that includes multi-match queries, match phrase queries,
   * match-all queries with a focus on ensuring relevant articles are matched and displayed.
   *
   * @param searchTerm the search term to be added to the query, used for searching across fields
   *     and constructing nested article queries
   * @param query the BoolQueryBuilder instance to which the constructed search logic will be
   *     appended
   */
  public static void addSearchTerm(String searchTerm, BoolQueryBuilder query) {
    // --- Article Search within Nested Documents ---
    // Norm articles are stored as nested documents and require a dedicated nested query.
    // This query aims to find relevant articles, even if only a single term within the article
    // matches.
    BoolQueryBuilder articleNestedQuery = QueryBuilders.boolQuery();

    // Use a Multi-Match query to search across multiple fields.
    // ZeroTermsQuery.ALL ensures that if the analyzer removes all terms (e.g., stop words),
    // the query still matches all documents instead of returning an empty result set.
    MultiMatchQueryBuilder nestedMatchQuery =
        new MultiMatchQueryBuilder(searchTerm)
            .zeroTermsQuery(MatchQuery.ZeroTermsQuery.ALL)
            .operator(Operator.OR)
            .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS);
    articleNestedQuery.should(nestedMatchQuery);

    // Allow searching articles by a combined "search keyword" (e.g., article number and norm
    // abbreviation).
    // Slop is added to account for re-ordering of the components.
    articleNestedQuery.should(
        new MatchPhraseQueryBuilder("articles.search_keyword", searchTerm).slop(3));

    // Include a MatchAllQuery with boost 0 to ensure all articles are considered for display,
    // even if they don't explicitly match the query, but without influencing their ranking.
    // This is useful for filling in results if few highly relevant articles are found.
    articleNestedQuery.should(new MatchAllQueryBuilder().boost(0));

    // Construct the nested query for articles.
    // ScoreMode.None maxes the nested query ranking not impact the main query ranking.
    // This is wanted because the main query already uses article_names and article_texts
    NestedQueryBuilder articleQueryBuilder =
        QueryBuilders.nestedQuery(Norm.Fields.ARTICLES, articleNestedQuery, ScoreMode.None);

    // Configure inner hits for the nested article query.
    // Inner hits enable highlighting and extraction of specific article context (name, eId)
    // to facilitate direct linking in the UI.
    InnerHitBuilder articleInnerHitBuilder =
        new InnerHitBuilder()
            .setSize(ARTICLE_INNER_HITS_SIZE)
            .setHighlightBuilder(getArticleFieldsHighlighter())
            .setFetchSourceContext(
                new FetchSourceContext(
                    true,
                    new String[] {Norm.Fields.ARTICLES + ".name", Norm.Fields.ARTICLES + ".eid"},
                    null));
    articleQueryBuilder.innerHit(articleInnerHitBuilder);

    query.should(articleQueryBuilder);
  }

  private static HighlightBuilder getArticleFieldsHighlighter() {
    return RisHighlightBuilder.baseHighlighter().field("articles.text").field("articles.name");
  }
}
