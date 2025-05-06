package de.bund.digitalservice.ris.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChangelogService {

  public static final String CHANGELOG = "changelogs/";
  private static final Logger logger = LoggerFactory.getLogger(ChangelogService.class);

  public List<String> getNewChangelogsSinceInstant(
      ObjectStorage changelogBucket, Instant threshold) {
    List<String> allChangelogFiles = changelogBucket.getAllKeysByPrefix(CHANGELOG);

    List<String> result = new ArrayList<>();
    // we need a loop here to be able to throw the ObjectStoreServiceException
    for (String filename : allChangelogFiles) {
      if (!CHANGELOG.equals(filename) && changelogIsNewerThanThreshold(filename, threshold)) {
        result.add(filename);
      }
    }
    Collections.sort(result);
    return result;
  }

  public Changelog parseOneChangelog(ObjectStorage changelogBucket, String filename)
      throws ObjectStoreServiceException {
    Optional<String> changelogContent = changelogBucket.getFileAsString(filename);
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

  public Optional<Instant> getInstantFromChangelog(String filename) {
    try {
      String timeString = filename.substring(filename.indexOf("/") + 1, filename.indexOf("Z") + 1);
      Instant instant = Instant.parse(timeString);
      return Optional.of(instant);
    } catch (StringIndexOutOfBoundsException | NullPointerException | DateTimeParseException e) {
      logger.error("unable to parse invalid changelog timestamp {}", filename);
      return Optional.empty();
    }
  }

  private boolean changelogIsNewerThanThreshold(String filename, Instant threshold) {
    return getInstantFromChangelog(filename).map(time -> time.isAfter(threshold)).orElse(false);
  }
}
