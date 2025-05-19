package de.bund.digitalservice.ris.search.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.repository.objectstorage.LocalFilesystemObjectStorageClient;
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocalFilesystemObjectStorageClientTest {

  String path = "backend/src/test/resources/data/";
  String bucket = "tmp";

  LocalFilesystemObjectStorageClient client;

  @BeforeEach
  void beforeEach() {
    this.client = new LocalFilesystemObjectStorageClient(bucket, path);
  }

  @AfterEach
  void afterEach() {
    for (String key : this.client.listKeysByPrefix("")) {
      this.client.delete(key);
    }
  }

  @Test
  void itStoresListsAndDeletesGivenFilesByPath() throws NoSuchKeyException {
    String path1 = "path/to/file1";
    String path2 = "path/to/file2";

    client.save(path1, "content1");
    client.save(path2, "content2");

    List<String> filenames = client.listKeysByPrefix("path/to/");

    assertThat(filenames).containsExactlyInAnyOrder(path1, path2);

    FilterInputStream inputStream = client.getStream("path/to/file1");
    assertThat(inputStream).hasContent("content1");

    client.delete("path/to/file1");
    client.delete("path/to/file2");

    assertThat(client.listKeysByPrefix("")).isEmpty();
  }

  @Test
  void itSupportEmptyPrefixes() {
    client.save("path/to/file", "content");
    assertThat(client.listKeysByPrefix("")).containsExactly("path/to/file");
  }

  @Test
  void itSupportsIncompletePrefixes() {
    client.save("path/to/file", "content");
    client.save("path/to/file2", "content");
    assertThat(client.listKeysByPrefix("path"))
        .containsExactlyInAnyOrder("path/to/file", "path/to/file2");

    assertThat(client.listKeysByPrefix("path/to"))
        .containsExactlyInAnyOrder("path/to/file", "path/to/file2");

    assertThat(client.listKeysByPrefix("path/t"))
        .containsExactlyInAnyOrder("path/to/file", "path/to/file2");

    assertThat(client.listKeysByPrefix("path/to/"))
        .containsExactlyInAnyOrder("path/to/file", "path/to/file2");

    assertThat(client.listKeysByPrefix("path/to/file"))
        .containsExactlyInAnyOrder("path/to/file", "path/to/file2");

    assertThat(client.listKeysByPrefix("path/to/file2")).containsExactlyInAnyOrder("path/to/file2");
  }

  @Test
  void ItReturnsAnEmptyListOnInvalidPaths() {
    assertThat(client.listKeysByPrefix("another/")).isEmpty();
  }

  @Test
  void itWritesStreamToFile() throws NoSuchKeyException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream("content".getBytes());
    client.putStream("stream", inputStream);

    assertThat(client.getStream("stream")).hasContent("content");
  }
}
