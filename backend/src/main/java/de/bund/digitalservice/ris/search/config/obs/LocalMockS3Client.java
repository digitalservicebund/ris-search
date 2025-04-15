package de.bund.digitalservice.ris.search.config.obs;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public class LocalMockS3Client extends MockS3Client implements S3Client {
  private static final Logger LOGGER = LoggerFactory.getLogger(LocalMockS3Client.class);

  @Value("${local.file-storage}")
  private String relativeLocalStorageDirectory;

  private Path localStorageDirectory;

  @Override
  public String serviceName() {
    return null;
  }

  public LocalMockS3Client(String bucketName) {
    super(bucketName);
  }

  @Override
  public void close() {
    /* this method is empty because of mock */
  }

  @PostConstruct
  public void init() {
    try {
      this.localStorageDirectory = findProjectRoot().resolve(relativeLocalStorageDirectory);
      Files.createDirectories(localStorageDirectory.resolve(bucketname));
    } catch (IOException e) {
      LOGGER.error("Couldn't setup LocalMockS3Client: {}", e.getMessage(), e);
    }
  }

  @Override
  public ListObjectsV2Response listObjectsV2(ListObjectsV2Request request) {
    Path bucket = localStorageDirectory.resolve(request.bucket());
    Path bucketWithPrefix = bucket.resolve(request.prefix());
    List<String> fileNames =
        getFileNamesByPath(bucketWithPrefix.toString()).stream()
            .map(e -> e.substring(bucket.toString().length() + 1))
            .toList();
    if (fileNames.isEmpty()) {
      LOGGER.warn("No files found in {}", bucketWithPrefix);
    }
    return buildResponse(request, fileNames);
  }

  @Override
  public ResponseBytes<GetObjectResponse> getObjectAsBytes(GetObjectRequest objectRequest)
      throws NoSuchKeyException {
    String fileName = objectRequest.key();
    File file = localStorageDirectory.resolve(objectRequest.bucket() + "/" + fileName).toFile();
    try (FileInputStream fl = new FileInputStream(file)) {
      byte[] bytes = new byte[(int) file.length()];
      int readBytes = fl.read(bytes);
      if (readBytes != file.length()) {
        LOGGER.warn("different size between file length and read bytes");
      }
      return ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), bytes);
    } catch (IOException ex) {
      logObjectRetrievalError(file.getAbsolutePath());
      throw NoSuchKeyException.builder().build();
    }
  }

  @Override
  public DeleteObjectResponse deleteObject(DeleteObjectRequest deleteObjectRequest)
      throws AwsServiceException, SdkClientException {
    String fileName = deleteObjectRequest.key();
    Path file =
        localStorageDirectory
            .resolve(deleteObjectRequest.bucket() + "/" + fileName)
            .toFile()
            .toPath();
    try {
      Files.delete(file);
      return DeleteObjectResponse.builder().build();
    } catch (IOException e) {
      LOGGER.error("Couldn't delete object from local storage: {}", e.getMessage(), e);
      return DeleteObjectResponse.builder().build();
    }
  }

  @Override
  public ResponseInputStream<GetObjectResponse> getObject(GetObjectRequest objectRequest)
      throws NoSuchKeyException {
    String fileName = objectRequest.key();
    File file = localStorageDirectory.resolve(objectRequest.bucket() + "/" + fileName).toFile();
    try {
      FileInputStream fl = new FileInputStream(file);
      return new ResponseInputStream<>(GetObjectResponse.builder().build(), fl);
    } catch (FileNotFoundException e) {
      logObjectRetrievalError(file.getAbsolutePath());
      throw NoSuchKeyException.builder().build();
    }
  }

  @Override
  public PutObjectResponse putObject(PutObjectRequest putObjectRequest, RequestBody requestBody) {
    String bucket = putObjectRequest.bucket();
    String key = putObjectRequest.key();

    InputStream inputStream = requestBody.contentStreamProvider().newStream();
    File bucketDirectory = new File(localStorageDirectory.toAbsolutePath().toString(), bucket);
    File file = new File(bucketDirectory, key);

    try {
      FileUtils.copyInputStreamToFile(inputStream, file);
    } catch (IOException e) {
      LOGGER.error(e.toString());
    }

    return PutObjectResponse.builder().build();
  }

  protected void logObjectRetrievalError(String message) {
    LOGGER.info("Couldn't get object from local storage: {}", message);
  }
}
