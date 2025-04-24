package de.bund.digitalservice.ris.search.config.obs;

import de.bund.digitalservice.ris.search.repository.objectstorage.FileSystemObjectStorage;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.repository.objectstorage.S3ObjectStorage;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class ObsConfig {
  public static final String REGION = "eu-de";

  @Value("${s3.file-storage.case-law.endpoint}")
  private String caseLawEndpoint;

  @Value("${s3.file-storage.case-law.access-key-id}")
  private String caseLawAccessKeyId;

  @Value("${s3.file-storage.case-law.secret-access-key}")
  private String caseLawSecretAccessKey;

  @Value("${s3.file-storage.norm.endpoint}")
  private String normEndpoint;

  @Value("${s3.file-storage.norm.access-key-id}")
  private String normAccessKeyId;

  @Value("${s3.file-storage.norm.secret-access-key}")
  private String normSecretAccessKey;

  @Value("${s3.file-storage.portal.endpoint}")
  private String portalEndpoint;

  @Value("${s3.file-storage.portal.access-key-id}")
  private String portalAccessKeyId;

  @Value("${s3.file-storage.portal.secret-access-key}")
  private String portalSecretAccessKey;

  @Bean(name = "normObjectStorage")
  @Profile({"production", "staging", "prototype"})
  public ObjectStorage normS3Client(@Value("${s3.file-storage.norm.bucket-name}") String bucket)
      throws URISyntaxException {
    return new S3ObjectStorage(
        S3Client.builder()
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(portalAccessKeyId, portalSecretAccessKey)))
            .endpointOverride(new URI(portalEndpoint))
            .region(Region.of(REGION))
            .build(),
        bucket);
  }

  @Bean(name = "caseLawObjectStorage")
  @Profile({"staging", "prototype"})
  public ObjectStorage caseLawS3Client(
      @Value("${s3.file-storage.case-law.bucket-name}") String bucket) throws URISyntaxException {
    return new S3ObjectStorage(
        S3Client.builder()
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(portalAccessKeyId, portalSecretAccessKey)))
            .endpointOverride(new URI(portalEndpoint))
            .region(Region.of(REGION))
            .build(),
        bucket) {};
  }

  @Bean(name = "portalObjectStorage")
  @Profile({"production", "staging", "prototype"})
  public ObjectStorage portalS3Client(@Value("${s3.file-storage.portal.bucket-name}") String bucket)
      throws URISyntaxException {
    return new S3ObjectStorage(
        S3Client.builder()
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(portalAccessKeyId, portalSecretAccessKey)))
            .endpointOverride(new URI(portalEndpoint))
            .region(Region.of(REGION))
            .build(),
        bucket);
  }

  @Bean(name = "normObjectStorage")
  @Profile({"default"})
  public ObjectStorage mockNormS3Client(
      @Value("${local.file-storage}") String relativeLocalStorageDirectory) {
    return new FileSystemObjectStorage("norm", relativeLocalStorageDirectory);
  }

  @Bean(name = "caseLawObjectStorage")
  @Profile({"default"})
  public ObjectStorage mockCaseLawS3Client(
      @Value("${local.file-storage}") String relativeLocalStorageDirectory) {
    return new FileSystemObjectStorage("caselaw", relativeLocalStorageDirectory);
  }

  @Bean(name = "portalObjectStorage")
  @Profile({"default"})
  public ObjectStorage mockPortalS3Client(
      @Value("${local.file-storage}") String relativeLocalStorageDirectory) {
    return new FileSystemObjectStorage("portal", relativeLocalStorageDirectory);
  }
}
