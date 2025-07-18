package de.bund.digitalservice.ris.search.sitemap.eclicrawler.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexSyncJob;
import de.bund.digitalservice.ris.search.service.IndexingState;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.mapper.EcliCrawlerDocumentMapper;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.model.EcliCrawlerDocument;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository.EcliCrawlerDocumentRepository;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemap.Sitemap;
import jakarta.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;

@Service
public class DailySitemapJob {

  SitemapService sitemapService;

  IndexSyncJob indexJob;

  PortalBucket portalBucket;

  CaseLawBucket caselawbucket;

  IndexStatusService indexStatusService;

  CaseLawService caseLawService;

  EcliCrawlerDocumentRepository repository;

  final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static final String STATUS_FILE = "ecli_sitemaps_status.json";

  private final LocalDate now = LocalDate.now();

  public DailySitemapJob(
      SitemapService service,
      CaseLawIndexSyncJob indexJob,
      PortalBucket portalBucket,
      CaseLawBucket caselawbucket,
      IndexStatusService indexStatusService,
      CaseLawService caseLawService,
      EcliCrawlerDocumentRepository repository) {
    this.sitemapService = service;
    this.indexJob = indexJob;
    this.portalBucket = portalBucket;
    this.caselawbucket = caselawbucket;
    this.indexStatusService = indexStatusService;
    this.caseLawService = caseLawService;
    this.repository = repository;
  }

  public void run() throws ObjectStoreServiceException, FatalDailySitemapJobException {

    if (!sitemapService.getSitemapFilesPathsForDay(now).isEmpty()) {
      throw new FatalDailySitemapJobException(
          String.format("Sitemaps for day %s already exists", now.format(formatter)));
    }

    IndexingState state = indexStatusService.loadStatus(STATUS_FILE);
    String lastProcessedEcliChangelogFile = state.lastProcessedChangelogFile();

    if (Objects.isNull(lastProcessedEcliChangelogFile)) {
      createAll();
    } else {
      String lastSuccesfulIndexingJob =
          indexStatusService
              .loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME)
              .lastProcessedChangelogFile();

      boolean indexingIsFinished =
          !Objects.isNull(lastSuccesfulIndexingJob)
              && lastSuccesfulIndexingJob.compareTo(lastProcessedEcliChangelogFile) > 0;
      if (indexingIsFinished) {
        List<String> newChangelogPaths =
            indexJob.getNewChangelogs(caselawbucket, lastProcessedEcliChangelogFile);
        createFromChangelogs(newChangelogPaths);
      }
    }
  }

  private List<String> writeSitemaps(List<EcliCrawlerDocument> ecliDocuments, LocalDate cutoffDate)
      throws JAXBException {

    List<Sitemap> urlSets = sitemapService.createSitemaps(ecliDocuments);
    List<String> indexPaths = sitemapService.writeSitemapFiles(urlSets, cutoffDate);
    if (!indexPaths.isEmpty()) {
      repository.saveAll(ecliDocuments);
    }
    return indexPaths;
  }

  private void createAll() throws FatalDailySitemapJobException {
    Stream<List<EcliCrawlerDocument>> docUnits =
        getAllCreatedDocumentsFromStream(caseLawService.getAllEcliDocuments());

    docUnits.forEach(
        documents -> {
          try {
            List<String> sitemapPaths = writeSitemaps(documents, now);
            if (!sitemapPaths.isEmpty()) {
              sitemapService.writeRobotsTxt(sitemapPaths);
            }
            indexStatusService.saveStatus(
                STATUS_FILE,
                new IndexingState()
                    .withLastProcessedChangelogFile(
                        IndexSyncJob.CHANGELOGS_PREFIX + Instant.now().toString()));
          } catch (JAXBException ex) {
            throw new FatalDailySitemapJobException(ex.getMessage());
          }
        });
  }

  /**
   * process the caselawDocumentationUnit stream in batches of 10000 to avoid outOfMemoryErrors in
   * case we receive too many items
   *
   * @param stream Original Stream of CaseLawDocumentationUnits
   * @return Returns a stream of CaseLawDocumentationUnit batches
   */
  private Stream<List<EcliCrawlerDocument>> getAllCreatedDocumentsFromStream(
      Stream<CaseLawDocumentationUnit> stream) {
    Iterator<CaseLawDocumentationUnit> iterator = stream.iterator();
    Iterator<List<EcliCrawlerDocument>> listIterator =
        new Iterator<>() {

          public boolean hasNext() {
            return iterator.hasNext();
          }

          public List<EcliCrawlerDocument> next() {
            List<EcliCrawlerDocument> result = new ArrayList<>(10000);
            for (int i = 0; i < 10000 && iterator.hasNext(); i++) {
              result.add(EcliCrawlerDocumentMapper.fromCaseLawDocumentationUnit(iterator.next()));
            }
            return result;
          }
        };

    return StreamSupport.stream(
        ((Iterable<List<EcliCrawlerDocument>>) () -> listIterator).spliterator(), false);
  }

  private void createFromChangelogs(List<String> filePaths)
      throws ObjectStoreServiceException, FatalDailySitemapJobException {

    List<EcliCrawlerDocument> ecliDocuments = getAllChangesFromChangelogs(filePaths);
    try {
      if (!ecliDocuments.isEmpty()) {
        List<String> sitemapIndexPaths = writeSitemaps(ecliDocuments, now);
        sitemapService.updateRobotsTxt(sitemapIndexPaths);

        indexStatusService.saveStatus(
            STATUS_FILE, new IndexingState().withLastProcessedChangelogFile(filePaths.getLast()));
      }
    } catch (FileNotFoundException | JAXBException ex) {
      throw new FatalDailySitemapJobException(ex.getMessage());
    }
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

    List<String> createIdentifiers =
        mergedChangelog.getChanged().stream().map(i -> i.replace(".xml", "")).toList();
    List<EcliCrawlerDocument> changes =
        new ArrayList<>(
            caseLawService.getEcliDocumentsByDocumentNumbers(createIdentifiers).stream()
                .map(EcliCrawlerDocumentMapper::fromCaseLawDocumentationUnit)
                .toList());

    List<String> deleteIdentifiers =
        mergedChangelog.getDeleted().stream().map(i -> i.replace(".xml", "")).toList();
    changes.addAll(
        repository.findAllByIsPublishedIsTrueAndIdIn(deleteIdentifiers).stream()
            .map(
                ecliSitemap -> {
                  ecliSitemap.setPublished(false);
                  return ecliSitemap;
                })
            .toList());

    return changes;
  }
}
