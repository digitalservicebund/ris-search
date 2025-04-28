package de.bund.digitalservice.ris.search.caselawhandover.shared;

import de.bund.digitalservice.ris.search.exception.RetryableObjectStoreException;
import java.io.IOException;
import java.io.InputStream;
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
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

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

  /**
   * Uploads an InputStream to S3 using multipart upload.
   *
   * @param inputStream The InputStream to upload.
   * @return The total size of the uploaded object in bytes.
   * @throws IOException If an error occurs during the upload.
   */
  public long putStream(String objectKey, InputStream inputStream) throws IOException {
    CreateMultipartUploadRequest createRequest =
        CreateMultipartUploadRequest.builder().bucket(bucketName).key(objectKey).build();

    String uploadId = s3Client.createMultipartUpload(createRequest).uploadId();
    logger.info("Started multipart upload with ID: {}", uploadId);

    List<String> partETags = new java.util.ArrayList<>();
    List<CompletedPart> completedParts = new ArrayList<>();
    int partSize =
        0x500000; // 5MiB is the minimum part size, except for the last part, which may be smaller.
    int partNumber = 1;
    long totalBytesRead = 0;

    try (inputStream) { // try-with-resources closes the input stream when exiting the block
      // Read the input stream in chunks and upload each part
      byte[] buffer = new byte[partSize];
      int bytesRead;
      while ((bytesRead = readChunk(inputStream, buffer, partSize)) > 0) {
        UploadPartRequest uploadRequest =
            UploadPartRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .uploadId(uploadId)
                .partNumber(partNumber)
                .build();

        InputStream partInputStream = new java.io.ByteArrayInputStream(buffer, 0, bytesRead);
        UploadPartResponse uploadResult =
            s3Client.uploadPart(
                uploadRequest, RequestBody.fromInputStream(partInputStream, bytesRead));

        partETags.add(uploadResult.eTag());
        completedParts.add(
            CompletedPart.builder().partNumber(partNumber).eTag(uploadResult.eTag()).build());

        partNumber++;
        totalBytesRead += bytesRead;
      }

      // Complete the multipart upload.
      CompleteMultipartUploadRequest completeRequest =
          CompleteMultipartUploadRequest.builder()
              .bucket(bucketName)
              .key(objectKey)
              .uploadId(uploadId)
              .multipartUpload(upload -> upload.parts(completedParts))
              .build();
      s3Client.completeMultipartUpload(completeRequest);
      logger.info("Completed multipart upload ({} parts) with ID: {}", partETags.size(), uploadId);

      return totalBytesRead;

    } catch (IOException e) {
      logger.warn("Error uploading to S3, aborting multipart upload");
      s3Client.abortMultipartUpload(
          AbortMultipartUploadRequest.builder()
              .bucket(bucketName)
              .key(objectKey)
              .uploadId(uploadId)
              .build());
      throw new IOException("Failed to upload to S3: " + e.getMessage(), e);
    }
  }

  /**
   * Keeps reading from a stream until a number of bytes has been read, or the stream is closed.
   *
   * @param inputStream The stream to read from.
   * @param buffer The buffer to read into.
   * @param maxByteCount The number of bytes to read.
   * @return The number of bytes read.
   */
  private int readChunk(InputStream inputStream, byte[] buffer, int maxByteCount)
      throws IOException {
    int offset = 0;
    int bytesRead;
    while (offset < maxByteCount) {
      final int byteCountRemaining = maxByteCount - offset;
      bytesRead = inputStream.read(buffer, offset, byteCountRemaining);
      if (bytesRead != -1) {
        offset += bytesRead;
      } else {
        break;
      }
    }
    assert offset <= maxByteCount;
    return offset;
  }
}
