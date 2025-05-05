package de.bund.digitalservice.ris.search.unit.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.bund.digitalservice.ris.search.importer.ImportTaskProcessor;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.ImportService;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImportTaskProcessorTest {

  @Mock private ImportService importService;

  @Mock private IndexNormsService indexNormsService;

  @Mock private IndexCaselawService indexCaselawService;

  @Mock private NormsBucket normsBucket;

  @Mock private CaseLawBucket caseLawBucket;

  private ImportTaskProcessor processor;

  @BeforeEach
  void setUp() {
    processor =
        new ImportTaskProcessor(
            importService, indexNormsService, indexCaselawService, normsBucket, caseLawBucket);
  }

  @Test
  void shouldRun_whenArgsContainsImportFlag_returnsTrue() {
    // Given
    String[] args = {"--other-arg", "--import", "norms"};

    // When
    boolean result = processor.shouldRun(args);

    // Then
    assertTrue(result);
  }

  @Test
  void shouldRun_whenArgsDoesNotContainImportFlag_returnsFalse() {
    // Given
    String[] args = {"--other-arg", "norms"};

    // When
    boolean result = processor.shouldRun(args);

    // Then
    assertFalse(result);
  }

  @Test
  void parseTargets_withValidArgs_returnsTargets() {
    // Given
    String[] args = {"--other-arg", "--import", "norms", "--import", "caselaw"};

    // When
    List<String> targets = ImportTaskProcessor.parseTargets(args);

    // Then
    assertEquals(2, targets.size());
    assertEquals("norms", targets.get(0));
    assertEquals("caselaw", targets.get(1));
  }

  @Test
  void parseTargets_withNoTargetAfterImportFlag_throwsException() {
    // Given
    String[] args = {"--import"};

    // When/Then
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> ImportTaskProcessor.parseTargets(args));
    assertEquals("Expected a target argument following --import", exception.getMessage());
  }

  @Test
  void run_withValidTargets_returnsZero() {
    // Given
    String[] args = {"--import", "norms", "--import", "caselaw"};
    doNothing().when(importService).lockAndImportChangelogs(any(), anyString(), anyString(), any());

    // When
    int exitCode = processor.run(args);

    // Then
    assertEquals(0, exitCode);
    verify(importService)
        .lockAndImportChangelogs(
            indexNormsService,
            ImportService.NORM_LOCK_FILENAME,
            ImportService.NORM_LAST_SUCCESS_FILENAME,
            normsBucket);
    verify(importService)
        .lockAndImportChangelogs(
            indexCaselawService,
            ImportService.CASELAW_LOCK_FILENAME,
            ImportService.CASELAW_LAST_SUCCESS_FILENAME,
            caseLawBucket);
  }

  @Test
  void run_withInvalidTarget_returnsOne() {
    // Given
    String[] args = {"--import", "invalid-target"};

    // When
    int exitCode = processor.run(args);

    // Then
    assertEquals(1, exitCode);
    verify(importService, never()).lockAndImportChangelogs(any(), anyString(), anyString(), any());
  }

  @Test
  void runTask_withNormsTarget_callsImportServiceWithNormsParams() {
    // Given
    String target = "norms";

    // When
    processor.runTask(target);

    // Then
    verify(importService)
        .lockAndImportChangelogs(
            (indexNormsService),
            (ImportService.NORM_LOCK_FILENAME),
            (ImportService.NORM_LAST_SUCCESS_FILENAME),
            (normsBucket));
  }

  @Test
  void runTask_withCaselawTarget_callsImportServiceWithCaselawParams() {
    // Given
    String target = "caselaw";

    // When
    processor.runTask(target);

    // Then
    verify(importService)
        .lockAndImportChangelogs(
            (indexCaselawService),
            (ImportService.CASELAW_LOCK_FILENAME),
            (ImportService.CASELAW_LAST_SUCCESS_FILENAME),
            (caseLawBucket));
  }

  @Test
  void runTask_withInvalidTarget_throwsException() {
    // Given
    String target = "invalid-target";

    // When/Then
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> processor.runTask(target));
    assertEquals("Unexpected target 'invalid-target'", exception.getMessage());
  }
}
