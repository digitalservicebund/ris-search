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
      // Parse the search term to differentiate between quoted (phrase) and unquoted (individual)
      // terms
      var termsResult = new QuotedStringParser(params.getSearchTerm()).parse();
      final List<String> unquotedSearchTerms = termsResult.unquotedTerms();
      final List<String> quotedSearchPhrases = termsResult.quotedTerms();

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
        MultiMatchQueryBuilder unquotedQuery =
            new MultiMatchQueryBuilder(term)
                .zeroTermsQuery(MatchQuery.ZeroTermsQuery.ALL)
                .operator(Operator.AND)
                .type(Type.CROSS_FIELDS);
        query.must(unquotedQuery);
        articleNestedQuery.should(unquotedQuery);
      }

      for (String phrase : quotedSearchPhrases) {
        // Quoted terms are treated as phrases using a Multi-Match query with type PHRASE.
        MultiMatchQueryBuilder quotedQuery = new MultiMatchQueryBuilder(phrase).type(Type.PHRASE);
        query.must(quotedQuery);
        articleNestedQuery.should(quotedQuery);
      }

      // Allow searching articles by a combined "search keyword" (e.g., article number and norm
      // abbreviation).
      // Slop is added to account for re-ordering of the components, and a boost prioritizes these
      // matches.
      articleNestedQuery.should(
          new MatchPhraseQueryBuilder("articles.search_keyword", params.getSearchTerm())
              .slop(3)
              .boost(3));

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
                  new FetchSourceContext(
                      true, new String[] {"articles.name", "articles.eid"}, null));
      articleQueryBuilder.innerHit(articleInnerHitBuilder);

      query.should(articleQueryBuilder);

      // Add a "BEST_FIELDS" query with the entire search term to boost documents
      // where the complete term appears in high-priority fields.
      query.should(
          new MultiMatchQueryBuilder(params.getSearchTerm())
              .type(Type.BEST_FIELDS)
              .operator(Operator.AND)
              .field(CaseLawDocumentationUnit.Fields.FILE_NUMBERS)
              .field(CaseLawDocumentationUnit.Fields.ECLI)
              .field(Norm.Fields.OFFICIAL_SHORT_TITLE)
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
