package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.mapper.NormLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import de.bund.digitalservice.ris.search.utils.eli.ManifestationEli;
import de.bund.digitalservice.ris.search.utils.eli.WorkEli;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexNormsService implements IndexService {

  private static final Logger logger = LogManager.getLogger(IndexNormsService.class);

  private final NormsRepository normsRepository;
  private final NormsBucket normsBucket;

  @Autowired
  public IndexNormsService(NormsBucket normsBucket, NormsRepository normsRepository) {
    this.normsBucket = normsBucket;
    this.normsRepository = normsRepository;
  }

  public void reindexAll(String startingTimestamp) {
    Set<WorkEli> workElis = getWorks(normsBucket.getAllKeysByPrefix("eli/").stream());
    processWorkEliUpdates(workElis, startingTimestamp);
    clearOldNorms(startingTimestamp);
  }

  @Override
  public void indexChangelog(Changelog changelog) {
    try {
      Set<WorkEli> workElis =
          getWorks(Stream.concat(changelog.getChanged().stream(), changelog.getDeleted().stream()));
      processWorkEliUpdates(workElis, Instant.now().toString());
    } catch (IllegalArgumentException e) {
      logger.error("Error while reading changelog file: {}", e.getMessage());
    }
  }

  private Set<WorkEli> getWorks(Stream<String> files) {
    return files
        .map(EliFile::fromString)
        // this filters out the files that are not an EliFile
        .flatMap(Optional::stream)
        .map(EliFile::getWorkEli)
        // collecting to a set makes sure each work eli occurs at most once
        .collect(Collectors.toSet());
  }

  private void processWorkEliUpdates(Collection<WorkEli> workElis, String startingTimestamp) {
    List<List<WorkEli>> batches = ListUtils.partition(workElis.stream().toList(), 100);
    for (int i = 0; i < batches.size(); i++) {
      List<WorkEli> oneBatch = batches.get(i);
      logger.info("Indexing batch {} of {}", i + 1, batches.size());
      for (WorkEli eli : oneBatch) {
        processOneNormWork(eli, startingTimestamp);
      }
    }
  }

  private void processOneNormWork(WorkEli workEli, String startingTimestamp) {
    // Get all expressions for the current work from the bucket
    Set<ExpressionEli> expressionElis =
        normsBucket.getAllKeysByPrefix(workEli.toString() + "/").stream()
            .map(EliFile::fromString)
            .flatMap(Optional::stream)
            .map(EliFile::getExpressionEli)
            .collect(Collectors.toSet());

    for (ExpressionEli expressionEli : expressionElis) {
      try {
        getNormFromS3(expressionEli).ifPresent(normsRepository::save);
      } catch (ObjectStoreServiceException e) {
        // If we can't get the content of an expression we log an error and move on
        // That means on failure of a work "changed" it will end up deleted
        logger.error("Error while reading norm file {}. {}", expressionEli, e.getMessage());
      }
    }
    // delete the expressions from this work that were indexed before the start time
    normsRepository.deleteByWorkEliAndIndexedAtBefore(workEli.toString(), startingTimestamp);
  }

  private Optional<Norm> getNormFromS3(ExpressionEli expressionEli)
      throws ObjectStoreServiceException {

    // Get all files for the current expression.
    final List<String> keysMatchingExpressionEli =
        normsBucket.getAllKeysByPrefix(expressionEli.toString());

    Optional<String> newestFileName =
        keysMatchingExpressionEli.stream()
            // filter only valid Eli files
            .map(EliFile::fromString)
            .flatMap(Optional::stream)
            // Convert to manifestation Eli. This will create duplicates, but it doesn't matter
            .map(EliFile::getManifestationEli)
            // only get the manifestation files that represent expression xml files
            .filter(e -> e.subtype().startsWith("regelungstext-"))
            // take the manifestation with the latest pointInTimeManifestation
            .map(ManifestationEli::toString)
            .max(java.util.Comparator.naturalOrder());

    if (newestFileName.isEmpty()) {
      logger.error(
          "Changelog file contained {}, but no manifestation files found for that norm expression.",
          expressionEli);
      return Optional.empty();
    }
    String fileName = newestFileName.get();
    Optional<String> fileContent = normsBucket.getFileAsString(fileName);
    if (fileContent.isEmpty()) {
      logger.error("Error reading file content for file {} from S3.", fileName);
      return Optional.empty();
    }

    /*
    Find offenestruktur-*.xml (attachment) files that share the same manifestation ELI prefix and download their
    contents for processing.
     */
    String prefix = fileName.substring(0, fileName.lastIndexOf("/"));
    Map<String, String> attachments = new HashMap<>();
    for (String key : keysMatchingExpressionEli) {
      if (key.startsWith(prefix) && !Objects.equals(key, fileName)) {
        Optional<String> attachment = normsBucket.getFileAsString(key);
        attachment.ifPresent(a -> attachments.put(key, a));
      }
    }
    Optional<Norm> norm = NormLdmlToOpenSearchMapper.parseNorm(fileContent.get(), attachments);
    if (norm.isEmpty()) {
      logger.error("Unknown error while processing file {} during Norm import.", fileName);
      return Optional.empty();
    } else {
      return norm;
    }
  }

  private void clearOldNorms(String timestamp) {
    normsRepository.deleteByIndexedAtBefore(timestamp);
    normsRepository.deleteByIndexedAtIsNull();
  }

  @Override
  public int getNumberOfIndexedEntities() {
    return (int) normsRepository.count();
  }

  public int getNumberOfIndexableDocumentsInBucket() {
    Set<ExpressionEli> norms =
        normsBucket.getAllKeysByPrefix("eli/").stream()
            .map(EliFile::fromString)
            .flatMap(Optional::stream)
            .filter(e -> e.fileName().startsWith("regelungstext-"))
            .map(EliFile::getExpressionEli)
            .collect(Collectors.toSet());
    return norms.size();
  }
}
