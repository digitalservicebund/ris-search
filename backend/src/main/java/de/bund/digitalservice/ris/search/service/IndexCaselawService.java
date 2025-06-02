package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.mapper.CaseLawLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawSynthesizedRepository;
import java.time.Instant;
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
public class IndexCaselawService implements IndexService {

  private static final Logger logger = LogManager.getLogger(IndexCaselawService.class);

  private static final Integer IMPORT_BATCH_SIZE = 1000;

  private final CaseLawSynthesizedRepository repository;
  private final CaseLawBucket bucket;

  @Autowired
  public IndexCaselawService(CaseLawBucket bucket, CaseLawSynthesizedRepository repository) {
    this.bucket = bucket;
    this.repository = repository;
  }

  @Override
  public void reindexAll(String startingTimestamp) throws ObjectStoreServiceException {
    List<String> ldmlFiles = getAllCaseLawFilenames();
    indexFileList(ldmlFiles);
    repository.deleteByIndexedAtBefore(startingTimestamp);
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
    return filename.substring(0, filename.lastIndexOf('.'));
  }

  public void indexFileList(List<String> filenames) throws ObjectStoreServiceException {
    List<List<String>> fileBatches = ListUtils.partition(filenames, IMPORT_BATCH_SIZE);
    logger.info("Import caselaw process will have {} batches", fileBatches.size());
    for (int i = 0; i < fileBatches.size(); i++) {
      indexOneBatch(fileBatches.get(i));
      logger.info("Import caselaw batch {} of {} complete.", (i + 1), fileBatches.size());
    }
  }

  public void indexOneBatch(List<String> filenames) throws ObjectStoreServiceException {
    for (String filename : filenames) {
      Optional<String> content = bucket.getFileAsString(filename);
      if (content.isPresent()) {
        parseOneDocument(filename, content.get()).ifPresent(repository::save);
      } else {
        logger.warn("Tried to index caselaw file {}, but it doesn't exist.", filename);
      }
    }
  }

  public Optional<CaseLawDocumentationUnit> parseOneDocument(String filename, String fileContent) {
    try {
      return Optional.of(CaseLawLdmlToOpenSearchMapper.fromString(fileContent));
    } catch (OpenSearchMapperException e) {
      logger.error("unable to parse file {}", filename, e);
      return Optional.empty();
    }
  }

  public int getNumberOfIndexedDocuments() {
    return (int) repository.count();
  }

  public int getNumberOfFilesInBucket() {
    return getAllCaseLawFilenames().size();
  }

  private List<String> getAllCaseLawFilenames() {
    return bucket.getAllKeys().stream()
        .filter(s -> !s.contains(IndexSyncJob.CHANGELOGS_PREFIX))
        .toList();
  }
}
