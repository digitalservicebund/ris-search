package de.bund.digitalservice.ris.search.caselawhandover.shared;

import de.bund.digitalservice.ris.search.exception.RetryableObjectStoreException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3Bucket {

  private final Logger logger;
  private final S3Client s3Client;
  private final String bucketName;

  public S3Bucket(S3Client s3Client, String bucketName, Logger logger) {
    this.s3Client = s3Client;
    this.bucketName = bucketName;
    this.logger = logger;
  }

  public List<String> getAllFilenames() {
    return getAllFilenamesByPath("");
  }

  public List<String> getAllFilenamesByPath(String path) {
    List<String> keys = new ArrayList<>();
    ListObjectsV2Response response;
    ListObjectsV2Request request =
        ListObjectsV2Request.builder().bucket(bucketName).prefix(path).build();
    do {
      response = s3Client.listObjectsV2(request);
      keys.addAll(response.contents().stream().map(S3Object::key).toList());
      String token = response.nextContinuationToken();
      request =
          ListObjectsV2Request.builder()
              .bucket(bucketName)
              .prefix(path)
              .continuationToken(token)
              .build();
    } while (Boolean.TRUE.equals(response.isTruncated()));

    return keys;
  }

  public Optional<byte[]> get(String objectKey) {
    try {
      return checkedGet(objectKey);
    } catch (RetryableObjectStoreException e) {
      logger.error("AWS S3 encountered an issue.", e);
    }
    return Optional.empty();
  }

  public Optional<String> getFileAsString(String filename) throws RetryableObjectStoreException {
    Optional<byte[]> s3Response = checkedGet(filename);
    return s3Response.map(bytes -> new String(bytes, StandardCharsets.UTF_8));
  }

  public Optional<byte[]> checkedGet(String objectKey) throws RetryableObjectStoreException {
    try {
      final var response = getStream(objectKey);
      return Optional.of(response.readAllBytes());
    } catch (NoSuchKeyException e) {
      logger.warn(String.format("Object key %s does not exist", objectKey));
      return Optional.empty();
    } catch (IOException | AwsServiceException | SdkClientException e) {
      throw new RetryableObjectStoreException(
          String.format("S3 has encountered a problem while trying to get object %s.", objectKey),
          e);
    }
  }

  public ResponseInputStream<GetObjectResponse> getStream(String objectKey) {
    GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(objectKey).build();
    return s3Client.getObject(request);
  }

  public void delete(String fileName) {
    s3Client.deleteObject(builder -> builder.bucket(bucketName).key(fileName));
  }

  public void save(String fileName, String fileContent) {
    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder().bucket(bucketName).key(fileName).build();
    s3Client.putObject(putObjectRequest, RequestBody.fromString(fileContent));
  }

  public void close() {
    s3Client.close();
  }
}
