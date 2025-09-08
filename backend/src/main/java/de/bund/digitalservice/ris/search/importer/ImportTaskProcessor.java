package de.bund.digitalservice.ris.search.importer;

import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.Job;
import de.bund.digitalservice.ris.search.service.NormIndexSyncJob;
import de.bund.digitalservice.ris.search.service.SitemapsUpdateJob;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.DailyEcliSitemapJob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImportTaskProcessor {

  public static final int OK_RETURN_CODE = 0;
  public static final int ERROR_RETURN_CODE = 1;

  private final NormIndexSyncJob normIndexSyncJob;
  private final CaseLawIndexSyncJob caseLawIndexSyncJob;
  private final SitemapsUpdateJob sitemapsUpdateJob;
  private final DailyEcliSitemapJob ecliSitemapJob;

  private static final Logger logger = LogManager.getLogger(ImportTaskProcessor.class);

  private static final String TASK_ARGUMENT = "--task";

  @Autowired
  public ImportTaskProcessor(
      NormIndexSyncJob normIndexSyncJob,
      CaseLawIndexSyncJob caseLawIndexSyncJob,
      SitemapsUpdateJob sitemapsUpdateJob,
      DailyEcliSitemapJob ecliSitemapJob) {
    this.normIndexSyncJob = normIndexSyncJob;
    this.caseLawIndexSyncJob = caseLawIndexSyncJob;
    this.sitemapsUpdateJob = sitemapsUpdateJob;
    this.ecliSitemapJob = ecliSitemapJob;
  }

  public boolean shouldRun(String[] args) {
    return Arrays.asList(args).contains(TASK_ARGUMENT);
  }

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

  public int runTask(String target) {
    return switch (target) {
      case "import_norms" -> runTask(normIndexSyncJob);
      case "import_caselaw" -> runTask(caseLawIndexSyncJob);
      case "update_sitemaps" -> runTask(sitemapsUpdateJob);
      case "generate_ecli_sitemaps" -> runTask(ecliSitemapJob);
      default -> throw new IllegalArgumentException("Unexpected target '%s'".formatted(target));
    };
  }
}
