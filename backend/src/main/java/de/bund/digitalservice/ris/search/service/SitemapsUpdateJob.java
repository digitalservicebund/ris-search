package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
  private final LiteratureBucket literatureBucket;
  private final NormsBucket normsBucket;
  private final SitemapService sitemapService;

  /**
   * Executes the sitemap generation logic.
   *
   * @return ReturnCode indicating the success of the job execution.
   */
  public Job.ReturnCode runJob() {
    Instant jobStarted = Instant.now();
    logger.info("Starting sitemaps update job for case law");
    createSitemaps(caseLawBucket, DocumentKind.CASE_LAW, "case-law");
    logger.info("Starting sitemaps update job for literature");
    createSitemaps(literatureBucket, DocumentKind.LITERATURE, "literature");
    logger.info("Starting sitemaps update job for norms");
    createSitemaps(normsBucket, DocumentKind.LEGISLATION, "norms");
    logger.info("Clear old sitemap files");
    sitemapService.deleteSitemapFiles(jobStarted);
    return ReturnCode.SUCCESS;
  }

  /**
   * Executes sitemap generation for one sitemap docKind
   *
   * @param currentBucket the bucket for the current sitemap docKind
   * @param docKind the current sitemap docKind
   * @param prefix the prefix for the current sitemap docKind
   */
  public void createSitemaps(ObjectStorage currentBucket, DocumentKind docKind, String prefix) {
    List<String> ids =
        currentBucket.getAllKeys().stream()
            .map(e -> DocumentKind.extractIdFromFileName(e, docKind))
            .flatMap(Optional::stream)
            .distinct()
            .toList();
    List<List<String>> batches = ListUtils.partition(ids, urlsPerPage);
    for (int i = 0; i < batches.size(); i++) {
      logger.info("Creating Sitemap for {} of batch {} of {}.", prefix, (i + 1), batches.size());
      sitemapService.createBatchSitemap(i + 1, batches.get(i), docKind, prefix);
    }
    sitemapService.createIndexSitemap(batches.size(), docKind);
  }
}
