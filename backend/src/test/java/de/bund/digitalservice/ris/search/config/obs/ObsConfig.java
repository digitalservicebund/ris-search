package de.bund.digitalservice.ris.search.config.obs;

import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.repository.objectstorage.S3ObjectStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ObsConfig {

  @Bean(name = "normObjectStorage")
  @Profile({"test"})
  public ObjectStorage mockNormTestS3Client(
      @Value("${local.file-storage}") String relativeLocalStorageDirectory) {
    return new S3ObjectStorage(new TestMockS3Client("norm", relativeLocalStorageDirectory), "norm");
  }

  @Bean(name = "caseLawObjectStorage")
  @Profile({"test"})
  public ObjectStorage mockCaseLawTestS3Client(
      @Value("${local.file-storage}") String relativeLocalStorageDirectory) {
    return new S3ObjectStorage(
        new TestMockS3Client("caselaw", relativeLocalStorageDirectory), "caselaw");
  }

  @Bean(name = "portalObjectStorage")
  @Profile({"test"})
  public ObjectStorage mockPortalTestS3Client(
      @Value("${local.file-storage}") String relativeLocalStorageDirectory) {
    return new S3ObjectStorage(
        new TestMockS3Client("portal", relativeLocalStorageDirectory), "portal");
  }
}
