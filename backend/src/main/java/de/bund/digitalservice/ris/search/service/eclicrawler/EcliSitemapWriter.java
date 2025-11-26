package de.bund.digitalservice.ris.search.service.eclicrawler;

import de.bund.digitalservice.ris.search.exception.FileNotFoundException;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemap.Url;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemapindex.SitemapIndexEntry;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemapindex.Sitemapindex;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import jakarta.xml.bind.JAXBException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

/** Service for writing ECLI sitemaps and sitemap indices to the portal bucket. */
@Service
public class EcliSitemapWriter {

  PortalBucket portalBucket;

  EcliMarshaller marshaller;

  public static final int MAX_SITEMAP_URLS = 50000;
  public static final String PATH_PREFIX = "eclicrawler/";
  public static final String ROBOTS_TXT_PATH = PATH_PREFIX + "robots.txt";

  /**
   * Constructor for EcliSitemapWriter.
   *
   * @param portalBucket The PortalBucket for storing sitemap files.
   * @param marshaller The EcliMarshaller for marshalling sitemap objects.
   */
  public EcliSitemapWriter(PortalBucket portalBucket, EcliMarshaller marshaller) {
    this.portalBucket = portalBucket;
    this.marshaller = marshaller;
  }

  /**
   * Write URLs to a sitemap for a specific day.
   *
   * @param day The date for which the sitemap is generated.
   * @param urls The list of URLs to include in the sitemap.
   * @param sitemapNr The sitemap number for naming purposes.
   * @return The written Sitemap object.
   * @throws JAXBException If there is an error during marshalling.
   */
  public Sitemap writeUrlsToSitemap(LocalDate day, List<Url> urls, int sitemapNr)
      throws JAXBException {
    Sitemap sitemap = new Sitemap();
    sitemap.setUrl(urls);
    sitemap.setName(String.format("%s/sitemap_%s.xml", getDatePartition(day), sitemapNr));
    writeSitemap(PATH_PREFIX, sitemap);
    return sitemap;
  }

  /**
   * Write sitemap indices for the given sitemaps.
   *
   * @param baseUrl The base URL to prepend to sitemap locations.
   * @param day The date for which the sitemaps are generated.
   * @param sitemaps The list of sitemaps to include in the indices.
   * @return A list of written Sitemapindex objects.
   * @throws JAXBException If there is an error during marshalling.
   */
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

  /**
   * Retrieve a sitemap file by its filename.
   *
   * @param filename The name of the sitemap file to retrieve.
   * @return An Optional containing the byte array of the sitemap file if found, otherwise empty.
   */
  public Optional<byte[]> getSitemapFile(String filename) {
    try {
      return portalBucket.get(PATH_PREFIX + filename);
    } catch (ObjectStoreServiceException e) {
      return Optional.empty();
    }
  }

  /**
   * Retrieve the robots.txt file.
   *
   * @return An Optional containing the byte array of the robots.txt file if found, otherwise empty.
   */
  public Optional<byte[]> getRobots() {
    try {
      return portalBucket.get(ROBOTS_TXT_PATH);
    } catch (ObjectStoreServiceException e) {
      return Optional.empty();
    }
  }

  /** Write the initial robots.txt file to disallow all crawlers except DG_JUSTICE_CRAWLER. */
  public void writeRobotsTxt() {

    String header =
        """
            User-agent: *
            Disallow: /
            User-agent: DG_JUSTICE_CRAWLER
            """;

    portalBucket.save(ROBOTS_TXT_PATH, header);
  }

  /**
   * Update the robots.txt file by appending sitemap entries.
   *
   * @param baseUrl The base URL to prepend to sitemap paths.
   * @param sitemapIndinces List of Sitemapindex objects to append to robots.txt.
   * @throws ObjectStoreServiceException If there is an error accessing the object store.
   * @throws FileNotFoundException If the robots.txt file does not exist.
   */
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
