package de.bund.digitalservice.ris.search.jobs;

import static de.bund.digitalservice.ris.ZipTestUtils.readZipStream;
import static de.bund.digitalservice.ris.search.service.BulkExportService.BULK_ZIP_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import de.bund.digitalservice.ris.search.service.BulkExportService;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class LiteratureBulkExportIntegrationTest extends ContainersIntegrationBase {

  @Qualifier("literatureBulkExportService")
  @Autowired
  BulkExportService literatureBulkExportService;

  @Autowired LiteratureBucket literatureBucket;

  @Test
  void updateLatestZip_includesOnlyXmlDocumentsAndAttachments()
      throws IOException, NoSuchKeyException {
    literatureBucket.save("XXLS000000001/XXLS000000001.akn.xml", "<xml/>");
    literatureBucket.save("XXLS000000002/XXLS000000002.akn.pdf", "pdf");
    literatureBucket.save("XXLS000000002/XXLS000000002.akn.html", "html");

    assertThat(literatureBulkExportService.updateLatestZip(Instant.now())).isTrue();

    List<String> archives =
        publicFilesBucket.getAllKeysByPrefix(
            BULK_ZIP_PREFIX + DocumentKind.LITERATURE.getBulkZipPath());
    assertThat(archives).hasSize(1);
    try (InputStream zip = publicFilesBucket.getStream(archives.getFirst())) {
      assertThat(readZipStream(zip)).containsOnlyKeys("XXLS000000001/XXLS000000001.akn.xml");
    }
  }
}
