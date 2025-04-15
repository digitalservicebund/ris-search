package de.bund.digitalservice.ris.search.config.obs;

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

  @Bean(name = "normS3Client")
  @Profile({"production", "staging", "prototype"})
  public S3Client normS3Client() throws URISyntaxException {
    return S3Client.builder()
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(normAccessKeyId, normSecretAccessKey)))
        .endpointOverride(new URI(normEndpoint))
        .region(Region.of(REGION))
        .build();
  }

  @Bean(name = "caseLawS3Client")
  @Profile({"staging", "prototype"})
  public S3Client caseLawS3Client() throws URISyntaxException {
    return S3Client.builder()
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(caseLawAccessKeyId, caseLawSecretAccessKey)))
        .endpointOverride(new URI(caseLawEndpoint))
        .region(Region.of(REGION))
        .build();
  }

  @Bean(name = "portalS3Client")
  @Profile({"production", "staging", "prototype"})
  public S3Client portalS3Client() throws URISyntaxException {
    return S3Client.builder()
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(portalAccessKeyId, portalSecretAccessKey)))
        .endpointOverride(new URI(portalEndpoint))
        .region(Region.of(REGION))
        .build();
  }

  @Bean(name = "normS3Client")
  @Profile({"default"})
  public S3Client mockNormS3Client() {
    return new LocalMockS3Client("norm");
  }

  @Bean(name = "normS3Client")
  @Profile({"test"})
  public S3Client mockNormTestS3Client() {
    return new TestMockS3Client("norm");
  }

  @Bean(name = "caseLawS3Client")
  @Profile({"default"})
  public S3Client mockCaseLawS3Client() {
    return new LocalMockS3Client("caselaw");
  }

  @Bean(name = "caseLawS3Client")
  @Profile({"test"})
  public S3Client mockCaseLawTestS3Client() {
    return new TestMockS3Client("caselaw");
  }

  @Bean(name = "portalS3Client")
  @Profile({"default"})
  public S3Client mockPortalS3Client() {
    return new LocalMockS3Client("portal");
  }

  @Bean(name = "portalS3Client")
  @Profile({"test"})
  public S3Client mockPortalTestS3Client() {
    return new TestMockS3Client("portal");
  }
}
