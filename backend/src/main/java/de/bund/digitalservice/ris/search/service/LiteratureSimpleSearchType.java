package de.bund.digitalservice.ris.search.service;

import static org.opensearch.index.query.QueryBuilders.matchQuery;

import de.bund.digitalservice.ris.search.models.api.parameters.LiteratureSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;

/** Service class for interacting with the database and return the search results. */
public class LiteratureSimpleSearchType implements SimpleSearchType {

  public static final Map<String, Float> FIELD_BOOSTS =
      Map.ofEntries(
          Map.entry(Literature.Fields.ADDITIONAL_TITLES, 1.0f),
          Map.entry(Literature.Fields.AUTHORS, 1.0f),
          Map.entry(Literature.Fields.COLLABORATORS, 1.0f),
          Map.entry(Literature.Fields.CONFERENCE_NOTE, 1.0f),
          Map.entry(Literature.Fields.DEPENDENT_REFERENCES, 1.0f),
          Map.entry(Literature.Fields.DOCUMENTARY_TITLE, 1.0f),
          Map.entry(Literature.Fields.DOCUMENT_NUMBER, 1.0f),
          Map.entry(Literature.Fields.DOCUMENT_TYPES, 1.0f),
          Map.entry(Literature.Fields.EDITION, 1.0f),
          Map.entry(Literature.Fields.EDITOR, 1.0f),
          Map.entry(Literature.Fields.FOOTNOTES, 1.0f),
          Map.entry(Literature.Fields.FOUNDER, 1.0f),
          Map.entry(Literature.Fields.FULL_TITLE_ADDITIONS, 1.0f),
          Map.entry(Literature.Fields.ID, 1.0f),
          Map.entry(Literature.Fields.INDEPENDENT_REFERENCES, 1.0f),
          Map.entry(Literature.Fields.INTERNATIONAL_IDENTIFIERS, 1.0f),
          Map.entry(Literature.Fields.LANGUAGE, 1.0f),
          Map.entry(Literature.Fields.MAIN_TITLE, 1.0f),
          Map.entry(Literature.Fields.MAIN_TITLE_ADDITIONS, 1.0f),
          Map.entry(Literature.Fields.NORM_REFERENCES, 1.0f),
          Map.entry(Literature.Fields.ORIGINATOR, 1.0f),
          Map.entry(Literature.Fields.OUTLINE, 1.0f),
          Map.entry(Literature.Fields.PUBLISHER_INFORMATION, 1.0f),
          Map.entry(Literature.Fields.PUBLISHER_ORGANIZATIONS, 1.0f),
          Map.entry(Literature.Fields.PUBLISHER_PERSONS, 1.0f),
          Map.entry(Literature.Fields.SHORT_REPORT, 1.0f),
          Map.entry(Literature.Fields.SHORT_TITLES, 1.0f),
          Map.entry(Literature.Fields.UNIVERSITY_NOTES, 1.0f),
          Map.entry(Literature.Fields.VOLUMES, 1.0f),
          Map.entry(Literature.Fields.YEARS_OF_PUBLICATION, 1.0f));
  private static final List<String> LITERATURE_FETCH_EXCLUDED_FIELDS =
      List.of(Literature.Fields.OUTLINE);

  private final LiteratureSearchParams searchParams;

  public LiteratureSimpleSearchType(LiteratureSearchParams searchParams) {
    this.searchParams = searchParams;
  }

  public static List<HighlightBuilder.Field> getHighlightedFieldsStatic() {
    return List.of(
        new HighlightBuilder.Field(Literature.Fields.MAIN_TITLE).numOfFragments(0),
        new HighlightBuilder.Field(Literature.Fields.DOCUMENTARY_TITLE).numOfFragments(0),
        new HighlightBuilder.Field(Literature.Fields.OUTLINE),
        new HighlightBuilder.Field(Literature.Fields.SHORT_REPORT));
  }

  @Override
  public Map<String, Float> getBoosts() {
    return FIELD_BOOSTS;
  }

  @Override
  public List<String> getExcludedFields() {
    return LITERATURE_FETCH_EXCLUDED_FIELDS;
  }

  @Override
  public List<HighlightBuilder.Field> getHighlightedFields() {
    return getHighlightedFieldsStatic();
  }

  /**
   * Adds filters to the given query based on the provided search parameters.
   *
   * @param searchTerm the searchTerm (may be null)
   * @param query the main BoolQueryBuilder to which filters will be added
   */
  @Override
  public void addExtraLogic(String searchTerm, BoolQueryBuilder query) {
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
