package de.bund.digitalservice.ris.search.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.SitemapService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SitemapServiceTest {
  private SitemapService sitemapService;
  @Mock private PortalBucket portalBucket;

  @BeforeEach
  void setUp() {
    sitemapService = new SitemapService(portalBucket);
    ReflectionTestUtils.setField(sitemapService, "baseUrl", "https://test.local/");
  }

  @Test
  void testGetNormsBatchSitemapPath() {
    assertEquals("sitemaps/norms/1.xml", sitemapService.getNormsBatchSitemapPath(1));
  }

  @Test
  void testGetNormsIndexSitemapPath() {
    assertEquals("sitemaps/norms/index.xml", sitemapService.getNormsIndexSitemapPath());
  }

  @Test
  void testGenerateNormsSitemap() {
    Norm norm = mock(Norm.class);
    when(norm.getExpressionEli()).thenReturn("eli/test/1");
    when(norm.getEntryIntoForceDate()).thenReturn(LocalDate.parse("2025-08-06"));
    String normSitemap = sitemapService.generateNormsSitemap(List.of(norm));
    assertTrue(normSitemap.contains("<loc>https://test.local/norms/eli/test/1</loc>"));
    assertTrue(normSitemap.contains("<lastmod>2025-08-06</lastmod>"));
  }

  @Test
  void testGenerateIndexXml() {
    String indexXml = sitemapService.generateIndexXml(2);
    assertTrue(indexXml.contains("<loc>https://test.local/sitemaps/norms/1.xml</loc>"));
    assertTrue(indexXml.contains("<loc>https://test.local/sitemaps/norms/2.xml</loc>"));
    assertTrue(indexXml.contains("<sitemapindex"));
  }

  @Test
  void testCreateNormsBatchSitemap() {
    Norm norm = mock(Norm.class);
    when(norm.getExpressionEli()).thenReturn("eli/test/1");
    when(norm.getEntryIntoForceDate()).thenReturn(LocalDate.parse("2025-08-06"));
    sitemapService.createNormsBatchSitemap(1, List.of(norm));
    verify(portalBucket).save(eq("sitemaps/norms/1.xml"), anyString());
  }

  @Test
  void testCreateNormsIndexSitemap() {
    sitemapService.createNormsIndexSitemap(2);
    verify(portalBucket).save(eq("sitemaps/norms/index.xml"), anyString());
  }
}
