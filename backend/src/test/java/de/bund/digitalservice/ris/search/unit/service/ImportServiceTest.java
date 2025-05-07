package de.bund.digitalservice.ris.search.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import de.bund.digitalservice.ris.search.service.ImportService;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexingState;
import de.bund.digitalservice.ris.search.service.PersistedIndexingState;
import java.time.Instant;
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
  @Mock NormsBucket normsBucket;
  @Mock ChangelogService changelogService;

  @Mock IndexNormsService indexNormsService;
  ImportService service;

  @BeforeEach
  void setup() {
    service = new ImportService(indexStatusService, changelogService);
  }

  @Test
  void lockIsAcquiredAndReleasedOnSuccesfulProcess() throws ObjectStoreServiceException {

    Changelog changelog = new Changelog();
    changelog.setChangeAll(true);
    Instant time = Instant.now();
    List<String> changelogs = List.of(ChangelogService.CHANGELOG + time + "-changelog.json");

    when(indexStatusService.lockIndex(any())).thenReturn(true);
    when(indexStatusService.loadStatus(any()))
        .thenReturn(new PersistedIndexingState(time.toString(), time.toString(), null, null));
    when(changelogService.getNewChangelogsSinceInstant(any(), any())).thenReturn(changelogs);
    when(changelogService.parseOneChangelog(any(), any())).thenReturn(changelog);
    when(changelogService.getInstantFromChangelog(any())).thenCallRealMethod();

    IndexingState state = getMockState();
    service.lockAndImportChangelogs(state);

    verify(indexStatusService, times(1)).lockIndex(any());
    verify(indexStatusService, times(1)).unlockIndex(any());
    verify(indexStatusService, times(1))
        .updateLastSuccess(ImportService.NORM_STATUS_FILENAME, time);
  }

  @Test
  void itTriggersReindexAll() throws ObjectStoreServiceException {
    Changelog changelog = new Changelog();
    changelog.setChangeAll(true);
    IndexingState state = getMockState();

    service.importChangelogContent(changelog, state);

    verify(indexNormsService, times(1)).reindexAll(any());
  }

  @Test
  void itThrowsAnExceptionOnDuplicateIdsInChangedAndDeletedFields() {
    Changelog changelog = new Changelog();
    changelog.setChanged(Sets.newHashSet(List.of("identifier1")));
    changelog.setDeleted(Sets.newHashSet(List.of("identifier1")));
    IndexingState state = getMockState();

    Assertions.assertThrows(
        IllegalArgumentException.class, () -> service.importChangelogContent(changelog, state));
  }

  @Test
  void itLogsAnErrorWhenTheNumberInBucketAnIndexDiffer(CapturedOutput output) {
    when(indexNormsService.getNumberOfIndexedDocuments()).thenReturn(100);
    when(indexNormsService.getNumberOfFilesInBucket()).thenReturn(99);

    IndexingState indexingState = getMockState();
    service.alertOnNumberMismatch(indexingState);

    String expectedOutput = "IndexNormsService has 99 files in bucket but 100 indexed documents";

    assertThat(output).contains(expectedOutput);
  }

  private IndexingState getMockState() {
    Instant time = Instant.now();
    IndexingState indexingState =
        new IndexingState(normsBucket, ImportService.NORM_STATUS_FILENAME, indexNormsService);
    indexingState.setPersistedIndexingState(
        new PersistedIndexingState(time.toString(), time.toString(), null, null));
    return indexingState;
  }
}
