package de.bund.digitalservice.ris.search.unit.service;

import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.service.SitemapService;
import java.io.IOException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SitemapServiceTest {
    private SitemapService sitemapService;
    private NormsBucket normsBucket;

    @BeforeEach
    void setUp() {
        sitemapService = new SitemapService();
        normsBucket = mock(NormsBucket.class);
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
    void testGenerateNormsSitemap() throws IOException {
        Norm norm = mock(Norm.class);
        when(norm.getExpressionEli()).thenReturn("eli/test/1");
        when(norm.getDatePublished()).thenReturn(LocalDate.parse("2025-08-06"));
        InputStream normSitemap = sitemapService.generateNormsSitemap(List.of(norm));
        String xml = new String(normSitemap.readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(xml.contains("<loc>https://test.local/norms/eli/test/1</loc>"));
        assertTrue(xml.contains("<lastmod>2025-08-06</lastmod>"));
    }

    @Test
    void testGenerateIndexXml() throws IOException {
        InputStream indexXml = sitemapService.generateIndexXml(2);
        String xml = new String(indexXml.readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(xml.contains("<loc>https://test.local/api/v1/sitemaps/norms/1.xml</loc>"));
        assertTrue(xml.contains("<loc>https://test.local/api/v1/sitemaps/norms/2.xml</loc>"));
        assertTrue(xml.contains("<sitemapindex"));
    }

    @Test
    void testCreateNormsBatchSitemap() throws Exception {
        Norm norm = mock(Norm.class);
        when(norm.getExpressionEli()).thenReturn("eli/test/1");
        when(norm.getDatePublished()).thenReturn(LocalDate.parse("2025-08-06"));
        doNothing().when(normsBucket).putStream(anyString(), any(InputStream.class));
        sitemapService.createNormsBatchSitemap(1, List.of(norm), normsBucket);
        verify(normsBucket).putStream(eq("sitemaps/norms/1.xml"), any(InputStream.class));
    }

    @Test
    void testCreateNormsIndexSitemap() throws Exception {
        doNothing().when(normsBucket).putStream(anyString(), any(InputStream.class));
        sitemapService.createNormsIndexSitemap(2, normsBucket);
        verify(normsBucket).putStream(eq("sitemaps/norms/index.xml"), any(InputStream.class));
    }

    @Test
    void testCreateNormsBatchSitemapThrowsException() throws Exception {
        Norm norm = mock(Norm.class);
        when(norm.getExpressionEli()).thenReturn("eli/test/1");
        when(norm.getDatePublished()).thenReturn(LocalDate.parse("2025-08-06"));
        doThrow(new RuntimeException("fail")).when(normsBucket).putStream(anyString(), any(InputStream.class));
        Exception ex = assertThrows(ObjectStoreServiceException.class, () ->
            sitemapService.createNormsBatchSitemap(1, List.of(norm), normsBucket));
        assertTrue(ex.getMessage().contains("Failed to create norms sitemap"));
    }

    @Test
    void testCreateNormsIndexSitemapThrowsException() throws Exception {
        doThrow(new RuntimeException("fail")).when(normsBucket).putStream(anyString(), any(InputStream.class));
        Exception ex = assertThrows(ObjectStoreServiceException.class, () ->
            sitemapService.createNormsIndexSitemap(2, normsBucket));
        assertTrue(ex.getMessage().contains("Failed to create norms index sitemap"));
    }
}

