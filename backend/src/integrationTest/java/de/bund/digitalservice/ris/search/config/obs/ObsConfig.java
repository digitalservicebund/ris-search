package de.bund.digitalservice.ris.search.config.obs;

import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorageClient;
import de.bund.digitalservice.ris.search.repository.objectstorage.S3ObjectStorageClient;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/** Configuration for S3 clients backed by the S3Mock container used in testing. */
@Configuration
@Profile({"test"})
public class ObsConfig {

  private final URI endpoint;

  public ObsConfig(@Value("${s3mock.endpoint}") String endpoint) {
    this.endpoint = URI.create(endpoint);
  }

  /**
   * S3 client for case law documents
   *
   * @return ObjectStorageClient
   */
  @Bean(name = "caseLawS3Client")
  public ObjectStorageClient caseLawTestS3Client() {
    return createClient("caselaw");
  }

  /**
   * S3 client for literature documents
   *
   * @return ObjectStorageClient
   */
  @Bean(name = "literatureS3Client")
  public ObjectStorageClient literatureTestS3Client() {
    return createClient("literature");
  }

  /**
   * S3 client for administrative directive documents
   *
   * @return ObjectStorageClient
   */
  @Bean(name = "administrativeDirectiveS3Client")
  public ObjectStorageClient administrativeDirectiveTestS3Client() {
    return createClient("administrative-directive");
  }

  /**
   * S3 client for norm documents
   *
   * @return ObjectStorageClient
   */
  @Bean(name = "normS3Client")
  public ObjectStorageClient normTestS3Client() {
    return createClient("norm");
  }

  /**
   * S3 client for portal documents
   *
   * @return ObjectStorageClient
   */
  @Bean(name = "portalS3Client")
  public ObjectStorageClient portalTestS3Client() {
    return createClient("portal");
  }

  /**
   * S3 client for public documents
   *
   * @return ObjectStorageClient
   */
  @Bean(name = "publicFilesS3Client")
  public ObjectStorageClient publicFilesTestS3Client() {
    return createClient("public");
  }

  private ObjectStorageClient createClient(String bucketName) {
    return new S3ObjectStorageClient(
        S3Client.builder()
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("local-s3-mock-key", "local-s3-mock-secret")))
            .forcePathStyle(true)
            .region(Region.of("eu-de"))
            .endpointOverride(endpoint)
            .build(),
        bucketName);
  }
}
