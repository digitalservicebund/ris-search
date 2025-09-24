package de.bund.digitalservice.ris.search.unit.eclicrawler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.eclicrawler.controller.EcliSitemapController;
import de.bund.digitalservice.ris.search.eclicrawler.service.EcliSitemapService;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class EcliSitemapControllerTest {

  EcliSitemapController controller;

  @Mock EcliSitemapService service;

  @BeforeEach()
  void setup() {
    controller = new EcliSitemapController(service);
  }

  @Test
  void itRetrievesTheCorrectFile() {
    String expectedPath = "2025/01/01/sitemap_1.xml";
    byte[] expectedContent =
        """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset>
                   <url>
                      <loc>http://baseUrl/case-law/file</loc>
                   </url>
                </urlset>
                """
            .getBytes();

    when(service.getSitemapFile(expectedPath)).thenReturn(Optional.of(expectedContent));

    var response = controller.getEcliSitemapfiles("2025", "01", "01", "sitemap_1.xml");

    verify(service).getSitemapFile("2025/01/01/sitemap_1.xml");

    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
    Assertions.assertEquals(MediaType.APPLICATION_XML, response.getHeaders().getContentType());
    Assertions.assertEquals(expectedContent, response.getBody());
  }

  @Test
  void itReturns404OnNotFound() {
    String expectedPath = "2025/01/01/sitemap_1.xml";
    when(service.getSitemapFile(expectedPath)).thenReturn(Optional.empty());

    var response = controller.getEcliSitemapfiles("2025", "01", "01", "sitemap_1.xml");

    verify(service).getSitemapFile("2025/01/01/sitemap_1.xml");
    Assertions.assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
  }
}
