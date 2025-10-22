package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.ParsedSearchTerm;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import de.bund.digitalservice.ris.search.utils.RisHighlightBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.opensearch.action.search.SearchType;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.MultiMatchQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.search.MatchQuery;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.stereotype.Service;

@Service
public class SimpleSearchQueryBuilder {

  private final SearchTermParser searchTermParser;

  public SimpleSearchQueryBuilder(SearchTermParser searchTermParser) {
    this.searchTermParser = searchTermParser;
  }

  public NativeSearchQuery buildQuery(
      List<SimpleSearchType> searchTypes,
      @NotNull UniversalSearchParams params,
      Pageable pageable) {
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

    // handle searchTerm
    ParsedSearchTerm parsedSearchTerm = searchTermParser.parse(params.getSearchTerm());
    if (StringUtils.isNotEmpty(parsedSearchTerm.original())) {
      applyMustLogic(
          parsedSearchTerm.unquotedTokens(), parsedSearchTerm.quotedSearchPhrases(), boolQuery);
      applyShouldLogic(parsedSearchTerm.original(), boolQuery);
    }
    // handle date
    DateUtils.buildQuery("DATUM", params.getDateFrom(), params.getDateTo())
        .ifPresent(boolQuery::filter);

    HighlightBuilder highlightBuilder = RisHighlightBuilder.baseHighlighter();
    List<String> excludedFields = new ArrayList<>();
    for (SimpleSearchType searchType : searchTypes) {
      searchType.addHighlightedFields(highlightBuilder);
      excludedFields.addAll(searchType.getExcludedFields());
      searchType.addExtraLogic(params.getSearchTerm(), boolQuery);
    }

    // add pagination and other parameters
    NativeSearchQuery result =
        new NativeSearchQueryBuilder()
            .withSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .withPageable(pageable)
            .withQuery(boolQuery)
            .withHighlightBuilder(highlightBuilder)
            .build();

    // exclude fields with long text from search results
    result.addSourceFilter(
        new FetchSourceFilter(true, null, excludedFields.toArray(String[]::new)));

    return result;
  }

  public static final Map<String, Float> caseLawFieldBoosts =
      Map.of(
          CaseLawDocumentationUnit.Fields.GUIDING_PRINCIPLE, convertOrderingToBoost(2),
          CaseLawDocumentationUnit.Fields.HEADLINE, convertOrderingToBoost(3),
          CaseLawDocumentationUnit.Fields.OTHER_HEADNOTE, convertOrderingToBoost(3),
          CaseLawDocumentationUnit.Fields.TENOR, convertOrderingToBoost(3),
          CaseLawDocumentationUnit.Fields.DECISION_GROUNDS, convertOrderingToBoost(4),
          CaseLawDocumentationUnit.Fields.GROUNDS, convertOrderingToBoost(4),
          CaseLawDocumentationUnit.Fields.CASE_FACTS, convertOrderingToBoost(5),
          CaseLawDocumentationUnit.Fields.OTHER_LONG_TEXT, convertOrderingToBoost(6),
          CaseLawDocumentationUnit.Fields.DISSENTING_OPINION, convertOrderingToBoost(7));

  public static final Map<String, Float> normFieldBoosts =
      Map.of(
          Norm.Fields.OFFICIAL_ABBREVIATION, convertOrderingToBoost(1),
          Norm.Fields.OFFICIAL_SHORT_TITLE, convertOrderingToBoost(1),
          Norm.Fields.OFFICIAL_TITLE, convertOrderingToBoost(1),
          Norm.Fields.PREAMBLE_FORMULA, convertOrderingToBoost(2),
          Norm.Fields.ARTICLE_NAMES, convertOrderingToBoost(2),
          Norm.Fields.ARTICLE_TEXTS, convertOrderingToBoost(2));

  private void applyMustLogic(
      List<String> unquotedSearchTokens, List<String> quotedSearchPhrases, BoolQueryBuilder query) {
    // Our filtering logic is that all unquotedSearchTokens and all quotedSearchPhrases occur in at
    // least one (not necessarily the same) field. This is the so called "AND" logic.
    // The entirety of our filtering logic is in this method. The QueryBuilder classes only hold
    // logic that is not related to filtering. For example, ranking and/or highlighting.
    // In particular, that means that for norms when an article was the thing that satisfied the
    // filtering requirement this code is still responsible for the filtering logic.
    // We satisfy this requirement with the article_names and article_texts fields which are NOT
    // nested and therefore work here
    // In addition, to filtering the must clauses are responsible for ranking and boosting

    for (String term : unquotedSearchTokens) {
      query.must(buildOneClause(term, false));
    }

    for (String phrase : quotedSearchPhrases) {
      // Quoted terms use opensearch phrase search
      query.must(buildOneClause(phrase, true));
    }
  }

  private void applyShouldLogic(String searchTerm, BoolQueryBuilder query) {
    // Targeted search. If the entire search term is an exact match for a unique identifier it
    // should get a very large boost.
    query.should(
        new MultiMatchQueryBuilder(searchTerm)
            .field(CaseLawDocumentationUnit.Fields.ECLI_KEYWORD)
            .field(CaseLawDocumentationUnit.Fields.FILE_NUMBERS_KEYWORD)
            .field(Norm.Fields.WORK_ELI_KEYWORD)
            .field(Norm.Fields.EXPRESSION_ELI_KEYWORD)
            .field(Norm.Fields.OFFICIAL_TITLE_KEYWORD)
            .field(Norm.Fields.OFFICIAL_SHORT_TITLE_KEYWORD)
            .field(Norm.Fields.OFFICIAL_ABBREVIATION_KEYWORD)
            .boost(10.0f));
  }

  private MultiMatchQueryBuilder buildOneClause(String searchedText, boolean phraseMatch) {
    // Use a Multi-Match query to search across multiple fields.
    // ZeroTermsQuery.ALL ensures that if the analyzer removes all terms (e.g., stop words),
    // the query still matches all documents instead of returning an empty result set.
    MultiMatchQueryBuilder result =
        new MultiMatchQueryBuilder(searchedText)
            .zeroTermsQuery(MatchQuery.ZeroTermsQuery.ALL)
            .operator(Operator.AND)
            .field("*", 1.0f)
            .fields(caseLawFieldBoosts)
            .fields(normFieldBoosts);
    if (phraseMatch) {
      result.type(MultiMatchQueryBuilder.Type.PHRASE);
    } else {
      result.type(MultiMatchQueryBuilder.Type.BEST_FIELDS);
    }
    return result;
  }

  static Float convertOrderingToBoost(Integer ordering) {
    return switch (ordering) {
      case 1 -> 5.0f;
      case 2 -> 1.8f;
      case 3 -> 1.7f;
      case 4 -> 1.6f;
      case 5 -> 1.5f;
      case 6 -> 1.4f;
      case 7 -> 1.3f;
      default -> throw new IllegalArgumentException("Unknown ordering: " + ordering);
    };
  }
}
