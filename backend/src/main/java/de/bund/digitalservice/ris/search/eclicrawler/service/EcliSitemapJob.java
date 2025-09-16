package de.bund.digitalservice.ris.search.eclicrawler.service;

import de.bund.digitalservice.ris.search.eclicrawler.mapper.EcliCrawlerDocumentMapper;
import de.bund.digitalservice.ris.search.eclicrawler.model.EcliCrawlerDocument;
import de.bund.digitalservice.ris.search.eclicrawler.repository.EcliCrawlerDocumentRepository;
import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.mapper.CaseLawLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.IndexSyncJob;
import de.bund.digitalservice.ris.search.service.Job;
import jakarta.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class EcliSitemapJob implements Job {

  EcliSitemapService sitemapService;

  PortalBucket portalBucket;

  CaseLawBucket caselawbucket;

  EcliCrawlerDocumentRepository repository;

  CaseLawIndexSyncJob indexJob;

  private final LocalDate today = LocalDate.now();

  private static final Logger logger = LogManager.getLogger(EcliSitemapJob.class);
  public static final String PATH_PREFIX = "eclicrawler/";
  public static final String LAST_PROCESSED_CHANGELOG = PATH_PREFIX + "last_processed_changelog";

  public EcliSitemapJob(
      EcliSitemapService service,
      PortalBucket portalBucket,
      CaseLawBucket caselawbucket,
      EcliCrawlerDocumentRepository repository,
      CaseLawIndexSyncJob indexJob) {
    this.sitemapService = service;
    this.portalBucket = portalBucket;
    this.caselawbucket = caselawbucket;
    this.repository = repository;
    this.indexJob = indexJob;
  }

  public ReturnCode runJob() {
    if (!sitemapService.getSitemapFilesPathsForDay(today).isEmpty()) {
      logger.warn("day partition for ecli sitemap run already created");
      return ReturnCode.SUCCESS;
    }

    boolean isInitialRun = repository.count() == 0;
    try {
      if (isInitialRun) {
        logger.info("initial run, publish all");
        Changelog changelog = getFullDiffChangelog();
        String lastProcessed = indexJob.getNewChangelogs(caselawbucket, "0").getLast();
        List<EcliCrawlerDocument> changes = getAllEcliCrawlerDocumentsFromChangelog(changelog);
        var sitemapIndexPaths = persistEcliDcoumentChanges(changes, today);

        sitemapService.writeRobotsTxt(sitemapIndexPaths);
        portalBucket.save(LAST_PROCESSED_CHANGELOG, lastProcessed);
      } else {
        var lastProcessed = getLastProcessedChangelog();
        List<String> changelogPaths = indexJob.getNewChangelogs(caselawbucket, lastProcessed);
        if (changelogPaths.isEmpty()) {
          logger.info("no new changelogs to parse");
          return ReturnCode.SUCCESS;
        }
        var changelogs = getNewChangelogs(changelogPaths);

        Changelog mergedChangelog = ChangelogParser.mergeChangelogs(changelogs);
        List<EcliCrawlerDocument> changes =
            getAllEcliCrawlerDocumentsFromChangelog(mergedChangelog);

        var sitemapIndexPaths = persistEcliDcoumentChanges(changes, today);
        updateRobotsTxt(sitemapIndexPaths);
        portalBucket.save(LAST_PROCESSED_CHANGELOG, lastProcessed);
      }
    } catch (FatalEcliSitemapJobException e) {
      logger.error(e.getMessage());
      return ReturnCode.ERROR;
    }

    return ReturnCode.SUCCESS;
  }

  private String getLastProcessedChangelog() {
    try {
      return portalBucket
          .getFileAsString(LAST_PROCESSED_CHANGELOG)
          .orElseThrow(() -> new FatalEcliSitemapJobException("unable to parse status file"));
    } catch (ObjectStoreServiceException e) {
      throw new FatalEcliSitemapJobException(e.getMessage());
    }
  }

  private void updateRobotsTxt(List<String> sitemapIndexPaths) {
    if (!sitemapIndexPaths.isEmpty()) {
      try {
        sitemapService.updateRobotsTxt(sitemapIndexPaths);
      } catch (FileNotFoundException | ObjectStoreServiceException ex) {
        throw new FatalEcliSitemapJobException(ex.getMessage());
      }
    }
  }

  private List<Changelog> getNewChangelogs(List<String> changelogPaths) {
    var changelogs =
        changelogPaths.stream()
            .map(
                path -> {
                  try {
                    return Optional.ofNullable(
                        this.indexJob.parseOneChangelog(caselawbucket, path));
                  } catch (ObjectStoreServiceException e) {
                    throw new FatalEcliSitemapJobException(e.getMessage());
                  }
                })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

    return ChangelogParser.getLatestReleveantChangelogs(changelogs);
  }

  private List<String> persistEcliDcoumentChanges(
      List<EcliCrawlerDocument> ecliDocuments, LocalDate cutoffDate) {

    logger.info("publish changes {}", ecliDocuments.toArray());
    try {
      List<Sitemap> urlSets = sitemapService.createSitemaps(ecliDocuments);
      List<String> indexPaths = sitemapService.writeSitemapFiles(PATH_PREFIX, urlSets, cutoffDate);
      if (!indexPaths.isEmpty()) {
        repository.saveAll(ecliDocuments);
      }
      return indexPaths;
    } catch (JAXBException ex) {
      throw new FatalEcliSitemapJobException(ex.getMessage());
    }
  }

  private Changelog getFullDiffChangelog() {
    Changelog changelog = new Changelog();

    List<String> allFiles =
        caselawbucket.getAllKeys().stream()
            .filter(s -> s.endsWith(".xml") && !s.contains(IndexSyncJob.CHANGELOGS_PREFIX))
            .toList();
    List<List<String>> filenamebatches = ListUtils.partition(allFiles, 10000);

    for (List<String> batch : filenamebatches) {
      var unitsFromBatch = getAllCaseLawDocumentationUnits(batch);

      unitsFromBatch.forEach(
          (path, unit) -> {
            if (!Objects.isNull(unit.ecli())) {
              changelog.getChanged().add(path);
            }
          });

      var docsToBeDeleted = repository.findAllByIsPublishedIsTrueAndFilenameNotIn(allFiles);
      for (EcliCrawlerDocument doc : docsToBeDeleted) {
        changelog.getDeleted().add(doc.filename());
      }
    }
    return changelog;
  }

  private Map<String, CaseLawDocumentationUnit> getAllCaseLawDocumentationUnits(
      List<String> filenames) {
    Map<String, CaseLawDocumentationUnit> caseLawDocumentationUnits = new HashMap<>();

    for (String filename : filenames) {
      try {

        Optional<String> contentOption = caselawbucket.getFileAsString(filename);
        contentOption.ifPresent(
            content ->
                caseLawDocumentationUnits.put(
                    filename, CaseLawLdmlToOpenSearchMapper.fromString(content)));
      } catch (OpenSearchMapperException ex) {
        logger.warn("unable to parse file {} to DocumentationUnit", filename);
      } catch (ObjectStoreServiceException ex) {
        throw new FatalEcliSitemapJobException(ex.getMessage());
      }
    }
    return caseLawDocumentationUnits;
  }

  private List<EcliCrawlerDocument> getAllEcliCrawlerDocumentsFromChangelog(
      Changelog mergedChangelog) {

    var caseLawDocumentationUnitsToChange =
        getAllCaseLawDocumentationUnits(mergedChangelog.getChanged().stream().toList());

    List<EcliCrawlerDocument> changes = new ArrayList<>();
    caseLawDocumentationUnitsToChange.forEach(
        (path, unit) ->
            changes.add(EcliCrawlerDocumentMapper.fromCaseLawDocumentationUnit(path, unit)));

    changes.addAll(
        repository.findAllByIsPublishedIsTrueAndFilenameIn(mergedChangelog.getDeleted()).stream()
            .map(
                ecliSitemap ->
                    new EcliCrawlerDocument(
                        ecliSitemap.document_number(),
                        ecliSitemap.filename(),
                        ecliSitemap.ecli(),
                        ecliSitemap.courtType(),
                        ecliSitemap.decisionDate(),
                        ecliSitemap.documentType(),
                        false))
            .toList());
    return changes;
  }
}
