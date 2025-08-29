package de.bund.digitalservice.ris.search.unit.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.ImportTaskProcessor;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.Job;
import de.bund.digitalservice.ris.search.service.NormIndexSyncJob;
import de.bund.digitalservice.ris.search.service.SitemapsUpdateJob;
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
  @Mock private SitemapsUpdateJob sitemapsUpdateJob;

  private ImportTaskProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new ImportTaskProcessor(normIndexSyncJob, caseLawIndexSyncJob, sitemapsUpdateJob);
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
    String[] args = {
      "--other-arg",
      "--task",
      "import_norms",
      "--task",
      "import_caselaw",
      "--task",
      "update_sitemaps"
    };

    // When
    List<String> targets = ImportTaskProcessor.parseTargets(args);

    // Then
    assertEquals(3, targets.size());
    assertEquals("import_norms", targets.get(0));
    assertEquals("import_caselaw", targets.get(1));
    assertEquals("update_sitemaps", targets.get(2));
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
  void run_withValidTargets_returnsZero() {
    // Given
    String[] args = {
      "--task", "import_norms", "--task", "import_caselaw", "--task", "update_sitemaps"
    };
    when(normIndexSyncJob.runJob()).thenReturn(Job.ReturnCode.SUCCESS);
    when(caseLawIndexSyncJob.runJob()).thenReturn(Job.ReturnCode.SUCCESS);
    when(sitemapsUpdateJob.runJob()).thenReturn(Job.ReturnCode.SUCCESS);

    // When
    int exitCode = processor.run(args);

    // Then
    assertEquals(0, exitCode);
    verify(normIndexSyncJob).runJob();
    verify(caseLawIndexSyncJob).runJob();
    verify(sitemapsUpdateJob).runJob();
  }

  @Test
  void run_withInvalidTarget_returnsOne() {
    // Given
    String[] args = {"--task", "invalid-target"};

    // When
    int exitCode = processor.run(args);

    // Then
    assertEquals(1, exitCode);
    verify(normIndexSyncJob, never()).runJob();
    verify(caseLawIndexSyncJob, never()).runJob();
    verify(sitemapsUpdateJob, never()).runJob();
  }

  @Test
  void runTask_withNormsTarget_callsImportServiceWithNormsParams() {
    // Given
    String target = "import_norms";

    // When
    when(normIndexSyncJob.runJob()).thenReturn(Job.ReturnCode.SUCCESS);
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
    when(caseLawIndexSyncJob.runJob()).thenReturn(Job.ReturnCode.SUCCESS);
    processor.runTask(target);

    // Then
    verify(caseLawIndexSyncJob).runJob();
  }

  @Test
  void runTask_withSitemapsTarget_callsSitemapsUpdateJob() {
    // Given
    String target = "update_sitemaps";

    // When
    when(sitemapsUpdateJob.runJob()).thenReturn(Job.ReturnCode.SUCCESS);
    processor.runTask(target);

    // Then
    verify(sitemapsUpdateJob).runJob();
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
  void runTask_withNonzeroExitCode_returnsExitCode() {
    // Given
    String[] args = {
      "--task", "import_norms", "--task", "import_caselaw", "--task", "update_sitemaps"
    };

    // When
    when(normIndexSyncJob.runJob()).thenReturn(Job.ReturnCode.ERROR);

    int exitCode = processor.run(args);

    // Then
    assertEquals(1, exitCode);
    verify(normIndexSyncJob).runJob();
    verify(caseLawIndexSyncJob, never()).runJob();
    verify(sitemapsUpdateJob, never()).runJob();
  }
}
