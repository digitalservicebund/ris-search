package de.bund.digitalservice.ris.search.eclicrawler.service;

import de.bund.digitalservice.ris.search.eclicrawler.mapper.EcliCrawlerDocumentMapper;
import de.bund.digitalservice.ris.search.eclicrawler.model.EcliCrawlerDocument;
import de.bund.digitalservice.ris.search.eclicrawler.repository.EcliCrawlerDocumentRepository;
import de.bund.digitalservice.ris.search.eclicrawler.schema.ecli.Courts;
import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemapindex.Sitemapindex;
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
import org.springframework.beans.factory.annotation.Value;
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
  public final String baseUrl;

  public EcliSitemapJob(
      EcliSitemapService service,
      PortalBucket portalBucket,
      CaseLawBucket caselawbucket,
      EcliCrawlerDocumentRepository repository,
      CaseLawIndexSyncJob indexJob,
      @Value("${server.front-end-url}") String frontEndUrl) {
    this.sitemapService = service;
    this.portalBucket = portalBucket;
    this.caselawbucket = caselawbucket;
    this.repository = repository;
    this.indexJob = indexJob;
    this.baseUrl = frontEndUrl + "case-law/";
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
        sitemapService.writeRobotsTxt();
        String lastProcessed = indexJob.getNewChangelogs(caselawbucket, "0").getLast();
        Changelog changelog = getFullDiffChangelog();
        List<EcliCrawlerDocument> changes = getAllEcliCrawlerDocumentsFromChangelog(changelog);
        persistEcliDcoumentChanges(changes);
        portalBucket.save(LAST_PROCESSED_CHANGELOG, lastProcessed);
      } else {
        var lastProcessed = getLastProcessedChangelog();
        List<String> changelogPaths = indexJob.getNewChangelogs(caselawbucket, lastProcessed);
        if (changelogPaths.isEmpty()) {
          logger.info("no new changelogs to parse");
          return ReturnCode.SUCCESS;
        }
        var changelogs = getNewChangelogs(changelogPaths);

        var parseChangeAll =
            changelogs.stream()
                .map(
                    log -> {
                      if (log.isChangeAll()) {
                        logger.info("change all: doing full diff");
                        return getFullDiffChangelog();
                      }
                      return log;
                    })
                .toList();

        Changelog mergedChangelog = BulkChangelogParser.mergeChangelogs(parseChangeAll);
        List<EcliCrawlerDocument> changes =
            getAllEcliCrawlerDocumentsFromChangelog(mergedChangelog);

        persistEcliDcoumentChanges(changes);
        portalBucket.save(LAST_PROCESSED_CHANGELOG, changelogPaths.getLast());
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

    return BulkChangelogParser.getLatestRelevantChangelogs(changelogs);
  }

  private void persistEcliDcoumentChanges(List<EcliCrawlerDocument> ecliDocuments) {

    logger.info("publish changes {}", ecliDocuments.toArray());
    try {
      Map<String, Sitemap> sitemaps =
          sitemapService.writeDocumentsToSitemaps(PATH_PREFIX, today, ecliDocuments);
      Map<String, Sitemapindex> sitemapIndices =
          sitemapService.writeSitemapnamesToIndices(
              PATH_PREFIX, baseUrl, today, sitemaps.keySet().stream().toList());
      if (!sitemapIndices.isEmpty()) {
        repository.saveAll(ecliDocuments);
      }
      sitemapService.updateRobotsTxt(baseUrl, sitemapIndices.keySet().stream().toList());
    } catch (JAXBException | ObjectStoreServiceException ex) {
      throw new FatalEcliSitemapJobException(ex.getMessage());
    } catch (FileNotFoundException e) {
      throw new FatalEcliSitemapJobException("no robots.txt found");
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
      var unitsFromBatch = getCaseLawDocumentationUnits(batch);
      unitsFromBatch.keySet().forEach(key -> changelog.getChanged().add(key));

      var docsToBeDeleted = repository.findAllByIsPublishedIsTrueAndFilenameNotIn(allFiles);
      for (EcliCrawlerDocument doc : docsToBeDeleted) {
        changelog.getDeleted().add(doc.filename());
      }
    }
    return changelog;
  }

  private Map<String, CaseLawDocumentationUnit> getCaseLawDocumentationUnits(
      List<String> filenames) {
    Map<String, CaseLawDocumentationUnit> caseLawDocumentationUnits = new HashMap<>();

    for (String filename : filenames) {
      try {
        Optional<String> contentOption = caselawbucket.getFileAsString(filename);
        contentOption.ifPresent(
            content -> {
              CaseLawDocumentationUnit unit = CaseLawLdmlToOpenSearchMapper.fromString(content);
              if (!Objects.isNull(unit.ecli())
                  && Courts.supportedCourtNames.containsKey(unit.courtType())) {
                caseLawDocumentationUnits.put(
                    filename, CaseLawLdmlToOpenSearchMapper.fromString(content));
              }
            });
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
        getCaseLawDocumentationUnits(mergedChangelog.getChanged().stream().toList());

    List<EcliCrawlerDocument> changes = new ArrayList<>();
    caseLawDocumentationUnitsToChange.forEach(
        (path, unit) ->
            changes.add(
                EcliCrawlerDocumentMapper.fromCaseLawDocumentationUnit(baseUrl, path, unit)));

    changes.addAll(
        repository.findAllByFilenameIn(mergedChangelog.getDeleted()).stream()
            .map(
                ecliSitemap ->
                    new EcliCrawlerDocument(
                        ecliSitemap.document_number(),
                        ecliSitemap.filename(),
                        ecliSitemap.ecli(),
                        ecliSitemap.courtType(),
                        ecliSitemap.decisionDate(),
                        ecliSitemap.documentType(),
                        ecliSitemap.url(),
                        false))
            .toList());
    return changes;
  }
}
