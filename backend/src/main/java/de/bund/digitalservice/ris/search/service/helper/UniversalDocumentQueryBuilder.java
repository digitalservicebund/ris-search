package de.bund.digitalservice.ris.search.service.helper;

import static org.opensearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.opensearch.index.query.QueryBuilders.matchQuery;

import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawDocumentTypeGroup;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import de.bund.digitalservice.ris.search.utils.QuotedStringParser;
import de.bund.digitalservice.ris.search.utils.RisHighlightBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.jetbrains.annotations.NotNull;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.InnerHitBuilder;
import org.opensearch.index.query.MatchAllQueryBuilder;
import org.opensearch.index.query.MatchPhraseQueryBuilder;
import org.opensearch.index.query.MultiMatchQueryBuilder;
import org.opensearch.index.query.MultiMatchQueryBuilder.Type;
import org.opensearch.index.query.NestedQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.TermQueryBuilder;
import org.opensearch.index.search.MatchQuery;
import org.opensearch.search.fetch.subphase.FetchSourceContext;

/**
 * This class accepts different service parameter groups, such as {@link NormsSearchParams}, and
 * adds them to a BoolQuery that can be processed by OpenSearch.
 */
@Getter
public class UniversalDocumentQueryBuilder {
  BoolQueryBuilder query;
  static final int ARTICLE_INNER_HITS_SIZE = 3;

  public UniversalDocumentQueryBuilder() {
    query = QueryBuilders.boolQuery();
  }

  /**
   * Adds those parameters to the query object that may apply to both norms and case law.
   * Nevertheless, it contains some norms-specific logic, to allow generic parameters to match
   * norm-specific fields.
   *
   * @return The mutated UniversalDocumentQueryBuilder instance.
   */
  public UniversalDocumentQueryBuilder withUniversalSearchParams(UniversalSearchParams params) {
    if (params == null) {
      return this;
    }
    if (StringUtils.isNotEmpty(params.getSearchTerm())) {
      // separate quoted terms (contained in "") from unquoted terms
      var parsed = new QuotedStringParser(params.getSearchTerm()).parse();
      final List<String> unquotedTerms = parsed.unquotedTerms();
      final List<String> quotedTerms = parsed.quotedTerms();

      /*
       * Norm articles are stored as nested fields, and need to be searched separately using a nested query.
       * Since this is a separate query, the quoted and unquoted terms need to be applied to it separately.
       * This separate query is applied to the main query using QueryBuilders.nestedQuery.
       *
       * Unlike for the main query, minimumShouldMatch is set to 1: For example, if a user searches for the norm
       * abbreviation and a keyword in an article, only that keyword needs to match in order for the article
       * to be relevant.
       *
       * A simpler implementation would search a flat list of articles for the query terms. In order to show
       * the article name and link (eId), however, the context needs to be retrieved using a nested query.
       */
      BoolQueryBuilder articleQuery = QueryBuilders.boolQuery();
      articleQuery.minimumShouldMatch(1);

      for (var unquotedTerm : unquotedTerms) {
        /*
         * Each unquoted term is searched for using a
         * <a href="https://docs.opensearch.org/docs/latest/query-dsl/full-text/multi-match/">Multi-Match query</a>,
         * which searches multiple fields at once.
         * ZeroTermsQuery.ALL ensures that if the analyzer removes all terms (e.g. stop words), the now-empty
         * term matches all documents instead of causing an empty result set.
         * */
        MultiMatchQueryBuilder queryBuilder =
            new MultiMatchQueryBuilder(unquotedTerm)
                .zeroTermsQuery(MatchQuery.ZeroTermsQuery.ALL)
                .operator(Operator.AND)
                .type(Type.CROSS_FIELDS);
        query.must(queryBuilder);
        articleQuery.should(queryBuilder);
      }
      for (var quotedTerm : quotedTerms) {
        /*
         * Each quoted term is searched for using a Multi-Match query, like for unquoted terms, but with type
         * set to PHRASE.
         * */
        MultiMatchQueryBuilder queryBuilder =
            new MultiMatchQueryBuilder(quotedTerm).type(Type.PHRASE);
        query.must(queryBuilder);
        articleQuery.should(queryBuilder);
      }

      /*
       * Allow searching articles by "search keyword", consisting of an article number and the norm abbreviation.
       * The slop parameter is added to allow for re-ordering of article number and abbreviation.
       */
      articleQuery.should(
          new MatchPhraseQueryBuilder("articles.search_keyword", params.getSearchTerm())
              .slop(3)
              .boost(3));

      /*
       * The search tries to show ARTICLE_INNER_HITS_SIZE number of relevant articles.
       * If fewer articles are found to be relevant, other articles should be shown in the textMatches instead.
       * However, they should not influence the ranking.
       * Therefore, match all (=all else) with boost 0 (no influence on ranking).
       */
      articleQuery.should(new MatchAllQueryBuilder().boost(0));

      /*
       * ScoreMode.Max is chosen to ensure that documents are ranked based on the highest-scoring matching article.
       * This prevents documents with many lower-scoring articles from disproportionately ranking higher than
       * documents with fewer, but highly relevant, articles.
       */
      NestedQueryBuilder nestedArticleQuery =
          QueryBuilders.nestedQuery("articles", articleQuery, ScoreMode.Max);

      /*
       * The "inner hits" property on the nested query is used to configure highlighting, and that the
       * necessary context (article name and eId) are returned together with any text highlights so that
       * the UI may link directly to those articles.
       * */
      InnerHitBuilder innerHitBuilder = new InnerHitBuilder().setSize(ARTICLE_INNER_HITS_SIZE);
      innerHitBuilder.setHighlightBuilder(RisHighlightBuilder.getArticleFieldsHighlighter());
      innerHitBuilder.setFetchSourceContext(
          new FetchSourceContext(true, new String[] {"articles.name", "articles.eid"}, null));
      nestedArticleQuery.innerHit(innerHitBuilder);

      query.should(nestedArticleQuery);

      /*
       Add a "BEST_FIELDS" query with the whole term in order to boost cases where the whole searchTerm appears in one
       field.
      */
      query.should(
          new MultiMatchQueryBuilder(params.getSearchTerm())
              .type(Type.BEST_FIELDS)
              .operator(Operator.AND)
              .field(CaseLawDocumentationUnit.Fields.FILE_NUMBERS)
              .field(CaseLawDocumentationUnit.Fields.ECLI)
              .field(Norm.Fields.OFFICIAL_ABBREVIATION));
    }
    DateUtils.buildQuery("DATUM", params.getDateFrom(), params.getDateTo())
        .ifPresent(query::filter);
    return this;
  }

  public UniversalDocumentQueryBuilder withNormsParams(NormsSearchParams params) {
    if (params == null) return this;
    if (params.getEli() != null) {
      query.must(matchQuery("work_eli", params.getEli()));
    }
    DateUtils.buildQueryForTemporalCoverage(
            params.getTemporalCoverageFrom(), params.getTemporalCoverageTo())
        .ifPresent(query::filter);

    return this;
  }

  public UniversalDocumentQueryBuilder withCaseLawSearchParams(CaseLawSearchParams params) {
    if (params == null) return this;
    if (params.getEcli() != null) {
      query.must(matchQuery("ecli", params.getEcli()));
    }
    if (params.getFileNumber() != null) {
      query.filter(matchQuery("file_numbers", params.getFileNumber()));
    }
    if (params.getCourt() != null) {
      var either =
          QueryBuilders.boolQuery()
              .should(
                  matchQuery(
                      CaseLawDocumentationUnit.Fields.COURT_KEYWORD_KEYWORD, params.getCourt()))
              .should(
                  matchPhraseQuery(CaseLawDocumentationUnit.Fields.COURT_TYPE, params.getCourt()))
              .minimumShouldMatch(1);
      query.filter(either);
    }
    if (params.getLegalEffect() != null) {
      query.filter(matchQuery("legal_effect", params.getLegalEffect().toString()));
    }
    if (params.getType() != null) {
      queryDocumentType(params.getType());
    }
    if (params.getTypeGroup() != null) {
      queryDocumentTypeGroup(params.getTypeGroup());
    }
    return this;
  }

  private void queryDocumentTypeGroup(CaseLawDocumentTypeGroup[] types) {
    var boolQuery = QueryBuilders.boolQuery().minimumShouldMatch(1);
    for (CaseLawDocumentTypeGroup group : types) {
      if (group == CaseLawDocumentTypeGroup.OTHER) {
        // query for all decisions that aren't one of the two main document types, "urteil" or
        // "beschluss"
        boolQuery.should(
            QueryBuilders.boolQuery()
                .mustNot(matchQuery(DOCUMENT_TYPE, "beschluss"))
                .mustNot(matchQuery(DOCUMENT_TYPE, "urteil")));
      } else {
        // use a match query to get subtypes, e.g., "Teilurteil" for query "Urteil"
        boolQuery.should(matchQuery(DOCUMENT_TYPE, group.toString().toLowerCase()));
      }
    }
    query.filter(boolQuery);
  }

  private static final String DOCUMENT_TYPE = "document_type";

  private void queryDocumentType(@NotNull String[] types) {
    // use the document_type.keyword field to match the query exactly
    Stream<TermQueryBuilder> termQueries =
        Arrays.stream(types)
            .map(documentType -> QueryBuilders.termQuery(DOCUMENT_TYPE + ".keyword", documentType));
    var boolQuery = QueryBuilders.boolQuery().minimumShouldMatch(1);
    termQueries.forEach(boolQuery::should);
    query.must(boolQuery);
  }
}
