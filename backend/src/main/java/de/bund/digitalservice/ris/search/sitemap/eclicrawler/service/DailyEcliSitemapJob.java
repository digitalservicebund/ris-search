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
      } else {
        createFromDiff();
      }
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

    List<String> allDocNumbers =
        caselawbucket.getAllKeys().stream().map(s -> s.replace(".xml", "")).toList();

    List<EcliCrawlerDocumentOS> allDocunits = new ArrayList<>();
    var iterator = getAllEcliDoclumentsIn(allDocNumbers);
    while (iterator.hasNext()) {
      allDocunits.addAll(iterator.next());
    }
    List<String> sitemapIndexPaths = writeSitemaps(allDocunits, now);
    if (!sitemapIndexPaths.isEmpty()) {
      sitemapService.writeRobotsTxt(sitemapIndexPaths);
    }
  }

  private Iterator<List<EcliCrawlerDocumentOS>> getAllEcliDoclumentsIn(List<String> ids) {

    List<List<String>> partitionedDocnumberLists = ListUtils.partition(ids, 10000);

    return new Iterator<>() {
      int index = 0;

      @Override
      public boolean hasNext() {
        return index < partitionedDocnumberLists.size();
      }

      @Override
      public List<EcliCrawlerDocumentOS> next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        List<EcliCrawlerDocumentOS> docList =
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

  private void createFromDiff()
      throws JAXBException, FileNotFoundException, ObjectStoreServiceException {
    List<String> allDocnumbers =
        caselawbucket.getAllKeys().stream().map(s -> s.replace(".xml", "")).toList();

    var iterator = getAllEcliDoclumentsIn(allDocnumbers);

    List<EcliCrawlerDocumentOS> toBeCreated = new ArrayList<>();
    while (iterator.hasNext()) {
      Map<String, EcliCrawlerDocumentOS> existingDocs = new HashMap<>();

      var fromCaselawRepo = iterator.next();
      repository
          .findAllByIsPublishedIsTrueAndIdIn(
              fromCaselawRepo.stream().map(EcliCrawlerDocumentOS::id).toList())
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
                        crawlerDoc.indexedAt(),
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
