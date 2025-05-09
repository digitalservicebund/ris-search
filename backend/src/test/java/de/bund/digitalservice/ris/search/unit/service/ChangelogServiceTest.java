package de.bund.digitalservice.ris.search.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.ImportService;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.NormImportService;
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

  @Mock IndexStatusService indexStatusService;
  @Mock NormsBucket normsBucket;
  @Mock IndexNormsService indexNormsService;

  NormImportService normImportService;

  @BeforeEach
  void setup() {
    normImportService = new NormImportService(indexStatusService, normsBucket, indexNormsService);
  }

  @Test
  void itSkipsInvalidChangelogContent() throws ObjectStoreServiceException {

    when(normsBucket.getFileAsString(any())).thenReturn(Optional.of("you shall not parse"));
    Changelog changelog = normImportService.parseOneChangelog(normsBucket, "mockFileName");
    Assertions.assertNull(changelog);
  }

  @Test
  void itSkipsEmptyChangelogFiles() throws ObjectStoreServiceException {

    when(normsBucket.getFileAsString(any())).thenReturn(Optional.empty());

    Changelog changelog = normImportService.parseOneChangelog(normsBucket, "mockFileName");
    Assertions.assertNull(changelog);
  }

  @Test
  void itReturnsChangelogsSortedByTimestamp() {
    Instant lastSuccess = Instant.now().minus(2, ChronoUnit.HOURS);
    String changelogFile1 =
        ImportService.CHANGELOG + lastSuccess.plus(1, ChronoUnit.HOURS) + "-changelog.json";
    String changelogFile2 =
        ImportService.CHANGELOG + lastSuccess.plus(2, ChronoUnit.HOURS) + "-changelog.json";

    when(normsBucket.getAllKeysByPrefix(ImportService.CHANGELOG))
        .thenReturn(List.of(changelogFile2, changelogFile1));

    List<String> changelogs =
        normImportService.getNewChangelogsSinceInstant(normsBucket, lastSuccess);
    Assertions.assertEquals(changelogs.toArray()[0], changelogFile1);
    Assertions.assertEquals(changelogs.toArray()[1], changelogFile2);
  }

  @Test
  void itRetrievesAnInstantFromAChangelogPath() {
    Instant instant =
        normImportService
            .getInstantFromChangelog("changelogs/2025-03-21T15:10:03.382450489Z-changelog.json")
            .orElseThrow();
    Assertions.assertEquals("2025-03-21T15:10:03.382450489Z", instant.toString());
  }

  @Test
  void itRetrievesEmptyOnParsingError() {
    Optional<Instant> instant = normImportService.getInstantFromChangelog("not parseable");
    Assertions.assertEquals(Optional.empty(), instant);
  }
}
