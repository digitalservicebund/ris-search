package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.ArticleWithExpressions;
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
   * document number so that only one {@link Article} per distinct document number is returned. Each
   * result also carries the expressionElis of every expression that article occurs in.
   *
   * @param documentNumber the document number prefix
   * @param type the legislation part type to filter on
   * @param preferredExpressionEli if this expressionEli occurs in a collapse group, that group's
   *     Article is returned as the representative instead of whichever one OpenSearch's collapsing
   *     would otherwise pick; may be {@code null} to leave the default selection untouched
   * @param pageable the pagination parameters defining page size and index
   * @return a page of articles, one per distinct document number, with their expressionElis
   */
  Page<ArticleWithExpressions> findAllByDocumentNumberStartingWithAndDocumentType(
      String documentNumber,
      LegislationPartType type,
      String preferredExpressionEli,
      Pageable pageable);
}
