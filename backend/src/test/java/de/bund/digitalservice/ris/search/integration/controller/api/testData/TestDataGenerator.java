package de.bund.digitalservice.ris.search.integration.controller.api.testData;

import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import java.util.List;

/** Utility class for generating and retrieving test data in search integration tests. */
public class TestDataGenerator {

  /**
   * Returns the IDs of all CaseLawDocumentationUnit entities in the given list.
   *
   * @param searchEntities List of AbstractSearchEntity objects to extract CaseLawDocumentationUnit
   *     IDs from.
   * @return List of IDs of CaseLawDocumentationUnit entities.
   */
  public static List<String> getCaseLawIds(List<AbstractSearchEntity> searchEntities) {
    return get(searchEntities, CaseLawDocumentationUnit.class).stream()
        .map(CaseLawDocumentationUnit::id)
        .toList();
  }

  public static List<String> getLiteratureIds(List<AbstractSearchEntity> searchEntities) {
    return get(searchEntities, Literature.class).stream().map(Literature::id).toList();
  }

  public static List<String> getNormIds(List<AbstractSearchEntity> searchEntities) {
    return get(searchEntities, Norm.class).stream().map(Norm::getId).toList();
  }

  private static <T extends AbstractSearchEntity> List<T> get(
      List<AbstractSearchEntity> input, Class<T> type) {
    return input.stream().filter(type::isInstance).map(type::cast).toList();
  }
}
