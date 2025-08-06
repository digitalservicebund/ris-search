package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SitemapService {
  @Value("${server.front-end-url}")
  private String baseUrl;

  private final String NORMS_SITEMAPS_PREFIX = "sitemaps/norms/";

  public String getNormsBatchSitemapPath(int batchNumber) {
    return NORMS_SITEMAPS_PREFIX + String.format("%d.xml", batchNumber);
  }

  public String getNormsIndexSitemapPath() {
    return NORMS_SITEMAPS_PREFIX + "index.xml";
  }

  public void createNormsBatchSitemap(int batchNumber, List<Norm> norms, NormsBucket normsBucket)
      throws ObjectStoreServiceException {
    String path = this.getNormsBatchSitemapPath(batchNumber);
    try {
      normsBucket.putStream(path, this.generateNormsSitemap(norms));
    } catch (Exception e) {
      throw new ObjectStoreServiceException("Failed to create norms sitemap: " + path, e);
    }
  }

  public void createNormsIndexSitemap(int size, NormsBucket normsBucket)
      throws ObjectStoreServiceException {
    String path = this.getNormsIndexSitemapPath();
    try {
      normsBucket.putStream(path, this.generateIndexXml(size));
    } catch (Exception e) {
      throw new ObjectStoreServiceException("Failed to create norms index sitemap: " + path, e);
    }
  }

  public InputStream generateNormsSitemap(List<Norm> norms) {
    StringBuilder builder = new StringBuilder();
    builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    builder.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
    for (Norm norm : norms) {
      builder.append("  <url>\n");
      builder
          .append("    <loc>")
          .append(baseUrl + "norms/")
          .append(norm.getExpressionEli())
              .append("</loc>\n");
      if (norm.getDatePublished() != null) {
        builder.append("    <lastmod>").append(norm.getDatePublished()).append("</lastmod>\n");
      }
      builder.append("  </url>\n");
    }
    builder.append("</urlset>");
    return new ByteArrayInputStream(builder.toString().getBytes());
  }

  public InputStream generateIndexXml(int size) {
    StringBuilder builder = new StringBuilder();
    builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    builder.append("<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
    for (int i = 1; i <= size; i++) {
      builder
          .append("  <sitemap>\n")
          .append("    <loc>")
          .append(baseUrl + "api/v1/")
          .append(this.getNormsBatchSitemapPath(i))
          .append("</loc>\n")
          .append("    <lastmod>")
          .append(Instant.now().toString())
          .append("</lastmod>\n")
          .append("  </sitemap>\n");
    }
    builder.append("</sitemapindex>");
    return new ByteArrayInputStream(builder.toString().getBytes());
  }
}
