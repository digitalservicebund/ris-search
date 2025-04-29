package de.bund.digitalservice.ris.search.repository.objectstorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

class S3ObjectStorageClientTest {

  private static final String BUCKET_NAME = "test-bucket";
  private static final String OBJECT_KEY = "test-object";
  private static final String UPLOAD_ID = "test-upload-id";
  private static final long PART_SIZE = 0x500000L; // 5 MiB

  private static final AbortMultipartUploadRequest expectedAbortRequest =
      AbortMultipartUploadRequest.builder()
          .uploadId(UPLOAD_ID)
          .bucket(BUCKET_NAME)
          .key(OBJECT_KEY)
          .build();

  @Mock private S3Client s3Client;

  @InjectMocks private S3ObjectStorageClient s3Service;

  @Captor private ArgumentCaptor<CreateMultipartUploadRequest> createRequestCaptor;

  @Captor private ArgumentCaptor<UploadPartRequest> uploadPartRequestCaptor;

  @Captor private ArgumentCaptor<RequestBody> requestBodyCaptor;

  @Captor private ArgumentCaptor<CompleteMultipartUploadRequest> completeRequestCaptor;

  @Captor private ArgumentCaptor<AbortMultipartUploadRequest> abortRequestCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    s3Service = new S3ObjectStorageClient(s3Client, BUCKET_NAME);
  }

  @Test
  void putStream_singlePartUpload_success() throws IOException {
    byte[] data = "This is a small test string.".getBytes();
    InputStream inputStream = new ByteArrayInputStream(data);

    CreateMultipartUploadResponse createResponse =
        CreateMultipartUploadResponse.builder().uploadId(UPLOAD_ID).build();
    when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
        .thenReturn(createResponse);

    UploadPartResponse uploadResponse = UploadPartResponse.builder().eTag("etag-part-1").build();
    when(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class)))
        .thenReturn(uploadResponse);

    CompleteMultipartUploadResponse completeResponse =
        CompleteMultipartUploadResponse.builder().build();
    when(s3Client.completeMultipartUpload(any(CompleteMultipartUploadRequest.class)))
        .thenReturn(completeResponse);

    long actualSize = s3Service.putStream(OBJECT_KEY, inputStream);

    assertThat(actualSize).isEqualTo(data.length);

    verify(s3Client).createMultipartUpload(createRequestCaptor.capture());
    CreateMultipartUploadRequest capturedCreateRequest = createRequestCaptor.getValue();
    assertEquals(BUCKET_NAME, capturedCreateRequest.bucket());
    assertEquals(OBJECT_KEY, capturedCreateRequest.key());

    verify(s3Client).uploadPart(uploadPartRequestCaptor.capture(), requestBodyCaptor.capture());
    UploadPartRequest capturedUploadRequest = uploadPartRequestCaptor.getValue();
    assertThat(capturedUploadRequest)
        .isEqualTo(
            UploadPartRequest.builder()
                .bucket(BUCKET_NAME)
                .key(OBJECT_KEY)
                .uploadId(UPLOAD_ID)
                .partNumber(1)
                .build());

    assertThat(requestBodyCaptor.getValue().optionalContentLength())
        .get()
        .isEqualTo((long) data.length);

    verify(s3Client).completeMultipartUpload(completeRequestCaptor.capture());
    CompleteMultipartUploadRequest capturedCompleteRequest = completeRequestCaptor.getValue();

    assertThat(capturedCompleteRequest)
        .isEqualTo(
            CompleteMultipartUploadRequest.builder()
                .bucket(BUCKET_NAME)
                .key(OBJECT_KEY)
                .uploadId(UPLOAD_ID)
                .multipartUpload(
                    builder ->
                        builder.parts(
                            CompletedPart.builder().eTag("etag-part-1").partNumber(1).build()))
                .build());

    verify(s3Client, never()).abortMultipartUpload(any(AbortMultipartUploadRequest.class));
  }

  @Test
  void putStream_multiplePartsUpload_success() throws IOException {
    int dataSize = (int) PART_SIZE * 2 + 100;
    byte[] data = new byte[dataSize];
    java.util.Arrays.fill(data, (byte) 'A');
    InputStream inputStream = new ByteArrayInputStream(data);

    when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
        .thenReturn(CreateMultipartUploadResponse.builder().uploadId(UPLOAD_ID).build());

    when(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class)))
        .thenAnswer(
            s -> {
              UploadPartRequest part = s.getArgument(0);
              String etag = "etag-part-" + part.partNumber();
              return UploadPartResponse.builder().eTag(etag).build();
            });

    when(s3Client.completeMultipartUpload(any(CompleteMultipartUploadRequest.class)))
        .thenReturn(CompleteMultipartUploadResponse.builder().build());

    long actualSize = s3Service.putStream(OBJECT_KEY, inputStream);

    assertEquals(dataSize, actualSize);

    verify(s3Client, times(3))
        .uploadPart(uploadPartRequestCaptor.capture(), requestBodyCaptor.capture());
    List<UploadPartRequest> capturedUploadRequests = uploadPartRequestCaptor.getAllValues();

    assertThat(capturedUploadRequests).map(UploadPartRequest::partNumber).containsExactly(1, 2, 3);
    assertThat(requestBodyCaptor.getAllValues())
        .map(RequestBody::optionalContentLength)
        .map(Optional::get)
        .containsExactly(PART_SIZE, PART_SIZE, 100L);

    verify(s3Client).completeMultipartUpload(completeRequestCaptor.capture());
    CompleteMultipartUploadRequest capturedCompleteRequest = completeRequestCaptor.getValue();

    assertThat(capturedCompleteRequest.multipartUpload().parts())
        .isEqualTo(
            List.of(
                CompletedPart.builder().partNumber(1).eTag("etag-part-1").build(),
                CompletedPart.builder().partNumber(2).eTag("etag-part-2").build(),
                CompletedPart.builder().partNumber(3).eTag("etag-part-3").build()));

    verify(s3Client, never()).abortMultipartUpload(any(AbortMultipartUploadRequest.class));
  }

  @Test
  void putStream_uploadPartFails_abortsUploadAndThrowsIOException() {
    byte[] data = "Test data".getBytes();
    InputStream inputStream = new ByteArrayInputStream(data);

    CreateMultipartUploadResponse createResponse =
        CreateMultipartUploadResponse.builder().uploadId(UPLOAD_ID).build();
    when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
        .thenReturn(createResponse);

    when(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class)))
        .thenThrow(AwsServiceException.create("Upload part failed", null));

    SdkException thrown =
        assertThrows(SdkException.class, () -> s3Service.putStream(OBJECT_KEY, inputStream));

    assertEquals("Upload part failed", thrown.getMessage());

    verify(s3Client).abortMultipartUpload(abortRequestCaptor.capture());
    assertThat(abortRequestCaptor.getValue()).isEqualTo(expectedAbortRequest);

    verify(s3Client, never()).completeMultipartUpload(any(CompleteMultipartUploadRequest.class));
  }

  @Test
  void putStream_completeMultipartUploadFails_abortsUploadAndThrowsIOException() {
    byte[] data = "Test data".getBytes();
    InputStream inputStream = new ByteArrayInputStream(data);

    CreateMultipartUploadResponse createResponse =
        CreateMultipartUploadResponse.builder().uploadId(UPLOAD_ID).build();
    when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
        .thenReturn(createResponse);

    UploadPartResponse uploadResponse = UploadPartResponse.builder().eTag("etag-part-1").build();
    when(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class)))
        .thenReturn(uploadResponse);

    when(s3Client.completeMultipartUpload(any(CompleteMultipartUploadRequest.class)))
        .thenThrow(AwsServiceException.create("Complete upload failed", null));

    SdkException thrown =
        assertThrows(SdkException.class, () -> s3Service.putStream(OBJECT_KEY, inputStream));

    verify(s3Client)
        .completeMultipartUpload(
            any(CompleteMultipartUploadRequest.class)); // Still called before the exception

    assertEquals("Complete upload failed", thrown.getMessage());

    verify(s3Client).abortMultipartUpload(abortRequestCaptor.capture());
    assertThat(abortRequestCaptor.getValue()).isEqualTo(expectedAbortRequest);
  }

  @Test
  void readChunk_readsLessThanMaxBytes() throws IOException {
    byte[] data = "short".getBytes();
    InputStream inputStream = new ByteArrayInputStream(data);
    byte[] buffer = new byte[10];
    int maxByteCount = 10;

    int bytesRead = s3Service.readChunk(inputStream, buffer);

    assertThat(bytesRead).isEqualTo(data.length);
    assertThat(buffer).isEqualTo("short\0\0\0\0\0".getBytes());
  }

  @Test
  void readChunk_readsExactlyMaxBytes() throws IOException {
    byte[] data = "exact length".getBytes();
    InputStream inputStream = new ByteArrayInputStream(data);
    byte[] buffer = new byte[data.length];
    int maxByteCount = data.length;

    int bytesRead = s3Service.readChunk(inputStream, buffer);

    assertEquals(maxByteCount, bytesRead);
    assertArrayEquals(data, buffer);
  }

  @Test
  void readChunk_readsMultipleTimesToReachMaxBytes() throws IOException {
    byte[] data = "longer string to test chunking".getBytes();
    InputStream inputStream = new ByteArrayInputStream(data);
    byte[] buffer = new byte[5];
    int maxByteCount = data.length;
    byte[] readData = new byte[maxByteCount];
    int totalBytesRead = 0;
    int bytesRead;

    while (totalBytesRead < maxByteCount) {
      bytesRead = s3Service.readChunk(inputStream, buffer);
      if (bytesRead > 0) {
        System.arraycopy(buffer, 0, readData, totalBytesRead, bytesRead);
        totalBytesRead += bytesRead;
      } else {
        break;
      }
    }

    assertEquals(maxByteCount, totalBytesRead);
    assertArrayEquals(data, readData);
  }

  @Test
  void readChunk_inputStreamClosesMidRead() throws IOException {
    try (InputStream inputStream =
        new InputStream() {
          private int count = 0;
          private final byte[] data = "partial".getBytes();

          @Override
          public int read() throws IOException {
            if (count < data.length) {
              return data[count++] & 0xFF;
            }
            if (count == data.length) {
              count++;
              return data[data.length - 1] & 0xFF; // Simulate still readable for one more call
            }
            return -1; // End of stream
          }

          @Override
          public void close() throws IOException {
            // Simulate closing after a few reads
            if (count > 2) {
              super.close();
            }
          }
        }) {
      byte[] buffer = new byte[10];
      int maxByteCount = 10;

      int bytesRead = assertDoesNotThrow(() -> s3Service.readChunk(inputStream, buffer));
      assertTrue(bytesRead <= maxByteCount);
      assertTrue(bytesRead > 0);
    }
  }
}
