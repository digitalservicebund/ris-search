package de.bund.digitalservice.ris.search.repository.objectstorage;

import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3ObjectStorage implements ObjectStorage {

  private final S3Client s3Client;
  private final String bucketName;

  private final Logger logger = LogManager.getLogger();

  public S3ObjectStorage(S3Client s3Client, String bucketName) {
    this.s3Client = s3Client;
    this.bucketName = bucketName;
  }

  @Override
  public List<String> getAllFilenames() {
    return getAllFilenamesByPath("");
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
  public Logger getLogger() {
    return logger;
  }

  @Override
  public FilterInputStream getStream(String objectKey) throws FileNotFoundException {
    GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(objectKey).build();
    try {
      return s3Client.getObject(request);
    } catch (NoSuchKeyException e) {
      throw new FileNotFoundException(e.getMessage());
    }
  }

  @Override
  public void delete(String fileName) {
    s3Client.deleteObject(builder -> builder.bucket(bucketName).key(fileName));
  }

  @Override
  public void save(String fileName, String fileContent) {
    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder().bucket(bucketName).key(fileName).build();
    s3Client.putObject(putObjectRequest, RequestBody.fromString(fileContent));
  }

  @Override
  public void close() {
    this.s3Client.close();
  }
}
