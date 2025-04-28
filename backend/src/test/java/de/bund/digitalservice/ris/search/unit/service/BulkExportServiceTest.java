package de.bund.digitalservice.ris.search.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.ZipTestUtils;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.service.BulkExportService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@ExtendWith(MockitoExtension.class)
class BulkExportServiceTest {

  @InjectMocks private BulkExportService bulkExportService;

  final Function<byte[], ResponseInputStream<GetObjectResponse>> makeInputStream =
      bytes -> {
        final var stream = new ByteArrayInputStream(bytes);
        return new ResponseInputStream<>(Mockito.mock(GetObjectResponse.class), stream);
      };

  @Test
  void updateExport_successfulZipAndUpload() throws IOException {
    ObjectStorage sourceBucket = mock(ObjectStorage.class);
    ObjectStorage destinationBucket = mock(ObjectStorage.class);
    String outputName = "test-export";
    String prefix = "some/path/";

    when(sourceBucket.getAllFilenamesByPath(prefix)).thenReturn(List.of("file1.txt", "file2.pdf"));
    final byte[] bytes1 = "This is the content of file 1.".getBytes();
    when(sourceBucket.getStream("file1.txt")).thenReturn(makeInputStream.apply(bytes1));
    final byte[] bytes2 = "%PDF-1.5...".getBytes();
    when(sourceBucket.getStream("file2.pdf")).thenReturn(makeInputStream.apply(bytes2));
    when(destinationBucket.getAllFilenamesByPath(anyString())).thenReturn(Collections.emptyList());

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

    assertDoesNotThrow(
        () -> bulkExportService.updateExport(sourceBucket, destinationBucket, outputName, prefix));

    verify(sourceBucket, times(1)).getAllFilenamesByPath(prefix);
    verify(sourceBucket, times(1)).getStream("file1.txt");
    verify(sourceBucket, times(1)).getStream("file2.pdf");
    verify(destinationBucket, times(1)).getAllFilenamesByPath(anyString());
    verify(destinationBucket, times(1))
        .putStream(
            matches("archive/test-export_\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+Z\\.zip"),
            any(InputStream.class));
    verify(destinationBucket, times(0)).delete(anyString());

    assertThat(files.get())
        .containsExactly(Map.entry("file1.txt", bytes1), Map.entry("file2.pdf", bytes2));
  }

  @Test
  void updateExport_withObsoleteFiles_shouldDeleteThem() throws IOException {
    ObjectStorage sourceBucket = mock(ObjectStorage.class);
    ObjectStorage destinationBucket = mock(ObjectStorage.class);
    String outputName = "test-export";
    String prefix = "some/path/";
    String file1Content = "Some content";

    when(sourceBucket.getAllFilenamesByPath(prefix)).thenReturn(List.of("file1.txt"));
    when(sourceBucket.getStream("file1.txt"))
        .thenReturn(makeInputStream.apply(file1Content.getBytes()));
    when(destinationBucket.getAllFilenamesByPath(anyString()))
        .thenReturn(
            List.of(
                "archive/test-export_old1.zip",
                "archive/test-export_old2.zip",
                "archive/other-export_DO_NOT_DELETE.zip"));
    when(destinationBucket.putStream(anyString(), any(InputStream.class))).thenReturn(0L);
    doNothing().when(destinationBucket).delete(anyString());

    assertDoesNotThrow(
        () -> bulkExportService.updateExport(sourceBucket, destinationBucket, outputName, prefix));

    verify(sourceBucket, times(1)).getAllFilenamesByPath(prefix);
    verify(sourceBucket, times(1)).getStream("file1.txt");
    verify(destinationBucket, times(1)).getAllFilenamesByPath(anyString());
    verify(destinationBucket, times(1)).putStream(anyString(), any(InputStream.class));
    verify(destinationBucket, times(1)).delete("archive/test-export_old1.zip");
    verify(destinationBucket, times(1)).delete("archive/test-export_old2.zip");
  }

  @Test
  void updateExport_sourceBucketThrowsIOException_shouldPropagateException() throws IOException {
    ObjectStorage sourceBucket = mock(ObjectStorage.class);
    ObjectStorage destinationBucket = mock(ObjectStorage.class);
    String outputName = "test-export";
    String prefix = "some/path/";

    when(sourceBucket.getAllFilenamesByPath(prefix))
        .thenThrow(new RuntimeException("The mock source bucket does not want to list files"));

    assertThrows(
        RuntimeException.class,
        () -> bulkExportService.updateExport(sourceBucket, destinationBucket, outputName, prefix));

    verify(sourceBucket, times(0)).getStream(anyString());
    verify(destinationBucket, times(0)).putStream(anyString(), any(InputStream.class));
    verify(destinationBucket, times(0)).delete(anyString());
  }

  @Test
  void updateExport_destinationBucketPutStreamThrowsIOException_shouldPropagateException()
      throws IOException {
    ObjectStorage sourceBucket = mock(ObjectStorage.class);
    ObjectStorage destinationBucket = mock(ObjectStorage.class);

    when(sourceBucket.getAllFilenamesByPath("some/prefix/")).thenReturn(List.of("file.txt"));
    when(sourceBucket.getStream("file.txt"))
        .thenReturn(makeInputStream.apply("content".getBytes()));
    when(destinationBucket.getAllFilenamesByPath(anyString())).thenReturn(Collections.emptyList());
    when(destinationBucket.putStream(anyString(), any(InputStream.class)))
        .thenThrow(new IOException("The mock source bucket threw an exception"));

    assertThrows(
        Exception.class,
        () ->
            bulkExportService.updateExport(
                sourceBucket, destinationBucket, "test-export", "some/prefix/"));

    verify(sourceBucket, times(1)).getAllFilenamesByPath("some/prefix/");
    verify(sourceBucket, times(1)).getStream("file.txt");
    verify(destinationBucket, times(1)).getAllFilenamesByPath(anyString());
    verify(destinationBucket, times(0)).delete(anyString());
  }
}
