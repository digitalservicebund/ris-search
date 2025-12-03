package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.models.sitemap.SitemapFile;
import de.bund.digitalservice.ris.search.models.sitemap.SitemapIndex;
import de.bund.digitalservice.ris.search.models.sitemap.Url;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Service for generating sitemaps. */
@Service
@RequiredArgsConstructor
public class SitemapService {
  @Value("${server.front-end-url}")
  private String baseUrl;

  private static final String SITEMAP_PREFIX = "sitemaps/";
  public final PortalBucket portalBucket;

  /**
   * Returns the path of a sitemap file for a given batch number
   *
   * @param batchNumber batch number
   * @param type the type of sitemap currently being generated
   * @return sitemap file path
   */
  public String getBatchSitemapPath(int batchNumber, DocumentKind type) {
    return SITEMAP_PREFIX + String.format("%s/%d.xml", type.getSiteMapPath(), batchNumber);
  }

  /**
   * Creates a batch sitemap and saves it to the portal bucket
   *
   * @param batchNumber the batch number
   * @param ids the list of ids for this batch
   * @param docKind the docKind of sitemap currently being generated
   * @param prefix the sitemap prefix
   */
  public void createBatchSitemap(
      int batchNumber, List<String> ids, DocumentKind docKind, String prefix) {
    String path = getBatchSitemapPath(batchNumber, docKind);
    portalBucket.save(path, generateSitemap(ids, prefix));
  }

  /**
   * Returns the path of the sitemap index file
   *
   * @param type the type of sitemap currently being generated
   * @return sitemap index file path
   */
  public String getIndexSitemapPath(DocumentKind type) {
    return SITEMAP_PREFIX + String.format("%s/index.xml", type.getSiteMapPath());
  }

  /**
   * Creates a sitemap index file and saves it to the portal bucket
   *
   * @param size number of sitemap files
   * @param type the type of sitemap currently being generated
   */
  public void createIndexSitemap(int size, DocumentKind type) {
    String path = getIndexSitemapPath(type);
    portalBucket.save(path, generateIndexXml(size, type));
  }

  /**
   * Deletes sitemap files older than the given date
   *
   * @param beforeDateTime date before which sitemap files should be deleted
   */
  public void deleteSitemapFiles(Instant beforeDateTime) {
    portalBucket.getAllKeyInfosByPrefix(SITEMAP_PREFIX).stream()
        .filter(info -> info.lastModified().isBefore(beforeDateTime))
        .forEach(info -> portalBucket.delete(info.key()));
  }

  /**
   * Deletes sitemap files older than the given date
   *
   * @param documentationUnitIds the list of ids to put in the sitemap file
   * @param prefix the sitemap file prefix
   * @return sitemap xml content
   */
  public String generateSitemap(List<String> documentationUnitIds, String prefix) {
    List<Url> urls =
        documentationUnitIds.stream()
            .map(e -> new Url(String.format("%s%s/%s", baseUrl, prefix, e)))
            .toList();
    return marshal(new SitemapFile(urls));
  }

  /**
   * Generates sitemap index xml content
   *
   * @param size number of sitemap files
   * @param type the type of sitemap currently being generated
   * @return sitemap index xml content
   */
  public String generateIndexXml(int size, DocumentKind type) {
    List<Url> urls =
        IntStream.rangeClosed(1, size)
            .mapToObj(
                e ->
                    new Url(
                        String.format("%sv1/%s", baseUrl, getBatchSitemapPath(e, type)),
                        LocalDate.now()))
            .toList();
    return marshal(new SitemapIndex(urls));
  }

  private String marshal(Object sitemapFile) {
    try {
      JAXBContext context = JAXBContext.newInstance(sitemapFile.getClass());
      Marshaller mar = context.createMarshaller();
      mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      StringWriter sitemapFileContent = new StringWriter();
      mar.marshal(sitemapFile, sitemapFileContent);
      return sitemapFileContent.toString();
    } catch (JAXBException exception) {
      return "";
    }
  }
}
