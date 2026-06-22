package de.bund.digitalservice.ris.search.unit.service;

import static de.bund.digitalservice.ris.search.service.BulkExportService.JOB_STATE_STORAGE_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.BulkExportJob;
import de.bund.digitalservice.ris.search.service.BulkExportService;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import de.bund.digitalservice.ris.search.service.Job;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BulkExportJobTest {

  @Mock ChangelogService<?> changelogMock;
  @Mock PortalBucket portalBucketMock;
  @Mock ObjectStorage sourceBucket;
  @Mock BulkExportService exportService;

  private static final Clock clock =
      Clock.fixed(Instant.parse("2024-01-01T12:00:00.123Z"), ZoneId.of("UTC"));

  @Test
  void runJob_returnsEarlyWhenNoChangesAreDetected() {
    String outputName = "test-export";
    String prefix = "some/path/";

    when(portalBucketMock.getFileAsString(JOB_STATE_STORAGE_PREFIX + outputName))
        .thenReturn(Optional.of("2024-01-01T12:00:00Z"));

    Changelog noChangeChangelog =
        new Changelog(new HashSet<>(List.of()), new HashSet<>(List.of()), false);
    when(changelogMock.getChangesBetween(any(), any())).thenReturn(noChangeChangelog);

    BulkExportJob job =
        new BulkExportJob(exportService, portalBucketMock, outputName, changelogMock);

    var actual = job.runJob();
    assertThat(actual).isEqualTo(Job.ReturnCode.SUCCESS);
    verify(sourceBucket, never()).getAllKeysByPrefix(prefix);
  }

  @Test
  void runJob_returnsEarlyWhenOutsideRerunWindow() {
    String outputName = "test-export";

    when(portalBucketMock.getFileAsString(JOB_STATE_STORAGE_PREFIX + outputName))
        .thenReturn(Optional.of("2024-01-01T12:00:00Z"));

    Changelog changelog =
        new Changelog(new HashSet<>(List.of("something.xml")), new HashSet<>(List.of()), false);
    when(changelogMock.getChangesBetween(any(), any())).thenReturn(changelog);

    BulkExportJob job =
        new BulkExportJob(exportService, portalBucketMock, outputName, changelogMock);

    var actual = job.runJob();
    assertThat(actual).isEqualTo(Job.ReturnCode.SUCCESS);
    verify(exportService, never()).runJob(clock.instant());
  }

  @Test
  void runJob_triesToRecreateOnDetectedDeletion() {
    String outputName = "test-export";

    when(portalBucketMock.getFileAsString(JOB_STATE_STORAGE_PREFIX + outputName))
        .thenReturn(Optional.of("2024-01-01T13:00:00Z"));

    Changelog changelog =
        new Changelog(new HashSet<>(List.of()), new HashSet<>(List.of("something.xml")), false);
    when(changelogMock.getChangesBetween(any(), any())).thenReturn(changelog);

    BulkExportJob job =
        new BulkExportJob(exportService, portalBucketMock, outputName, changelogMock);

    job.runJob();
    verify(exportService, times(1)).deleteArchives();
    verify(exportService, times(1)).runJob(any());
  }
}
