package de.bund.digitalservice.ris.search.sitemap.eclicrawler.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.EcliCrawlerDocumentRepository;
import de.bund.digitalservice.ris.search.service.Job;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.mapper.EcliCrawlerDocumentMapper;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.model.EcliCrawlerDocumentOS;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemap.Sitemap;
import jakarta.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

@Service
public class DailyEcliSitemapJob implements Job {

  EcliSitemapService sitemapService;

  PortalBucket portalBucket;

  CaseLawBucket caselawbucket;

  CaseLawRepository caseLawRepo;

  EcliCrawlerDocumentRepository repository;

  public static final String STATUS_FILE = "ecli_sitemaps_status.json";

  private final LocalDate now = LocalDate.now();

  public DailyEcliSitemapJob(
      EcliSitemapService service,
      PortalBucket portalBucket,
      CaseLawBucket caselawbucket,
      CaseLawRepository caseLawRepo,
      EcliCrawlerDocumentRepository repository) {
    this.sitemapService = service;
    this.portalBucket = portalBucket;
    this.caselawbucket = caselawbucket;
    this.caseLawRepo = caseLawRepo;
    this.repository = repository;
  }

  public ReturnCode runJob() {
    if (!sitemapService.getSitemapFilesPathsForDay(now).isEmpty()) {
      return ReturnCode.ERROR;
    }
    try {
      if (sitemapService.isSitemapPathEmpty()) {
        createAll();
      }

      createFromDiff();
    } catch (JAXBException | FileNotFoundException | ObjectStoreServiceException e) {
      return ReturnCode.ERROR;
    }

    return ReturnCode.SUCCESS;
  }

  private List<String> writeSitemaps(
      List<EcliCrawlerDocumentOS> ecliDocuments, LocalDate cutoffDate) throws JAXBException {

    List<Sitemap> urlSets = sitemapService.createSitemaps(ecliDocuments);
    List<String> indexPaths = sitemapService.writeSitemapFiles(urlSets, cutoffDate);
    if (!indexPaths.isEmpty()) {
      repository.saveAll(ecliDocuments);
    }
    return indexPaths;
  }

  private void createAll() throws JAXBException {

    List<String> allDocnumbers =
        caselawbucket.getAllKeys().stream().map(s -> s.replace(".xml", "")).toList();
    List<List<String>> partitionedDocnumbers = ListUtils.partition(allDocnumbers, 10000);
    List<EcliCrawlerDocumentOS> allDocunits = new ArrayList<>();

    for (List<String> docNumbers : partitionedDocnumbers) {
      allDocunits.addAll(
          caseLawRepo.findAllValidFederalEcliDocumentsIn(docNumbers).stream()
              .map(EcliCrawlerDocumentMapper::fromCaseLawDocumentationUnit)
              .toList());
    }

    List<String> sitemapIndexPaths = writeSitemaps(allDocunits, now);
    if (!sitemapIndexPaths.isEmpty()) {
      sitemapService.writeRobotsTxt(sitemapIndexPaths);
    }
  }

  private void createFromDiff()
      throws JAXBException, FileNotFoundException, ObjectStoreServiceException {
    List<String> allDocnumbers =
        caselawbucket.getAllKeys().stream().map(s -> s.replace(".xml", "")).toList();

    List<List<String>> partitionedDocnumbers = ListUtils.partition(allDocnumbers, 10000);

    List<EcliCrawlerDocumentOS> toBeCreated = new ArrayList<>();
    for (List<String> docNumbers : partitionedDocnumbers) {
      Map<String, EcliCrawlerDocumentOS> existingDocs = new HashMap<>();

      var fromCaselawRepo =
          caseLawRepo.findAllValidFederalEcliDocumentsIn(docNumbers).stream()
              .map(EcliCrawlerDocumentMapper::fromCaseLawDocumentationUnit)
              .toList();
      repository
          .findAllByIsPublishedIsTrueAndIdIn(docNumbers)
          .forEach(doc -> existingDocs.put(doc.id(), doc));

      toBeCreated.addAll(
          fromCaselawRepo.stream()
              .filter(
                  doc -> {
                    if (!existingDocs.containsKey(doc.id())) {
                      return true;
                    }
                    EcliCrawlerDocumentOS existingDoc = existingDocs.get(doc.id());
                    return !existingDoc.metadataEquals(doc);
                  })
              .toList());
    }

    List<EcliCrawlerDocumentOS> toBeDeleted =
        repository.findAllByIsPublishedIsTrueAndIdNotIn(allDocnumbers).stream()
            .map(
                crawlerDoc ->
                    new EcliCrawlerDocumentOS(
                        crawlerDoc.id(),
                        crawlerDoc.ecli(),
                        crawlerDoc.courtType(),
                        crawlerDoc.decisionDate(),
                        crawlerDoc.documentType(),
                        false))
            .toList();

    List<EcliCrawlerDocumentOS> allChanges = new ArrayList<>();
    allChanges.addAll(toBeCreated);
    allChanges.addAll(toBeDeleted);

    List<String> sitemapIndexPaths = writeSitemaps(allChanges, now);
    if (!sitemapIndexPaths.isEmpty()) {
      sitemapService.updateRobotsTxt(sitemapIndexPaths);
    }
  }
}
