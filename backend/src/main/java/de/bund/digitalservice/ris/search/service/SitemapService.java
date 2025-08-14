package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.models.sitemap.SitemapFile;
import de.bund.digitalservice.ris.search.models.sitemap.SitemapIndex;
import de.bund.digitalservice.ris.search.models.sitemap.Url;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SitemapService {
  @Value("${server.front-end-url}")
  private String baseUrl;

  private static final String NORMS_SITEMAP_PREFIX = "sitemaps/norms/";
  public final PortalBucket portalBucket;

  public String getNormsBatchSitemapPath(int batchNumber) {
    return NORMS_SITEMAP_PREFIX + String.format("%d.xml", batchNumber);
  }

  public String getNormsIndexSitemapPath() {
    return NORMS_SITEMAP_PREFIX + "index.xml";
  }

  public void createNormsBatchSitemap(int batchNumber, List<Norm> norms) {
    String path = this.getNormsBatchSitemapPath(batchNumber);
    this.portalBucket.save(path, this.generateNormsSitemap(norms));
  }

  public void createNormsIndexSitemap(int size) {
    String path = this.getNormsIndexSitemapPath();
    this.portalBucket.save(path, this.generateIndexXml(size));
  }

  public String generateNormsSitemap(List<Norm> norms) {
    List<Url> urls = new ArrayList<>();
    for (Norm norm : norms) {
      Url url = new Url();
      if (norm.getEntryIntoForceDate() != null
          && norm.getEntryIntoForceDate().isBefore(LocalDate.now())) {
        url.setLastmod(norm.getEntryIntoForceDate());
      }
      url.setLoc(String.format("%snorms/%s", baseUrl, norm.getExpressionEli()));
      urls.add(url);
    }
    SitemapFile sitemapFile = new SitemapFile();
    sitemapFile.setUrls(urls);
    return marshal(sitemapFile);
  }

  public String generateIndexXml(int size) {
    List<Url> urls = new ArrayList<>();
    for (int i = 1; i <= size; i++) {
      Url url = new Url();
      url.setLastmod(LocalDate.now());
      url.setLoc(String.format("%s%s", baseUrl, this.getNormsBatchSitemapPath(i)));
      urls.add(url);
    }
    SitemapIndex sitemapIndexFile = new SitemapIndex();
    sitemapIndexFile.setUrls(urls);
    return marshal(sitemapIndexFile);
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
