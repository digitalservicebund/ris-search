package de.bund.digitalservice.ris.search.importer;

import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.ImportService;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImportTaskProcessor {

  private final ImportService importService;
  private final IndexNormsService indexNormsService;
  private final IndexCaselawService indexCaselawService;

  private static final Logger logger = LogManager.getLogger(ImportTaskProcessor.class);

  private static final String TASK_ARGUMENT = "--import";
  private final NormsBucket normsBucket;
  private final CaseLawBucket caseLawBucket;

  @Autowired
  public ImportTaskProcessor(
      ImportService importService,
      IndexNormsService indexNormsService,
      IndexCaselawService indexCaselawService,
      NormsBucket normsBucket,
      CaseLawBucket caseLawBucket) {
    this.importService = importService;
    this.indexNormsService = indexNormsService;
    this.indexCaselawService = indexCaselawService;
    this.normsBucket = normsBucket;
    this.caseLawBucket = caseLawBucket;
  }

  public boolean shouldRun(String[] args) {
    return Arrays.asList(args).contains(TASK_ARGUMENT);
  }

  public int run(String[] args) {
    try {
      for (String target : parseTargets(args)) {
        runTask(target);
      }
      return 0;
    } catch (IllegalArgumentException ex) {
      logger.error(ex.getMessage(), ex);
      return 1;
    }
  }

  @NotNull
  public static List<String> parseTargets(String[] args) {
    List<String> targets = new ArrayList<>();
    IntStream.range(0, args.length)
        .filter(i -> args[i].equals(TASK_ARGUMENT))
        .forEachOrdered(
            i -> {
              try {
                String target = args[i + 1];
                targets.add(target);
              } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException(
                    "Expected a target argument following %s".formatted(TASK_ARGUMENT));
              }
            });
    return targets;
  }

  public void runTask(String target) {
    switch (target) {
      case "norms":
        importService.lockAndImportChangelogs(
            indexNormsService,
            ImportService.NORM_LOCK_FILENAME,
            ImportService.NORM_LAST_SUCCESS_FILENAME,
            normsBucket);
        break;
      case "caselaw":
        importService.lockAndImportChangelogs(
            indexCaselawService,
            ImportService.CASELAW_LOCK_FILENAME,
            ImportService.CASELAW_LAST_SUCCESS_FILENAME,
            caseLawBucket);
        break;
      default:
        throw new IllegalArgumentException("Unexpected target '%s'".formatted(target));
    }
  }
}
