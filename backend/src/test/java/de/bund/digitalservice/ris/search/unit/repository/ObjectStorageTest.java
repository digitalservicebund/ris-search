package de.bund.digitalservice.ris.search.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorageClient;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ObjectStorageTest {

  @Mock ObjectStorageClient client;

  ObjectStorage storage;

  @BeforeEach
  void setup() {
    this.storage = new ObjectStorage(client, LogManager.getLogger(ObjectStorageTest.class), "");
  }

  @Test
  void getObjectsQuitsAndRethrowsObjectStoreServiceExceptions() throws NoSuchKeyException {
    when(client.getStream("key1")).thenThrow(new ObjectStoreServiceException("no connection"));

    assertThatThrownBy(() -> storage.getObjects(List.of("key1", "key2")))
        .isInstanceOf(ObjectStoreServiceException.class)
        .hasMessageContaining("no connection");
  }

  @Test
  void getObjectsDoesntAbortOnNonRuntimeExceptions() throws NoSuchKeyException {
    byte[] testdata = "testdata".getBytes(StandardCharsets.UTF_8);
    when(client.getStream("key1")).thenThrow(new NoSuchKeyException("noSuchKey", new Throwable()));
    when(client.getStream("key2"))
        .thenReturn(new DataInputStream(new ByteArrayInputStream(testdata)));

    var result = storage.getObjects(List.of("key1", "key2"));

    assertThat(result.getFirst().key()).isEqualTo("key1");
    assertThat(result.getFirst().bytes()).isEqualTo(Optional.empty());
    assertThat(result.getLast().key()).isEqualTo("key2");
    assertThat(result.getLast().bytes())
        .hasValueSatisfying(bytes -> assertThat(bytes).containsExactly(testdata));
  }
}
