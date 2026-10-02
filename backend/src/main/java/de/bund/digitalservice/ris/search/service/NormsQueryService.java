package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.opensearch.ArticlesRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Service for cross-concern queries spanning norms and the articles they consist of. Kept separate
 * from {@link NormsService} and {@link ArticleService} to avoid a circular dependency between them.
 */
@Service
public class NormsQueryService {

  private final NormsRepository normsRepository;

  private final ArticlesRepository articlesRepository;

  /**
   * Constructor for NormsQueryService.
   *
   * @param normsRepository Repository for Norm entities.
   * @param articlesRepository Repository for Article entities.
   */
  public NormsQueryService(NormsRepository normsRepository, ArticlesRepository articlesRepository) {
    this.normsRepository = normsRepository;
    this.articlesRepository = articlesRepository;
  }

  /**
   * Retrieve all norm expressions that contain the article revision with the given document number.
   *
   * @param documentNumber document number of the article revision
   * @return An unpaged {@link Page} of all {@link Norm} expressions containing the article, sorted
   *     by entryIntoForceDate descending, or an empty page if no article with the given document
   *     number exists.
   */
  public Page<Norm> getAllNormsContainingArticle(String documentNumber) {
    List<String> expressionElis =
        articlesRepository.findExpressionElisByDocumentNumber(documentNumber).stream()
            .map(Article::getExpressionEli)
            .toList();
    if (expressionElis.isEmpty()) {
      return Page.empty();
    }
    return normsRepository.findAllByIdIn(
        expressionElis, Pageable.unpaged(Sort.by(Sort.Direction.DESC, "entryIntoForceDate")));
  }
}
