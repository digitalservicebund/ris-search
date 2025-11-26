package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.sitemap.SitemapType;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * The SitemapsUpdateJob class is responsible for generating and managing sitemaps for legal norms
 * and case law. This job fetches the keys from the respective storage buckets, divides them into
 * manageable batches, and creates sitemaps for these batches. Additionally, it ensures that old
 * sitemap files are removed from storage to maintain consistency.
 *
 * <p>This class implements the `Job` interface, and its primary execution logic is encapsulated in
 * the `runJob` method.
 */
@Component
@RequiredArgsConstructor
public class SitemapsUpdateJob implements Job {

  private static final Logger logger = LogManager.getLogger(SitemapsUpdateJob.class);

  @Value("${sitemaps.urls-per-page:40000}")
  private Integer urlsPerPage;

  private final CaseLawBucket caseLawBucket;
  private final NormsBucket normsBucket;
  private final SitemapService sitemapService;

  /**
   * Executes the sitemap generation logic for legal norms and case law.
   *
   * @return ReturnCode indicating the success of the job execution.
   */
  public Job.ReturnCode runJob() {
    Instant jobStarted = Instant.now();
    logger.info("Starting sitemaps update job for norms");
    createSitemapsForNorms();
    logger.info("Starting sitemaps update job for caselaw");
    createSitemapsForCaselaw();
    logger.info("Clear old sitemap files");
    sitemapService.deleteSitemapFiles(jobStarted);
    return ReturnCode.SUCCESS;
  }

  /** Generates sitemaps for legal norms and organizes them into batches. */
  public void createSitemapsForNorms() {
    Set<ExpressionEli> expressions =
        normsBucket.getAllKeysByPrefix("eli/").stream()
            .map(EliFile::fromString)
            .flatMap(Optional::stream)
            .map(EliFile::getExpressionEli)
            .collect(Collectors.toSet());

    List<List<ExpressionEli>> batches =
        ListUtils.partition(expressions.stream().toList(), urlsPerPage);
    for (int i = 0; i < batches.size(); i++) {
      logger.info("Creating Sitemap for norms of batch {} of {}.", (i + 1), batches.size());
      sitemapService.createNormsBatchSitemap(i + 1, batches.get(i));
    }
    sitemapService.createIndexSitemap(batches.size(), SitemapType.NORMS);
  }

  /**
   * Generates sitemaps for case law data and organizes them into batches.
   *
   * <p>This method retrieves a list of keys representing case law entries from the `caseLawBucket`.
   * It filters the keys to include only those ending with ".xml" and excludes those containing the
   * `IndexSyncJob.CHANGELOGS_PREFIX`. The filtered keys are then partitioned into smaller batches,
   * each containing a specified number of URLs per page, defined by the `urlsPerPage` property.
   *
   * <p>For each batch, a distinct sitemap is created using the `sitemapService`. After all
   * batch-level sitemaps are generated, an index sitemap summarizing the entire set of case law
   * sitemaps is created.
   *
   * <p>Log messages are generated at each step to indicate progress in the sitemap creation
   * process.
   */
  public void createSitemapsForCaselaw() {
    List<String> paths =
        caseLawBucket.getAllKeys().stream()
            .filter(s -> s.endsWith(".xml") && !s.contains(IndexSyncJob.CHANGELOGS_PREFIX))
            .toList();
    List<List<String>> batches = ListUtils.partition(paths.stream().toList(), urlsPerPage);
    for (int i = 0; i < batches.size(); i++) {
      logger.info("Creating Sitemap for caselaw of batch {} of {}.", (i + 1), batches.size());
      sitemapService.createCaselawBatchSitemap(i + 1, batches.get(i));
    }
    sitemapService.createIndexSitemap(batches.size(), SitemapType.CASELAW);
  }
}
