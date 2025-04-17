package de.bund.digitalservice.ris.search.repository.objectstorage;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3ObjectStorageClient implements ObjectStorageClient {
  private final S3Client s3Client;
  private final String bucketName;

  public S3ObjectStorageClient(S3Client s3Client, String bucketName) {
    this.s3Client = s3Client;
    this.bucketName = bucketName;
  }

  @Override
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

  @Override
  public ResponseInputStream<GetObjectResponse> getStream(String objectKey)
      throws FileNotFoundException {
    GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(objectKey).build();
    try {
      return s3Client.getObject(request);
    } catch (NoSuchKeyException e) {
      throw new FileNotFoundException(e.getMessage());
    }
  }

  @Override
  public void save(String fileName, String fileContent) {
    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder().bucket(bucketName).key(fileName).build();
    s3Client.putObject(putObjectRequest, RequestBody.fromString(fileContent));
  }

  @Override
  public void close() {
    s3Client.close();
  }

  @Override
  public void delete(String fileName) {
    s3Client.deleteObject(builder -> builder.bucket(bucketName).key(fileName));
  }
}
