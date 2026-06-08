package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.service.BulkExportService;
import org.junit.jupiter.api.BeforeEach;
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

  private static final String caseLaw = DocumentKind.CASE_LAW.getBulkZipPath();
  private static final String legislation = DocumentKind.LEGISLATION.getBulkZipPath();
  private static final String literature = DocumentKind.LITERATURE.getBulkZipPath();
  private static final String admin = DocumentKind.ADMINISTRATIVE_DIRECTIVE.getBulkZipPath();

  @BeforeEach
  void setUpSearchControllerApiTest() {}

  @Test
  @DisplayName("Should return latest snapshot when 2 exist")
  void endpointShouldReturnCorrectCount() throws Exception {
    // Empty the bucket and add mock files. 2 for case law and 1 for the others. The content is not
    // relevant to test the links
    for (String key : publicFilesBucket.getAllKeys()) {
      publicFilesBucket.delete(key);
    }
    String bulkZipPattern = BulkExportService.BULK_ZIP_PREFIX + "%s_2026-01-%sT00:00:00.zip";
    publicFilesBucket.save(String.format(bulkZipPattern, caseLaw, "01"), "");
    publicFilesBucket.save(String.format(bulkZipPattern, caseLaw, "02"), "");
    publicFilesBucket.save(String.format(bulkZipPattern, legislation, "02"), "");
    publicFilesBucket.save(String.format(bulkZipPattern, literature, "02"), "");
    publicFilesBucket.save(String.format(bulkZipPattern, admin, "02"), "");

    String expectedPrefix = "https://object.storage.eu01.onstackit.cloud/public/snapshots/";

    DocumentContext json =
        JsonPath.parse(
            mockMvc
                .perform(
                    get(ApiConfig.Paths.BULK_ZIP_LINKS).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString());

    // Check that all document kinds have the correct link and caselaw picked the latest
    assertThat(json.read("$.case-law.link", String.class))
        .isEqualTo(expectedPrefix + "case-law_2026-01-02T00:00:00.zip");
    assertThat(json.read("$.legislation.link", String.class))
        .isEqualTo(expectedPrefix + "legislation_2026-01-02T00:00:00.zip");
    assertThat(json.read("$.literature.link", String.class))
        .isEqualTo(expectedPrefix + "literature_2026-01-02T00:00:00.zip");
    assertThat(json.read("$.administrative-directive.link", String.class))
        .isEqualTo(expectedPrefix + "administrative-directives_2026-01-02T00:00:00.zip");
  }
}
