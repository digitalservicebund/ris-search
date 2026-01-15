package de.bund.digitalservice.ris.search.service;

import static org.opensearch.index.query.QueryBuilders.matchQuery;

import de.bund.digitalservice.ris.search.models.api.parameters.AdministrativeDirectiveSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import java.util.List;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;

/** Simple search type for administrative directives. */
public class AdministrativeDirectiveSimpleSearchType implements SimpleSearchType {

  private final AdministrativeDirectiveSearchParams searchParams;

  public AdministrativeDirectiveSimpleSearchType(AdministrativeDirectiveSearchParams searchParams) {
    this.searchParams = searchParams;
  }

  @Override
  public List<String> getExcludedFields() {
    return List.of();
  }

  @Override
  public List<HighlightBuilder.Field> getHighlightedFields() {
    return getHighlightedFieldsStatic();
  }

  public static List<HighlightBuilder.Field> getHighlightedFieldsStatic() {
    return List.of(
        new HighlightBuilder.Field(AdministrativeDirective.Fields.HEADLINE).numOfFragments(0),
        new HighlightBuilder.Field(AdministrativeDirective.Fields.SHORT_REPORT));
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
          matchQuery(
                  AdministrativeDirective.Fields.DOCUMENT_NUMBER, searchParams.getDocumentNumber())
              .operator(Operator.AND));
    }
  }
}
