package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.schema.ZipDataCatalogSchema;
import de.bund.digitalservice.ris.search.service.BulkExportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class BulkZipControllerTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private static final String CASE_LAW = DocumentKind.CASE_LAW.getBulkZipPath();
  private static final String LEGISLATION = DocumentKind.LEGISLATION.getBulkZipPath();
  private static final String LITERATURE = DocumentKind.LITERATURE.getBulkZipPath();
  private static final String ADMIN = DocumentKind.ADMINISTRATIVE_DIRECTIVE.getBulkZipPath();

  @Test
  @DisplayName("Should return latest snapshot when 2 exist")
  void endpointShouldReturnCorrectCount() throws Exception {
    // Empty the bucket and add mock files. 2 for case law and 1 for the others. The content is not
    // relevant to test the links
    for (String key : publicFilesBucket.getAllKeysOnCurrentVersion()) {
      publicFilesBucket.delete(key);
    }
    String bulkZipPattern = BulkExportService.BULK_ZIP_PREFIX + "%s_2026-01-%sT00:00:00.zip";
    publicFilesBucket.save(String.format(bulkZipPattern, CASE_LAW, "01"), "");
    publicFilesBucket.save(String.format(bulkZipPattern, CASE_LAW, "02"), "");
    publicFilesBucket.save(String.format(bulkZipPattern, LEGISLATION, "02"), "");
    publicFilesBucket.save(String.format(bulkZipPattern, LITERATURE, "02"), "");
    publicFilesBucket.save(String.format(bulkZipPattern, ADMIN, "02"), "");

    String expectedPrefix = "https://object.storage.eu01.onstackit.cloud/public/snapshots/";

    String responseJson =
        mockMvc
            .perform(get(ApiConfig.Paths.BULK_ZIP_LINKS).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Check that all document kinds have the correct link and case law picked the latest
    assertThat(
            objectMapper.readValue(responseJson, ZipDataCatalogSchema.class).dataSet().stream()
                .map(e -> e.distribution().contentUrl())
                .toList())
        .containsExactly(
            expectedPrefix + "administrative-directives_2026-01-02T00:00:00.zip",
            expectedPrefix + "case-law_2026-01-02T00:00:00.zip",
            expectedPrefix + "legislation_2026-01-02T00:00:00.zip",
            expectedPrefix + "literature_2026-01-02T00:00:00.zip");
  }
}
