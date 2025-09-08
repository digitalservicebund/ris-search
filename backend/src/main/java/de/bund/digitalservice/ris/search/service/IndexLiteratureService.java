package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.mapper.LiteratureLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.LiteratureRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexLiteratureService implements IndexService {

  private static final Logger logger = LogManager.getLogger(IndexLiteratureService.class);

  private static final Integer IMPORT_BATCH_SIZE = 1000;

  private final LiteratureRepository repository;
  private final LiteratureBucket bucket;

  @Autowired
  public IndexLiteratureService(LiteratureBucket bucket, LiteratureRepository repository) {
    this.bucket = bucket;
    this.repository = repository;
  }

  @Override
  public void reindexAll(String startingTimestamp) throws ObjectStoreServiceException {
    List<String> ldmlFiles = getAllLiteratureFiles();
    indexFileList(ldmlFiles);
    repository.deleteByIndexedAtBefore(startingTimestamp);
    repository.deleteByIndexedAtIsNull();
  }

  @Override
  public void indexChangelog(String key, Changelog changelog) throws ObjectStoreServiceException {
    if (changelog.isChangeAll()) {
      reindexAll(Instant.now().toString());
    } else {
      indexFileList(changelog.getChanged().stream().toList());
      Set<String> deletedIds =
          changelog.getDeleted().stream()
              .map(this::extractIdFromFilename)
              .collect(Collectors.toSet());
      repository.deleteAllById(deletedIds);
    }
  }

  private String extractIdFromFilename(String filename) {
    return Arrays.stream(filename.split("\\.")).findFirst().orElse(null);
  }

  public void indexFileList(List<String> filenames) throws ObjectStoreServiceException {
    List<List<String>> fileBatches = ListUtils.partition(filenames, IMPORT_BATCH_SIZE);
    logger.info("Import literature process will have {} batches", fileBatches.size());
    for (int i = 0; i < fileBatches.size(); i++) {
      indexOneBatch(fileBatches.get(i));
      logger.info("Import literature batch {} of {} complete.", (i + 1), fileBatches.size());
    }
  }

  public void indexOneBatch(List<String> filenames) throws ObjectStoreServiceException {
    for (String filename : filenames) {
      Optional<String> content = bucket.getFileAsString(filename);
      if (content.isPresent()) {
        parseOneDocument(content.get()).ifPresent((repository::save));
      } else {
        logger.warn("Tried to index literature file {}, but it doesn't exist.", filename);
      }
    }
  }

  public Optional<Literature> parseOneDocument(String fileContent) {
    return LiteratureLdmlToOpenSearchMapper.mapLdml(fileContent);
  }

  public int getNumberOfIndexedDocuments() {
    return (int) repository.count();
  }

  public int getNumberOfFilesInBucket() {
    return getAllLiteratureFiles().size();
  }

  private List<String> getAllLiteratureFiles() {
    return bucket.getAllKeys().stream()
        .filter(key -> !key.contains(IndexSyncJob.CHANGELOGS_PREFIX))
        .toList();
  }
}
