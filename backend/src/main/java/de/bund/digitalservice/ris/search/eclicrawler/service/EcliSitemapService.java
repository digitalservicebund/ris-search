package de.bund.digitalservice.ris.search.eclicrawler.service;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.eclicrawler.mapper.EcliCrawlerDocumentMapper;
import de.bund.digitalservice.ris.search.eclicrawler.model.EcliCrawlerDocument;
import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemapindex.SitemapIndexEntry;
import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemapindex.Sitemapindex;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EcliSitemapService {

  PortalBucket portalBucket;

  JAXBContext jaxbCtx;

  @Value("${server.front-end-url}")
  String frontEndUrl;

  public static final String PATH_PREFIX = "eclicrawler/";
  private static final String ROBOTS_TXT_PATH = PATH_PREFIX + "robots.txt";
  private static final int MAX_SITEMAP_URLS = 10000;

  public EcliSitemapService(PortalBucket portalBucket) throws JAXBException {
    this.portalBucket = portalBucket;
    this.jaxbCtx = JAXBContext.newInstance(Sitemapindex.class, Sitemap.class);
  }

  public List<Sitemap> createSitemaps(List<EcliCrawlerDocument> ecliDocuments) {

    List<List<EcliCrawlerDocument>> partitioned =
        ListUtils.partition(ecliDocuments, MAX_SITEMAP_URLS);

    return partitioned.stream().map(this::createSitemap).toList();
  }

  public List<String> writeSitemapFiles(List<Sitemap> sitemaps, LocalDate day)
      throws JAXBException {
    List<String> urlSetLocations = writeSitemaps(sitemaps, day);

    List<List<Sitemap>> partitionedSitemaps = ListUtils.partition(sitemaps, MAX_SITEMAP_URLS);

    List<String> sitemapFilePaths = new ArrayList<>();
    for (int i = 0; i < partitionedSitemaps.size(); i++) {
      int indexEnumertaor = i + 1;
      Sitemapindex index = createSitemapIndex(urlSetLocations);
      sitemapFilePaths.add(writeSitemapIndex(index, day, indexEnumertaor));
    }

    return sitemapFilePaths;
  }

  private Sitemap createSitemap(List<EcliCrawlerDocument> ecliDocuments) {
    Sitemap set = new Sitemap();
    set.setUrl(ecliDocuments.stream().map(EcliCrawlerDocumentMapper::toSitemapUrl).toList());

    return set;
  }

  private Sitemapindex createSitemapIndex(List<String> sitemapLocations) {
    Sitemapindex index = new Sitemapindex();
    index.setSitemaps(
        sitemapLocations.stream().map(loc -> new SitemapIndexEntry().setLoc(loc)).toList());

    return index;
  }

  private String writeSitemapIndex(Sitemapindex index, LocalDate date, int enumerator)
      throws JAXBException {
    String content = EcliMarshaller.marshallSitemapIndex(index);

    String filename =
        String.format(PATH_PREFIX + "%s/sitemap_index_%s.xml", getDatePartition(date), enumerator);
    portalBucket.save(filename, content);

    return filename;
  }

  private String getDatePartition(LocalDate date) {
    return date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
  }

  public List<String> getSitemapFilesPathsForDay(LocalDate date) {
    return portalBucket.getAllKeysByPrefix(PATH_PREFIX + getDatePartition(date));
  }

  public Optional<byte[]> getSitemapFile(String year, String month, String day, String filename) {

    try {
      return portalBucket.get(
          EcliSitemapService.PATH_PREFIX + year + "/" + month + "/" + day + "/" + filename);
    } catch (ObjectStoreServiceException e) {
      return Optional.empty();
    }
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

  public void writeRobotsTxt(List<String> sitemapIndexPaths) {

    String header =
        """
            User-agent: *
            Disallow: /
            User-agent: DG_JUSTICE_CRAWLER
            Allow: /
            """;

    portalBucket.save(ROBOTS_TXT_PATH, appendSitemapPaths(header, sitemapIndexPaths));
  }

  public void updateRobotsTxt(List<String> sitemapIndexPaths)
      throws ObjectStoreServiceException, FileNotFoundException {
    Optional<String> contentOption = portalBucket.getFileAsString(ROBOTS_TXT_PATH);
    if (contentOption.isEmpty()) {
      throw new FileNotFoundException("no robots.txt found");
    }
    portalBucket.save(ROBOTS_TXT_PATH, appendSitemapPaths(contentOption.get(), sitemapIndexPaths));
  }

  private String appendSitemapPaths(String content, List<String> sitemapIndexPaths) {
    StringBuilder sb = new StringBuilder(content);

    for (String indexPath : sitemapIndexPaths) {
      sb.append("\nSitemap:")
          .append(ApiConfig.Paths.ECLICRAWLER)
          .append(indexPath.replace(PATH_PREFIX, "/"));
    }
    return sb.toString();
  }
}
