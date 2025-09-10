package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.Bucket;
import de.bund.digitalservice.ris.search.repository.opensearch.BaseRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.Logger;

public abstract class BaseIndexService<T> implements IndexService {

  private static final Integer IMPORT_BATCH_SIZE = 1000;
  private final Logger logger;

  protected final Bucket bucket;
  protected final BaseRepository<T> repository;

  protected BaseIndexService(Bucket bucket, BaseRepository<T> repository, Logger logger) {
    this.bucket = bucket;
    this.repository = repository;
    this.logger = logger;
  }

  public void reindexAll(String startingTimestamp) throws ObjectStoreServiceException {
    List<String> indexableFilenames = getAllIndexableFilenames();
    indexFiles(indexableFilenames);
    repository.deleteByIndexedAtBefore(startingTimestamp);
    repository.deleteByIndexedAtIsNull();
  }

  public void indexChangelog(Changelog changelog) throws ObjectStoreServiceException {
    if (changelog.isChangeAll()) {
      reindexAll(Instant.now().toString());
    } else {
      indexFiles(changelog.getChangedXml());
      Set<String> deletedIds =
          changelog.getDeleted().stream()
              .map(this::extractIdFromFilename)
              .collect(Collectors.toSet());
      repository.deleteAllById(deletedIds);
    }
  }

  public int getNumberOfIndexedDocuments() {
    return (int) repository.count();
  }

  public int getNumberOfIndexableDocumentsInBucket() {
    return getAllIndexableFilenames().size();
  }

  private void indexFiles(List<String> filenames) throws ObjectStoreServiceException {
    List<List<String>> fileBatches = ListUtils.partition(filenames, IMPORT_BATCH_SIZE);
    logger.info("Indexing process will have {} batches", fileBatches.size());
    for (int i = 0; i < fileBatches.size(); i++) {
      indexOneBatch(fileBatches.get(i));
      logger.info("Indexing batch {} of {} complete.", (i + 1), fileBatches.size());
    }
  }

  private void indexOneBatch(List<String> filenames) throws ObjectStoreServiceException {
    for (String filename : filenames) {
      Optional<String> content = bucket.getFileAsString(filename);
      if (content.isPresent()) {
        mapFileToEntity(filename, content.get()).ifPresent((repository::saveEntity));
      } else {
        logger.warn("Tried to index file {}, but it doesn't exist.", filename);
      }
    }
  }

  protected abstract String extractIdFromFilename(String filename);

  protected abstract Optional<T> mapFileToEntity(String filename, String fileContent);

  protected abstract List<String> getAllIndexableFilenames();
}
