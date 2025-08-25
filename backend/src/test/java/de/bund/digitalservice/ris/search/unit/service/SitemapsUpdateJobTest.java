package de.bund.digitalservice.ris.search.unit.service;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.models.sitemap.SitemapType;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.SitemapService;
import de.bund.digitalservice.ris.search.service.SitemapsUpdateJob;
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
  @Mock NormsBucket normsBucket;
  @Mock SitemapService sitemapService;

  SitemapsUpdateJob sitemapsUpdateJob;

  @BeforeEach
  void setup() {
    sitemapsUpdateJob = new SitemapsUpdateJob(caseLawBucket, normsBucket, sitemapService);
    ReflectionTestUtils.setField(sitemapsUpdateJob, "urlsPerPage", 1);
  }

  @Test
  void SitemapsUpdateJobCallsSitemapServiceForBatchesAndIndex() throws Exception {
    List<String> normsKeys = new ArrayList<>();
    List<String> caselawKeys = new ArrayList<>();
    for (int i = 1; i < 3; i++) {
      normsKeys.add(
          "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/1992-01-02/regelungstext-" + i + ".xml");
      caselawKeys.add("case-law/KORE12354" + i + ".xml");
    }

    when(this.normsBucket.getAllKeysByPrefix(anyString())).thenReturn(normsKeys);
    when(this.caseLawBucket.getAllKeys()).thenReturn(caselawKeys);

    this.sitemapsUpdateJob.runJob();
    verify(this.sitemapService, times(2)).createNormsBatchSitemap(anyInt(), anyList());
    verify(this.sitemapService, times(2)).createCaselawBatchSitemap(anyInt(), anyList());
    verify(this.sitemapService, times(1)).createIndexSitemap(anyInt(), eq(SitemapType.NORMS));
    verify(this.sitemapService, times(1)).createIndexSitemap(anyInt(), eq(SitemapType.CASELAW));
  }
}
