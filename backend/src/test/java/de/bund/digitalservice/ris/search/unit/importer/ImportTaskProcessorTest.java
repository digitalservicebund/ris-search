package de.bund.digitalservice.ris.search.unit.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.ImportTaskProcessor;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.NormIndexSyncJob;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImportTaskProcessorTest {

  @Mock private NormIndexSyncJob normIndexSyncJob;
  @Mock private CaseLawIndexSyncJob caseLawIndexSyncJob;

  private ImportTaskProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new ImportTaskProcessor(normIndexSyncJob, caseLawIndexSyncJob);
  }

  @Test
  void shouldRun_whenArgsContainsImportFlag_returnsTrue() {
    // Given
    String[] args = {"--other-arg", "--task", "import_norms"};

    // When
    boolean result = processor.shouldRun(args);

    // Then
    assertTrue(result);
  }

  @Test
  void shouldRun_whenArgsDoesNotContainImportFlag_returnsFalse() {
    // Given
    String[] args = {"--other-arg", "import_norms"};

    // When
    boolean result = processor.shouldRun(args);

    // Then
    assertFalse(result);
  }

  @Test
  void parseTargets_withValidArgs_returnsTargets() {
    // Given
    String[] args = {"--other-arg", "--task", "import_norms", "--task", "import_caselaw"};

    // When
    List<String> targets = ImportTaskProcessor.parseTargets(args);

    // Then
    assertEquals(2, targets.size());
    assertEquals("import_norms", targets.get(0));
    assertEquals("import_caselaw", targets.get(1));
  }

  @Test
  void parseTargets_withNoTargetAfterImportFlag_throwsException() {
    // Given
    String[] args = {"--task"};

    // When/Then
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> ImportTaskProcessor.parseTargets(args));
    assertEquals("Expected a target argument following --task", exception.getMessage());
  }

  @Test
  void run_withValidTargets_returnsZero() throws ObjectStoreServiceException {
    // Given
    String[] args = {"--task", "import_norms", "--task", "import_caselaw"};
    doNothing().when(normIndexSyncJob).runJob();
    doNothing().when(caseLawIndexSyncJob).runJob();

    // When
    int exitCode = processor.run(args);

    // Then
    assertEquals(0, exitCode);
    verify(normIndexSyncJob).runJob();
    verify(caseLawIndexSyncJob).runJob();
  }

  @Test
  void run_withInvalidTarget_returnsOne() throws ObjectStoreServiceException {
    // Given
    String[] args = {"--task", "invalid-target"};

    // When
    int exitCode = processor.run(args);

    // Then
    assertEquals(1, exitCode);
    verify(normIndexSyncJob, never()).runJob();
    verify(caseLawIndexSyncJob, never()).runJob();
  }

  @Test
  void runTask_withNormsTarget_callsImportServiceWithNormsParams()
      throws ObjectStoreServiceException {
    // Given
    String target = "import_norms";

    // When
    processor.runTask(target);

    // Then
    verify(normIndexSyncJob).runJob();
  }

  @Test
  void runTask_withCaselawTarget_callsImportServiceWithCaselawParams()
      throws ObjectStoreServiceException {
    // Given
    String target = "import_caselaw";

    // When
    processor.runTask(target);

    // Then
    verify(caseLawIndexSyncJob).runJob();
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

  @Test
  void runTask_withNonzeroExitCode_returnsExitCode() throws ObjectStoreServiceException {
    // Given
    String[] args = {"--task", "import_norms", "--task", "import_caselaw"};
    doThrow(new ObjectStoreServiceException("mock")).when(normIndexSyncJob).runJob();

    // When
    int exitCode = processor.run(args);

    // Then
    assertEquals(1, exitCode);
    verify(normIndexSyncJob).runJob();
    verify(caseLawIndexSyncJob, never()).runJob();
  }
}
