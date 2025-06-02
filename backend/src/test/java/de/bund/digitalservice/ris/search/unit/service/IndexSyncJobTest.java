package de.bund.digitalservice.ris.search.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexSyncJob;
import de.bund.digitalservice.ris.search.service.IndexingState;
import de.bund.digitalservice.ris.search.service.NormIndexSyncJob;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
class IndexSyncJobTest {

  @Mock IndexStatusService indexStatusService;
  @Mock NormsBucket normsBucket;
  @Mock IndexNormsService indexNormsService;

  NormIndexSyncJob normIndexSyncJob;

  @BeforeEach
  void setup() {
    normIndexSyncJob = new NormIndexSyncJob(indexStatusService, normsBucket, indexNormsService);
  }

  @Test
  void lockIsAcquiredAndReleasedOnSuccessfulProcess() throws ObjectStoreServiceException {

    Instant time = Instant.now();
    String changelogFileName = IndexSyncJob.CHANGELOGS_PREFIX + time + "-changelog.json";
    List<String> changelogs = List.of(changelogFileName);

    when(indexStatusService.lockIndex(any(), any())).thenReturn(true);
    when(indexStatusService.loadStatus(any()))
        .thenReturn(
            new IndexingState(time.minusSeconds(10).toString(), time.toString(), time.toString()));
    when(normsBucket.getAllKeysByPrefix(IndexSyncJob.CHANGELOGS_PREFIX)).thenReturn(changelogs);
    String changeAll = "{\"change_all\" : true}";
    when(normsBucket.getFileAsString(changelogFileName)).thenReturn(Optional.of(changeAll));

    normIndexSyncJob.runJob();

    verify(indexStatusService, times(1)).lockIndex(any(), any());
    verify(indexStatusService, times(1)).unlockIndex(any());
    verify(indexStatusService, times(1))
        .updateLastProcessedChangelog(NormIndexSyncJob.NORM_STATUS_FILENAME, changelogFileName);
  }

  @Test
  void itTriggersReindexAll() throws ObjectStoreServiceException {
    Changelog changelog = new Changelog();
    changelog.setChangeAll(true);

    normIndexSyncJob.importChangelogContent(changelog, Instant.now().toString(), "testFileName");

    verify(indexNormsService, times(1)).reindexAll(any());
  }

  @Test
  void itThrowsAnExceptionOnDuplicateIdsInChangedAndDeletedFields() {
    Changelog changelog = new Changelog();
    changelog.setChanged(Sets.newHashSet(List.of("identifier1")));
    changelog.setDeleted(Sets.newHashSet(List.of("identifier1")));

    String now = Instant.now().toString();

    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> normIndexSyncJob.importChangelogContent(changelog, now, "testFileName"));
  }

  @Test
  void itLogsAWarningWhenTheNumberInBucketAnIndexDiffer(CapturedOutput output) {
    when(indexNormsService.getNumberOfIndexedDocuments()).thenReturn(100);
    when(indexNormsService.getNumberOfFilesInBucket()).thenReturn(99);

    Instant time = Instant.now();
    normIndexSyncJob.alertOnNumberMismatch(
        new IndexingState(time.toString(), time.toString(), time.toString()));

    String expectedOutput = "IndexNormsService has 99 files in bucket but 100 indexed documents";

    assertThat(output).contains(expectedOutput);
  }
}
