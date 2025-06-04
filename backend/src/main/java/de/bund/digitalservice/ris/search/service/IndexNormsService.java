package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.mapper.NormLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsSynthesizedRepository;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import de.bund.digitalservice.ris.search.utils.eli.ManifestationEli;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexNormsService implements IndexService {

  private static final Logger logger = LogManager.getLogger(IndexNormsService.class);

  private final NormsSynthesizedRepository normsSynthesizedRepository;
  private final NormsBucket normsBucket;

  @Autowired
  public IndexNormsService(
      NormsBucket normsBucket, NormsSynthesizedRepository normsSynthesizedRepository) {
    this.normsBucket = normsBucket;
    this.normsSynthesizedRepository = normsSynthesizedRepository;
  }

  public void reindexAll(String startingTimestamp) throws ObjectStoreServiceException {
    List<ManifestationEli> manifestations =
        normsBucket.getAllKeysByPrefix("eli/").stream()
            .map(ManifestationEli::fromString)
            .flatMap(Optional::stream)
            .toList();
    Set<ExpressionEli> expressions = getExpressions(manifestations);
    List<List<ExpressionEli>> batches = ListUtils.partition(expressions.stream().toList(), 100);
    for (List<ExpressionEli> batch : batches) {
      indexOneNormBatch(batch);
    }
    clearOldNorms(startingTimestamp);
  }

  private Set<ExpressionEli> getExpressions(List<ManifestationEli> manifestations) {
    return manifestations.stream()
        .map(ManifestationEli::getExpressionEli)
        .collect(Collectors.toSet());
  }

  @Override
  public void indexChangelog(String changelogKey, Changelog changelog)
      throws ObjectStoreServiceException {
    try {
      List<ManifestationEli> changedFiles =
          getValidManifestations(changelogKey, changelog.getChanged().stream().toList());
      List<ManifestationEli> deletedFiles =
          getValidManifestations(changelogKey, changelog.getDeleted().stream().toList());
      Set<ExpressionEli> changedWorks = getExpressions(changedFiles);
      Set<ExpressionEli> deletedWorks = getExpressions(deletedFiles);
      // We want to process the changes first to have no downtime, but we also want to keep a Norm
      // if it was both deleted and changed. Therefore, process changes first, but also remove
      // Norms from the delete list if the Norm occurs in both.
      List<List<ExpressionEli>> batches = ListUtils.partition(changedWorks.stream().toList(), 100);
      for (int i = 0; i < batches.size(); i++) {
        List<ExpressionEli> oneBatch = batches.get(i);
        logger.info("Indexing {}. Batch {} of {}", changelogKey, i + 1, batches.size());
        indexOneNormBatch(oneBatch);
      }

      deletedWorks.removeAll(changedWorks);

      for (String deleted : deletedWorks.stream().map(ExpressionEli::toString).toList()) {
        if (normsSynthesizedRepository.existsById(deleted)) {
          normsSynthesizedRepository.deleteById(deleted);
        } else {
          logger.warn(
              "Changelog requested to delete norm {}, but it already doesn't exist.", deleted);
        }
      }
    } catch (IllegalArgumentException e) {
      logger.error("Error while reading changelog file {}. {}", changelogKey, e.getMessage());
    }
  }

  private void indexOneNormBatch(List<ExpressionEli> expressionElis)
      throws ObjectStoreServiceException {
    List<Norm> norms = new ArrayList<>();
    for (ExpressionEli eli : expressionElis) {
      if (eli.subtype().startsWith("regelungstext-")) {
        Norm norm = getNormFromS3(eli);
        if (norm != null) {
          norms.add(norm);
        }
      }
    }
    normsSynthesizedRepository.saveAll(norms);
  }

  private List<ManifestationEli> getValidManifestations(String changelogKey, List<String> elis) {
    List<ManifestationEli> result = new ArrayList<>();
    for (String eli : elis) {
      Optional<ManifestationEli> manifestationEli = ManifestationEli.fromString(eli);
      if (manifestationEli.isPresent()) {
        result.add(manifestationEli.get());
      } else {
        logger.warn("Error processing manifestation {} in {}", eli, changelogKey);
      }
    }
    return result;
  }

  private Norm getNormFromS3(ExpressionEli expressionEli) throws ObjectStoreServiceException {
    // Get the newest manifestation for the current expression.
    final List<String> keysMatchingExpressionEli =
        normsBucket.getAllKeysByPrefix(expressionEli.getUriPrefix());

    Optional<String> newestFileName =
        // get all files with the given norm manifestation prefix
        keysMatchingExpressionEli.stream()
            // filter only valid manifestations
            .map(ManifestationEli::fromString)
            .flatMap(Optional::stream)
            /*
            Since pre-filtering by prefix, which does not contain the subtype, might include other
            documents within the same "Mantelgesetz" structure, post-filter by subtype equality.
             */
            .filter(e -> e.subtype().equals(expressionEli.subtype()))
            // take the manifestation with the latest in force date
            .map(ManifestationEli::toString)
            .max(java.util.Comparator.naturalOrder());

    if (newestFileName.isEmpty()) {
      logger.error("Changelog file contained {}, but no files found for that norm.", expressionEli);
      return null;
    }
    String fileName = newestFileName.get();
    Optional<String> fileContent = normsBucket.getFileAsString(fileName);
    if (fileContent.isEmpty()) {
      logger.error("Error reading file content for file {} from S3.", fileName);
      return null;
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
      return null;
    } else {
      return norm.get();
    }
  }

  private void clearOldNorms(String timestamp) {
    normsSynthesizedRepository.deleteByIndexedAtBefore(timestamp);
    normsSynthesizedRepository.deleteByIndexedAtIsNull();
  }

  public int getNumberOfIndexedDocuments() {
    return (int) normsSynthesizedRepository.count();
  }

  public int getNumberOfFilesInBucket() {
    List<ManifestationEli> norms =
        normsBucket.getAllKeysByPrefix("eli/").stream()
            .map(ManifestationEli::fromString)
            .flatMap(Optional::stream)
            .filter(e -> e.subtype().startsWith("regelungstext-"))
            .toList();
    return norms.size();
  }
}
