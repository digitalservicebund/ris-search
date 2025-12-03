package de.bund.digitalservice.ris.search.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.SitemapService;
import de.bund.digitalservice.ris.search.service.SitemapsUpdateJob;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
class SitemapsUpdateJobTest {

  @Mock CaseLawBucket caseLawBucket;
  @Mock LiteratureBucket literatureBucket;
  @Mock NormsBucket normsBucket;
  @Mock SitemapService sitemapService;

  SitemapsUpdateJob sitemapsUpdateJob;

  @BeforeEach
  void setup() {
    sitemapsUpdateJob =
        new SitemapsUpdateJob(caseLawBucket, literatureBucket, normsBucket, sitemapService);
    ReflectionTestUtils.setField(sitemapsUpdateJob, "urlsPerPage", 1);
  }

  @Test
  void sitemapsUpdateJobCallsSitemapServiceForBatchesAndIndex() {
    List<String> caseLawKeys = new ArrayList<>();
    List<String> literatureKeys = new ArrayList<>();
    List<String> normsKeys = new ArrayList<>();
    for (int i = 1; i < 3; i++) {
      caseLawKeys.add("case-law/KORE12354" + i + ".xml");
      literatureKeys.add("literature/XXLU00000" + i + ".akn.xml");
      normsKeys.add(
          "eli/bund/bgbl-1/1992/s101-"
              + i
              + "/1992-01-01/1/deu/1992-01-02/regelungstext-verkuendung-1.xml");
    }

    when(caseLawBucket.getAllKeys()).thenReturn(caseLawKeys);
    when(literatureBucket.getAllKeys()).thenReturn(literatureKeys);
    when(normsBucket.getAllKeys()).thenReturn(normsKeys);

    sitemapsUpdateJob.runJob();

    verify(sitemapService, times(6)).createBatchSitemap(anyInt(), anyList(), any(), anyString());

    verify(sitemapService, times(2))
        .createBatchSitemap(anyInt(), anyList(), eq(DocumentKind.CASE_LAW), anyString());
    verify(sitemapService, times(1)).createIndexSitemap(anyInt(), eq(DocumentKind.CASE_LAW));

    verify(sitemapService, times(2))
        .createBatchSitemap(anyInt(), anyList(), eq(DocumentKind.LITERATURE), anyString());
    verify(sitemapService, times(1)).createIndexSitemap(anyInt(), eq(DocumentKind.LITERATURE));

    verify(sitemapService, times(2))
        .createBatchSitemap(anyInt(), anyList(), eq(DocumentKind.LEGISLATION), anyString());
    verify(sitemapService, times(1)).createIndexSitemap(anyInt(), eq(DocumentKind.LEGISLATION));

    verify(sitemapService, times(1)).deleteSitemapFiles(any(Instant.class));
  }

  @Test
  void invalidFilesDoNotProduceSitemapFiles() {
    // simulate an image with no parent xml
    List<String> caselawKeys = List.of("case-law/KORE12354.png");

    // simulate an image with no parent xml
    List<String> literatureKeys = List.of("literature/XXLU000001.png");

    // these files don't follow a valid eli structure
    List<String> normsKeys =
        List.of(
            "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/1992-01-02/NOT_VALID/regelungstext-1.xml",
            "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/1992-01-02/NOT_VALID/something-else.xml");

    when(caseLawBucket.getAllKeys()).thenReturn(caselawKeys);
    when(literatureBucket.getAllKeys()).thenReturn(literatureKeys);
    when(normsBucket.getAllKeys()).thenReturn(normsKeys);

    sitemapsUpdateJob.runJob();

    verify(sitemapService, times(0)).createBatchSitemap(anyInt(), anyList(), any(), anyString());
    verify(sitemapService, times(3)).createIndexSitemap(anyInt(), any());
  }

  @Test
  void createSitemapsForNorms_filtersToOnlyNormFileContent() {
    List<String> normsKeys =
        List.of(
            "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/1992-01-02/regelungstext-verkuendung-1.xml",
            "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/1992-01-02/regelungstext-2.xml",
            "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/1992-01-02/anlage-regelungstext-1.xml");

    when(normsBucket.getAllKeys()).thenReturn(normsKeys);

    sitemapsUpdateJob.createSitemaps(normsBucket, DocumentKind.LEGISLATION, "norms");

    verify(sitemapService, times(1)).createBatchSitemap(anyInt(), anyList(), any(), anyString());
    verify(sitemapService, times(1))
        .createBatchSitemap(anyInt(), anyList(), eq(DocumentKind.LEGISLATION), anyString());
    verify(sitemapService, times(1)).createIndexSitemap(1, DocumentKind.LEGISLATION);
  }

  @Test
  void createSitemapsForNorms_deduplicatesSameExpressionFromMultipleManifestations() {
    List<String> normsKeys =
        List.of(
            "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/1992-01-02/regelungstext-verkuendung-1.xml",
            "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/1992-03-10/regelungstext-verkuendung-1.xml");

    when(normsBucket.getAllKeys()).thenReturn(normsKeys);

    sitemapsUpdateJob.createSitemaps(normsBucket, DocumentKind.LEGISLATION, "norms");

    verify(sitemapService, times(1)).createBatchSitemap(anyInt(), anyList(), any(), anyString());
    verify(sitemapService, times(1))
        .createBatchSitemap(anyInt(), anyList(), eq(DocumentKind.LEGISLATION), anyString());
    verify(sitemapService, times(1)).createIndexSitemap(anyInt(), any());
    verify(sitemapService, times(1)).createIndexSitemap(1, DocumentKind.LEGISLATION);
  }
}
