package de.bund.digitalservice.ris.search.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.RetryableObjectStoreException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

  @BeforeEach()
  void setup() {
    this.changelogService = new ChangelogService();
  }

  @Test
  void itSkipsInvalidChangelogContent() throws RetryableObjectStoreException {

    when(bucket.getFileAsString(any())).thenReturn(Optional.of("you shall not parse"));
    Changelog changelog = changelogService.parseOneChangelog(bucket, "mockFileName");
    Assertions.assertNull(changelog);
  }

  @Test
  void itSkipsEmptyChangelogFiles() throws RetryableObjectStoreException {

    when(bucket.getFileAsString(any())).thenReturn(Optional.empty());

    Changelog changelog = changelogService.parseOneChangelog(bucket, "mockFileName");
    Assertions.assertNull(changelog);
  }

  @Test
  void itReturnsChangelogsSortedByTimestamp() {
    Instant lastSuccess = Instant.now().minus(2, ChronoUnit.HOURS);
    String changelogFile1 =
        ChangelogService.CHANGELOG + lastSuccess.plus(1, ChronoUnit.HOURS) + "-changelog.json";
    String changelogFile2 =
        ChangelogService.CHANGELOG + lastSuccess.plus(2, ChronoUnit.HOURS) + "-changelog.json";

    when(bucket.getAllFilenamesByPath(ChangelogService.CHANGELOG))
        .thenReturn(List.of(changelogFile2, changelogFile1));

    List<String> changelogs = changelogService.getNewChangelogsSinceInstant(bucket, lastSuccess);
    Assertions.assertEquals(changelogs.toArray()[0], changelogFile1);
    Assertions.assertEquals(changelogs.toArray()[1], changelogFile2);
  }

  @Test
  void itRetrievesAnInstantFromAChangelogPath() {
    Instant instant =
        changelogService
            .getInstantFromChangelog("changelogs/2025-03-21T15:10:03.382450489Z-changelog.json")
            .orElseThrow();
    Assertions.assertEquals("2025-03-21T15:10:03.382450489Z", instant.toString());
  }

  @Test
  void itRetrievesEmptyOnParsingError() {
    Optional<Instant> instant = changelogService.getInstantFromChangelog("not parseable");
    Assertions.assertEquals(Optional.empty(), instant);
  }
}
