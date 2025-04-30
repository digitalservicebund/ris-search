package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import java.util.Set;
import org.springframework.data.domain.Sort;

public class SortingDefinitions {
  private SortingDefinitions() {}

  /**
   * A universal search alias field that points to more specific date fields, like decision date.
   */
  private static final String DATE_ALIAS_FIELD = "DATUM";

  public static final Set<String> documentSortFields =
      Set.of("date", DATE_ALIAS_FIELD, "courtName", "documentNumber", "legislationIdentifier");
  public static final Set<String> caseLawSortFields =
      Set.of("date", DATE_ALIAS_FIELD, "courtName", "documentNumber");
  public static final Set<String> normsSortFields =
      Set.of("date", DATE_ALIAS_FIELD, "legislationIdentifier");

  public static boolean canSortByField(
      String openSearchFieldName, MappingDefinitions.ResolutionMode resolutionMode) {
    return switch (resolutionMode) {
      case ALL -> documentSortFields.contains(openSearchFieldName);
      case NORMS -> normsSortFields.contains(openSearchFieldName);
      case CASE_LAW -> caseLawSortFields.contains(openSearchFieldName);
    };
  }

  public static Sort getDefaultSort(MappingDefinitions.ResolutionMode resolutionMode) {
    return switch (resolutionMode) {
      case ALL -> Sort.by(Sort.Direction.DESC, DATE_ALIAS_FIELD);
      case NORMS -> Sort.by(Sort.Direction.DESC, Norm.Fields.NORMS_DATE);
      case CASE_LAW -> Sort.by(Sort.Direction.DESC, CaseLawDocumentationUnit.Fields.DECISION_DATE);
    };
  }
}
