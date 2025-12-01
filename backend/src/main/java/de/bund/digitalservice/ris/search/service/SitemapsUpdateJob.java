package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.sitemap.SitemapType;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
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
    createSitemaps(normsBucket, SitemapType.NORMS, "norms");
    logger.info("Starting sitemaps update job for case law");
    createSitemaps(caseLawBucket, SitemapType.CASELAW, "case-law");
    logger.info("Clear old sitemap files");
    sitemapService.deleteSitemapFiles(jobStarted);
    return ReturnCode.SUCCESS;
  }

  /**
   * Executes sitemap generation for one sitemap type
   *
   * @param currentBucket the bucket for the current sitemap type
   * @param type the current sitemap type
   * @param prefix the prefix for the current sitemap type
   */
  public void createSitemaps(ObjectStorage currentBucket, SitemapType type, String prefix) {
    List<String> ids = extractIds(currentBucket.getAllKeys(), type);
    List<List<String>> batches = ListUtils.partition(ids, urlsPerPage);
    for (int i = 0; i < batches.size(); i++) {
      logger.info("Creating Sitemap for {} of batch {} of {}.", prefix, (i + 1), batches.size());
      sitemapService.createBatchSitemap(i + 1, batches.get(i), type, prefix);
    }
    sitemapService.createIndexSitemap(batches.size(), type);
  }

  private List<String> extractIds(List<String> paths, SitemapType type) {
    if (type == SitemapType.NORMS) {
      return paths.stream()
          .map(EliFile::fromString)
          .flatMap(Optional::stream)
          .map(EliFile::getExpressionEli)
          .map(ExpressionEli::toString)
          .distinct()
          .toList();
    } else {
      return paths.stream()
          .map(path -> path.substring(path.lastIndexOf("/") + 1, path.length() - 4))
          .toList();
    }
  }
}
