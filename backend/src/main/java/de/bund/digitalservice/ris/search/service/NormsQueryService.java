package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.opensearch.ArticlesRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NormsQueryService {

  NormsRepository normsRepository;

  ArticlesRepository articlesRepository;

  public NormsQueryService(NormsRepository normsRepository, ArticlesRepository articlesRepository) {
    this.normsRepository = normsRepository;
    this.articlesRepository = articlesRepository;
  }

  public Page<Norm> getAllNormsContainingArticle(String documentNumber) {
    List<String> expressionElis =
        articlesRepository.findAllByDocumentNumber(documentNumber).stream()
            .map(Article::getExpressionEli)
            .toList();
    if (expressionElis.isEmpty()) {
      return Page.empty();
    }
    return normsRepository.findAllByIdIn(expressionElis, Pageable.unpaged());
  }
}
