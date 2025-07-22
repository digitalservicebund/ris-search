package de.bund.digitalservice.ris.search.sitemap.eclicrawler.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.mapper.RisToEcliMapper;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemapindex.SitemapIndexEntry;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemapindex.Sitemapindex;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

@Service
public class SitemapService {

  PortalBucket portalBucket;

  JAXBContext jaxbCtx;

  public static final String PATH_PREFIX = "eclicrawler/";
  public static final String ROBOTS_TXT_PATH = PATH_PREFIX + "robots.txt";

  public SitemapService(PortalBucket portalBucket) throws JAXBException {
    this.portalBucket = portalBucket;
    this.jaxbCtx = JAXBContext.newInstance(Sitemapindex.class, Sitemap.class);
  }

  public List<Sitemap> createSitemaps(
      List<ChangedDocument> changedDocuments, int maxSitemapEntries) {

    List<ChangedDocument> partsOfSitemap =
        changedDocuments.stream().filter(filterIncomplete()).toList();

    if (partsOfSitemap.isEmpty()) {
      return List.of();
    }
    List<List<ChangedDocument>> partitioned =
        ListUtils.partition(partsOfSitemap, maxSitemapEntries);

    return partitioned.stream().map(this::createSitemap).toList();
  }

  public List<Sitemap> createSitemaps(List<ChangedDocument> changedDocuments) {
    return createSitemaps(changedDocuments, 10000);
  }

  public Optional<String> writeSitemapFiles(List<Sitemap> urlSets, LocalDate day)
      throws JAXBException {
    List<String> urlSetLocations = writeSitemaps(urlSets, day);

    if (!urlSetLocations.isEmpty()) {
      Sitemapindex index = createSitemapIndex(urlSetLocations);
      String indexFilename = writeSitemapIndex(index, day);
      return Optional.of(indexFilename);
    } else {
      return Optional.empty();
    }
  }

  private Sitemap createSitemap(List<ChangedDocument> changedDocuments) {
    Sitemap set = new Sitemap();
    set.setUrl(
        changedDocuments.stream()
            .map(
                changed ->
                    switch (changed) {
                      case CreatedDocument(CaseLawDocumentationUnit docUnit) ->
                          RisToEcliMapper.caselawDocumentationUnitToEcliUrl(docUnit);
                      case DeletedDocument(String identifier) ->
                          RisToEcliMapper.deletedDocumentToEcliUrl(identifier);
                    })
            .toList());

    return set;
  }

  private Sitemapindex createSitemapIndex(List<String> sitemapLocations) {
    Sitemapindex index = new Sitemapindex();
    index.setSitemaps(
        sitemapLocations.stream().map(loc -> new SitemapIndexEntry().setLoc(loc)).toList());

    return index;
  }

  private String writeSitemapIndex(Sitemapindex index, LocalDate date) throws JAXBException {
    String content = EcliMarshaller.marshallSitemapIndex(index);

    String filename = String.format(PATH_PREFIX + "%s/sitemap_index_1.xml", getDatePartition(date));
    portalBucket.save(filename, content);

    return filename;
  }

  private String getDatePartition(LocalDate date) {
    return String.format("%s/%s/%s", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
  }

  public List<String> getSitemapFilesPathsForDay(LocalDate date) {
    return portalBucket.getAllKeysByPrefix(PATH_PREFIX + getDatePartition(date));
  }

  private List<String> writeSitemaps(List<Sitemap> sets, LocalDate date) throws JAXBException {

    List<String> locations = new ArrayList<>();
    for (int i = 0; i < sets.size(); i++) {
      int sitemapNr = i + 1;
      String filename =
          String.format(PATH_PREFIX + "%s/sitemap_%s.xml", getDatePartition(date), sitemapNr);
      String content = EcliMarshaller.marshallSitemap(sets.get(i));
      portalBucket.save(filename, content);
      locations.add(filename);
    }

    return locations;
  }

  public void writeRobotsTxt(String sitemapIndexPath) {
    String header =
        """
            User-agent: DG_JUSTICE_CRAWLER
            Allow: /
            """;

    String content = header + "Sitemap:" + sitemapIndexPath;

    portalBucket.save(ROBOTS_TXT_PATH, content);
  }

  public void updateRobotsTxt(String sitemapIndexPath)
      throws ObjectStoreServiceException, FileNotFoundException {
    Optional<String> contentOption = portalBucket.getFileAsString(ROBOTS_TXT_PATH);
    if (contentOption.isEmpty()) {
      throw new FileNotFoundException("no robots.txt found");
    }
    portalBucket.save(ROBOTS_TXT_PATH, contentOption.get() + "\nSitemap:" + sitemapIndexPath);
  }

  private Predicate<ChangedDocument> filterIncomplete() {
    return document ->
        switch (document) {
          case DeletedDocument ignored -> true;
          case CreatedDocument(CaseLawDocumentationUnit docUnit) ->
              Stream.of(
                      docUnit.ecli(),
                      docUnit.id(),
                      docUnit.decisionDate(),
                      docUnit.courtType(),
                      docUnit.documentType())
                  .noneMatch(Objects::isNull);
        };
  }
}
