package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import org.springframework.data.domain.Sort;

public class SortingDefinitions {
  private SortingDefinitions() {}

  /**
   * A universal search alias field that points to more specific date fields, like decision date.
   */
  private static final String DATE_ALIAS_FIELD = "DATUM";

  public static Sort getDefaultSort(MappingDefinitions.ResolutionMode resolutionMode) {
    return switch (resolutionMode) {
      case ALL -> Sort.by(Sort.Direction.DESC, DATE_ALIAS_FIELD);
      case NORMS -> Sort.by(Sort.Direction.DESC, Norm.Fields.NORMS_DATE);
      case CASE_LAW -> Sort.by(Sort.Direction.DESC, CaseLawDocumentationUnit.Fields.DECISION_DATE);
    };
  }
}
