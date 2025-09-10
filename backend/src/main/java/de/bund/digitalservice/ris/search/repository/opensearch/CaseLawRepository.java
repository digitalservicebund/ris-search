package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Repository interface for interacting with the database and managing {@link
 * CaseLawDocumentationUnit} entity. This interface extends {@link ElasticsearchRepository} and
 * focuses on operations related to {@link CaseLawDocumentationUnit}.
 */
public interface CaseLawRepository
    extends ElasticsearchRepository<CaseLawDocumentationUnit, String>,
        BaseRepository<CaseLawDocumentationUnit> {

  List<CaseLawDocumentationUnit> findByDocumentNumber(String documentNumber);

  void deleteByIndexedAtBefore(String indexedAt);

  void deleteByIndexedAtIsNull();

  void deleteAllById(@NotNull Iterable<? extends String> ids);

  @Override
  default void deleteEntitiesByIndexedAtBefore(String indexedAt) {
    deleteByIndexedAtBefore(indexedAt);
  }

  @Override
  default void deleteEntitiesByIndexedAtIsNull() {
    deleteByIndexedAtIsNull();
  }

  @Override
  default void deleteAllEntitiesById(Iterable<String> ids) {
    deleteAllById(ids);
  }

  @Override
  default int countEntities() {
    return (int) count();
  }

  @Override
  default void saveEntity(CaseLawDocumentationUnit entity) {
    save(entity);
  }
}
