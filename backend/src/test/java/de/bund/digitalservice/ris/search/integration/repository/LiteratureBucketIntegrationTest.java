package de.bund.digitalservice.ris.search.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import de.bund.digitalservice.ris.search.config.obs.ObsConfig;
import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.repository.objectstorage.literature.LiteratureBucketImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {LiteratureBucketImpl.class, ObsConfig.class})
@Tag("integration")
class LiteratureBucketIntegrationTest {

  @Autowired LiteratureBucketImpl bucket;

  @BeforeEach
  void setUp() {
    bucket.getAllKeys().forEach(key -> bucket.delete(key));
  }

  @Test
  void getAllKeysReturnsAllKeys() {
    bucket.save("XXLU000000001.akn.xml", "");
    bucket.save("changelog.json", "");
    assertThat(bucket.getAllKeys())
        .containsExactlyInAnyOrder("changelog.json", "XXLU000000001.akn.xml");
  }

  @Test
  void getAllKeysReturnsEmptyListIfBucketIsEmpty() {
    assertThat(bucket.getAllKeys()).isEmpty();
  }

  @Test
  void getAllKeysByPrefixReturnsOnlyKeysWithMatchingPrefix() {
    bucket.save("XXLU010000001.akn.xml", "");
    bucket.save("XXLU020000001.akn.xml", "");
    bucket.save("changelog.json", "");
    assertThat(bucket.getAllKeysByPrefix("XXLU01")).containsExactly("XXLU010000001.akn.xml");
  }

  @Test
  void getAllKeysByPrefixReturnsEmptyListIfNoKeysMatchThePrefix() {
    bucket.save("XXLU010000001.akn.xml", "");
    bucket.save("XXLU020000001.akn.xml", "");
    assertThat(bucket.getAllKeysByPrefix("FOO")).isEmpty();
  }

  @Test
  void getAllKeyInfosByPrefixReturnsMatchingKeyInfos() {
    bucket.save("XXLU010000001.akn.xml", "");
    bucket.save("XXLU020000001.akn.xml", "");
    bucket.save("changelog.json", "");
    assertThat(bucket.getAllKeyInfosByPrefix("XXLU01")).hasSize(1);
  }

  @Test
  void getAllKeyInfosByPrefixReturnEmptyListIfNoKeysMatchThePrefix() {
    bucket.save("XXLU010000001.akn.xml", "");
    bucket.save("XXLU020000001.akn.xml", "");
    assertThat(bucket.getAllKeyInfosByPrefix("FOO")).isEmpty();
  }

  @Test
  void getFileAsStringReturnsContentAsString() throws ObjectStoreServiceException {
    bucket.save("XXLU010000001.akn.xml", "This is a test!");
    assertThat(bucket.getFileAsString("XXLU010000001.akn.xml")).get().isEqualTo("This is a test!");
  }

  @Test
  void getFileAsStringReturnsEmptyOptionalIfObjectDoesntExist() throws ObjectStoreServiceException {
    assertThat(bucket.getFileAsString("XXLU010000001.akn.xml")).isEmpty();
  }

  @Test
  void getBytesReturnsContentAsBytes() throws ObjectStoreServiceException {
    bucket.save("XXLU010000001.akn.xml", "This is a test!");
    assertThat(bucket.get("XXLU010000001.akn.xml").get()).asString().isEqualTo("This is a test!");
  }

  @Test
  void getBytesReturnsEmptyOptionalIfObjectDoesNotExist() throws ObjectStoreServiceException {
    assertThat(bucket.get("XXLU010000001.akn.xml")).isEmpty();
  }

  @Test
  void getStreamReturnsFilterInputStream() throws NoSuchKeyException {
    bucket.save("XXLU010000001.akn.xml", "This is a test!");
    assertThat(bucket.getStream("XXLU010000001.akn.xml")).hasContent("This is a test!");
  }

  @Test
  void getStreamThrowsIfObjectDoesNotExist() {
    assertThatExceptionOfType(NoSuchKeyException.class)
        .isThrownBy(() -> bucket.getStream("foo.xml"));
  }
}
