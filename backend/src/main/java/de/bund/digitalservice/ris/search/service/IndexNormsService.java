package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.mapper.NormLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import de.bund.digitalservice.ris.search.utils.eli.ManifestationEli;
import de.bund.digitalservice.ris.search.utils.eli.WorkEli;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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

  // We can't use LocalDate.MIN or LocalDate.MAX because opensearch min and max differ from java
  public static final LocalDate TIME_RELEVANCE_MIN = LocalDate.of(1, 1, 1);
  public static final LocalDate TIME_RELEVANCE_MAX = LocalDate.of(9999, 1, 1);

  @Autowired
  public IndexNormsService(NormsBucket normsBucket, NormsRepository normsRepository) {
    this.normsBucket = normsBucket;
    this.normsRepository = normsRepository;
  }

  public void reindexAll(String startingTimestamp) {
    DateUtils.avoidOpenSearchSubMillisecondDateBug();
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

    // parse the norms
    List<Norm> normsToIndex = new ArrayList<>();
    for (ExpressionEli expressionEli : expressionElis) {
      try {
        getNormFromS3(expressionEli).ifPresent(normsToIndex::add);
      } catch (ObjectStoreServiceException e) {
        // If we can't get the content of an expression we log an error and move on
        // That means on failure of a work "changed" it will end up deleted
        logger.error("Error while reading norm file {}. {}", expressionEli, e.getMessage());
      }
    }

    addTimeRelevanceWindows(workEli.toString(), normsToIndex);
    normsRepository.saveAll(normsToIndex);

    // delete the expressions from this work that were indexed before the start time
    normsRepository.deleteByWorkEliAndIndexedAtBefore(workEli.toString(), startingTimestamp);
  }

  private void addTimeRelevanceWindows(String workEli, List<Norm> norms) {
    // Prototype won't have valid norms (in terms of ris:inkraft) until 2028
    // This check is for prototype
    if (norms.size() == 1) {
      norms.getFirst().setTimeRelevanceStartDate(TIME_RELEVANCE_MIN);
      norms.getFirst().setTimeRelevanceEndDate(TIME_RELEVANCE_MAX);
      return;
    }

    filterAndSortNorms(workEli, norms);

    if (norms.isEmpty()) {
      return;
    }

    // At this point filterAndSortNorms has already run, and we know
    // * norms has at least 1 norm
    // * All entries have inkraft not null
    // * All entries except possibly the last one have ausserkraft not null

    norms.getFirst().setTimeRelevanceStartDate(TIME_RELEVANCE_MIN);
    for (int i = 0; i < norms.size() - 1; i++) {
      LocalDate relevanceEndDate = norms.get(i).getExpiryDate();
      norms.get(i).setTimeRelevanceEndDate(relevanceEndDate);
      norms.get(i + 1).setTimeRelevanceStartDate(relevanceEndDate.plus(Period.ofDays(1)));
    }
    norms.getLast().setTimeRelevanceEndDate(TIME_RELEVANCE_MAX);
  }

  private void filterAndSortNorms(String workEli, List<Norm> norms) {
    // remove the norms that don't have inkraft defined
    norms.removeIf(e -> e.getEntryIntoForceDate() == null);

    if (norms.isEmpty()) {
      logger.warn("Trying to index {}, but no expressions have EntryIntoForce defined", workEli);
      return;
    }

    // Sort the list in ascending order by inkraft
    norms.sort(Comparator.comparing(Norm::getEntryIntoForceDate));

    // Only the norm with the largest ris:inkraft can have ris:ausserkraft null, so we remove others
    // with ris:ausserkraft null
    String lastNormId = norms.getLast().getId();
    norms.removeIf(e -> e.getExpiryDate() == null && !lastNormId.equals(e.getId()));

    // validate that in force ranges don't overlap
    validateExpressionsDontOverlap(workEli, norms);
  }

  private static void validateExpressionsDontOverlap(String workEli, List<Norm> norms) {
    for (int i = 1; i < norms.size(); i++) {
      if (norm2StartsBeforeNorm1Ends(norms.get(i - 1), norms.get(i))) {
        logger.warn(
            "Trying to index {}, but expressions' inkraft and ausserkraft overlap.", workEli);
        norms.clear();
        return;
      }
    }
  }

  private static boolean norm2StartsBeforeNorm1Ends(Norm norm1, Norm norm2) {
    return !norm2.getEntryIntoForceDate().isAfter(norm1.getExpiryDate());
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
