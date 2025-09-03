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
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.mapper.EcliSitemapMetadataMapper;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository.EcliDocumentRepository;
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

  EcliDocumentRepository repository;

  final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static final String STATUS_FILE = "caselaw_sitemaps_status.json";

  public DailySitemapJob(
      SitemapService service,
      CaseLawIndexSyncJob indexJob,
      PortalBucket portalBucket,
      CaseLawBucket caselawbucket,
      IndexStatusService indexStatusService,
      CaseLawService caseLawService,
      EcliDocumentRepository repository) {
    this.sitemapService = service;
    this.indexJob = indexJob;
    this.portalBucket = portalBucket;
    this.caselawbucket = caselawbucket;
    this.indexStatusService = indexStatusService;
    this.caseLawService = caseLawService;
    this.repository = repository;
  }

  public void run(LocalDate cutOffDate)
      throws ObjectStoreServiceException, FatalDailySitemapJobException {

    if (!sitemapService.getSitemapFilesPathsForDay(cutOffDate).isEmpty()) {
      throw new FatalDailySitemapJobException(
          String.format("Sitemaps for day %s already exists", cutOffDate.format(formatter)));
    }

    IndexingState state = indexStatusService.loadStatus(STATUS_FILE);
    String lastProcessedChangelogFile = state.lastProcessedChangelogFile();

    if (Objects.isNull(lastProcessedChangelogFile)) {
      createAll();
    } else {
      String lastSuccesfulIndexingJob =
          indexStatusService
              .loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME)
              .lastProcessedChangelogFile();

      boolean indexingIsFinished =
          !Objects.isNull(lastSuccesfulIndexingJob)
              && lastSuccesfulIndexingJob.compareTo(lastProcessedChangelogFile) >= 0;
      if (indexingIsFinished) {
        List<String> newChangelogPaths =
            indexJob.getNewChangelogs(caselawbucket, lastProcessedChangelogFile);
        createFromChangelogs(newChangelogPaths, cutOffDate);
      }
    }
  }

  private List<String> writeSitemaps(List<EcliDocumentChange> ecliDocuments, LocalDate cutoffDate)
      throws JAXBException {

    List<Sitemap> urlSets = sitemapService.createSitemaps(ecliDocuments);
    List<String> indexPaths = sitemapService.writeSitemapFiles(urlSets, cutoffDate);
    if (!indexPaths.isEmpty()) {
      repository.persistEcliDocumentsChanges(ecliDocuments);
    }
    return indexPaths;
  }

  private void createAll() throws FatalDailySitemapJobException {
    LocalDate yesterday = LocalDate.now().minusDays(1);

    Stream<List<EcliDocumentChange>> docUnits =
        getAllCreatedDocumentsFromStream(caseLawService.getAllEcliDocuments());

    docUnits.forEach(
        documents -> {
          try {
            List<String> sitemapPaths = writeSitemaps(documents, yesterday);
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
  private Stream<List<EcliDocumentChange>> getAllCreatedDocumentsFromStream(
      Stream<CaseLawDocumentationUnit> stream) {
    Iterator<CaseLawDocumentationUnit> iterator = stream.iterator();
    Iterator<List<EcliDocumentChange>> listIterator =
        new Iterator<>() {

          public boolean hasNext() {
            return iterator.hasNext();
          }

          public List<EcliDocumentChange> next() {
            List<EcliDocumentChange> result = new ArrayList<>(10000);
            for (int i = 0; i < 10000 && iterator.hasNext(); i++) {
              result.add(
                  new EcliDocumentChange(
                      EcliSitemapMetadataMapper.fromCaseLawDocumentationUnit(iterator.next()),
                      EcliDocumentChange.ChangeType.CHANGE));
            }
            return result;
          }
        };

    return StreamSupport.stream(
        ((Iterable<List<EcliDocumentChange>>) () -> listIterator).spliterator(), false);
  }

  private void createFromChangelogs(List<String> filePaths, LocalDate cutoffDate)
      throws ObjectStoreServiceException, FatalDailySitemapJobException {

    var changelogsUpToCutoff =
        filePaths.stream()
            .filter(
                e -> {
                  int datetimeStringOffset = IndexSyncJob.CHANGELOGS_PREFIX.length();
                  String datetimeString =
                      e.substring(datetimeStringOffset, datetimeStringOffset + 10);
                  LocalDate time = LocalDate.parse(datetimeString, formatter);
                  return time.isBefore(cutoffDate) || time.isEqual(cutoffDate);
                })
            .toList();

    List<EcliDocumentChange> ecliDocuments = getAllChangesFromChangelogs(changelogsUpToCutoff);
    try {
      if (!ecliDocuments.isEmpty()) {
        List<String> sitemapIndexPaths = writeSitemaps(ecliDocuments, cutoffDate);
        sitemapService.updateRobotsTxt(sitemapIndexPaths);

        indexStatusService.saveStatus(
            STATUS_FILE, new IndexingState().withLastProcessedChangelogFile(filePaths.getLast()));
      }
    } catch (FileNotFoundException | JAXBException ex) {
      throw new FatalDailySitemapJobException(ex.getMessage());
    }
  }

  private List<EcliDocumentChange> getAllChangesFromChangelogs(List<String> changelogPaths)
      throws ObjectStoreServiceException {

    Changelog mergedChangelog = mergeChangelogs(changelogPaths);

    List<String> createIdentifiers =
        mergedChangelog.getChanged().stream().map(i -> i.replace(".xml", "")).toList();
    List<EcliDocumentChange> changes =
        new ArrayList<>(
            caseLawService.getEcliDocumentsByDocumentNumbers(createIdentifiers).stream()
                .map(
                    unit ->
                        new EcliDocumentChange(
                            EcliSitemapMetadataMapper.fromCaseLawDocumentationUnit(unit),
                            EcliDocumentChange.ChangeType.CHANGE))
                .toList());

    List<String> deleteIdentifiers =
        mergedChangelog.getDeleted().stream().map(i -> i.replace(".xml", "")).toList();
    changes.addAll(
        repository.getAllMetadataById(deleteIdentifiers).stream()
            .map(meta -> new EcliDocumentChange(meta, EcliDocumentChange.ChangeType.DELETE))
            .toList());

    return changes;
  }

  private Changelog mergeChangelogs(List<String> changelogPaths)
      throws ObjectStoreServiceException {
    Changelog mergedChangelog = new Changelog();
    for (String changelogPath : changelogPaths) {
      Optional<Changelog> logOptional =
          Optional.ofNullable(this.indexJob.parseOneChangelog(caselawbucket, changelogPath));

      if (logOptional.isPresent()) {
        var log = logOptional.get();

        mergedChangelog.setChanged(log.getChanged());
        mergedChangelog.getDeleted().removeAll(log.getChanged());

        mergedChangelog.setDeleted(log.getDeleted());
        mergedChangelog.getChanged().removeAll(log.getDeleted());
      }
    }
    return mergedChangelog;
  }
}
