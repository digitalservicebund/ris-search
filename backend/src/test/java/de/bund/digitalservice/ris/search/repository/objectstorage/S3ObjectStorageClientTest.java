package de.bund.digitalservice.ris.search.repository.objectstorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
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
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
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
  void listByPrefixWithLastModified_singlePage_returnsKeyInfos() {
    String prefix = "sitemaps/";
    ObjectKeyInfo objectKeyInfo1 =
        new ObjectKeyInfo("sitemaps/norms/1.xml", Instant.parse("2023-01-01T00:00:00Z"));
    ObjectKeyInfo objectKeyInfo2 =
        new ObjectKeyInfo("sitemaps/norms/2.xml", Instant.parse("2023-01-02T00:00:00Z"));
    S3Object obj1 =
        S3Object.builder()
            .key(objectKeyInfo1.key())
            .lastModified(objectKeyInfo1.lastModified())
            .build();
    S3Object obj2 =
        S3Object.builder()
            .key(objectKeyInfo2.key())
            .lastModified(objectKeyInfo2.lastModified())
            .build();

    ListObjectsV2Response page =
        ListObjectsV2Response.builder().isTruncated(false).contents(obj1, obj2).build();

    when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(page);

    assertThat(s3Service.listByPrefixWithLastModified(prefix))
        .hasSize(2)
        .containsAll(List.of(objectKeyInfo1, objectKeyInfo2));

    ArgumentCaptor<ListObjectsV2Request> captor =
        ArgumentCaptor.forClass(ListObjectsV2Request.class);
    verify(s3Client).listObjectsV2(captor.capture());
    assertThat(captor.getValue().bucket()).isEqualTo(BUCKET_NAME);
    assertThat(captor.getValue().prefix()).isEqualTo(prefix);
  }

  @Test
  void listByPrefixWithLastModified_handlesPagination_andAggregatesAllPages() {
    String prefix = "sitemaps/";
    ObjectKeyInfo objectKeyInfo1 =
        new ObjectKeyInfo("sitemaps/norms/1.xml", Instant.parse("2023-01-01T00:00:00Z"));
    ObjectKeyInfo objectKeyInfo2 =
        new ObjectKeyInfo("sitemaps/norms/2.xml", Instant.parse("2023-01-02T00:00:00Z"));
    ObjectKeyInfo objectKeyInfo3 =
        new ObjectKeyInfo("sitemaps/caselaw/3.xml", Instant.parse("2023-01-03T00:00:00Z"));
    S3Object p1o1 =
        S3Object.builder()
            .key(objectKeyInfo1.key())
            .lastModified(objectKeyInfo1.lastModified())
            .build();
    S3Object p1o2 =
        S3Object.builder()
            .key(objectKeyInfo2.key())
            .lastModified(objectKeyInfo2.lastModified())
            .build();
    ListObjectsV2Response page1 =
        ListObjectsV2Response.builder()
            .isTruncated(true)
            .nextContinuationToken("token-1")
            .contents(p1o1, p1o2)
            .build();

    S3Object p2o1 =
        S3Object.builder()
            .key(objectKeyInfo3.key())
            .lastModified(objectKeyInfo3.lastModified())
            .build();
    ListObjectsV2Response page2 =
        ListObjectsV2Response.builder().isTruncated(false).contents(p2o1).build();

    when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
        .thenReturn(page1)
        .thenReturn(page2);

    assertThat(s3Service.listByPrefixWithLastModified(prefix))
        .isEqualTo(List.of(objectKeyInfo1, objectKeyInfo2, objectKeyInfo3));

    ArgumentCaptor<ListObjectsV2Request> captor =
        ArgumentCaptor.forClass(ListObjectsV2Request.class);
    verify(s3Client, times(2)).listObjectsV2(captor.capture());
    List<ListObjectsV2Request> calls = captor.getAllValues();

    assertThat(calls.getFirst().bucket()).isEqualTo(BUCKET_NAME);
    assertThat(calls.getFirst().prefix()).isEqualTo(prefix);
    assertThat(calls.getFirst().continuationToken()).isNull();

    assertThat(calls.getLast().bucket()).isEqualTo(BUCKET_NAME);
    assertThat(calls.getLast().prefix()).isEqualTo(prefix);
    assertThat(calls.getLast().continuationToken()).isEqualTo("token-1");
  }

  @Test
  void listByPrefixWithLastModified_emptyPage_returnsEmptyList() {
    String prefix = "sitemaps/";
    ListObjectsV2Response empty =
        ListObjectsV2Response.builder().isTruncated(false).contents(List.of()).build();

    when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(empty);

    assertThat(s3Service.listByPrefixWithLastModified(prefix)).isEmpty();

    ArgumentCaptor<ListObjectsV2Request> captor =
        ArgumentCaptor.forClass(ListObjectsV2Request.class);
    verify(s3Client).listObjectsV2(captor.capture());
    assertThat(captor.getValue().prefix()).isEqualTo(prefix);
  }
}
