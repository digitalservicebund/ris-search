package de.bund.digitalservice.ris.search.unit.service.eclicrawler;

import static de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapService.MAX_SITEMAP_URLS;
import static de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapService.PATH_PREFIX;
import static de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapService.ROBOTS_TXT_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemap.Url;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemapindex.SitemapIndexEntry;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemapindex.Sitemapindex;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliMarshaller;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapService;
import jakarta.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EcliSitemapServiceTest {

  EcliSitemapService service;
  @Mock PortalBucket bucket;
  @Mock EcliMarshaller marshaller;

  @BeforeEach
  void setup() {
    service = new EcliSitemapService(bucket, marshaller);
  }

  @Test
  void itWritesDocumentsToSitemaps() throws JAXBException {

    List<Url> urls = new ArrayList<>();
    urls.add(new Url().setLoc("loc1"));
    urls.add(new Url().setLoc("loc2"));

    LocalDate day = LocalDate.of(2025, 1, 1);
    Sitemap sitemap = new Sitemap().setUrl(urls).setName("2025/01/01/sitemap_1.xml");
    when(marshaller.marshallSitemap(sitemap)).thenReturn("xmlContent");

    String expectedSitemapPath = "eclicrawler/2025/01/01/sitemap_1.xml";

    List<Sitemap> actualSitemaps = service.writeUrlsToSitemaps(day, urls);

    verify(bucket).save(expectedSitemapPath, "xmlContent");
    Assertions.assertEquals(1, actualSitemaps.size());

    List<Url> actualUrls = actualSitemaps.getFirst().getUrl();

    Assertions.assertEquals("loc1", actualUrls.getFirst().getLoc());
    Assertions.assertEquals("loc2", actualUrls.get(1).getLoc());
    Assertions.assertEquals("2025/01/01/sitemap_1.xml", actualSitemaps.getFirst().getName());
  }

  @Test
  void itPartitionsSitemapsAccordingToMaxEntries() throws JAXBException {
    List<Url> urls = new ArrayList<>();
    for (int i = 0; i <= MAX_SITEMAP_URLS + 1; i++) {
      urls.add(new Url().setLoc(String.valueOf(i)));
    }
    LocalDate day = LocalDate.of(2025, 1, 1);

    ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
    Mockito.doNothing().when(bucket).save(argument.capture(), any());

    List<Sitemap> sitemaps = service.writeUrlsToSitemaps(day, urls);

    List<String> values = argument.getAllValues();

    Assertions.assertTrue(values.contains(PATH_PREFIX + "2025/01/01/sitemap_1.xml"));
    Assertions.assertTrue(values.contains(PATH_PREFIX + "2025/01/01/sitemap_2.xml"));
    Assertions.assertEquals(2, sitemaps.size());
  }

  @Test
  void itReturnsSitemapFilesForAGivenDay() {
    LocalDate date = LocalDate.of(2025, 1, 1);
    when(bucket.getAllKeysByPrefix("eclicrawler/2025/01/01"))
        .thenReturn(List.of("eclicrawler/2025/01/01/sitemap_1.xml"));

    Assertions.assertEquals(
        "eclicrawler/2025/01/01/sitemap_1.xml",
        service.getSitemapFilesPathsForDay(date).getFirst());
  }

  @Test
  void itWritesARobotsTxt() {
    String expectedContent =
        """
                User-agent: *
                Disallow: /
                User-agent: DG_JUSTICE_CRAWLER
                Allow: /
                """;
    service.writeRobotsTxt();

    verify(bucket).save(ROBOTS_TXT_PATH, expectedContent);
  }

  @Test
  void itUpdatesAnExistingRobotsTxt() throws FileNotFoundException, ObjectStoreServiceException {
    String existingContent =
        """
                User-agent: DG_JUSTICE_CRAWLER
                Allow: /
                Sitemap:sitemapPath1""";
    String expectedContent =
        """
                User-agent: DG_JUSTICE_CRAWLER
                Allow: /
                Sitemap:sitemapPath1
                Sitemap:baseUrl/2025/01/01/sitemap_index_1.xml""";

    when(bucket.getFileAsString(ROBOTS_TXT_PATH)).thenReturn(Optional.of(existingContent));

    service.updateRobotsTxt(
        "baseUrl/", List.of(new Sitemapindex().setName("2025/01/01/sitemap_index_1.xml")));

    verify(bucket).save(ROBOTS_TXT_PATH, expectedContent);
  }

  @Test
  void itThrowsAnErrorOnUpdatingNonExistingRobotsTxt() throws ObjectStoreServiceException {
    when(bucket.getFileAsString(ROBOTS_TXT_PATH)).thenReturn(Optional.empty());
    Assertions.assertThrows(
        FileNotFoundException.class,
        () -> {
          service.updateRobotsTxt("baseUrl", List.of(new Sitemapindex()));
        });
  }

  @Test
  void itReturnsTheRobotsFile() throws ObjectStoreServiceException {
    var expectedContent = "Allow: *".getBytes();
    when(bucket.get(ROBOTS_TXT_PATH)).thenReturn(Optional.of(expectedContent));

    var result = service.getRobots();
    Assertions.assertEquals(expectedContent, result.orElseThrow());
  }

  @Test
  void itReturnsEmptyOnObjectStoreExceptionWhileGettingRobotsFile()
      throws ObjectStoreServiceException {
    when(bucket.get(ROBOTS_TXT_PATH)).thenThrow(ObjectStoreServiceException.class);

    var result = service.getRobots();
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void itReturnsASpecificEcliFile() throws ObjectStoreServiceException {
    String filename = "2025/01/01/sitemap_1.xml";
    Optional<byte[]> expectedContent = Optional.of("test".getBytes(StandardCharsets.UTF_16));

    when(bucket.get(PATH_PREFIX + filename)).thenReturn(expectedContent);

    var actual = service.getSitemapFile(filename);
    Assertions.assertEquals(expectedContent, actual);
  }

  @Test
  void itWritesSitemapIndices() throws JAXBException {
    String baseUrl = "http://base/";
    LocalDate date = LocalDate.of(2025, 1, 1);
    Sitemap sitemap = new Sitemap().setName("2025/01/01/sitemap_1.xml");
    Sitemapindex index = new Sitemapindex().setName("2025/01/01/sitemap_index_1.xml");
    SitemapIndexEntry indexEntry =
        new SitemapIndexEntry().setLoc(baseUrl + "2025/01/01/sitemap_1.xml");
    index.setSitemaps(List.of(indexEntry));

    when(marshaller.marshallSitemapIndex(index)).thenReturn("xmlContent");

    service.writeSitemapsIndices(baseUrl, date, List.of(sitemap));

    verify(bucket).save(PATH_PREFIX + "2025/01/01/sitemap_index_1.xml", "xmlContent");
  }

  @Test
  void itPartitionsSitemapIndicesAccordingToMaxEntries() throws JAXBException {
    List<Sitemap> sitemaps = new ArrayList<>();
    for (int i = 0; i <= MAX_SITEMAP_URLS + 1; i++) {
      sitemaps.add(new Sitemap().setName(String.valueOf(i)));
    }
    LocalDate day = LocalDate.of(2025, 1, 1);

    ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
    Mockito.doNothing().when(bucket).save(argument.capture(), any());

    List<Sitemapindex> indicesList = service.writeSitemapsIndices("url", day, sitemaps);

    List<String> values = argument.getAllValues();

    Assertions.assertTrue(values.contains(PATH_PREFIX + "2025/01/01/sitemap_index_1.xml"));
    Assertions.assertTrue(values.contains(PATH_PREFIX + "2025/01/01/sitemap_index_2.xml"));
    Assertions.assertEquals(2, indicesList.size());
  }
}
