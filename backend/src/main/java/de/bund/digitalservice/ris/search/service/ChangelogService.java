package de.bund.digitalservice.ris.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.utils.StringUtils;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/** Service to retrieve and parse changelog files */
public class ChangelogService<T extends ObjectStorage> {

  private static final Logger logger = LogManager.getLogger(ChangelogService.class);

  public static final String CHANGELOGS_PREFIX = "changelogs/";

  private final ObjectStorage bucket;
  private final ObjectReader changelogReader;

  /**
   * Changelog service to manage changelogs of documents
   *
   * @param bucket bucket containing the changelogs
   * @param objectMapper global ObjectMapper to create a Changelog Reader
   */
  public ChangelogService(T bucket, ObjectMapper objectMapper) {
    this.bucket = bucket;
    this.changelogReader = objectMapper.readerFor(Changelog.class);
  }

  /**
   * Retrieves a list of new changelog file names from the specified object storage, filtered by
   * those that are newer than the specified last processed changelog.
   *
   * <p>The method fetches all file keys from the changelog bucket with the prefix defined by
   * `CHANGELOGS_PREFIX` and excludes the prefix itself from the results. It then filters out files
   * that are lexicographically older or equal to the `lastProcessedChangelog` and sorts the
   * remaining file names in ascending order.
   *
   * @param lastProcessedChangelog the filename of the last processed changelog; files with
   *     lexicographically greater names will be included
   * @return a sorted list of file names representing unprocessed changelog files
   */
  public List<String> getNewChangelogsPaths(@NotNull String lastProcessedChangelog) {

    return bucket.getAllKeysByPrefix(CHANGELOGS_PREFIX).stream()
        .filter(e -> !CHANGELOGS_PREFIX.equals(e))
        .filter(e -> e.compareTo(lastProcessedChangelog) > 0)
        .sorted()
        .toList();
  }

  /**
   * Parses a single changelog file from the given object storage and converts its content to a
   * {@link de.bund.digitalservice.ris.search.importer.changelog.Changelog} object.
   *
   * <p>The method fetches the file content from the object storage using the provided filename. If
   * no content is found, or if there is an error during parsing, the method logs an error and
   * returns null.
   *
   * @param filename The name of the file to be fetched and parsed as a changelog.
   * @return A {@link de.bund.digitalservice.ris.search.importer.changelog.Changelog} object if
   *     parsing is successful, or null if the file could not be retrieved or parsed.
   * @throws de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException If an error
   *     occurs while accessing the object storage.
   */
  public Optional<Changelog> parseOneChangelog(String filename) throws ObjectStoreServiceException {
    Optional<String> changelogContent = bucket.getFileAsString(filename);
    if (changelogContent.isEmpty()) {
      logger.error(
          "Changelog file {} in bucket {} could not be fetched during import.", filename, bucket);
      return Optional.empty();
    } else {
      try {
        return Optional.of(
            stripVersionPrefix(
                bucket.getVersionPrefix(), changelogReader.readValue(changelogContent.get())));
      } catch (JsonProcessingException e) {
        logger.error("Error while parsing changelog file {} in bucket {}", filename, bucket, e);
        return Optional.empty();
      }
    }
  }

  private Changelog stripVersionPrefix(String versionPrefix, Changelog input) {
    input.setChanged(
        input.getChanged().stream()
            .map(e -> StringUtils.stripPrefix(e, versionPrefix))
            .collect(Collectors.toCollection(HashSet::new)));
    input.setDeleted(
        input.getDeleted().stream()
            .map(e -> StringUtils.stripPrefix(e, versionPrefix))
            .collect(Collectors.toCollection(HashSet::new)));
    return input;
  }

  /**
   * Retrieves an aggregated Changelog containing all document changes between two timestamps
   * (inclusive).
   *
   * @param from the starting timestamp boundary (inclusive)
   * @param to the ending timestamp boundary (inclusive)
   * @throws de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException If an error
   *     occurs while accessing the object storage.
   * @return Changelog object including all changes that were indexed
   */
  public Changelog getChangesBetween(Instant from, Instant to) throws ObjectStoreServiceException {
    var keys =
        bucket.getAllKeysByPrefix(CHANGELOGS_PREFIX).stream()
            .filter(key -> !CHANGELOGS_PREFIX.equals(key))
            .filter(key -> {
              Instant changelogTime = parseInstantFromKey(key);
              // get all changelogs between timestamps inclusive
              return !changelogTime.isBefore(from) && !changelogTime.isAfter(to);
            })
            .sorted()
            .toList();

    return parseAndFoldChangelogs(keys);
  }

  private Instant parseInstantFromKey(String key) {
    String fileName = key.substring(key.lastIndexOf('/') + 1);
    String timestampStr = fileName.substring(0, fileName.indexOf('Z') + 1);

    try {
      return Instant.parse(timestampStr);
    } catch (DateTimeParseException e) {
      String msg =
          String.format("Could not parse timestamp %s of changelog with key %s", timestampStr, key);
      throw new DateTimeParseException(msg, e.getParsedString(), e.getErrorIndex());
    }
  }

  /**
   * Parses multiple changelog files from the given object storage and collapses them into a single
   * changelog. If no changes occurred the changed and deleted lists will be empty.
   *
   * @param filenames the list of changelog files to be collapsed.
   * @return A {@link de.bund.digitalservice.ris.search.importer.changelog.Changelog} object. If
   *     there were no changes, the lists in the object are empty.
   * @throws de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException If an error
   *     occurs while accessing the object storage.
   */
  public Changelog getChangesFromFiles(List<String> filenames) throws ObjectStoreServiceException {
    return parseAndFoldChangelogs(filenames.stream().sorted().toList());
  }

  /**
   * Parses the given changelog files in order and folds them into a single changelog. As soon as a
   * changelog with changeAll is encountered, the method short circuits without parsing the
   * remaining files and returns a changeAll=true changelog.
   *
   * @param sortedKeys changelog file keys, sorted in the order they should be applied
   * @return the merged changelog
   */
  private Changelog parseAndFoldChangelogs(List<String> sortedKeys) {
    Changelog result = new Changelog();
    for (String key : sortedKeys) {
      Optional<Changelog> changelog = parseOneChangelog(key);
      if (changelog.isEmpty()) {
        continue;
      }
      if (changelog.get().isChangeAll()) {
        Changelog changeAll = new Changelog();
        changeAll.setChangeAll(true);
        return changeAll;
      }
      foldInto(result, changelog.get());
    }
    return result;
  }

  /**
   * Merges a single changelog into the accumulated result. The last action for a file wins. If a
   * file is marked as changed and deleted in the same changelog, it will be marked as deleted.
   *
   * @param result the accumulated changelog, modified in place
   * @param changelog the changelog to merge, which must not have changeAll set
   */
  private static void foldInto(Changelog result, Changelog changelog) {
    for (String filename : changelog.getChanged()) {
      result.getDeleted().remove(filename);
      result.getChanged().add(filename);
    }
    for (String filename : changelog.getDeleted()) {
      result.getChanged().remove(filename);
      result.getDeleted().add(filename);
    }
  }
}
