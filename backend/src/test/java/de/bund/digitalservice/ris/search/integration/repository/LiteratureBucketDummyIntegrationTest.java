package de.bund.digitalservice.ris.search.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.repository.objectstorage.literature.LiteratureBucketDummy;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = LiteratureBucketDummy.class)
@Tag("integration")
class LiteratureBucketDummyIntegrationTest {

  LiteratureBucketDummy bucket = new LiteratureBucketDummy();

  @Test
  void getAllKeysAlwaysReturnsEmptyList() {
    bucket.save("foo.xml", "");
    assertThat(bucket.getAllKeys()).isEmpty();
  }

  @Test
  void getAllKeysByPrefixAlwaysReturnsEmptyList() {
    bucket.save("foo-bar.xml", "");
    assertThat(bucket.getAllKeysByPrefix("foo")).isEmpty();
  }

  @Test
  void getFileAsStringAlwaysReturnsEmptyOptional() throws ObjectStoreServiceException {
    bucket.save("foo.xml", "");
    assertThat(bucket.getFileAsString("foo.xml")).isEmpty();
  }

  @Test
  void getBytesAlwaysReturnsEmptyOptional() throws ObjectStoreServiceException {
    bucket.save("foo.xml", "");
    assertThat(bucket.get("foo.xml")).isEmpty();
  }

  @Test
  void getStreamAlwaysThrowsNoSuchKeyException() {
    bucket.save("foo.xml", "");
    assertThatExceptionOfType(NoSuchKeyException.class)
        .isThrownBy(() -> bucket.getStream("foo.xml"));
  }

  @Test
  void getAllKeyInfosByPrefixAlwaysReturnsEmptyList() {
    bucket.save("foo-bar.xml", "");
    assertThat(bucket.getAllKeyInfosByPrefix("foo")).isEmpty();
  }
}
