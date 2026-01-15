package de.bund.digitalservice.ris.search.importer;

import de.bund.digitalservice.ris.search.service.AdministrativeDirectiveIndexSyncJob;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.Job;
import de.bund.digitalservice.ris.search.service.LiteratureIndexSyncJob;
import de.bund.digitalservice.ris.search.service.NormIndexSyncJob;
import de.bund.digitalservice.ris.search.service.SitemapsUpdateJob;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapJob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes import-related command-line tasks and dispatches them to the appropriate jobs.
 *
 * <p>Parses task targets from args and executes corresponding sync or update jobs.
 */
@Profile({"default", "staging", "uat", "test", "prototype"})
@Component
public class ImportTaskProcessor {

  public static final int OK_RETURN_CODE = 0;
  public static final int ERROR_RETURN_CODE = 1;

  private final NormIndexSyncJob normIndexSyncJob;
  private final CaseLawIndexSyncJob caseLawIndexSyncJob;
  private final LiteratureIndexSyncJob literatureIndexSyncJob;
  private final SitemapsUpdateJob sitemapsUpdateJob;
  private final EcliSitemapJob ecliSitemapJob;
  private final AdministrativeDirectiveIndexSyncJob administrativeDirectiveUpdateJob;

  private static final Logger logger = LogManager.getLogger(ImportTaskProcessor.class);

  private static final String TASK_ARGUMENT = "--task";

  /**
   * Create an ImportTaskProcessor wired with the available job implementations.
   *
   * @param normIndexSyncJob job that syncs norms index
   * @param caseLawIndexSyncJob job that syncs case law index
   * @param sitemapsUpdateJob job that updates sitemaps
   * @param literatureIndexSyncJob job that syncs literature index
   * @param ecliSitemapJob job that generates ECLI sitemaps
   * @param administrativeDirectiveUpdateJob job that syncs administrative directives
   */
  @Autowired
  public ImportTaskProcessor(
      NormIndexSyncJob normIndexSyncJob,
      CaseLawIndexSyncJob caseLawIndexSyncJob,
      SitemapsUpdateJob sitemapsUpdateJob,
      LiteratureIndexSyncJob literatureIndexSyncJob,
      EcliSitemapJob ecliSitemapJob,
      AdministrativeDirectiveIndexSyncJob administrativeDirectiveUpdateJob) {
    this.normIndexSyncJob = normIndexSyncJob;
    this.caseLawIndexSyncJob = caseLawIndexSyncJob;
    this.literatureIndexSyncJob = literatureIndexSyncJob;
    this.sitemapsUpdateJob = sitemapsUpdateJob;
    this.ecliSitemapJob = ecliSitemapJob;
    this.administrativeDirectiveUpdateJob = administrativeDirectiveUpdateJob;
  }

  public boolean shouldRun(String[] args) {
    return Arrays.asList(args).contains(TASK_ARGUMENT);
  }

  /**
   * Executes a series of tasks based on the provided arguments, ensuring each task completes
   * successfully before moving to the next. If any task returns an error code, the process halts,
   * and the error code is returned.
   *
   * @param args the array of input arguments specifying the tasks to run and their targets
   * @return the return code indicating the result of execution. Possible values include: - a
   *     success code if all tasks complete successfully - an error code if an exception is thrown
   *     or a task fails
   */
  public int run(String[] args) {
    try {
      for (String target : parseTargets(args)) {
        int returnValue = runTask(target);
        if (returnValue != OK_RETURN_CODE) {
          return returnValue;
        }
      }
      return OK_RETURN_CODE;
    } catch (IllegalArgumentException ex) {
      logger.error(ex.getMessage(), ex);
      return ERROR_RETURN_CODE;
    }
  }

  /**
   * Parses target arguments from the provided list of input arguments. This method identifies all
   * targets that follow instances of a specific task argument flag and returns them as a list. If a
   * target is missing after a task argument, an exception is thrown.
   *
   * @param args the array of input arguments that may contain task argument flags followed by
   *     target identifiers
   * @return a list of target identifiers extracted from the input arguments
   * @throws IllegalArgumentException if a task argument is not followed by a target
   */
  @NotNull
  public static List<String> parseTargets(String[] args) {
    List<String> targets = new ArrayList<>();
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals(TASK_ARGUMENT)) {
        try {
          String target = args[i + 1];
          targets.add(target);
        } catch (IndexOutOfBoundsException e) {
          throw new IllegalArgumentException(
              "Expected a target argument following %s".formatted(TASK_ARGUMENT));
        }
      }
    }
    return targets;
  }

  private int runTask(Job job) {
    return job.runJob().getValue();
  }

  /**
   * Executes a specific task based on the provided target identifier. The target corresponds to
   * predefined job types such as importing norms, case law, literature, administrative directives,
   * or updating sitemaps.
   *
   * @param target the identifier of the task to execute. Expected values include: "import_norms",
   *     "import_caselaw", "import_literature", "import_administrative_directive",
   *     "update_sitemaps", and "generate_ecli_sitemaps".
   * @return the return code of the executed job. Typically indicates the success or failure of a
   *     task, with success represented by a specific constant.
   * @throws IllegalArgumentException if the provided target is not recognized.
   */
  public int runTask(String target) {
    return switch (target) {
      case "import_norms" -> runTask(normIndexSyncJob);
      case "import_caselaw" -> runTask(caseLawIndexSyncJob);
      case "import_literature" -> runTask(literatureIndexSyncJob);
      case "import_administrative_directive" -> runTask(administrativeDirectiveUpdateJob);
      case "update_sitemaps" -> runTask(sitemapsUpdateJob);
      case "generate_ecli_sitemaps" -> runTask(ecliSitemapJob);
      default -> throw new IllegalArgumentException("Unexpected target '%s'".formatted(target));
    };
  }
}
