package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LiteratureRepository extends ElasticsearchRepository<Literature, String> {

  void deleteByIndexedAtBefore(String indexedAt);

  void deleteByIndexedAtIsNull();

  void deleteAllById(@NotNull Iterable<? extends String> ids);

  List<Literature> findByDocumentNumber(String documentNumber);
}
