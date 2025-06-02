package de.bund.digitalservice.ris.search.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexSyncJob;
import de.bund.digitalservice.ris.search.service.NormIndexSyncJob;
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

  NormIndexSyncJob normIndexSyncJob;

  @BeforeEach
  void setup() {
    normIndexSyncJob = new NormIndexSyncJob(indexStatusService, normsBucket, indexNormsService);
  }

  @Test
  void itSkipsInvalidChangelogContent() throws ObjectStoreServiceException {

    when(normsBucket.getFileAsString(any())).thenReturn(Optional.of("you shall not parse"));
    Changelog changelog = normIndexSyncJob.parseOneChangelog(normsBucket, "mockFileName");
    Assertions.assertNull(changelog);
  }

  @Test
  void itSkipsEmptyChangelogFiles() throws ObjectStoreServiceException {

    when(normsBucket.getFileAsString(any())).thenReturn(Optional.empty());

    Changelog changelog = normIndexSyncJob.parseOneChangelog(normsBucket, "mockFileName");
    Assertions.assertNull(changelog);
  }

  @Test
  void itReturnsChangelogsSortedByTimestamp() {
    Instant now = Instant.now();
    String lastSuccess = now.minus(2, ChronoUnit.HOURS).toString();
    String changelogFile1 =
        IndexSyncJob.CHANGELOGS_PREFIX + now.plus(1, ChronoUnit.HOURS) + "-changelog.json";
    String changelogFile2 =
        IndexSyncJob.CHANGELOGS_PREFIX + now.plus(2, ChronoUnit.HOURS) + "-changelog.json";

    when(normsBucket.getAllKeysByPrefix(IndexSyncJob.CHANGELOGS_PREFIX))
        .thenReturn(List.of(changelogFile2, changelogFile1));

    List<String> changelogs = normIndexSyncJob.getNewChangelogs(normsBucket, lastSuccess);
    Assertions.assertEquals(changelogs.toArray()[0], changelogFile1);
    Assertions.assertEquals(changelogs.toArray()[1], changelogFile2);
  }
}
