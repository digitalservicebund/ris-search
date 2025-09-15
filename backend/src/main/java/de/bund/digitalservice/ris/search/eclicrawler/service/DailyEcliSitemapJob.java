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
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.Job;
import jakarta.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

@Service
public class DailyEcliSitemapJob implements Job {

  EcliSitemapService sitemapService;

  PortalBucket portalBucket;

  CaseLawBucket caselawbucket;

  CaseLawRepository caseLawRepo;

  EcliCrawlerDocumentRepository repository;

  CaseLawIndexSyncJob indexJob;

  private final LocalDate today = LocalDate.now();

  private final String now = Instant.now().toString();

  public DailyEcliSitemapJob(
      EcliSitemapService service,
      PortalBucket portalBucket,
      CaseLawBucket caselawbucket,
      CaseLawRepository caseLawRepo,
      EcliCrawlerDocumentRepository repository,
      CaseLawIndexSyncJob indexJob) {
    this.sitemapService = service;
    this.portalBucket = portalBucket;
    this.caselawbucket = caselawbucket;
    this.caseLawRepo = caseLawRepo;
    this.repository = repository;
    this.indexJob = indexJob;
  }

  public ReturnCode runJob() {
    if (!sitemapService.getSitemapFilesPathsForDay(today).isEmpty()) {
      return ReturnCode.ERROR;
    }

    boolean isInitialRun = repository.count() == 0;
    try {

      if (isInitialRun) {
        List<EcliCrawlerDocument> changes = getAllChanges();
        var sitemapIndexPaths = persistEcliDcoumentChanges(changes, today);

        if (!sitemapIndexPaths.isEmpty()) {
          sitemapService.writeRobotsTxt(sitemapIndexPaths);
        }
      } else {
        var lastProcessed = repository.findTopByOrderByUpdatedAtDesc().updatedAt();
        List<EcliCrawlerDocument> changes =
            getAllChangesFromChangelogs(indexJob.getNewChangelogs(caselawbucket, lastProcessed));
        var sitemapIndexPaths = persistEcliDcoumentChanges(changes, today);
        if (!sitemapIndexPaths.isEmpty()) {
          sitemapService.updateRobotsTxt(sitemapIndexPaths);
        }
      }
    } catch (JAXBException | FileNotFoundException | ObjectStoreServiceException e) {
      return ReturnCode.ERROR;
    }

    return ReturnCode.SUCCESS;
  }

  private List<String> persistEcliDcoumentChanges(
      List<EcliCrawlerDocument> ecliDocuments, LocalDate cutoffDate) throws JAXBException {

    List<Sitemap> urlSets = sitemapService.createSitemaps(ecliDocuments);
    List<String> indexPaths = sitemapService.writeSitemapFiles(urlSets, cutoffDate);
    if (!indexPaths.isEmpty()) {
      repository.saveAll(ecliDocuments);
    }
    return indexPaths;
  }

  private List<EcliCrawlerDocument> getAllChanges() throws ObjectStoreServiceException {
    List<String> allFiles = caselawbucket.getAllKeys();
    List<List<String>> filenamebatches = ListUtils.partition(allFiles, 10000);

    List<EcliCrawlerDocument> changes = new ArrayList<>();

    Map<String, EcliCrawlerDocument> existingDocs = new HashMap<>();
    for (List<String> batch : filenamebatches) {
      List<String> filenames = batch.stream().map(s -> s.replace(".xml", "")).toList();

      repository
          .findAllByIsPublishedIsTrueAndIdIn(filenames)
          .forEach(doc -> existingDocs.put(doc.id(), doc));

      var unitsFromBatch =
          getAllCaseLawDocumentationUnits(batch).stream()
              .filter(unit -> !Objects.isNull(unit.ecli()));

      changes.addAll(
          unitsFromBatch
              .filter(
                  doc -> {
                    if (!existingDocs.containsKey(doc.id())) {
                      return true;
                    }

                    EcliCrawlerDocument existingDoc = existingDocs.get(doc.id());
                    return existingDoc.indexedAt().compareTo(doc.indexedAt()) < 0;
                  })
              .map(unit -> EcliCrawlerDocumentMapper.fromCaseLawDocumentationUnit(unit, now))
              .toList());
    }

    changes.addAll(
        repository
            .findAllByIsPublishedIsTrueAndIdNotIn(
                allFiles.stream().map(s -> s.replace(".xml", "")).toList())
            .stream()
            .map(
                crawlerDoc ->
                    new EcliCrawlerDocument(
                        crawlerDoc.id(),
                        crawlerDoc.ecli(),
                        crawlerDoc.courtType(),
                        crawlerDoc.decisionDate(),
                        crawlerDoc.documentType(),
                        crawlerDoc.indexedAt(),
                        false,
                        now))
            .toList());
    return changes;
  }

  private List<CaseLawDocumentationUnit> getAllCaseLawDocumentationUnits(List<String> filenames)
      throws ObjectStoreServiceException {
    List<CaseLawDocumentationUnit> caseLawDocumentationUnits = new ArrayList<>();
    for (String filename : filenames) {
      Optional<String> contentOption = caselawbucket.getFileAsString(filename);
      try {
        contentOption.ifPresent(
            content ->
                caseLawDocumentationUnits.add(CaseLawLdmlToOpenSearchMapper.fromString(content)));

      } catch (OpenSearchMapperException ex) {
        System.out.println("skip");
      }
    }
    return caseLawDocumentationUnits;
  }

  private List<EcliCrawlerDocument> getAllChangesFromChangelogs(List<String> changelogPaths)
      throws ObjectStoreServiceException {

    List<Changelog> changelogs = new ArrayList<>();
    for (String changelogPath : changelogPaths) {
      Optional<Changelog> logOptional =
          Optional.ofNullable(this.indexJob.parseOneChangelog(caselawbucket, changelogPath));
      logOptional.ifPresent(changelogs::add);
    }
    Changelog mergedChangelog = ChangelogParser.mergeChangelogs(changelogs);

    List<CaseLawDocumentationUnit> caseLawDocumentationUnitsToChange =
        getAllCaseLawDocumentationUnits(mergedChangelog.getChanged().stream().toList());

    List<EcliCrawlerDocument> changes = new ArrayList<>();
    caseLawDocumentationUnitsToChange.forEach(
        unit -> {
          changes.add(EcliCrawlerDocumentMapper.fromCaseLawDocumentationUnit(unit, now));
        });

    List<String> deleteIdentifiers =
        mergedChangelog.getDeleted().stream().map(i -> i.replace(".xml", "")).toList();
    changes.addAll(
        repository.findAllByIsPublishedIsTrueAndIdIn(deleteIdentifiers).stream()
            .map(
                ecliSitemap ->
                    new EcliCrawlerDocument(
                        ecliSitemap.id(),
                        ecliSitemap.ecli(),
                        ecliSitemap.courtType(),
                        ecliSitemap.decisionDate(),
                        ecliSitemap.documentType(),
                        ecliSitemap.indexedAt(),
                        false,
                        now))
            .toList());
    return changes;
  }
}
