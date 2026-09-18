package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.LegislationPartType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom {@link ArticlesRepository} operations that can't be expressed as derived Spring Data query
 * methods.
 */
public interface ArticlesRepositoryCustom {

  /**
   * Retrieves articles whose document number starts with the given prefix, collapsed on the full
   * document number so that only one {@link Article} per distinct document number is returned.
   *
   * @param documentNumber the document number prefix
   * @param type the legislation part type to filter on
   * @param pageable the pagination parameters defining page size and index
   * @return a page of articles, one per distinct document number
   */
  Page<Article> findAllByDocumentNumberStartingWithAndDocumentType(
      String documentNumber, LegislationPartType type, Pageable pageable);
}
