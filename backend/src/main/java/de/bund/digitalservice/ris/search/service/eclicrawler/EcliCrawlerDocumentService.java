package de.bund.digitalservice.ris.search.service.eclicrawler;

import de.bund.digitalservice.ris.search.exception.FatalEcliSitemapJobException;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.mapper.EcliCrawlerDocumentMapper;
import de.bund.digitalservice.ris.search.models.eclicrawler.ecli.Courts;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.EcliCrawlerDocument;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.EcliCrawlerDocumentRepository;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.service.IndexSyncJob;
import jakarta.xml.bind.JAXBException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
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
  EcliSitemapWriter sitemapWriter;
  private final String documentUrl;
  private static final int DOCUMENT_STORE_BULK_SIZE = 10000;
  public static final int MAX_SITEMAP_URLS = 50000;
  private static final Logger logger = LogManager.getLogger(EcliCrawlerDocumentService.class);

  // ecli regex provided by the ecli xsd
  Pattern ecliPattern =
      Pattern.compile("ECLI:[a-zA-Z]{1,5}:[a-zA-Z][a-zA-Z0-9]{0,6}:[12]\\d{3}:[a-zA-Z0-9.]{1,25}");

  public EcliCrawlerDocumentService(
      CaseLawBucket caseLawBucket,
      EcliCrawlerDocumentRepository repository,
      CaseLawService caselawService,
      EcliSitemapWriter sitemapWriter,
      @Value("${server.front-end-url}") String frontEndUrl) {
    this.caselawBucket = caseLawBucket;
    this.repository = repository;
    this.caselawService = caselawService;
    this.sitemapWriter = sitemapWriter;
    this.documentUrl = frontEndUrl + "case-law/";
  }

  public void saveAll(List<EcliCrawlerDocument> docs) {
    ListUtils.partition(docs, DOCUMENT_STORE_BULK_SIZE).forEach(i -> repository.saveAll(i));
  }

  private boolean isValidEcliDocument(CaseLawDocumentationUnit unit) {
    if (Stream.of(unit.documentNumber(), unit.ecli(), unit.courtType(), unit.decisionDate())
        .anyMatch(Objects::isNull)) {
      return false;
    }
    return ecliPattern.matcher(unit.ecli()).find()
        && Courts.supportedCourtNames.containsKey(unit.courtType());
  }

  private EcliCrawlerDocument setDeleted(EcliCrawlerDocument doc) {
    return new EcliCrawlerDocument(
        doc.documentNumber(),
        doc.filename(),
        doc.ecli(),
        doc.courtType(),
        doc.decisionDate(),
        doc.url(),
        false);
  }

  public void writeFromChangelog(String apiUrl, LocalDate day, Changelog changelog) {

    ChangedEcliCrawlerDocumentsIterator iterator =
        new ChangedEcliCrawlerDocumentsIterator(
            this::getFromBucket,
            this::getPublishedDocument,
            new ArrayList<>(changelog.getChanged()),
            new ArrayList<>(changelog.getDeleted()),
            MAX_SITEMAP_URLS);

    writeFilesFromIterator(apiUrl, day, iterator);
  }

  public void writeFullDiff(String apiUrl, LocalDate day) {
    List<String> allFiles =
        caselawBucket.getAllKeys().stream()
            .filter(s -> s.endsWith(".xml") && !s.contains(IndexSyncJob.CHANGELOGS_PREFIX))
            .toList();

    List<String> toBeDeleted;
    try (Stream<String> allPublished = repository.findFilenameByIsPublishedIsTrue()) {
      toBeDeleted =
          new ArrayList<>(allPublished.filter(filename -> !allFiles.contains(filename)).toList());
    }

    ChangedEcliCrawlerDocumentsIterator iterator =
        new ChangedEcliCrawlerDocumentsIterator(
            this::getFromBucket,
            this::getPublishedDocument,
            allFiles,
            toBeDeleted,
            MAX_SITEMAP_URLS);

    writeFilesFromIterator(apiUrl, day, iterator);
  }

  private void writeFilesFromIterator(
      String apiUrl, LocalDate day, ChangedEcliCrawlerDocumentsIterator iterator) {
    try {
      List<Sitemap> writtenSitemaps = new ArrayList<>();
      while (iterator.hasNext()) {
        var docs = iterator.next();
        logger.info("write {} urls to sitemap", docs.size());
        writtenSitemaps.add(
            sitemapWriter.writeUrlsToSitemap(
                day,
                docs.stream().map(EcliCrawlerDocumentMapper::toSitemapUrl).toList(),
                writtenSitemaps.size() + 1));
        saveAll(docs);
      }
      logger.info("write {} sitemapindices", writtenSitemaps.size());
      var sitemapIndices = sitemapWriter.writeSitemapsIndices(apiUrl, day, writtenSitemaps);
      sitemapWriter.updateRobotsTxt(apiUrl, sitemapIndices);
    } catch (JAXBException | ObjectStoreServiceException e) {
      throw new FatalEcliSitemapJobException(e.getMessage());
    }
  }

  private Optional<EcliCrawlerDocument> getFromBucket(String filename) {
    try {
      return caselawService
          .getFromBucket(filename)
          .flatMap(
              unit -> {
                if (isValidEcliDocument(unit)) {
                  return Optional.of(
                      EcliCrawlerDocumentMapper.fromCaseLawDocumentationUnit(
                          documentUrl, filename, unit));
                }
                return Optional.empty();
              });
    } catch (ObjectStoreServiceException e) {
      throw new FatalEcliSitemapJobException("no connection to bucket");
    }
  }

  private Optional<EcliCrawlerDocument> getPublishedDocument(String filename) {
    return repository.findByFilenameIn(filename).map(this::setDeleted);
  }
}
