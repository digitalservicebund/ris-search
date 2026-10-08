package de.bund.digitalservice.ris.search.jobs;

import static de.bund.digitalservice.ris.ZipTestUtils.readZipStream;
import static de.bund.digitalservice.ris.search.service.BulkExportService.BULK_ZIP_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.service.BulkExportService;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CaseLawBulkExportIntegrationTest extends ContainersIntegrationBase {

  @Qualifier("caseLawBulkExportService")
  @Autowired
  BulkExportService caseLawBulkExportService;

  @BeforeEach
  void setUp() {
    clearBuckets();
  }

  @Test
  void updateLatestZip_excludesGeneratedRenderings() throws IOException, NoSuchKeyException {
    caseLawBucket.save("KORE1/KORE1.xml", "<xml/>");
    caseLawBucket.save("KORE1/KORE1.pdf", "pdf");
    caseLawBucket.save("KORE1/KORE1.html", "html");
    caseLawBucket.save("KORE1/image.png", "png");

    assertThat(caseLawBulkExportService.updateLatestZip(Instant.now())).isTrue();

    List<String> archives =
        publicFilesBucket.getAllKeysByPrefix(
            BULK_ZIP_PREFIX + DocumentKind.CASE_LAW.getBulkZipPath());
    assertThat(archives).hasSize(1);
    try (InputStream zip = publicFilesBucket.getStream(archives.getFirst())) {
      assertThat(readZipStream(zip)).containsOnlyKeys("KORE1/KORE1.xml", "KORE1/image.png");
    }
  }
}
