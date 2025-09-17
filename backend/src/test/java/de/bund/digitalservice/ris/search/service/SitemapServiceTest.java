package de.bund.digitalservice.ris.search.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.models.sitemap.SitemapType;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectKeyInfo;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SitemapServiceTest {
  private static final String TEST_EXPRESSION_ELI =
      "eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/regelungstext-1";
  private SitemapService sitemapService;
  @Mock private PortalBucket portalBucket;

  @BeforeEach
  void setUp() {
    sitemapService = new SitemapService(portalBucket);
    ReflectionTestUtils.setField(sitemapService, "baseUrl", "https://test.local/");
  }

  @Test
  void testGetNormsBatchSitemapPath() {
    assertEquals("sitemaps/norms/1.xml", sitemapService.getBatchSitemapPath(1));
  }

  @Test
  void testGetNormsIndexSitemapPath() {
    assertEquals("sitemaps/norms/index.xml", sitemapService.getIndexSitemapPath());
  }

  @Test
  void testGenerateNormsSitemap() {
    ExpressionEli norm = ExpressionEli.fromString(TEST_EXPRESSION_ELI);
    String normSitemap = sitemapService.generateNormsSitemap(List.of(norm));
    assertTrue(
        normSitemap.contains("<loc>https://test.local/norms/" + TEST_EXPRESSION_ELI + "</loc>"));
  }

  @Test
  void testGenerateCaselawSitemap() {
    String path = "some/path/to/file/caselaw/KORE1.xml";
    String caselawSitemap = sitemapService.generateCaselawSitemap(List.of(path));
    assertTrue(caselawSitemap.contains("<loc>https://test.local/case-law/KORE1</loc>"));
  }

  @Test
  void testGenerateIndexXml() {
    String indexXml = sitemapService.generateIndexXml(2);
    assertTrue(indexXml.contains("<loc>https://test.local/sitemaps/norms/1.xml</loc>"));
    assertTrue(indexXml.contains("<loc>https://test.local/sitemaps/norms/2.xml</loc>"));
    assertTrue(indexXml.contains("<sitemapindex"));
    sitemapService.setSitemapType(SitemapType.CASELAW);
    indexXml = sitemapService.generateIndexXml(1);
    assertTrue(indexXml.contains("<loc>https://test.local/sitemaps/caselaw/1.xml</loc>"));
  }

  @Test
  void testCreateNormsBatchSitemap() {
    ExpressionEli norm = mock(ExpressionEli.class);
    sitemapService.createNormsBatchSitemap(1, List.of(norm));
    verify(portalBucket).save(eq("sitemaps/norms/1.xml"), anyString());
  }

  @Test
  void testCreateNormsIndexSitemap() {
    sitemapService.createIndexSitemap(2, SitemapType.NORMS);
    verify(portalBucket).save(eq("sitemaps/norms/index.xml"), anyString());
  }

  @Test
  void testCreateCaselawBatchSitemap() {
    sitemapService.createCaselawBatchSitemap(1, List.of("caselaw/KORE12315.xml"));
    verify(portalBucket).save(eq("sitemaps/caselaw/1.xml"), anyString());
  }

  @Test
  void testCreateCaselawIndexSitemap() {
    sitemapService.createIndexSitemap(2, SitemapType.CASELAW);
    verify(portalBucket).save(eq("sitemaps/caselaw/index.xml"), anyString());
  }

  @Test
  void testDeleteOldSitemapFiles() {
    Instant now = Instant.now();
    ObjectKeyInfo oldFile = new ObjectKeyInfo("sitemaps/norms/1.xml", now.minusSeconds(86400));
    ObjectKeyInfo nowFile = new ObjectKeyInfo("sitemaps/caselaw/2.xml", now);
    ObjectKeyInfo newFile = new ObjectKeyInfo("sitemaps/norms/3.xml", now.plusSeconds(60));

    when(portalBucket.getAllKeyInfosByPrefix("sitemaps/"))
        .thenReturn(List.of(oldFile, nowFile, newFile));

    sitemapService.deleteSitemapFiles(now);

    verify(portalBucket).getAllKeyInfosByPrefix("sitemaps/");
    verify(portalBucket).delete(oldFile.key());
    verify(portalBucket, never()).delete(newFile.key());
    verify(portalBucket, never()).delete(nowFile.key());
  }
}
