package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.mapper.CaseLawLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexCaselawService extends BaseIndexService<CaseLawDocumentationUnit> {

  private final CaseLawRepository repository;

  @Autowired
  public IndexCaselawService(CaseLawBucket bucket, CaseLawRepository repository) {
    super(bucket);
    this.repository = repository;
  }

  @Override
  public int getNumberOfIndexedEntities() {
    return (int) repository.count();
  }

  @Override
  protected String extractIdFromFilename(String filename) {
    return filename.substring(0, filename.lastIndexOf('.'));
  }

  @Override
  protected Optional<CaseLawDocumentationUnit> mapFileToEntity(
      String filename, String fileContent) {
    try {
      return Optional.of(CaseLawLdmlToOpenSearchMapper.fromString(fileContent));
    } catch (OpenSearchMapperException e) {
      logger.error("unable to parse file {}", filename, e);
      return Optional.empty();
    }
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
  protected void saveEntity(CaseLawDocumentationUnit entity) {
    repository.save(entity);
  }
}
