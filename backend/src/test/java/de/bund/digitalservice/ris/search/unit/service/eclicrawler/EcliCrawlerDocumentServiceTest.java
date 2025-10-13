package de.bund.digitalservice.ris.search.unit.service.eclicrawler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.models.eclicrawler.ecli.Document;
import de.bund.digitalservice.ris.search.models.eclicrawler.ecli.IsVersionOf;
import de.bund.digitalservice.ris.search.models.eclicrawler.ecli.Metadata;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemap.Url;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemapindex.Sitemapindex;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.EcliCrawlerDocument;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.EcliCrawlerDocumentRepository;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliCrawlerDocumentService;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapWriter;
import jakarta.xml.bind.JAXBException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.apache.commons.collections4.IteratorUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EcliCrawlerDocumentServiceTest {

  @Mock EcliCrawlerDocumentRepository repository;
  @Mock CaseLawIndexSyncJob syncJob;
  @Mock CaseLawBucket caseLawBucket;
  @Mock CaseLawService caselawService;
  @Mock EcliSitemapWriter sitemapWriter;

  EcliCrawlerDocumentService documentService;

  String frontendUrl = "frontend-url/";

  @BeforeEach
  void setup() {
    documentService =
        new EcliCrawlerDocumentService(
            caseLawBucket, repository, caselawService, sitemapWriter, frontendUrl);
  }

  private CaseLawDocumentationUnit getTestDocUnit(String docNumber) {
    return CaseLawDocumentationUnit.builder()
        .documentNumber(docNumber)
        .ecli("ECLI:DE:XX:2025:1111111")
        .courtType("BGH")
        .decisionDate(LocalDate.of(2025, 1, 1))
        .build();
  }

  private EcliCrawlerDocument getTestDocument(String ecli, String docNumber, boolean isPublished) {
    return new EcliCrawlerDocument(
        docNumber,
        docNumber + ".xml",
        ecli,
        "BGH",
        "2025-01-01",
        "frontend-url/case-law/" + docNumber,
        isPublished);
  }

  @Test
  void itSplitsSitemapsByMaxNumberOfUrls() throws ObjectStoreServiceException, JAXBException {
    LocalDate day = LocalDate.of(2025, 1, 1);
    var filenames = IntStream.range(0, 51000).boxed().map(i -> "file_" + i + ".xml").toList();

    when(caseLawBucket.getAllKeys()).thenReturn(filenames);
    when(caselawService.getFromBucket(any())).thenReturn(Optional.of(getTestDocUnit("docNumber")));

    when(sitemapWriter.writeUrlsToSitemap(eq(day), any(), eq(1)))
        .thenReturn(new Sitemap().setName("1"));
    when(sitemapWriter.writeUrlsToSitemap(eq(day), any(), eq(2)))
        .thenReturn(new Sitemap().setName("2"));

    when(sitemapWriter.writeSitemapsIndices(eq("apiUrl"), eq(day), any()))
        .thenReturn(List.of(new Sitemapindex().setName("index")));

    documentService.writeFullDiff("apiUrl", day);
    InOrder orderVerify = inOrder(sitemapWriter);

    orderVerify.verify(sitemapWriter).writeUrlsToSitemap(eq(day), any(), eq(1));
    orderVerify.verify(sitemapWriter).writeUrlsToSitemap(eq(day), any(), eq(2));
    verify(sitemapWriter)
        .writeSitemapsIndices(
            eq("apiUrl"),
            eq(day),
            argThat(
                sitemaps ->
                    sitemaps.getFirst().getName().equals("1")
                        && sitemaps.get(1).getName().equals("2")));

    verify(repository, times(6)).saveAll(any());
    verify(sitemapWriter)
        .updateRobotsTxt(
            eq("apiUrl"),
            argThat(sitemapindices -> sitemapindices.getFirst().getName().equals("index")));
  }

  @Test
  void itWritesFilesFromAChangelog() throws ObjectStoreServiceException, JAXBException {
    LocalDate day = LocalDate.of(2025, 1, 1);
    String createdFilename = "createdDoc.xml";
    String deletedFilename = "deletedDoc.xml";
    Changelog changelog = new Changelog();
    changelog.setChanged(new HashSet<>(List.of(createdFilename)));
    changelog.setDeleted(new HashSet<>(List.of(deletedFilename)));

    var expectedCretedDocument = getTestDocument("ECLI:DE:XX:2025:1111111", "createdDoc", true);
    var expectedDeletedDocument = getTestDocument("ECLI:DE:XX:2025:1111112", "deletedDoc", false);

    when(caselawService.getFromBucket(createdFilename))
        .thenReturn(Optional.of(getTestDocUnit("createdDoc")));
    when(repository.findByFilenameIn(deletedFilename))
        .thenReturn(Optional.of(expectedDeletedDocument));

    Url expectedCreatedUrl =
        new Url()
            .setDocument(
                new Document()
                    .setMetadata(
                        new Metadata()
                            .setIsVersionOf(
                                new IsVersionOf().setValue("ECLI:DE:XX:2025:1111111"))));
    Url expectedDeletedUrl =
        new Url()
            .setDocument(
                new Document()
                    .setStatus(Document.STATUS_DELETED)
                    .setMetadata(
                        new Metadata()
                            .setIsVersionOf(
                                new IsVersionOf().setValue("ECLI:DE:XX:2025:1111112"))));

    when(sitemapWriter.writeUrlsToSitemap(
            eq(day),
            argThat(
                left ->
                    new UrlListMatcher(List.of(expectedCreatedUrl, expectedDeletedUrl))
                        .matches(left)),
            eq(1)))
        .thenReturn(new Sitemap().setName("sitemap_name"));

    var expectedSitemapIndex = List.of(new Sitemapindex());
    when(sitemapWriter.writeSitemapsIndices(
            eq("apiUrl"),
            eq(day),
            argThat(sitemaps -> sitemaps.getFirst().getName().equals("sitemap_name"))))
        .thenReturn(expectedSitemapIndex);

    documentService.writeFromChangelog("apiUrl", day, changelog);

    verify(repository)
        .saveAll(
            argThat(
                docs ->
                    new EcliCrawlerDocumentsMatcher(
                            List.of(expectedCretedDocument, expectedDeletedDocument))
                        .matches(IteratorUtils.toList(docs.iterator()))));
    verify(sitemapWriter).updateRobotsTxt("apiUrl", expectedSitemapIndex);
  }
}
