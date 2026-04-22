package de.bund.digitalservice.ris.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

/** Service to retrieve and parse changelog files */
@Service
public class ChangelogService {

  private static final Logger logger = LogManager.getLogger(ChangelogService.class);

  public static final String CHANGELOGS_PREFIX = "changelogs/";

  /**
   * Retrieves a list of new changelog file names from the specified object storage, filtered by
   * those that are newer than the specified last processed changelog.
   *
   * <p>The method fetches all file keys from the changelog bucket with the prefix defined by
   * `CHANGELOGS_PREFIX` and excludes the prefix itself from the results. It then filters out files
   * that are lexicographically older or equal to the `lastProcessedChangelog` and sorts the
   * remaining file names in ascending order.
   *
   * @param bucket bucket containing chainlogs
   * @param lastProcessedChangelog the filename of the last processed changelog; files with
   *     lexicographically greater names will be included
   * @return a sorted list of file names representing unprocessed changelog files
   */
  public List<String> getNewChangelogsPaths(
      ObjectStorage bucket, @NotNull String lastProcessedChangelog) {

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
   * @param bucket The bucket where the changelog files are stored
   * @param filename The name of the file to be fetched and parsed as a changelog.
   * @return A {@link de.bund.digitalservice.ris.search.importer.changelog.Changelog} object if
   *     parsing is successful, or null if the file could not be retrieved or parsed.
   * @throws de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException If an error
   *     occurs while accessing the object storage.
   */
  public @Nullable Changelog parseOneChangelog(ObjectStorage bucket, String filename)
      throws ObjectStoreServiceException {
    Optional<String> changelogContent = bucket.getFileAsString(filename);
    if (changelogContent.isEmpty()) {
      logger.error("Changelog file {} could not be fetched during import.", filename);
      return null;
    } else {
      try {
        return new ObjectMapper().readValue(changelogContent.get(), Changelog.class);
      } catch (JsonProcessingException e) {
        logger.error("Error while parsing changelog file {}", filename, e);
        return null;
      }
    }
  }

  /**
   * Parses multiple changelog files from the given object storage and converts its content to a
   * List of {@link de.bund.digitalservice.ris.search.importer.changelog.Changelog} objects.
   *
   * <p>The method fetches the file content from the object storage using the provided filename. If
   * no content is found, or if there is an error during parsing, the method logs an error and
   * continues with the following files.
   *
   * @param bucket the bucket where the changelog files are stored
   * @param filenames the list of changelogfiles to be parsed.
   * @return A {@link de.bund.digitalservice.ris.search.importer.changelog.Changelog} object if
   *     parsing is successful, or null if the file could not be retrieved or parsed.
   * @throws de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException If an error
   *     occurs while accessing the object storage.
   */
  public List<Changelog> parseChangelogs(ObjectStorage bucket, List<String> filenames)
      throws ObjectStoreServiceException {
    List<Changelog> changelogs = new ArrayList<>();
    for (String filename : filenames) {
      Changelog changelog = parseOneChangelog(bucket, filename);
      if (Objects.nonNull(changelog)) {
        changelogs.add(changelog);
      }
    }
    return changelogs;
  }

  /**
   * Detects if a changeAll flag is set in any changelog in a list of changelogs
   *
   * @param logs changelogs to be checked for a changeAll flag
   * @return true if any changelog contains a changeAll flag
   */
  public static boolean containsChangeAll(List<Changelog> logs) {
    return !logs.stream().filter(Changelog::isChangeAll).toList().isEmpty();
  }

  /**
   * Merges multiple changelogs into a single changelog. If a file is marked as changed and deleted
   * in the same changelog, it will be marked as deleted. ChangeAll flags are ignored.
   *
   * @param changelogs the list of changelogs to merge
   * @return the merged changelog
   */
  public static Changelog mergeChangelogs(List<Changelog> changelogs) {
    enum Action {
      CHANGED,
      DELETED
    }
    Map<String, Action> mergedChanges = new HashMap<>();
    for (Changelog log : changelogs) {
      log.getChanged().forEach(filename -> mergedChanges.put(filename, Action.CHANGED));
      log.getDeleted().forEach(filename -> mergedChanges.put(filename, Action.DELETED));
    }
    Changelog mergedChangelog = new Changelog();
    mergedChanges.forEach(
        (id, type) -> {
          if (Action.CHANGED.equals(type)) {
            mergedChangelog.getChanged().add(id);
          } else {
            mergedChangelog.getDeleted().add(id);
          }
        });

    return mergedChangelog;
  }
}
