package de.bund.digitalservice.ris.search.eclicrawler.service;

import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemap.Url;
import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemapindex.SitemapIndexEntry;
import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemapindex.Sitemapindex;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
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

  private static final int MAX_SITEMAP_URLS = 10000;

  public EcliSitemapService(PortalBucket portalBucket, EcliMarshaller marshaller) {
    this.portalBucket = portalBucket;
    this.marshaller = marshaller;
  }

  public List<Sitemap> writeUrlsToSitemaps(String basepath, LocalDate day, List<Url> urls)
      throws JAXBException {

    List<List<Url>> partitioned = ListUtils.partition(urls, MAX_SITEMAP_URLS);

    List<Sitemap> writtenSitemaps = new ArrayList<>();
    for (int i = 0; i < partitioned.size(); i++) {
      int sitemapNr = i + 1;
      Sitemap sitemap = new Sitemap();
      sitemap.setUrl(urls);
      sitemap.setName(String.format("%s/sitemap_%s.xml", getDatePartition(day), sitemapNr));
      writeSitemap(basepath, sitemap);

      writtenSitemaps.add(sitemap);
    }
    return writtenSitemaps;
  }

  public List<Sitemapindex> writeSitemapsIndices(
      String basePath, String baseUrl, LocalDate day, List<Sitemap> sitemaps) throws JAXBException {
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
      writeSitemapIndex(basePath, index);

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

  public List<String> getSitemapFilesPathsForDay(String basePath, LocalDate date) {
    return portalBucket.getAllKeysByPrefix(basePath + getDatePartition(date));
  }

  public Optional<byte[]> getSitemapFile(String filename) {
    try {
      return portalBucket.get(filename);
    } catch (ObjectStoreServiceException e) {
      return Optional.empty();
    }
  }

  public void writeRobotsTxt(String filename) {

    String header =
        """
            User-agent: *
            Disallow: /
            User-agent: DG_JUSTICE_CRAWLER
            Allow: /
            """;

    portalBucket.save(filename, header);
  }

  public void updateRobotsTxt(String filename, String baseUrl, List<Sitemapindex> sitemapIndinces)
      throws ObjectStoreServiceException, FileNotFoundException {
    Optional<String> contentOption = portalBucket.getFileAsString(filename);
    if (contentOption.isEmpty()) {
      throw new FileNotFoundException("no robots.txt found");
    }
    portalBucket.save(filename, appendSitemaps(baseUrl, contentOption.get(), sitemapIndinces));
  }

  private String appendSitemaps(String baseUrl, String content, List<Sitemapindex> sitemapIndices) {
    StringBuilder sb = new StringBuilder(content);

    for (Sitemapindex index : sitemapIndices) {
      sb.append("\nSitemap:").append(baseUrl).append(index.getName());
    }
    return sb.toString();
  }
}
