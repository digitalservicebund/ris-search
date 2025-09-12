package de.bund.digitalservice.ris.search.eclicrawler.service;

import de.bund.digitalservice.ris.search.eclicrawler.mapper.EcliCrawlerDocumentMapper;
import de.bund.digitalservice.ris.search.eclicrawler.model.EcliCrawlerDocument;
import de.bund.digitalservice.ris.search.eclicrawler.repository.EcliCrawlerDocumentRepository;
import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.service.Job;
import jakarta.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

@Service
public class DailyEcliSitemapJob implements Job {

  EcliSitemapService sitemapService;

  PortalBucket portalBucket;

  CaseLawBucket caselawbucket;

  CaseLawRepository caseLawRepo;

  EcliCrawlerDocumentRepository repository;

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

    boolean isInitialRun = repository.count() == 0;
    try {
      var sitemapIndexPaths = writeSitemaps();

      if (isInitialRun) {
        if (!sitemapIndexPaths.isEmpty()) {
          sitemapService.writeRobotsTxt(sitemapIndexPaths);
        }
      } else {
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

  private Iterator<List<EcliCrawlerDocument>> getAllEcliDocumentsFromIndex(List<String> ids) {

    List<List<String>> partitionedDocnumberLists = ListUtils.partition(ids, 10000);

    return new Iterator<>() {
      int index = 0;

      @Override
      public boolean hasNext() {
        return index < partitionedDocnumberLists.size();
      }

      @Override
      public List<EcliCrawlerDocument> next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        List<EcliCrawlerDocument> docList =
            caseLawRepo
                .findAllValidFederalEcliDocumentsIn(partitionedDocnumberLists.get(index))
                .stream()
                .map(EcliCrawlerDocumentMapper::fromCaseLawDocumentationUnit)
                .toList();
        index++;
        return docList;
      }
    };
  }

  private List<String> writeSitemaps() throws JAXBException {
    List<String> allDocnumbers =
        caselawbucket.getAllKeys().stream().map(s -> s.replace(".xml", "")).toList();

    Iterator<List<EcliCrawlerDocument>> iterator = getAllEcliDocumentsFromIndex(allDocnumbers);

    List<EcliCrawlerDocument> toBeCreated = new ArrayList<>();
    while (iterator.hasNext()) {
      Map<String, EcliCrawlerDocument> existingDocs = new HashMap<>();

      var fromCaselawRepo = iterator.next();
      repository
          .findAllByIsPublishedIsTrueAndIdIn(
              fromCaselawRepo.stream().map(EcliCrawlerDocument::id).toList())
          .forEach(doc -> existingDocs.put(doc.id(), doc));

      toBeCreated.addAll(
          fromCaselawRepo.stream()
              .filter(
                  doc -> {
                    if (!existingDocs.containsKey(doc.id())) {
                      return true;
                    }
                    EcliCrawlerDocument existingDoc = existingDocs.get(doc.id());
                    return !existingDoc.metadataEquals(doc);
                  })
              .toList());
    }

    List<EcliCrawlerDocument> toBeDeleted =
        repository.findAllByIsPublishedIsTrueAndIdNotIn(allDocnumbers).stream()
            .map(
                crawlerDoc ->
                    new EcliCrawlerDocument(
                        crawlerDoc.id(),
                        crawlerDoc.ecli(),
                        crawlerDoc.courtType(),
                        crawlerDoc.decisionDate(),
                        crawlerDoc.documentType(),
                        crawlerDoc.indexedAt(),
                        false))
            .toList();

    List<EcliCrawlerDocument> allChanges = new ArrayList<>();
    allChanges.addAll(toBeCreated);
    allChanges.addAll(toBeDeleted);

    return persistEcliDcoumentChanges(allChanges, now);
  }
}
