package de.bund.digitalservice.ris.search.repository.opensearch;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface DocumentRepository<T> extends ElasticsearchRepository<T, String> {

  void deleteByIndexedAtBefore(String indexedAt);

  void deleteByIndexedAtIsNull();

  void deleteAllById(@NotNull Iterable<? extends String> ids);

  List<T> findByDocumentNumber(String documentNumber);
}
