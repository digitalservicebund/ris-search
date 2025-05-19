package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Repository interface for interacting with the database and managing {@link
 * CaseLawDocumentationUnit} entity. This interface extends {@link ElasticsearchRepository} and
 * focuses on operations related to {@link CaseLawDocumentationUnit}.
 */
public interface CaseLawSynthesizedRepository
    extends ElasticsearchRepository<CaseLawDocumentationUnit, String> {
  List<CaseLawDocumentationUnit> findByDocumentNumber(String documentNumber);

  void deleteByIndexedAtBefore(String indexedAt);

  void deleteAllById(Iterable<? extends String> ids);
}
