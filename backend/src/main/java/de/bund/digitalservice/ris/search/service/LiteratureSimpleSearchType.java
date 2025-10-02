package de.bund.digitalservice.ris.search.service;

import static org.opensearch.index.query.QueryBuilders.matchQuery;

import de.bund.digitalservice.ris.search.models.ParsedSearchTerm;
import de.bund.digitalservice.ris.search.models.api.parameters.LiteratureSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import java.util.Arrays;
import java.util.List;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;

public class LiteratureSimpleSearchType implements SimpleSearchType {

  private static final List<String> LITERATURE_HIGHLIGHT_CONTENT_FIELDS =
      List.of(Literature.Fields.MAIN_TITLE);

  private static final List<String> LITERATURE_FETCH_EXCLUDED_FIELDS =
      List.of(Literature.Fields.OUTLINE, Literature.Fields.SHORT_REPORT);

  private final LiteratureSearchParams searchParams;

  public LiteratureSimpleSearchType(LiteratureSearchParams searchParams) {
    this.searchParams = searchParams;
  }

  @Override
  public void addHighlightedFields(HighlightBuilder builder) {
    LITERATURE_HIGHLIGHT_CONTENT_FIELDS.forEach(builder::field);
  }

  @Override
  public List<String> getExcludedFields() {
    return LITERATURE_FETCH_EXCLUDED_FIELDS;
  }

  /**
   * Adds filters to the given query based on the provided search parameters.
   *
   * @param searchTerm the searchTerm (may be null)
   * @param query the main BoolQueryBuilder to which filters will be added
   */
  @Override
  public void addExtraLogic(ParsedSearchTerm searchTerm, BoolQueryBuilder query) {
    if (searchParams == null) {
      return;
    }

    if (searchParams.getDocumentNumber() != null) {
      query.must(
          matchQuery(Literature.Fields.DOCUMENT_NUMBER, searchParams.getDocumentNumber())
              .operator(Operator.AND));
    }

    // Array fields
    addArrayFilter(
        query, Literature.Fields.YEARS_OF_PUBLICATION, searchParams.getYearOfPublication());
    addArrayFilter(query, Literature.Fields.DOCUMENT_TYPES, searchParams.getDocumentType());
    addArrayFilter(query, Literature.Fields.AUTHORS, searchParams.getAuthor());
    addArrayFilter(query, Literature.Fields.COLLABORATORS, searchParams.getCollaborator());
  }

  /**
   * Adds a filter to the main query for an array of values.
   *
   * <p>Each element in the array is added as a {@code matchQuery} on the field, wrapped in a {@code
   * boolQuery} with {@code should} clauses. The main query will require at least one of the values
   * to match.
   *
   * @param query the main BoolQueryBuilder to which the filter will be added
   * @param field the name of the field in the OpenSearch index
   * @param values an array of values to filter by; if null or empty, no filter is added
   */
  private static void addArrayFilter(BoolQueryBuilder query, String field, String[] values) {
    if (values == null || values.length == 0) return;

    var boolQuery = QueryBuilders.boolQuery().minimumShouldMatch(1);
    Arrays.stream(values)
        .map(value -> QueryBuilders.matchQuery(field, value).operator(Operator.AND))
        .forEach(boolQuery::should);

    query.must(boolQuery);
  }
}
