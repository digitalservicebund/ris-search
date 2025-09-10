package de.bund.digitalservice.ris.search.repository.opensearch;

public interface BaseRepository<T> {

  void deleteByIndexedAtBefore(String indexedAt);

  void deleteByIndexedAtIsNull();

  // Ignoring the warning on the usage of "? extends String" vs just "String"
  // as this the only way to allow the actual repositories
  // to implement this interface together with the elasticsearch interface
  // without a name clash on the deleteAllById method
  @SuppressWarnings("java:S4968")
  void deleteAllById(Iterable<? extends String> ids);

  long count();

  void saveEntity(T entity);
}
