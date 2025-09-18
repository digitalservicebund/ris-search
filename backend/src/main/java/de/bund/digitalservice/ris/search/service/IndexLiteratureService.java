package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.mapper.LiteratureLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.repository.objectstorage.literature.LiteratureBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.LiteratureRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexLiteratureService extends BaseIndexService<Literature> {

  private final LiteratureRepository repository;

  @Autowired
  public IndexLiteratureService(LiteratureBucket bucket, LiteratureRepository repository) {
    super(bucket);
    this.repository = repository;
  }

  @Override
  public int getNumberOfIndexedEntities() {
    return (int) repository.count();
  }

  @Override
  protected String extractIdFromFilename(String filename) {
    return Arrays.stream(filename.split("\\.")).findFirst().orElse(null);
  }

  @Override
  protected Optional<Literature> mapFileToEntity(String filename, String fileContent) {
    return LiteratureLdmlToOpenSearchMapper.mapLdml(fileContent);
  }

  @Override
  protected List<String> getAllIndexableFilenames() {
    return objectStorage.getAllKeys().stream()
        .filter(s -> s.endsWith(".xml") && !s.contains(IndexSyncJob.CHANGELOGS_PREFIX))
        .toList();
  }

  @Override
  protected void deleteAllOldAndNullEntities(String startingTimestamp) {
    repository.deleteByIndexedAtBefore(startingTimestamp);
    repository.deleteByIndexedAtIsNull();
  }

  @Override
  protected void deleteAllEntitiesById(Iterable<String> ids) {
    repository.deleteAllById(ids);
  }

  @Override
  protected void saveEntity(Literature entity) {
    repository.save(entity);
  }
}
