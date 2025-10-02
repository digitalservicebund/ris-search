package de.bund.digitalservice.ris.search.service.helper;

import static org.opensearch.index.query.QueryBuilders.matchQuery;

import de.bund.digitalservice.ris.search.models.api.parameters.LiteratureSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import java.util.Arrays;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilders;

/** Helper class to build OpenSearch queries for Literature entities. */
public class LiteratureQueryBuilder {

  private LiteratureQueryBuilder() {}

  /**
   * Adds filters to the given query based on the provided search parameters.
   *
   * @param params the search parameters (may be null)
   * @param query the main BoolQueryBuilder to which filters will be added
   */
  public static void addLiteratureFilters(LiteratureSearchParams params, BoolQueryBuilder query) {
    if (params == null) {
      return;
    }

    if (params.getDocumentNumber() != null) {
      query.must(
          matchQuery(Literature.Fields.DOCUMENT_NUMBER, params.getDocumentNumber())
              .operator(Operator.AND));
    }

    // Array fields
    addArrayFilter(query, Literature.Fields.YEARS_OF_PUBLICATION, params.getYearOfPublication());
    addArrayFilter(query, Literature.Fields.DOCUMENT_TYPES, params.getDocumentType());
    addArrayFilter(query, Literature.Fields.AUTHORS, params.getAuthor());
    addArrayFilter(query, Literature.Fields.COLLABORATORS, params.getCollaborator());
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
