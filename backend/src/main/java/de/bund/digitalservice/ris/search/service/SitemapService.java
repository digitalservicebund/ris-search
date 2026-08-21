package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.config.ApiConfig;
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
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Service for generating sitemaps. */
@Service
public class SitemapService {
  private final String baseUrl;

  /**
   * Service for generating sitemaps.
   *
   * @param portalBucket PortalBucket
   * @param baseUrl baseUrl of frontend application to reference changed sites
   */
  public SitemapService(
      PortalBucket portalBucket, @Value("${server.front-end-url}") String baseUrl) {
    this.portalBucket = portalBucket;
    // remove trailing / to cleanly concatenate with api paths
    this.baseUrl = Strings.CS.removeEnd(baseUrl, "/");
  }

  private static final String PORTAL_BUCKET_SITEMAP_PREFIX = "sitemaps/";
  public final PortalBucket portalBucket;

  private String getControllerPath(DocumentKind type) {
    return switch (type) {
      case LEGISLATION -> ApiConfig.Paths.LEGISLATION_SITEMAPS;
      case ADMINISTRATIVE_DIRECTIVE -> ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE_SITEMAPS;
      case LITERATURE -> ApiConfig.Paths.LITERATURE_SITEMAPS;
      case CASE_LAW -> ApiConfig.Paths.CASELAW_SITEMAPS;
    };
  }

  private String getFrontendPath(DocumentKind type) {
    return switch (type) {
      case LEGISLATION -> "gesetze";
      case ADMINISTRATIVE_DIRECTIVE -> "verwaltungsregelungen";
      case LITERATURE -> "literaturnachweise";
      case CASE_LAW -> "gerichtsentscheidungen";
    };
  }

  private String getS3Path(DocumentKind type) {
    return switch (type) {
      case LEGISLATION -> "norms";
      case ADMINISTRATIVE_DIRECTIVE -> "administrative-directive";
      case LITERATURE -> "literature";
      case CASE_LAW -> "case-law";
    };
  }

  /**
   * Returns the path of a sitemap file for a given batch number
   *
   * @param batchNumber batch number
   * @param type the type of sitemap currently being generated
   * @return sitemap file path
   */
  public String getBatchSitemapS3Path(int batchNumber, DocumentKind type) {
    return PORTAL_BUCKET_SITEMAP_PREFIX + String.format("%s/%d.xml", getS3Path(type), batchNumber);
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
    String path = getBatchSitemapS3Path(batchNumber, docKind);
    portalBucket.save(path, generateSitemap(ids, docKind));
  }

  /**
   * Returns the path of the sitemap index file
   *
   * @param type the type of sitemap currently being generated
   * @return sitemap index file path
   */
  public String getIndexSitemapPath(DocumentKind type) {
    return PORTAL_BUCKET_SITEMAP_PREFIX + String.format("%s/index.xml", getS3Path(type));
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
    portalBucket.getAllKeyInfosByPrefix(PORTAL_BUCKET_SITEMAP_PREFIX).stream()
        .filter(info -> info.lastModified().isBefore(beforeDateTime))
        .forEach(info -> portalBucket.delete(info.key()));
  }

  /**
   * Deletes sitemap files older than the given date
   *
   * @param documentationUnitIds the list of ids to put in the sitemap file
   * @param documentKind the DocumentKind the sitemap is generated for
   * @return sitemap xml content
   */
  public String generateSitemap(List<String> documentationUnitIds, DocumentKind documentKind) {
    List<Url> urls =
        documentationUnitIds.stream()
            .map(e -> new Url(String.format("%s/%s/%s", baseUrl, getFrontendPath(documentKind), e)))
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
                batch ->
                    new Url(
                        String.format("%s%s/%s.xml", baseUrl, getControllerPath(type), batch),
                        LocalDate.now(ZoneOffset.UTC)))
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
    } catch (JAXBException _) {
      return "";
    }
  }
}
