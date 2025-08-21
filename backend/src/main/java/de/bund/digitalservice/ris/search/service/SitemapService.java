package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.sitemap.SitemapFile;
import de.bund.digitalservice.ris.search.models.sitemap.SitemapIndex;
import de.bund.digitalservice.ris.search.models.sitemap.SitemapType;
import de.bund.digitalservice.ris.search.models.sitemap.Url;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SitemapService {
  @Value("${server.front-end-url}")
  private String baseUrl;

  @Setter private SitemapType sitemapType = SitemapType.NORMS;

  private static final String SITEMAP_PREFIX = "sitemaps/";
  public final PortalBucket portalBucket;

  public String getBatchSitemapPath(int batchNumber) {
    return SITEMAP_PREFIX
        + String.format("%s/%d.xml", this.sitemapType.name().toLowerCase(), batchNumber);
  }

  public String getIndexSitemapPath() {
    return SITEMAP_PREFIX + String.format("%s/index.xml", this.sitemapType.name().toLowerCase());
  }

  public void createIndexSitemap(int size, SitemapType type) {
    this.setSitemapType(type);
    String path = this.getIndexSitemapPath();
    this.portalBucket.save(path, this.generateIndexXml(size));
  }

  public void createNormsBatchSitemap(int batchNumber, List<ExpressionEli> norms) {
    this.setSitemapType(SitemapType.NORMS);
    String path = this.getBatchSitemapPath(batchNumber);
    this.portalBucket.save(path, this.generateNormsSitemap(norms));
  }

  public void createCaselawBatchSitemap(int batchNumber, List<String> paths) {
    this.setSitemapType(SitemapType.CASELAW);
    String path = this.getBatchSitemapPath(batchNumber);
    this.portalBucket.save(path, this.generateCaselawSitemap(paths));
  }

  private String generateSitemap(List<?> items, Function<Object, Url> urlMapper) {
    List<Url> urls = new ArrayList<>();
    for (Object item : items) {
      urls.add(urlMapper.apply(item));
    }
    SitemapFile sitemapFile = new SitemapFile();
    sitemapFile.setUrls(urls);
    return marshal(sitemapFile);
  }

  public String generateNormsSitemap(List<ExpressionEli> norms) {
    return generateSitemap(
        norms,
        item -> {
          ExpressionEli norm = (ExpressionEli) item;
          Url url = new Url();
          url.setLoc(String.format("%snorms/%s", baseUrl, norm));
          return url;
        });
  }

  public String generateCaselawSitemap(List<String> paths) {
    List<String> documentNumbers =
        paths.stream()
            .map(path -> path.substring(path.lastIndexOf("/") + 1, path.length() - 4))
            .toList();
    return generateSitemap(
        documentNumbers,
        item -> {
          String documentNumber = (String) item;
          Url url = new Url();
          url.setLoc(String.format("%scase-law/%s", baseUrl, documentNumber));
          return url;
        });
  }

  public String generateIndexXml(int size) {
    List<Url> urls = new ArrayList<>();
    for (int i = 1; i <= size; i++) {
      Url url = new Url();
      url.setLastmod(LocalDate.now());
      url.setLoc(String.format("%s%s", baseUrl, this.getBatchSitemapPath(i)));
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
