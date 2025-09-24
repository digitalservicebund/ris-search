package de.bund.digitalservice.ris.search.eclicrawler.service;

import de.bund.digitalservice.ris.search.eclicrawler.mapper.EcliCrawlerDocumentMapper;
import de.bund.digitalservice.ris.search.eclicrawler.model.EcliCrawlerDocument;
import de.bund.digitalservice.ris.search.eclicrawler.repository.EcliCrawlerDocumentRepository;
import de.bund.digitalservice.ris.search.eclicrawler.schema.ecli.Courts;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.service.IndexSyncJob;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EcliCrawlerDocumentService {

  CaseLawBucket caselawBucket;
  EcliCrawlerDocumentRepository repository;
  CaseLawService caselawService;
  private final String documentUrl;
  private static final int OPEN_SEARCH_MAX_RESULT_WINDOW = 10000;

  private static final Logger logger = LogManager.getLogger(EcliCrawlerDocumentService.class);

  public EcliCrawlerDocumentService(
      CaseLawBucket caseLawBucket,
      EcliCrawlerDocumentRepository repository,
      CaseLawService caselawService,
      @Value("${server.front-end-url}") String frontEndUrl) {
    this.caselawBucket = caseLawBucket;
    this.repository = repository;
    this.caselawService = caselawService;
    this.documentUrl = frontEndUrl + "case-law/";
  }

  public Iterable<EcliCrawlerDocument> saveAll(List<EcliCrawlerDocument> docs) {
    return repository.saveAll(docs);
  }

  public List<EcliCrawlerDocument> getFromChangelogs(List<Changelog> changelogs) {
    List<EcliCrawlerDocument> docs = new ArrayList<>();

    if (BulkChangelogParser.containsChangeAll(changelogs)) {
      docs.addAll(getFullDiff());
    }
    // apply changelogs happening after the changeAll to catch deletes
    var latestChangelogs = BulkChangelogParser.getChangelogsFromLastChangeAll(changelogs);
    Changelog mergedChangelog = BulkChangelogParser.mergeChangelogs(latestChangelogs);
    return applyChangelog(docs, mergedChangelog);
  }

  private List<EcliCrawlerDocument> applyChangelog(List<EcliCrawlerDocument> docs, Changelog log) {
    List<EcliCrawlerDocument> changes = getAllEcliCrawlerDocumentsFromChangelog(log);

    Stream<EcliCrawlerDocument> removeChangedDocuments =
        docs.stream()
            .filter(
                doc ->
                    !changes.stream()
                        .map(EcliCrawlerDocument::documentNumber)
                        .toList()
                        .contains(doc.documentNumber()));

    return Stream.concat(changes.stream(), removeChangedDocuments).toList();
  }

  public List<EcliCrawlerDocument> getFullDiff() {
    List<EcliCrawlerDocument> allEcliDocumnets = new ArrayList<>();
    List<String> allFiles =
        caselawBucket.getAllKeys().stream()
            .filter(s -> s.endsWith(".xml") && !s.contains(IndexSyncJob.CHANGELOGS_PREFIX))
            .toList();
    List<List<String>> filenamebatches =
        ListUtils.partition(allFiles, OPEN_SEARCH_MAX_RESULT_WINDOW);

    for (List<String> batch : filenamebatches) {
      allEcliDocumnets.addAll(getEcliCrawlerDocuments(batch));
    }

    return allEcliDocumnets;
  }

  private List<EcliCrawlerDocument> getEcliCrawlerDocuments(List<String> filenames) {
    List<EcliCrawlerDocument> ecliDocuments = new ArrayList<>();

    for (String filename : filenames) {
      try {
        Optional<CaseLawDocumentationUnit> unitOption = caselawService.getFromBucket(filename);
        unitOption.ifPresentOrElse(
            unit -> {
              if (!Objects.isNull(unit.ecli())
                  && Courts.supportedCourtNames.containsKey(unit.courtType())) {
                ecliDocuments.add(
                    EcliCrawlerDocumentMapper.fromCaseLawDocumentationUnit(
                        documentUrl, filename, unit));
              }
            },
            () -> logger.info("Unable to parse caseLawDocumentationunit {} - continue", filename));
      } catch (ObjectStoreServiceException ex) {
        throw new FatalEcliSitemapJobException(ex.getMessage());
      }
    }
    return ecliDocuments;
  }

  private List<EcliCrawlerDocument> getAllEcliCrawlerDocumentsFromChangelog(
      Changelog mergedChangelog) {

    List<EcliCrawlerDocument> changes = new ArrayList<>();
    changes.addAll(getEcliCrawlerDocuments(mergedChangelog.getChanged().stream().toList()));

    changes.addAll(
        repository.findAllByFilenameIn(mergedChangelog.getDeleted()).stream()
            .map(
                ecliDocument ->
                    new EcliCrawlerDocument(
                        ecliDocument.documentNumber(),
                        ecliDocument.filename(),
                        ecliDocument.ecli(),
                        ecliDocument.courtType(),
                        ecliDocument.decisionDate(),
                        ecliDocument.documentType(),
                        ecliDocument.url(),
                        false))
            .toList());
    return changes;
  }
}
