package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.CaseLawTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import de.bund.digitalservice.ris.search.service.IndexAliasService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
@WithJwt("jwtTokens/ValidAccessToken.json")
class ExportControllerTest extends ContainersIntegrationBase {

  @Autowired private NormsRepository normsRepository;

  @Autowired private CaseLawRepository caseLawRepository;

  @Autowired private IndexAliasService indexAliasService;

  @Autowired private MockMvc mockMvc;

  @BeforeEach
  void setUpSearchControllerApiTest() throws IOException {
    assertTrue(openSearchContainer.isRunning());

    super.recreateIndex();
    super.updateMapping();

    indexAliasService.setIndexAlias();

    caseLawRepository.saveAll(CaseLawTestData.allDocuments);
    normsRepository.saveAll(NormsTestData.allDocuments);
  }

  @Test
  void csvExportWithQueryAndWithoutFieldsShouldWork() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.EXPORT_ADVANCED_SEARCH + "/query?query=test")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void csvExportWithInvalidFieldsShouldGiveError() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.EXPORT_ADVANCED_SEARCH + "/query?query=test&field=foo")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(
            content()
                .json(
                    """
                        {
                          "errors": [
                            {
                              "code": "invalid_fields",
                              "parameter": "field",
                              "message": "Invalid fields to be included in the CSV export: [foo]"
                            }
                          ]
                        }
                    """));
  }
}
