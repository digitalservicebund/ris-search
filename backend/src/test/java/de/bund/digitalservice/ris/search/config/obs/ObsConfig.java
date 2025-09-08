package de.bund.digitalservice.ris.search.config.obs;

import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorageClient;
import de.bund.digitalservice.ris.search.repository.objectstorage.S3ObjectStorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ObsConfig {

  @Bean(name = "normS3Client")
  @Profile({"test"})
  public ObjectStorageClient mockNormTestS3Client(
      @Value("${local.file-storage}") String relativeLocalStorageDirectory) {
    return new S3ObjectStorageClient(
        new TestMockS3Client("norm", relativeLocalStorageDirectory), "norm");
  }

  @Bean(name = "caseLawS3Client")
  @Profile({"test"})
  public ObjectStorageClient mockCaseLawTestS3Client(
      @Value("${local.file-storage}") String relativeLocalStorageDirectory) {
    return new S3ObjectStorageClient(
        new TestMockS3Client("caselaw", relativeLocalStorageDirectory), "caselaw");
  }

  @Bean(name = "literatureS3Client")
  @Profile({"test"})
  public ObjectStorageClient mockLiteratureTestS3Client(
      @Value("${local.file-storage}") String relativeLocalStorageDirectory) {
    var bucketName = "literature";
    return new S3ObjectStorageClient(
        new TestMockS3Client(bucketName, relativeLocalStorageDirectory), bucketName);
  }

  @Bean(name = "portalS3Client")
  @Profile({"test"})
  public ObjectStorageClient mockPortalTestS3Client(
      @Value("${local.file-storage}") String relativeLocalStorageDirectory) {
    return new S3ObjectStorageClient(
        new TestMockS3Client("portal", relativeLocalStorageDirectory), "portal");
  }
}
