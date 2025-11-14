package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AdministrativeDirectiveRepository
    extends ElasticsearchRepository<AdministrativeDirective, String> {

  void deleteByIndexedAtBefore(String indexedAt);

  void deleteByIndexedAtIsNull();

  void deleteAllById(@NotNull Iterable<? extends String> ids);

  List<AdministrativeDirective> findByDocumentNumber(String documentNumber);
}
