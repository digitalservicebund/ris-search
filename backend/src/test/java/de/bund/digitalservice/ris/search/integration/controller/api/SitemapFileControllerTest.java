package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import de.bund.digitalservice.ris.search.config.ApiConfig.Paths;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
@WithJwt("jwtTokens/ValidAccessToken.json")
class SitemapFileControllerTest extends ContainersIntegrationBase {
  @Autowired private MockMvc mockMvc;
  @Autowired private PortalBucket portalBucket;

  @Test
  void testGetNormsIndexSitemapXml() throws Exception {
    byte[] indexSitemap = "<sitemapindex></sitemapindex>".getBytes();
    portalBucket.save("sitemaps/norms/index.xml", new String(indexSitemap));
    mockMvc
        .perform(get(Paths.SITEMAP_NORMS + "/index.xml"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Type", "application/xml"))
        .andExpect(content().bytes(indexSitemap));
  }

  @Test
  void testGetNormsBatchSitemapXml() throws Exception {
    byte[] xml = "<urlset></urlset>".getBytes();
    portalBucket.save("sitemaps/norms/1.xml", new String(xml));
    mockMvc
        .perform(get(Paths.SITEMAP_NORMS + "/1.xml"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Type", "application/xml"))
        .andExpect(content().bytes(xml));
  }

  @Test
  void testGetNormsBatchSitemapXmlNotFound() throws Exception {
    mockMvc.perform(get(Paths.SITEMAP_NORMS + "/99.xml")).andExpect(status().isNotFound());
  }

  @Test
  void testGetNormsBatchSitemapXmlInvalidBatch() throws Exception {
    mockMvc.perform(get(Paths.SITEMAP_NORMS + "/invalid.xml")).andExpect(status().isNotFound());
  }
}
