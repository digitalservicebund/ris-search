package de.bund.digitalservice.ris.search.unit.repository;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorageClientDummy;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ObjectStorageClientDummyTest {

  ObjectStorageClientDummy client = new ObjectStorageClientDummy();

  @BeforeEach()
  void setup() {
    client.save("foo-bar.xml", "");
  }

  @Test
  void itReturnsEmptyListOfKeysByPrefix() {
    Assertions.assertThat(client.listKeysByPrefix("foo")).isEmpty();
  }

  @Test
  void itReturnsEmptyListByLastModified() {
    Assertions.assertThat(client.listByPrefixWithLastModified("foo")).isEmpty();
  }

  @Test
  void itReturnsZeroOnPutStream() throws IOException {
    Assertions.assertThat(client.putStream("test", new ByteArrayInputStream("foo".getBytes())))
        .isZero();
  }

  @Test
  void itThrowsNoSuchKeyOnGetStream() {
    assertThatExceptionOfType(NoSuchKeyException.class).isThrownBy(() -> client.getStream("foo"));
  }
}
