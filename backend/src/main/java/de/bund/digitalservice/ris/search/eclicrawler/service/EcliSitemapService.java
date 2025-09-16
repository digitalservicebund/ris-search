package de.bund.digitalservice.ris.search.eclicrawler.service;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

@Service
public class EcliSitemapService {

  PortalBucket portalBucket;

  JAXBContext jaxbCtx;

  public static final String PATH_PREFIX = "eclicrawler/";
  private static final String ROBOTS_TXT_PATH = PATH_PREFIX + "robots.txt";
  private static final int MAX_SITEMAP_URLS = 10000;

  public EcliSitemapService(PortalBucket portalBucket) throws JAXBException {
    this.portalBucket = portalBucket;
    this.jaxbCtx = JAXBContext.newInstance(Sitemapindex.class, Sitemap.class);
  }

  public Map<String, Sitemap> writeDocumentsToSitemaps(
      String basepath, LocalDate day, List<EcliCrawlerDocument> ecliDocuments)
      throws JAXBException {

    List<List<EcliCrawlerDocument>> partitioned =
        ListUtils.partition(ecliDocuments, MAX_SITEMAP_URLS);

    Map<String, Sitemap> writtenSitemaps = new HashMap<>();
    for (int i = 0; i < partitioned.size(); i++) {
      int sitemapNr = i + 1;
      Sitemap sitemap = new Sitemap();
      sitemap.setUrl(
          partitioned.get(i).stream().map(EcliCrawlerDocumentMapper::toSitemapUrl).toList());
      String name = String.format("%s/sitemap_%s.xml", getDatePartition(day), sitemapNr);
      writtenSitemaps.put(name, sitemap);
      writeSitemap(basepath, name, sitemap);
    }
    return writtenSitemaps;
  }

  public Map<String, Sitemapindex> writeSitemapnamesToIndices(
      String basePath, String baseUrl, LocalDate day, List<String> sitemapNames)
      throws JAXBException {
    List<List<String>> partitioned = ListUtils.partition(sitemapNames, MAX_SITEMAP_URLS);

    Map<String, Sitemapindex> writtenIndices = new HashMap<>();
    for (int i = 0; i < partitioned.size(); i++) {
      int sitemapNr = i + 1;
      Sitemapindex index = new Sitemapindex();
      List<SitemapIndexEntry> locs =
          partitioned.get(i).stream()
              .map(name -> new SitemapIndexEntry().setLoc(baseUrl + name))
              .toList();
      index.setSitemaps(locs);
      String name = String.format("%s/sitemap_index_%s.xml", getDatePartition(day), sitemapNr);
      writtenIndices.put(name, index);
      writeSitemapIndex(basePath, name, index);
    }
    return writtenIndices;
  }

  private void writeSitemap(String path, String name, Sitemap sitemap) throws JAXBException {
    String content = EcliMarshaller.marshallSitemap(sitemap);
    portalBucket.save(path + name, content);
  }

  private void writeSitemapIndex(String path, String name, Sitemapindex index)
      throws JAXBException {
    String content = EcliMarshaller.marshallSitemapIndex(index);
    portalBucket.save(path + name, content);
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

  public void writeRobotsTxt() {

    String header =
        """
            User-agent: *
            Disallow: /
            User-agent: DG_JUSTICE_CRAWLER
            Allow: /
            """;

    portalBucket.save(ROBOTS_TXT_PATH, header);
  }

  public void updateRobotsTxt(String baseUrl, List<String> sitemapIndexPaths)
      throws ObjectStoreServiceException, FileNotFoundException {
    Optional<String> contentOption = portalBucket.getFileAsString(ROBOTS_TXT_PATH);
    if (contentOption.isEmpty()) {
      throw new FileNotFoundException("no robots.txt found");
    }
    portalBucket.save(
        ROBOTS_TXT_PATH, appendSitemapPaths(baseUrl, contentOption.get(), sitemapIndexPaths));
  }

  private String appendSitemapPaths(
      String baseUrl, String content, List<String> sitemapIndexPaths) {
    StringBuilder sb = new StringBuilder(content);

    for (String indexUrl : sitemapIndexPaths) {
      sb.append("\nSitemap:").append(baseUrl).append("api/v1/eclicrawler/").append(indexUrl);
    }
    return sb.toString();
  }
}
