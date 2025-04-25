package de.bund.digitalservice.ris.search.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.repository.objectstorage.LocalFilesystemObjectStorageClient;
import java.io.FilterInputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocalFilesystemObjectStorageClientTest {

  String path = "backend/src/test/resources/data/";
  String bucket = "tmp";

  LocalFilesystemObjectStorageClient client;

  @BeforeEach
  void beforeEach() {
    this.client = new LocalFilesystemObjectStorageClient(bucket, path);
    for (String key : this.client.getAllFilenamesByPath("")) {
      this.client.delete(key);
    }
  }

  @Test
  void itStoresListsAndDeletesGivenFilesByPath() throws IOException {
    String path1 = "path/to/file1";
    String path2 = "path/to/file2";

    client.save(path1, "content1");
    client.save(path2, "content2");

    List<String> filenames = client.getAllFilenamesByPath("path/to");

    assertThat(filenames).containsExactlyInAnyOrder(path1, path2);

    FilterInputStream inputStream = client.getStream("path/to/file1");
    assertThat(inputStream).hasContent("content1");

    client.delete("path/to/file1");
    client.delete("path/to/file2");

    assertThat(client.getAllFilenamesByPath("")).isEmpty();
  }

  @Test
  void itSupportEmptyPrefixes() {
    client.save("path/to/file", "content");
    assertThat(client.getAllFilenamesByPath("")).containsExactly("path/to/file");
  }

  @Test
  void ItReturnsAnEmptyListOnInvalidPaths() {
    assertThat(client.getAllFilenamesByPath("path/to")).isEmpty();
  }
}
