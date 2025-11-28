package de.bund.digitalservice.ris.search.integration.controller.api.testData;

import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.service.AllDocumentsService;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;

/** Utility class for generating and retrieving test data in search integration tests. */
public class TestDataGenerator {

  /**
   * Searches all documents using the given AllDocumentsService and search term, returning a list of
   * AbstractSearchEntity results.
   *
   * @param allDocumentsService The AllDocumentsService to use for the search.
   * @param searchTerm The search term to use for the search.
   * @return List of AbstractSearchEntity results matching the search term.
   */
  public static List<AbstractSearchEntity> searchAll(
      AllDocumentsService allDocumentsService, String searchTerm) {
    UniversalSearchParams universalSearchParams = new UniversalSearchParams();

    universalSearchParams.setSearchTerm(searchTerm);
    SearchPage<AbstractSearchEntity> result =
        allDocumentsService.simpleSearchAllDocuments(
            universalSearchParams, null, null, null, null, null, Pageable.ofSize(10000));
    return result.get().map(SearchHit::getContent).toList();
  }

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
