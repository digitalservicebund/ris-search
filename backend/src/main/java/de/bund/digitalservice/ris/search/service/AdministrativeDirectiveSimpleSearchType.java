package de.bund.digitalservice.ris.search.service;

import static org.opensearch.index.query.QueryBuilders.matchQuery;

import de.bund.digitalservice.ris.search.models.api.parameters.AdministrativeDirectiveSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import java.util.List;
import java.util.Map;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;

/** Simple search type for administrative directives. */
public class AdministrativeDirectiveSimpleSearchType implements SimpleSearchType {

  public static final Map<String, Float> FIELD_BOOSTS =
      Map.ofEntries(
          Map.entry(AdministrativeDirective.Fields.ACTIVE_ADMINISTRATIVE_REFERENCES, 1.0f),
          Map.entry(AdministrativeDirective.Fields.ACTIVE_NORM_REFERENCES, 1.0f),
          Map.entry(AdministrativeDirective.Fields.CASELAW_REFERENCES, 1.0f),
          Map.entry(AdministrativeDirective.Fields.DOCUMENT_NUMBER, 1.0f),
          Map.entry(AdministrativeDirective.Fields.DOCUMENT_TYPE, 1.0f),
          Map.entry(AdministrativeDirective.Fields.DOCUMENT_TYPE_DETAIL, 1.0f),
          Map.entry(AdministrativeDirective.Fields.FIELDS_OF_LAW, 1.0f),
          Map.entry(AdministrativeDirective.Fields.HEADLINE, 1.0f),
          Map.entry(AdministrativeDirective.Fields.ID, 1.0f),
          Map.entry(AdministrativeDirective.Fields.KEYWORDS, 1.0f),
          Map.entry(AdministrativeDirective.Fields.LEGISLATION_AUTHORITY, 1.0f),
          Map.entry(AdministrativeDirective.Fields.NORM_REFERENCES, 1.0f),
          Map.entry(AdministrativeDirective.Fields.OUTLINE, 1.0f),
          Map.entry(AdministrativeDirective.Fields.REFERENCES, 1.0f),
          Map.entry(AdministrativeDirective.Fields.REFERENCE_NUMBERS, 1.0f),
          Map.entry(AdministrativeDirective.Fields.SHORT_REPORT, 1.0f));
  private final AdministrativeDirectiveSearchParams searchParams;

  public AdministrativeDirectiveSimpleSearchType(AdministrativeDirectiveSearchParams searchParams) {
    this.searchParams = searchParams;
  }

  @Override
  public Map<String, Float> getBoosts() {
    return FIELD_BOOSTS;
  }

  @Override
  public List<String> getExcludedFields() {
    return List.of(AdministrativeDirective.Fields.OUTLINE);
  }

  @Override
  public List<HighlightBuilder.Field> getHighlightedFields() {
    return getHighlightedFieldsStatic();
  }

  public static List<HighlightBuilder.Field> getHighlightedFieldsStatic() {
    return List.of(
        new HighlightBuilder.Field(AdministrativeDirective.Fields.HEADLINE).numOfFragments(0),
        new HighlightBuilder.Field(AdministrativeDirective.Fields.SHORT_REPORT),
        new HighlightBuilder.Field(AdministrativeDirective.Fields.OUTLINE));
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
