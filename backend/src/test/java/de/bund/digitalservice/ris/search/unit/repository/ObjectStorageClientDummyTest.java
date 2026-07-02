package de.bund.digitalservice.ris.search.unit.repository;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorageClientDummy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ObjectStorageClientDummyTest {

  ObjectStorageClientDummy client = new ObjectStorageClientDummy();

  @Test
  void itReturnsEmptyListOfKeysByPrefix() {
    Assertions.assertThat(client.listKeysByPrefix("foo")).isEmpty();
  }

  @Test
  void itReturnsEmptyListByLastModified() {
    Assertions.assertThat(client.listByPrefixWithLastModified("foo")).isEmpty();
  }

  @Test
  void itThrowsUnsupportedOperationOnPutStream() {
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> client.putStream("foo", null));
  }

  @Test
  void itThrowsUnsupportedOperationOnSave() {
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> client.save("foo", "bar"));
  }

  @Test
  void itThrowsUnsupportedOperationOnDelete() {
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> client.delete("foo"));
  }

  @Test
  void itThrowsNoSuchKeyOnGetStream() {
    assertThatExceptionOfType(NoSuchKeyException.class).isThrownBy(() -> client.getStream("foo"));
  }
}
