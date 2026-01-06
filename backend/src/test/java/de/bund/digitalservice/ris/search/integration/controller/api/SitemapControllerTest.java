package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.config.ApiConfig.Paths;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import java.util.stream.Stream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class SitemapControllerTest extends ContainersIntegrationBase {
  @Autowired private MockMvc mockMvc;
  @Autowired private PortalBucket portalBucket;

  @ParameterizedTest
  @MethodSource("sitemapTestCases")
  void testSitemapXmlResponses(String url, String xmlContent) throws Exception {
    byte[] xmlBytes = xmlContent.getBytes();
    portalBucket.save(url.replace(Paths.SITEMAP + "/", "sitemaps/"), xmlContent);
    mockMvc
        .perform(get(url))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Type", "application/xml"))
        .andExpect(content().bytes(xmlBytes));
  }

  static Stream<Arguments> sitemapTestCases() {
    return Stream.of(
        Arguments.of(Paths.SITEMAP + "/case-law/index.xml", "<sitemapindex></sitemapindex>"),
        Arguments.of(Paths.SITEMAP + "/case-law/1.xml", "<urlset></urlset>"),
        Arguments.of(Paths.SITEMAP + "/literature/index.xml", "<sitemapindex></sitemapindex>"),
        Arguments.of(Paths.SITEMAP + "/literature/1.xml", "<urlset></urlset>"),
        Arguments.of(Paths.SITEMAP + "/norms/index.xml", "<sitemapindex></sitemapindex>"),
        Arguments.of(Paths.SITEMAP + "/norms/1.xml", "<urlset></urlset>"));
  }

  @ParameterizedTest
  @MethodSource("notFoundSitemapUrls")
  void testSitemapXmlNotFound(String url) throws Exception {
    mockMvc.perform(get(url)).andExpect(status().isNotFound());
  }

  static Stream<String> notFoundSitemapUrls() {
    return Stream.of(
        Paths.SITEMAP + "/norms/99.xml",
        Paths.SITEMAP + "/invalid/1.xml",
        Paths.SITEMAP + "/norms/invalid.xml");
  }
}
