package de.bund.digitalservice.ris.search.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.RetryableObjectStoreException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import de.bund.digitalservice.ris.search.service.ImportService;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
class ImportServiceTest {

  @Mock IndexStatusService indexStatusService;
  @Mock ObjectStorage normsBucket;
  @Mock ChangelogService changelogService;

  @Mock IndexNormsService indexNormsService;
  ImportService service;

  @BeforeEach
  void setup() {
    service = new ImportService(indexStatusService, changelogService);
  }

  @Test
  void lockIsAquiredAndReleasedOnSuccesfullProcess() throws RetryableObjectStoreException {
    when(indexStatusService.lockIndex(eq("norm_lock.txt"), any())).thenReturn(true);

    Changelog changelog = new Changelog();
    changelog.setChangeAll(true);
    Instant time = Instant.now();
    List<String> changelogs = List.of(ChangelogService.CHANGELOG + time + "-changelog.json");

    when(changelogService.getNewChangelogsSinceInstant(any(), any())).thenReturn(changelogs);
    when(changelogService.parseOneChangelog(any(), any())).thenReturn(changelog);
    when(changelogService.getInstantFromChangelog(any())).thenCallRealMethod();
    when(indexStatusService.getLastSuccess(any())).thenReturn(time);

    service.lockAndImportChangelogs(
        indexNormsService,
        ImportService.NORM_LOCK_FILENAME,
        ImportService.NORM_LAST_SUCCESS_FILENAME,
        normsBucket);

    verify(indexStatusService, times(1)).lockIndex(eq("norm_lock.txt"), any());
    verify(indexStatusService, times(1)).unlockIndex("norm_lock.txt");
    verify(indexStatusService, times(1)).updateLastSuccess("norm_last_success.txt", time);
  }

  @Test
  void lockIsAquiredAndReleasedOnRetryableException() throws RetryableObjectStoreException {

    Instant lastSuccess = Instant.now().minus(1, ChronoUnit.HOURS);

    when(indexStatusService.lockIndex(eq("norm_lock.txt"), any())).thenReturn(true);
    when(indexStatusService.getLastSuccess(ImportService.NORM_LAST_SUCCESS_FILENAME))
        .thenReturn(lastSuccess);
    when(changelogService.getNewChangelogsSinceInstant(any(), any()))
        .thenReturn(List.of("changelogs/" + Instant.now() + "-changelog.json"));
    when(changelogService.parseOneChangelog(any(), any()))
        .thenThrow(RetryableObjectStoreException.class);

    service.lockAndImportChangelogs(
        indexNormsService,
        ImportService.NORM_LOCK_FILENAME,
        ImportService.NORM_LAST_SUCCESS_FILENAME,
        normsBucket);

    verify(indexStatusService, times(1)).lockIndex(eq("norm_lock.txt"), any());
    verify(indexStatusService, times(1)).unlockIndex("norm_lock.txt");
    verify(indexStatusService, times(0)).updateLastSuccess(any(), any());
  }

  @Test
  void itTriggersReindexAll() throws RetryableObjectStoreException {
    Changelog changelog = new Changelog();
    changelog.setChangeAll(true);
    Instant startTime = Instant.now();

    this.service.importChangelogContent("changelog", changelog, indexNormsService, startTime);

    verify(indexNormsService, times(1)).reindexAll(startTime.toString());
  }

  @Test
  void itThrowsAnExceptionOnDuplicateIdsInChangedAndDeletedFields() {
    Changelog changelog = new Changelog();
    changelog.setChanged(Sets.newHashSet(List.of("identifier1")));
    changelog.setDeleted(Sets.newHashSet(List.of("identifier1")));
    Instant startTime = Instant.now();

    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            this.service.importChangelogContent(
                "changelog", changelog, indexNormsService, startTime));
  }

  @Test
  void itLogsAnErrorWhenTheNumberInBucketAnIndexDiffer(CapturedOutput output) {
    when(indexNormsService.getNumberOfIndexedDocuments()).thenReturn(100);
    when(indexNormsService.getNumberOfFilesInBucket()).thenReturn(99);

    service.alertOnNumberMismatch(
        indexStatusService,
        indexNormsService,
        ImportService.NORM_LAST_SUCCESS_FILENAME,
        normsBucket);

    String expectedOutput = "IndexNormsService has 99 files in bucket but 100 indexed documents";

    assertThat(output).contains(expectedOutput);
  }
}
