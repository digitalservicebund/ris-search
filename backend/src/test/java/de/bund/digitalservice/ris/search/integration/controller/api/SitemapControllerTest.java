package de.bund.digitalservice.ris.search.integration.controller.api;

import de.bund.digitalservice.ris.search.controller.api.SitemapController;
import de.bund.digitalservice.ris.search.service.SitemapService;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SitemapController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class SitemapControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private NormsBucket normsBucket;
    private SitemapService sitemapService;

    @BeforeEach
    void setUp() {
        normsBucket = mock(NormsBucket.class);
        sitemapService = mock(SitemapService.class);
    }

    @Test
    void testGetNormsIndexSitemapXml() throws Exception {
        byte[] xml = "<sitemapindex></sitemapindex>".getBytes();
        when(normsBucket.get(anyString())).thenReturn(Optional.of(xml));
        when(sitemapService.getNormsIndexSitemapPath()).thenReturn("sitemaps/norms/index.xml");
        mockMvc.perform(get("/v1/sitemaps/norms/index.xml"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/xml"))
                .andExpect(content().bytes(xml));
    }

    @Test
    void testGetNormsBatchSitemapXml() throws Exception {
        byte[] xml = "<urlset></urlset>".getBytes();
        when(normsBucket.get(anyString())).thenReturn(Optional.of(xml));
        when(sitemapService.getNormsBatchSitemapPath(1)).thenReturn("sitemaps/norms/1.xml");
        mockMvc.perform(get("/v1/sitemaps/norms/1.xml"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/xml"))
                .andExpect(content().bytes(xml));
    }

    @Test
    void testGetNormsBatchSitemapXmlNotFound() throws Exception {
        when(normsBucket.get(anyString())).thenReturn(Optional.empty());
        when(sitemapService.getNormsBatchSitemapPath(99)).thenReturn("sitemaps/norms/99.xml");
        mockMvc.perform(get("/v1/sitemaps/norms/99.xml"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetNormsBatchSitemapXmlInvalidBatch() throws Exception {
        mockMvc.perform(get("/v1/sitemaps/norms/invalid.xml"))
                .andExpect(status().isNotFound());
    }
}

