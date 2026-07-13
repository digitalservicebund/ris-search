package de.bund.digitalservice.ris.search.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.ZipTestUtils;
import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.service.BulkExportService;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class BulkExportServiceTest {

  private static final Clock clock =
      Clock.fixed(Instant.parse("2024-01-01T12:00:00.123Z"), ZoneId.of("UTC"));

  @Test
  void updateLatestZip_successfulZipAndUpload() throws IOException {
    ObjectStorage sourceBucket = mock(ObjectStorage.class);
    ObjectStorage destinationBucket = mock(ObjectStorage.class);
    String outputName = "test-export";

    when(sourceBucket.getAllKeys()).thenReturn(List.of("file1.txt", "file2.pdf"));
    final byte[] bytes1 = "This is the content of file 1.".getBytes();
    when(sourceBucket.get("file1.txt")).thenReturn(Optional.of(bytes1));
    final byte[] bytes2 = "%PDF-1.5...".getBytes();
    when(sourceBucket.get("file2.pdf")).thenReturn(Optional.of(bytes2));
    when(destinationBucket.getAllKeysByPrefix(anyString())).thenReturn(Collections.emptyList());

    AtomicReference<Map<String, byte[]>> files = new AtomicReference<>();
    when(destinationBucket.putStream(anyString(), any(InputStream.class)))
        .thenAnswer(
            i -> {
              // process the inputStream as it comes in, it will be closed after invocation the
              // method under test
              InputStream stream = i.getArgument(1);
              files.set(ZipTestUtils.readZipStream(stream));
              return -1L;
            });

    BulkExportService bulkExportService =
        new BulkExportService(sourceBucket, destinationBucket, outputName);

    boolean actual = bulkExportService.updateLatestZip(clock.instant());
    assertThat(actual).isTrue();

    verify(sourceBucket, times(1)).getAllKeys();
    verify(sourceBucket, times(1)).get("file1.txt");
    verify(sourceBucket, times(1)).get("file2.pdf");
    verify(destinationBucket, times(1)).getAllKeysByPrefix(anyString());
    verify(destinationBucket, times(1))
        .putStream(
            matches(
                "snapshots/test-export_\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+Z\\.zip"),
            any(InputStream.class));
    verify(destinationBucket, times(0)).delete(anyString());

    assertThat(files.get())
        .containsExactly(Map.entry("file1.txt", bytes1), Map.entry("file2.pdf", bytes2));
  }

  @Test
  void updateLatestZip_withObsoleteFiles_shouldDeleteThem() throws IOException {
    ObjectStorage sourceBucket = mock(ObjectStorage.class);
    ObjectStorage destinationBucket = mock(ObjectStorage.class);
    String outputName = "test-export";
    String file1Content = "Some content";

    when(sourceBucket.getAllKeys()).thenReturn(List.of("file1.txt"));
    when(sourceBucket.get("file1.txt")).thenReturn(Optional.of(file1Content.getBytes()));
    when(destinationBucket.getAllKeysByPrefix(anyString()))
        .thenReturn(
            List.of(
                "archive/test-export_old1.zip",
                "archive/test-export_old2.zip",
                "archive/other-export_DO_NOT_DELETE.zip"));
    when(destinationBucket.putStream(anyString(), any(InputStream.class))).thenReturn(0L);
    doNothing().when(destinationBucket).delete(anyString());

    BulkExportService bulkExportService =
        new BulkExportService(sourceBucket, destinationBucket, outputName);

    boolean actual = bulkExportService.updateLatestZip(clock.instant());
    assertThat(actual).isTrue();

    verify(sourceBucket, times(1)).getAllKeys();
    verify(sourceBucket, times(1)).get("file1.txt");
    verify(destinationBucket, times(1)).getAllKeysByPrefix(anyString());
    verify(destinationBucket, times(1)).putStream(anyString(), any(InputStream.class));
    verify(destinationBucket, times(1)).delete("archive/test-export_old1.zip");
    verify(destinationBucket, times(1)).delete("archive/test-export_old2.zip");
  }

  @Test
  void updateLatestZip_sourceBucketThrowsIOException_shouldPropagateException()
      throws IOException, NoSuchKeyException {
    ObjectStorage sourceBucket = mock(ObjectStorage.class);
    ObjectStorage destinationBucket = mock(ObjectStorage.class);
    String outputName = "test-export";

    when(sourceBucket.getAllKeys())
        .thenThrow(new RuntimeException("The mock source bucket does not want to list files"));

    BulkExportService bulkExportService =
        new BulkExportService(sourceBucket, destinationBucket, outputName);

    Instant timestamp = clock.instant();
    assertThrows(RuntimeException.class, () -> bulkExportService.updateLatestZip(timestamp));

    verify(sourceBucket, times(0)).getStream(anyString());
    verify(destinationBucket, times(0)).putStream(anyString(), any(InputStream.class));
    verify(destinationBucket, times(0)).delete(anyString());
  }

  @Test
  void updateLatestZip_destinationBucketPutStreamThrowsIOException_shouldReturnWithFalse()
      throws IOException {
    ObjectStorage sourceBucket = mock(ObjectStorage.class);
    ObjectStorage destinationBucket = mock(ObjectStorage.class);

    when(sourceBucket.getAllKeysByPrefix("")).thenReturn(List.of("file.txt"));
    when(sourceBucket.get("file.txt")).thenReturn(Optional.of("content".getBytes()));
    when(destinationBucket.getAllKeysByPrefix(anyString())).thenReturn(Collections.emptyList());
    when(destinationBucket.putStream(anyString(), any(InputStream.class)))
        .thenThrow(new IOException("The mock destination bucket threw an exception"));

    BulkExportService bulkExportService =
        new BulkExportService(sourceBucket, destinationBucket, "test-export");

    boolean actual = bulkExportService.updateLatestZip(clock.instant());
    assertThat(actual).isFalse();
  }

  @Test
  void updateLatestZip_onEmptyFiles_shouldReturnEarlyWithFalse() {
    ObjectStorage sourceBucket = mock(ObjectStorage.class);
    ObjectStorage destinationBucket = mock(ObjectStorage.class);

    when(sourceBucket.getAllKeysByPrefix("")).thenReturn(List.of("file.txt"));

    BulkExportService bulkExportService =
        new BulkExportService(sourceBucket, destinationBucket, "test-export");

    boolean actual = bulkExportService.updateLatestZip(clock.instant());
    assertThat(actual).isFalse();
  }

  @Test
  void updateLatestZip_whenContentOfKeyIsNotFound_shouldReturnFalse() {
    ObjectStorage sourceBucket = mock(ObjectStorage.class);
    ObjectStorage destinationBucket = mock(ObjectStorage.class);

    when(sourceBucket.getAllKeysByPrefix("")).thenReturn(List.of("file"));
    when(sourceBucket.get("file")).thenReturn(Optional.empty());

    BulkExportService bulkExportService =
        new BulkExportService(sourceBucket, destinationBucket, "test-export");

    boolean actual = bulkExportService.updateLatestZip(clock.instant());
    assertThat(actual).isFalse();
  }
}
