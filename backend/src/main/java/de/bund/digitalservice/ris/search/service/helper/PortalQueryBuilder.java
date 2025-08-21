package de.bund.digitalservice.ris.search.service.helper;

import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import java.util.List;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.MultiMatchQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.search.MatchQuery;

@Getter
public class PortalQueryBuilder {

  private final BoolQueryBuilder query;
  private QuotableSearchTerm searchTerm;

  public PortalQueryBuilder(UniversalSearchParams params) {
    query = QueryBuilders.boolQuery();
    if (params == null) {
      return;
    }

    if (StringUtils.isNotEmpty(params.getSearchTerm())) {
      searchTerm = QuotableSearchTerm.parse(params.getSearchTerm());
      applyMustLogic(searchTerm.unquotedSearchTerms(), searchTerm.quotedSearchPhrases());
      applyShouldLogic(params.getSearchTerm());
    }

    DateUtils.buildQuery("DATUM", params.getDateFrom(), params.getDateTo())
        .ifPresent(query::filter);
  }

  private void applyMustLogic(List<String> unquotedSearchTerms, List<String> quotedSearchPhrases) {
    for (String term : unquotedSearchTerms) {
      // Use a Multi-Match query to search across multiple fields.
      // ZeroTermsQuery.ALL ensures that if the analyzer removes all terms (e.g., stop words),
      // the query still matches all documents instead of returning an empty result set.
      MultiMatchQueryBuilder unquotedQuery =
          new MultiMatchQueryBuilder(term)
              .zeroTermsQuery(MatchQuery.ZeroTermsQuery.ALL)
              .operator(Operator.AND)
              .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS);
      query.must(unquotedQuery);
    }

    for (String phrase : quotedSearchPhrases) {
      // Quoted terms are treated as phrases using a Multi-Match query with type PHRASE.
      MultiMatchQueryBuilder quotedQuery =
          new MultiMatchQueryBuilder(phrase).type(MultiMatchQueryBuilder.Type.PHRASE);
      query.must(quotedQuery);
    }
  }

  private void applyShouldLogic(String searchTerm) {
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
}
