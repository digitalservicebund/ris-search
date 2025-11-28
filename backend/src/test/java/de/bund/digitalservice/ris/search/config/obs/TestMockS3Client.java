package de.bund.digitalservice.ris.search.config.obs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
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

/**
 * TestMockS3Client is a mock implementation of the S3Client interface for testing purposes. It
 * simulates S3 operations using a local file storage directory.
 */
public class TestMockS3Client extends MockS3Client implements S3Client {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestMockS3Client.class);

  private Path localStorageDirectory;

  private Map<String, byte[]> fileMap;

  /**
   * Constructs a TestMockS3Client with the specified bucket name and local storage directory.
   *
   * @param bucketname The name of the S3 bucket.
   * @param relativeLocalStorageDirectory The relative path to the local storage directory.
   */
  public TestMockS3Client(
      String bucketname, @Value("${local.file-storage}") String relativeLocalStorageDirectory) {
    super(bucketname);

    try {
      localStorageDirectory = findProjectRoot().resolve(relativeLocalStorageDirectory);
      Files.createDirectories(localStorageDirectory.resolve(bucketname));
      loadDefaultFiles();
    } catch (IOException e) {
      LOGGER.error("Couldn't load files to map: " + e.getMessage(), e);
    }
  }

  @Override
  public String serviceName() {
    return null;
  }

  @Override
  public void close() {
    fileMap.clear();
    fileMap = null;
  }

  /**
   * Loads default files from the local storage directory into the file map.
   *
   * @throws IOException if an I/O error occurs reading the files
   */
  public void loadDefaultFiles() throws IOException {
    fileMap = new HashMap<>();
    File bucket = localStorageDirectory.resolve(bucketname).toFile();
    List<String> files = getFileNamesByPath(bucket.getAbsolutePath());
    for (String file : files) {
      fileMap.put(
          file.substring(bucket.getAbsolutePath().length() + 1),
          Files.readAllBytes(Paths.get(file)));
    }
  }

  public void putFile(String fileName, String fileContent) {
    fileMap.put(fileName, fileContent.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public ListObjectsV2Response listObjectsV2(ListObjectsV2Request request) {
    List<String> fileNames =
        new ArrayList<>(fileMap.keySet())
            .stream().filter(e -> e.startsWith(request.prefix())).toList();
    return buildResponse(request, fileNames);
  }

  @Override
  public ResponseBytes<GetObjectResponse> getObjectAsBytes(GetObjectRequest objectRequest) {
    String fileName = objectRequest.key();
    byte[] bytes = fileMap.get(fileName);
    if (bytes == null) {
      throw NoSuchKeyException.builder().build();
    }
    return ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), bytes);
  }

  @Override
  public DeleteObjectResponse deleteObject(DeleteObjectRequest deleteObjectRequest)
      throws AwsServiceException, SdkClientException {
    String fileName = deleteObjectRequest.key();
    fileMap.remove(fileName);
    return DeleteObjectResponse.builder().build();
  }

  @Override
  public ResponseInputStream<GetObjectResponse> getObject(GetObjectRequest objectRequest) {
    String fileName = objectRequest.key();
    byte[] bytes = fileMap.get(fileName);
    if (bytes == null) {
      throw NoSuchKeyException.builder().build();
    }
    InputStream input = new ByteArrayInputStream(bytes);
    return new ResponseInputStream<>(GetObjectResponse.builder().build(), input);
  }

  @Override
  public PutObjectResponse putObject(PutObjectRequest putObjectRequest, RequestBody requestBody) {
    String key = putObjectRequest.key();

    InputStream inputStream = requestBody.contentStreamProvider().newStream();
    try {
      byte[] bytes = IOUtils.toByteArray(inputStream);
      fileMap.put(key, bytes);
      return PutObjectResponse.builder().build();
    } catch (IOException e) {
      LOGGER.error("Couldn't put object to local storage: {}", key);
      return null;
    }
  }

  protected void logObjectRetrievalError(String message) {
    LOGGER.error("Couldn't get object from local storage: {}", message);
  }
}
