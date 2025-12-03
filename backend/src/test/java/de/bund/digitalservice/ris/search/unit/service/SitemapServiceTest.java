package de.bund.digitalservice.ris.search.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectKeyInfo;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.SitemapService;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SitemapServiceTest {
  private static final String TEST_ELI_FILE =
      "eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-20/regelungstext-1.xml";
  private SitemapService sitemapService;
  @Mock private PortalBucket portalBucket;

  @BeforeEach
  void setUp() {
    sitemapService = new SitemapService(portalBucket);
    ReflectionTestUtils.setField(sitemapService, "baseUrl", "https://test.local/");
  }

  @Test
  void testGenerateIndexXml() {
    String indexXml = sitemapService.generateIndexXml(2, DocumentKind.LEGISLATION);
    assertTrue(indexXml.contains("<loc>https://test.local/v1/sitemaps/norms/1.xml</loc>"));
    assertTrue(indexXml.contains("<loc>https://test.local/v1/sitemaps/norms/2.xml</loc>"));
    assertTrue(indexXml.contains("<sitemapindex"));
    indexXml = sitemapService.generateIndexXml(1, DocumentKind.CASE_LAW);
    assertTrue(indexXml.contains("<loc>https://test.local/v1/sitemaps/case-law/1.xml</loc>"));
  }

  @Test
  void testGenerateCaseLawSitemap() {
    String caseLawSitemap = sitemapService.generateSitemap(List.of("KORE1"), "case-law");
    assertTrue(caseLawSitemap.contains("<loc>https://test.local/case-law/KORE1</loc>"));
  }

  @Test
  void testCreateCaseLawBatchSitemap() {
    sitemapService.createBatchSitemap(
        1, List.of("caselaw/KORE12315.xml"), DocumentKind.CASE_LAW, "case-law");
    verify(portalBucket).save(eq("sitemaps/case-law/1.xml"), anyString());
  }

  @Test
  void testCreateCaseLawIndexSitemap() {
    sitemapService.createIndexSitemap(2, DocumentKind.CASE_LAW);
    verify(portalBucket).save(eq("sitemaps/case-law/index.xml"), anyString());
  }

  @Test
  void testGenerateLiteratureSitemap() {
    String caseLawSitemap = sitemapService.generateSitemap(List.of("XXLU000001"), "literature");
    assertTrue(caseLawSitemap.contains("<loc>https://test.local/literature/XXLU000001</loc>"));
  }

  @Test
  void testCreateLiteratureBatchSitemap() {
    sitemapService.createBatchSitemap(
        1, List.of("literature/XXLU000001.xml"), DocumentKind.LITERATURE, "literature");
    verify(portalBucket).save(eq("sitemaps/literature/1.xml"), anyString());
  }

  @Test
  void testCreateLiteratureIndexSitemap() {
    sitemapService.createIndexSitemap(2, DocumentKind.LITERATURE);
    verify(portalBucket).save(eq("sitemaps/literature/index.xml"), anyString());
  }

  @Test
  void testGetNormsBatchSitemapPath() {
    assertEquals(
        "sitemaps/norms/1.xml", sitemapService.getBatchSitemapPath(1, DocumentKind.LEGISLATION));
  }

  @Test
  void testGetNormsIndexSitemapPath() {
    assertEquals(
        "sitemaps/norms/index.xml", sitemapService.getIndexSitemapPath(DocumentKind.LEGISLATION));
  }

  @Test
  void testGenerateNormsSitemap() {
    Optional<EliFile> file = EliFile.fromString(TEST_ELI_FILE);
    assertTrue(file.isPresent());
    String id = file.get().getExpressionEli().toString();
    String normSitemap = sitemapService.generateSitemap(List.of(id), "norms");
    assertTrue(normSitemap.contains("<loc>https://test.local/norms/" + id + "</loc>"));
  }

  @Test
  void testCreateNormsBatchSitemap() {
    sitemapService.createBatchSitemap(1, List.of(""), DocumentKind.LEGISLATION, "norms");
    verify(portalBucket).save(eq("sitemaps/norms/1.xml"), anyString());
  }

  @Test
  void testCreateNormsIndexSitemap() {
    sitemapService.createIndexSitemap(2, DocumentKind.LEGISLATION);
    verify(portalBucket).save(eq("sitemaps/norms/index.xml"), anyString());
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
