package de.bund.digitalservice.ris.search.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChangelogServiceTest {

  @Mock ObjectStorage bucket;

  ChangelogService changelogService;

  ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setup() {
    changelogService = new ChangelogService(bucket, objectMapper) {};
  }

  @Test
  void itSkipsInvalidChangelogContent() throws ObjectStoreServiceException {

    when(bucket.getFileAsString(any())).thenReturn(Optional.of("you shall not parse"));
    Optional<Changelog> changelog = changelogService.parseOneChangelog("mockFileName");
    Assertions.assertTrue(changelog.isEmpty());
  }

  @Test
  void itSkipsEmptyChangelogFiles() throws ObjectStoreServiceException {

    when(bucket.getFileAsString(any())).thenReturn(Optional.empty());

    Optional<Changelog> changelog = changelogService.parseOneChangelog("mockFileName");
    Assertions.assertTrue(changelog.isEmpty());
  }

  @Test
  void itReturnsChangelogsSortedByTimestamp() {
    Instant now = Instant.now();
    String lastSuccess =
        ChangelogService.CHANGELOGS_PREFIX
            + now.minus(2, ChronoUnit.HOURS).toString()
            + "-changelog.json";
    String olderChangelogFile =
        ChangelogService.CHANGELOGS_PREFIX + now.minus(3, ChronoUnit.HOURS) + "-changelog.json";
    String changelogFile1 =
        ChangelogService.CHANGELOGS_PREFIX + now.plus(1, ChronoUnit.HOURS) + "-changelog.json";
    String changelogFile2 =
        ChangelogService.CHANGELOGS_PREFIX + now.plus(2, ChronoUnit.HOURS) + "-changelog.json";

    when(bucket.getAllKeysByPrefix(ChangelogService.CHANGELOGS_PREFIX))
        .thenReturn(List.of(olderChangelogFile, changelogFile2, changelogFile1));

    List<String> changelogs = changelogService.getNewChangelogsPaths(lastSuccess);
    Assertions.assertEquals(changelogs.toArray()[0], changelogFile1);
    Assertions.assertEquals(changelogs.toArray()[1], changelogFile2);
  }

  @Test
  void itDetectsChangeAllChangelogs() throws JsonProcessingException {
    Changelog log1 = new Changelog();
    log1.setChanged(new HashSet<>(List.of("file")));
    Changelog log2 = new Changelog();
    log2.setChangeAll(true);
    Changelog log3 = new Changelog();
    log3.setDeleted(new HashSet<>(List.of("deleted")));

    when(bucket.getFileAsString("log1"))
        .thenReturn(Optional.of(objectMapper.writeValueAsString(log1)));
    when(bucket.getFileAsString("log2"))
        .thenReturn(Optional.of(objectMapper.writeValueAsString(log2)));
    when(bucket.getFileAsString("log3"))
        .thenReturn(Optional.of(objectMapper.writeValueAsString(log3)));

    Changelog result = changelogService.getChangesFromFiles(List.of("log1", "log2", "log3"));
    Assertions.assertTrue(result.isChangeAll());
  }

  @Test
  void itMergesAListOfChangelogs() throws JsonProcessingException {
    Changelog log1 = new Changelog();
    log1.setChanged(new HashSet<>(List.of("obsolete", "changed")));
    log1.setDeleted(new HashSet<>(List.of("obsolete")));
    Changelog log2 = new Changelog();
    log2.setChanged(new HashSet<>(List.of("changed2", "obsolete2")));
    log2.setDeleted(new HashSet<>(List.of("deleted")));
    Changelog log3 = new Changelog();
    log3.setChanged(new HashSet<>(List.of("changed3")));
    log3.setDeleted(new HashSet<>(List.of("obsolete2", "deleted2")));

    when(bucket.getFileAsString("log1"))
        .thenReturn(Optional.of(objectMapper.writeValueAsString(log1)));
    when(bucket.getFileAsString("log2"))
        .thenReturn(Optional.of(objectMapper.writeValueAsString(log2)));
    when(bucket.getFileAsString("log3"))
        .thenReturn(Optional.of(objectMapper.writeValueAsString(log3)));

    var result = changelogService.getChangesFromFiles(List.of("log1", "log2", "log3"));

    Assertions.assertEquals(3, result.getChanged().size());
    Assertions.assertTrue(result.getChanged().contains("changed"));
    Assertions.assertTrue(result.getChanged().contains("changed2"));
    Assertions.assertTrue(result.getChanged().contains("changed3"));

    Assertions.assertEquals(4, result.getDeleted().size());
    Assertions.assertTrue(result.getDeleted().contains("deleted"));
    Assertions.assertTrue(result.getDeleted().contains("deleted2"));
    Assertions.assertTrue(result.getDeleted().contains("obsolete"));
    Assertions.assertTrue(result.getDeleted().contains("obsolete2"));
  }

  @Test
  void itCollectsChangesBetweenTimestamps()
      throws JsonProcessingException, ObjectStoreServiceException {
    Instant from = Instant.parse("2026-07-03T12:00:00Z");
    Instant to = Instant.parse("2027-01-01T12:00:00Z");

    Changelog expectedFirst =
        new Changelog(new HashSet<>(List.of("file1")), new HashSet<>(), false);
    Changelog expectedSecond =
        new Changelog(new HashSet<>(List.of("file2")), new HashSet<>(), false);

    when(bucket.getAllKeysByPrefix(any()))
        .thenReturn(
            List.of(
                "changelogs/2026-07-03T11:00:00.000000Z-norm.json",
                "changelogs/2026-07-03T12:00:00.933434Z-norm.json",
                "changelogs/2026-07-08T12:00:00.933434Z-norm.json"));

    when(bucket.getFileAsString("changelogs/2026-07-03T12:00:00.933434Z-norm.json"))
        .thenReturn(Optional.of(objectMapper.writeValueAsString(expectedFirst)));
    when(bucket.getFileAsString("changelogs/2026-07-08T12:00:00.933434Z-norm.json"))
        .thenReturn(Optional.of(objectMapper.writeValueAsString(expectedSecond)));

    Changelog result = changelogService.getChangesBetween(from, to);

    Assertions.assertTrue(result.getChanged().contains("file1"));
    Assertions.assertTrue(result.getChanged().contains("file2"));
  }

  @Test
  void itDetectsChangeAllBetweenTimestamps()
      throws JsonProcessingException, ObjectStoreServiceException {
    Instant from = Instant.parse("2026-07-03T12:00:00Z");
    Instant to = Instant.parse("2027-01-01T12:00:00Z");

    Changelog expectedFirst = new Changelog(new HashSet<>(), new HashSet<>(), true);
    Changelog expectedSecond =
        new Changelog(new HashSet<>(List.of("file2")), new HashSet<>(), false);

    when(bucket.getAllKeysByPrefix(any()))
        .thenReturn(
            List.of(
                "changelogs/2026-07-03T12:00:00.000000Z-norm.json",
                "changelogs/2027-01-01T12:00:00.000000Z-norm.json"));

    when(bucket.getFileAsString("changelogs/2026-07-03T12:00:00.000000Z-norm.json"))
        .thenReturn(Optional.of(objectMapper.writeValueAsString(expectedFirst)));
    when(bucket.getFileAsString("changelogs/2027-01-01T12:00:00.000000Z-norm.json"))
        .thenReturn(Optional.of(objectMapper.writeValueAsString(expectedSecond)));

    Changelog result = changelogService.getChangesBetween(from, to);

    Assertions.assertTrue(result.isChangeAll());
  }
}
