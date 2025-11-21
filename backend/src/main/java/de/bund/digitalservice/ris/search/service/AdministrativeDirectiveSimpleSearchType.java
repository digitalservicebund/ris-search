package de.bund.digitalservice.ris.search.service;

import static org.opensearch.index.query.QueryBuilders.matchQuery;

import de.bund.digitalservice.ris.search.models.api.parameters.AdministrativeDirectiveSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import java.util.List;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;

public class AdministrativeDirectiveSimpleSearchType implements SimpleSearchType {

  private static final List<String> HIGHLIGHT_CONTENT_FIELDS =
      List.of(AdministrativeDirective.Fields.CONTENT, AdministrativeDirective.Fields.LONG_TITLE);

  private final AdministrativeDirectiveSearchParams searchParams;

  public AdministrativeDirectiveSimpleSearchType(AdministrativeDirectiveSearchParams searchParams) {
    this.searchParams = searchParams;
  }

  @Override
  public void addHighlightedFields(HighlightBuilder builder) {
    HIGHLIGHT_CONTENT_FIELDS.forEach(builder::field);
  }

  @Override
  public List<String> getExcludedFields() {
    return List.of();
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
