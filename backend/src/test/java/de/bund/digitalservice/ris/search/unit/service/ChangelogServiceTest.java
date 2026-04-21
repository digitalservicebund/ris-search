package de.bund.digitalservice.ris.search.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
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

  @Mock IndexStatusService indexStatusService;
  @Mock NormsBucket normsBucket;
  @Mock IndexNormsService indexNormsService;

  ChangelogService changelogService;

  @BeforeEach
  void setup() {
    changelogService = new ChangelogService() {};
  }

  @Test
  void itSkipsInvalidChangelogContent() throws ObjectStoreServiceException {

    when(normsBucket.getFileAsString(any())).thenReturn(Optional.of("you shall not parse"));
    Changelog changelog = changelogService.parseOneChangelog(normsBucket, "mockFileName");
    Assertions.assertNull(changelog);
  }

  @Test
  void itSkipsEmptyChangelogFiles() throws ObjectStoreServiceException {

    when(normsBucket.getFileAsString(any())).thenReturn(Optional.empty());

    Changelog changelog = changelogService.parseOneChangelog(normsBucket, "mockFileName");
    Assertions.assertNull(changelog);
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

    when(normsBucket.getAllKeysByPrefix(ChangelogService.CHANGELOGS_PREFIX))
        .thenReturn(List.of(olderChangelogFile, changelogFile2, changelogFile1));

    List<String> changelogs = changelogService.getNewChangelogsPaths(normsBucket, lastSuccess);
    Assertions.assertEquals(changelogs.toArray()[0], changelogFile1);
    Assertions.assertEquals(changelogs.toArray()[1], changelogFile2);
  }

  @Test
  void itDetectsChangeAllChangelogs() {
    Changelog log1 = new Changelog();
    log1.setChanged(new HashSet<>(List.of("file")));
    Changelog log2 = new Changelog();
    log2.setChangeAll(true);
    Changelog log3 = new Changelog();
    log3.setDeleted(new HashSet<>(List.of("deleted")));

    Assertions.assertTrue(ChangelogService.containsChangeAll(List.of(log1, log2, log3)));
  }

  @Test
  void itDetectsMissingChangeAllInChangelogs() {
    Changelog log1 = new Changelog();
    log1.setChanged(new HashSet<>(List.of("file")));
    Changelog log3 = new Changelog();
    log3.setDeleted(new HashSet<>(List.of("deleted")));

    Assertions.assertFalse(ChangelogService.containsChangeAll(List.of(log1, log3)));
  }

  @Test
  void itMergesAListOfConsecutiveChangelogsIntoASingleOne() {
    Changelog log1 = new Changelog();
    log1.setChanged(new HashSet<>(List.of("obsolete", "changed")));
    log1.setDeleted(new HashSet<>(List.of("obsolete")));
    Changelog log2 = new Changelog();
    log2.setChanged(new HashSet<>(List.of("changed2", "obsolete2")));
    log2.setDeleted(new HashSet<>(List.of("deleted")));
    Changelog log3 = new Changelog();
    log3.setChanged(new HashSet<>(List.of("changed3")));
    log3.setDeleted(new HashSet<>(List.of("obsolete2", "deleted2")));

    var result = ChangelogService.mergeChangelogs(List.of(log1, log2, log3));

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
}
