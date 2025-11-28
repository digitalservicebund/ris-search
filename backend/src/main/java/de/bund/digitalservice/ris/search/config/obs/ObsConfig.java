package de.bund.digitalservice.ris.search.config.obs;

import de.bund.digitalservice.ris.search.repository.objectstorage.LocalFilesystemObjectStorageClient;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorageClient;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorageClientDummy;
import de.bund.digitalservice.ris.search.repository.objectstorage.S3ObjectStorageClient;
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

/**
 * Configuration for object storage clients used by the application.
 *
 * <p>Provides beans for S3-backed and local filesystem-backed ObjectStorageClient instances for
 * different application profiles (production, staging, default, ...).
 */
@Configuration
public class ObsConfig {
  public static final String REGION = "eu-de";

  @Value("${s3.file-storage.case-law.endpoint}")
  private String caseLawEndpoint;

  @Value("${s3.file-storage.case-law.access-key-id}")
  private String caseLawAccessKeyId;

  @Value("${s3.file-storage.case-law.secret-access-key}")
  private String caseLawSecretAccessKey;

  @Value("${s3.file-storage.literature.endpoint}")
  private String literatureEndpoint;

  @Value("${s3.file-storage.literature.access-key-id}")
  private String literatureAccessKeyId;

  @Value("${s3.file-storage.literature.secret-access-key}")
  private String literatureSecretAccessKey;

  @Value("${s3.file-storage.administrative-directive.endpoint}")
  private String administrativeDirectiveEndpoint;

  @Value("${s3.file-storage.administrative-directive.access-key-id}")
  private String administrativeDirectiveAccessKeyId;

  @Value("${s3.file-storage.administrative-directive.secret-access-key}")
  private String administrativeDirectiveSecretAccessKey;

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

  /**
   * Creates an {@code ObjectStorageClient} for the "norm" context, which interacts with an
   * S3-compatible object storage service. This client is configured using specific credentials,
   * endpoint details, region, and the associated bucket name.
   *
   * @param bucket the name of the S3 bucket to be used by this client
   * @return an {@code ObjectStorageClient} configured for the "norm" context
   * @throws URISyntaxException if the endpoint URI is invalid
   */
  @Bean(name = "normS3Client")
  @Profile({"production", "staging", "uat", "prototype"})
  public ObjectStorageClient normS3Client(
      @Value("${s3.file-storage.norm.bucket-name}") String bucket) throws URISyntaxException {
    return new S3ObjectStorageClient(
        S3Client.builder()
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(normAccessKeyId, normSecretAccessKey)))
            .endpointOverride(new URI(normEndpoint))
            .region(Region.of(REGION))
            .build(),
        bucket);
  }

  /**
   * Creates an {@code ObjectStorageClient} for the "case law" context, which interacts with an
   * S3-compatible object storage service. This client is configured using specific credentials,
   * endpoint details, region, and the associated bucket name.
   *
   * @param bucket the name of the S3 bucket to be used by this client
   * @return an {@code ObjectStorageClient} configured for the "case law" context
   * @throws URISyntaxException if the endpoint URI is invalid
   */
  @Bean(name = "caseLawS3Client")
  @Profile({"staging", "uat", "prototype"})
  public ObjectStorageClient caseLawS3Client(
      @Value("${s3.file-storage.case-law.bucket-name}") String bucket) throws URISyntaxException {
    return new S3ObjectStorageClient(
        S3Client.builder()
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(caseLawAccessKeyId, caseLawSecretAccessKey)))
            .endpointOverride(new URI(caseLawEndpoint))
            .region(Region.of(REGION))
            .build(),
        bucket);
  }

  /**
   * Creates an {@code ObjectStorageClient} for the "literature" context, which interacts with an
   * S3-compatible object storage service. This client is configured using specific credentials,
   * endpoint details, and the associated bucket name.
   *
   * @param bucket the name of the S3 bucket to be used by this client
   * @return an {@code ObjectStorageClient} configured for the "literature" context
   * @throws URISyntaxException if the endpoint URI is invalid
   */
  @Bean(name = "literatureS3Client")
  @Profile({"staging"})
  public ObjectStorageClient literatureS3Client(
      @Value("${s3.file-storage.literature.bucket-name}") String bucket) throws URISyntaxException {
    return new S3ObjectStorageClient(
        S3Client.builder()
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(literatureAccessKeyId, literatureSecretAccessKey)))
            .endpointOverride(new URI(literatureEndpoint))
            .region(Region.of(REGION))
            .build(),
        bucket);
  }

  @Bean(name = "literatureS3Client")
  @Profile({"uat", "production", "prototype"})
  public ObjectStorageClient literatureS3DummyClient() {
    return new ObjectStorageClientDummy();
  }

  /**
   * Creates an {@code ObjectStorageClient} for the "administrative-directive" context, which
   * interacts with an S3-compatible object storage service. This client is configured using
   * specific credentials, endpoint details, and the associated bucket name.
   *
   * @param bucket the name of the S3 bucket to be used by this client
   * @return an {@code ObjectStorageClient} configured for the "administrative-directive" context
   * @throws URISyntaxException if the endpoint URI is invalid
   */
  @Bean(name = "administrativeDirectiveS3Client")
  @Profile({"staging"})
  public ObjectStorageClient administrativeDirectiveS3Client(
      @Value("${s3.file-storage.administrative-directive.bucket-name}") String bucket)
      throws URISyntaxException {
    return new S3ObjectStorageClient(
        S3Client.builder()
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        administrativeDirectiveAccessKeyId,
                        administrativeDirectiveSecretAccessKey)))
            .endpointOverride(new URI(administrativeDirectiveEndpoint))
            .region(Region.of(REGION))
            .build(),
        bucket);
  }

  @Bean(name = "administrativeDirectiveS3Client")
  @Profile({"uat", "production", "prototype"})
  public ObjectStorageClient administrativeDirectiveS3DummyClient() {
    return new ObjectStorageClientDummy();
  }

  /**
   * Creates an {@code ObjectStorageClient} for the "portal" context, which interacts with an
   * S3-compatible object storage service. This client is configured using specific credentials,
   * endpoint details, and the associated bucket name.
   *
   * @param bucket the name of the S3 bucket to be used by this client
   * @return an {@code ObjectStorageClient} configured for the "portal" context
   * @throws URISyntaxException if the endpoint URI is invalid
   */
  @Bean(name = "portalS3Client")
  @Profile({"production", "staging", "uat", "prototype"})
  public ObjectStorageClient portalS3Client(
      @Value("${s3.file-storage.portal.bucket-name}") String bucket) throws URISyntaxException {
    return new S3ObjectStorageClient(
        S3Client.builder()
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(portalAccessKeyId, portalSecretAccessKey)))
            .endpointOverride(new URI(portalEndpoint))
            .region(Region.of(REGION))
            .build(),
        bucket);
  }

  @Bean(name = "normS3Client")
  @Profile({"default"})
  public ObjectStorageClient mockNormS3Client(
      @Value("${local.file-storage}") String relativeLocalStorageDirectory) {
    return new LocalFilesystemObjectStorageClient("norm", relativeLocalStorageDirectory);
  }

  @Bean(name = "caseLawS3Client")
  @Profile({"default"})
  public ObjectStorageClient mockCaseLawS3Client(
      @Value("${local.file-storage}") String relativeLocalStorageDirectory) {
    return new LocalFilesystemObjectStorageClient("caselaw", relativeLocalStorageDirectory);
  }

  @Bean(name = "literatureS3Client")
  @Profile({"default"})
  public ObjectStorageClient mockLiteratureS3Client(
      @Value("${local.file-storage}") String relativeLocalStorageDirectory) {
    return new LocalFilesystemObjectStorageClient("literature", relativeLocalStorageDirectory);
  }

  /**
   * Creates a mock implementation of the {@code ObjectStorageClient} for the
   * "administrative-directive" context in a local environment. This mock client uses the local file
   * system for object storage operations.
   *
   * @param relativeLocalStorageDirectory the relative path for the local storage directory
   * @return a {@code LocalFilesystemObjectStorageClient} configured for the
   *     "administrative-directive" context
   */
  @Bean(name = "administrativeDirectiveS3Client")
  @Profile({"default"})
  public ObjectStorageClient mockAdministrativeDirectiveS3Client(
      @Value("${local.file-storage}") String relativeLocalStorageDirectory) {
    return new LocalFilesystemObjectStorageClient(
        "administrative-directive", relativeLocalStorageDirectory);
  }

  @Bean(name = "portalS3Client")
  @Profile({"default"})
  public ObjectStorageClient mockPortalS3Client(
      @Value("${local.file-storage}") String relativeLocalStorageDirectory) {
    return new LocalFilesystemObjectStorageClient("portal", relativeLocalStorageDirectory);
  }
}
