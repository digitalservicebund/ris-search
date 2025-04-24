package de.bund.digitalservice.ris.search.integration.repository;

import de.bund.digitalservice.ris.search.exception.FileTransformationException;
import de.bund.digitalservice.ris.search.repository.objectstorage.FileSystemObjectStorage;
import java.io.FilterInputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocalFilesystemObjectStorageClientTest {

  String path = "backend/src/test/resources/data/";
  String bucket = "tmp";

  FileSystemObjectStorage client;

  @BeforeEach
  void beforeEach() {
    this.client = new FileSystemObjectStorage(bucket, path);
  }

  @Test
  void itStoresListsAndDeletesGivenFilesByPath() throws IOException {
    String path1 = "path/to/file1";
    String path2 = "path/to/file2";

    client.save(path1, "content1");
    client.save(path2, "content2");

    List<String> filenames = client.getAllFilenamesByPath("path/to");

    Assertions.assertTrue(filenames.contains("path/to/file1"));
    Assertions.assertTrue(filenames.contains("path/to/file2"));
    Assertions.assertEquals(2, filenames.size());

    FilterInputStream inputStream = client.getStream("path/to/file1");
    String actual = new String(inputStream.readAllBytes());
    Assertions.assertEquals("content1", actual);

    client.delete("path/to/file1");
    client.delete("path/to/file2");

    Assertions.assertEquals(0, client.getAllFilenamesByPath("/").size());
  }

  @Test
  void ItThrowsAnExceptionOnInvalidPaths() {

    Assertions.assertThrows(
        FileTransformationException.class,
        () -> {
          client.getAllFilenamesByPath("path/to");
        });
  }
}
