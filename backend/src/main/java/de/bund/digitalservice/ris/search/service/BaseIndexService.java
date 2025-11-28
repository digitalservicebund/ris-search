package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.repository.opensearch.DocumentRepository;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Base class for index services. */
public abstract class BaseIndexService<T> implements IndexService {

  private static final Integer IMPORT_BATCH_SIZE = 1000;
  protected static final Logger logger = LogManager.getLogger(BaseIndexService.class);

  protected final ObjectStorage objectStorage;

  protected final DocumentRepository<T> documentRepository;

  protected BaseIndexService(ObjectStorage objectStorage, DocumentRepository<T> repository) {

    this.objectStorage = objectStorage;
    this.documentRepository = repository;
  }

  /**
   * Re-indexes all the indexable files and deletes old or null entities based on the provided
   * starting timestamp. This method retrieves all files eligible for indexing, processes them, and
   * performs cleanup operations by removing entities indexed before the given timestamp or with a
   * null indexed value.
   *
   * @param startingTimestamp the timestamp used as a threshold for deleting entities. Entities
   *     indexed before this timestamp or with a null indexed value will be removed.
   * @throws ObjectStoreServiceException if an error occurs during the indexing or cleanup process.
   */
  public void reindexAll(String startingTimestamp) throws ObjectStoreServiceException {
    DateUtils.avoidOpenSearchSubMillisecondDateBug();
    List<String> indexableFilenames = getAllIndexableFilenames();
    indexFiles(indexableFilenames);
    deleteAllOldAndNullEntities(startingTimestamp);
  }

  /**
   * Processes a changelog to index or delete entities in the system. If the provided changelog
   * indicates all items have changed, all entities are re-indexed. Otherwise, only the changed
   * files are indexed, and the deleted files are removed from the index.
   *
   * @param changelog the changelog containing the sets of changed and deleted identifiers, as well
   *     as the flag indicating whether all items have changed.
   * @throws ObjectStoreServiceException if an issue occurs during the indexing or deletion
   *     operations.
   */
  public void indexChangelog(Changelog changelog) throws ObjectStoreServiceException {
    if (changelog.isChangeAll()) {
      reindexAll(Instant.now().toString());
    } else {
      indexFiles(changelog.getChanged().stream().filter(s -> s.endsWith(".xml")).toList());
      Set<String> deletedIds =
          changelog.getDeleted().stream()
              .map(this::extractIdFromFilename)
              .collect(Collectors.toSet());

      deleteAllEntitiesById(deletedIds);
    }
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
      Optional<String> content = objectStorage.getFileAsString(filename);
      if (content.isPresent()) {
        mapFileToEntity(filename, content.get()).ifPresent(this::saveEntity);
      } else {
        logger.warn("Tried to index file {}, but it doesn't exist.", filename);
      }
    }
  }

  protected String extractIdFromFilename(String filename) {
    return Arrays.stream(filename.split("\\.")).findFirst().orElse(null);
  }

  protected abstract Optional<T> mapFileToEntity(String filename, String fileContent);

  protected List<String> getAllIndexableFilenames() {
    return objectStorage.getAllKeys().stream()
        .filter(s -> s.endsWith(".xml") && !s.contains(IndexSyncJob.CHANGELOGS_PREFIX))
        .toList();
  }

  protected void deleteAllOldAndNullEntities(String startingTimestamp) {
    documentRepository.deleteByIndexedAtBefore(startingTimestamp);
    documentRepository.deleteByIndexedAtIsNull();
  }

  protected void deleteAllEntitiesById(Iterable<String> ids) {
    documentRepository.deleteAllById(ids);
  }

  protected void saveEntity(T entity) {
    documentRepository.save(entity);
  }

  public int getNumberOfIndexedEntities() {
    return (int) documentRepository.count();
  }
}
