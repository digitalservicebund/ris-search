package de.bund.digitalservice.ris.search.repository.opensearch;

public interface BaseRepository<T> {

  void deleteEntitiesByIndexedAtBefore(String indexedAt);

  void deleteEntitiesByIndexedAtIsNull();

  void deleteAllEntitiesById(Iterable<String> ids);

  int countEntities();

  void saveEntity(T entity);
}
