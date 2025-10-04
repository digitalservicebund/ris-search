package de.bund.digitalservice.ris.search.service.eclicrawler;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemap.Url;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemapindex.SitemapIndexEntry;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemapindex.Sitemapindex;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import jakarta.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

@Service
public class EcliSitemapService {

  PortalBucket portalBucket;

  EcliMarshaller marshaller;

  public static final int MAX_SITEMAP_URLS = 50000;
  public static final String PATH_PREFIX = "eclicrawler/";
  public static final String ROBOTS_TXT_PATH = PATH_PREFIX + "robots.txt";

  public EcliSitemapService(PortalBucket portalBucket, EcliMarshaller marshaller) {
    this.portalBucket = portalBucket;
    this.marshaller = marshaller;
  }

  public List<Sitemap> writeUrlsToSitemaps(LocalDate day, List<Url> urls) throws JAXBException {

    List<List<Url>> partitioned = ListUtils.partition(urls, MAX_SITEMAP_URLS);

    List<Sitemap> writtenSitemaps = new ArrayList<>();
    for (int i = 0; i < partitioned.size(); i++) {
      int sitemapNr = i + 1;
      Sitemap sitemap = new Sitemap();
      sitemap.setUrl(partitioned.get(i));
      sitemap.setName(String.format("%s/sitemap_%s.xml", getDatePartition(day), sitemapNr));
      writeSitemap(PATH_PREFIX, sitemap);

      writtenSitemaps.add(sitemap);
    }
    return writtenSitemaps;
  }

  public List<Sitemapindex> writeSitemapsIndices(
      String baseUrl, LocalDate day, List<Sitemap> sitemaps) throws JAXBException {
    List<List<Sitemap>> partitioned = ListUtils.partition(sitemaps, MAX_SITEMAP_URLS);

    List<Sitemapindex> writtenIndices = new ArrayList<>();
    for (int i = 0; i < partitioned.size(); i++) {
      int sitemapNr = i + 1;
      Sitemapindex index = new Sitemapindex();
      List<SitemapIndexEntry> locs =
          partitioned.get(i).stream()
              .map(sitemap -> new SitemapIndexEntry().setLoc(baseUrl + sitemap.getName()))
              .toList();
      index.setSitemaps(locs);
      index.setName(String.format("%s/sitemap_index_%s.xml", getDatePartition(day), sitemapNr));
      writeSitemapIndex(PATH_PREFIX, index);

      writtenIndices.add(index);
    }
    return writtenIndices;
  }

  private void writeSitemap(String path, Sitemap sitemap) throws JAXBException {
    String content = marshaller.marshallSitemap(sitemap);
    portalBucket.save(path + sitemap.getName(), content);
  }

  private void writeSitemapIndex(String path, Sitemapindex index) throws JAXBException {
    String content = marshaller.marshallSitemapIndex(index);
    portalBucket.save(path + index.getName(), content);
  }

  private String getDatePartition(LocalDate date) {
    return date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
  }

  public List<String> getSitemapFilesPathsForDay(LocalDate date) {
    return portalBucket.getAllKeysByPrefix(PATH_PREFIX + getDatePartition(date));
  }

  public Optional<byte[]> getSitemapFile(String filename) {
    try {
      return portalBucket.get(PATH_PREFIX + filename);
    } catch (ObjectStoreServiceException e) {
      return Optional.empty();
    }
  }

  public Optional<byte[]> getRobots() {
    try {
      return portalBucket.get(ROBOTS_TXT_PATH);
    } catch (ObjectStoreServiceException e) {
      return Optional.empty();
    }
  }

  public void writeRobotsTxt() {

    String header =
        """
            User-agent: *
            Disallow: /
            User-agent: DG_JUSTICE_CRAWLER
            """;

    portalBucket.save(ROBOTS_TXT_PATH, header);
  }

  public void updateRobotsTxt(String baseUrl, List<Sitemapindex> sitemapIndinces)
      throws ObjectStoreServiceException, FileNotFoundException {
    Optional<String> contentOption = portalBucket.getFileAsString(ROBOTS_TXT_PATH);
    if (contentOption.isEmpty()) {
      throw new FileNotFoundException("no robots.txt found");
    }
    portalBucket.save(
        ROBOTS_TXT_PATH, appendSitemaps(baseUrl, contentOption.get(), sitemapIndinces));
  }

  private String appendSitemaps(String baseUrl, String content, List<Sitemapindex> sitemapIndices) {
    StringBuilder sb = new StringBuilder(content);

    for (Sitemapindex index : sitemapIndices) {
      sb.append("\nSitemap:").append(baseUrl).append(index.getName());
    }
    return sb.toString();
  }
}
