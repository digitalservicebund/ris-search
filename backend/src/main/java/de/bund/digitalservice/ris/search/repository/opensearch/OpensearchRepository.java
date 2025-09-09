package de.bund.digitalservice.ris.search.repository.opensearch;

public interface OpensearchRepository<T> {

  void deleteByIndexedAtBefore(String indexedAt);

  void deleteByIndexedAtIsNull();

  void deleteAllById(Iterable<? extends String> ids);

  long count();

  void saveEntity(T entity);
}
