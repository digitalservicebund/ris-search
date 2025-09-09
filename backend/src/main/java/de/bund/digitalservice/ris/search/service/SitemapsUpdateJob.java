package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.sitemap.SitemapType;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import de.bund.digitalservice.ris.search.utils.eli.ManifestationEli;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SitemapsUpdateJob implements Job {

  private static final Logger logger = LogManager.getLogger(SitemapsUpdateJob.class);

  @Value("${sitemaps.urls-per-page:40000}")
  private Integer urlsPerPage;

  private final CaseLawBucket caseLawBucket;
  private final NormsBucket normsBucket;
  private final SitemapService sitemapService;

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

  public void createSitemapsForNorms() {
    List<ManifestationEli> manifestations =
        normsBucket.getAllKeysByPrefix("eli/").stream()
            .filter(path -> path.contains("/regelungstext-verkuendung-"))
            .map(ManifestationEli::fromString)
            .flatMap(Optional::stream)
            .toList();
    List<ExpressionEli> expressions =
        manifestations.stream().map(ManifestationEli::getExpressionEli).distinct().toList();
    List<List<ExpressionEli>> batches = ListUtils.partition(expressions, urlsPerPage);
    for (int i = 0; i < batches.size(); i++) {
      logger.info("Creating Sitemap for norms of batch {} of {}.", (i + 1), batches.size());
      sitemapService.createNormsBatchSitemap(i + 1, batches.get(i));
    }
    sitemapService.createIndexSitemap(batches.size(), SitemapType.NORMS);
  }

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
