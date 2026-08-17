package de.bund.digitalservice.ris.search.unit.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.config.opensearch.Configurations;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.mapper.CaseLawLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.StorageObject;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.service.SimpleSearchQueryBuilder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@ExtendWith(MockitoExtension.class)
class CaseLawServiceTest {

  @Mock private CaseLawService caseLawService;
  @Mock private ElasticsearchOperations operationsMock;
  @Mock private CaseLawBucket caseLawBucketMock;
  @Mock private CaseLawLdmlToOpenSearchMapper marshaller;
  @Mock private CaseLawRepository caseLawRepositoryMock;

  @BeforeEach
  void setUp() {
    Configurations configurations = mock(Configurations.class);
    this.caseLawService =
        new CaseLawService(
            caseLawRepositoryMock,
            caseLawBucketMock,
            operationsMock,
            configurations,
            marshaller,
            new SimpleSearchQueryBuilder(null));
  }

  @Test
  @DisplayName("Should return existing file as bytes from folder if not in prototype envrionment")
  void shouldReturnFileAsBytesFromFolderWhenNotInPrototypeEnvironment()
      throws ObjectStoreServiceException {
    Optional<byte[]> expectedResult = Optional.of("file-content".getBytes());
    when(caseLawBucketMock.get("STRE201770751/STRE201770751.xml")).thenReturn(expectedResult);

    var actual = caseLawService.getFileByDocumentNumber("STRE201770751");
    Assertions.assertEquals(expectedResult, actual);
  }

  @Test
  @DisplayName("Should throw if file does not")
  void shouldThrowIfFileNotFoundInFolder() {
    when(caseLawBucketMock.get(any())).thenThrow(ObjectStoreServiceException.class);

    Assertions.assertThrows(
        ObjectStoreServiceException.class,
        () -> caseLawService.getFileByDocumentNumber("STRE201770751"),
        "Expected getFileByDocumentNumber to throw ObjectStoreServiceException, but it didn't");
  }

  @Test
  @DisplayName("Should return all filenames for document number if not in prototype environment")
  void shouldReturnAllFilenamesForADocumentNumberIfNotInPrototypeEnvironment() {
    List<String> expectedResult = List.of("FOO.xml", "FOO-image.png");
    when(caseLawBucketMock.getAllKeysByPrefix("FOO")).thenReturn(expectedResult);

    var actual = caseLawService.getAllFilenamesByDocumentNumber("FOO");
    Assertions.assertEquals(expectedResult, actual);
  }

  @Test
  void itIgnoresInvalidLdmlFiles() {
    String filename = "docNr/docNr.xml";
    byte[] content = "content".getBytes(StandardCharsets.UTF_8);

    when(caseLawBucketMock.getObjects(List.of(filename)))
        .thenReturn(List.of(new StorageObject("key", Optional.of(content))));
    when(marshaller.fromByteArray(content)).thenThrow(OpenSearchMapperException.class);
    Assertions.assertTrue(caseLawService.getFromBucket(List.of(filename)).isEmpty());
  }

  @Test
  void itReturnsEmptyForMissingContentFromFile() {
    String filename = "docNr/docNr.xml";

    when(caseLawBucketMock.getObjects(List.of(filename))).thenReturn(List.of());
    Assertions.assertTrue(caseLawService.getFromBucket(List.of(filename)).isEmpty());
  }

  @Test
  void itReturnsACaseLawDocumentationUnit() {
    String filename = "docNr/docNr.xml";
    byte[] content = "content".getBytes(StandardCharsets.UTF_8);
    CaseLawDocumentationUnit expectedUnit = CaseLawDocumentationUnit.builder().id("id").build();

    when(caseLawBucketMock.getObjects(List.of(filename)))
        .thenReturn(List.of(new StorageObject("key", Optional.of(content))));
    when(marshaller.fromByteArray(content)).thenReturn(expectedUnit);

    var result = caseLawService.getFromBucket(List.of(filename));
    assertThat(result).containsExactly(expectedUnit);
  }
}
