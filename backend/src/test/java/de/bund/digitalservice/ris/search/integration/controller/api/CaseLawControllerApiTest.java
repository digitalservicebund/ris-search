package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWithIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.PublicationStatus;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawSynthesizedRepository;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.utils.CaseLawLdmlTemplateUtils;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
@WithJwt("jwtTokens/ValidAccessToken.json")
class CaseLawControllerApiTest extends ContainersIntegrationBase {
  @Autowired private IndexCaselawService indexCaselawService;
  @Autowired private CaseLawSynthesizedRepository caseLawSynthesizedRepository;
  @Autowired private MockMvc mockMvc;

  @Autowired
  @Qualifier("caseLawObjectStorage")
  private ObjectStorage caseLawBucket;

  private final CaseLawLdmlTemplateUtils caseLawLdmlTemplateUtils = new CaseLawLdmlTemplateUtils();

  @BeforeEach
  void setUpSearchControllerApiTest() throws IOException {
    assertTrue(openSearchContainer.isRunning());

    super.recreateIndex();
    super.updateMapping();

    LocalDate decisionDate = LocalDate.of(2023, 1, 2);

    Map<String, Object> context = new HashMap<>();
    context.put("documentNumber", "BFRE000107055");
    context.put("courtType", "FG");
    context.put("location", "Berlin");
    context.put("documentType", "Urteil");
    context.put("decisionDate", decisionDate.toString());
    context.put("motivation", "Das ist der Leitsatz");
    context.put("otherLongText", "Sonstiger Langtext");
    context.put("otherHeadnote", "Sonstiger Orientierungssatz");
    context.put("background", "Tatbestand");
    context.put("publicationStatus", PublicationStatus.UNPUBLISHED.toString());
    context.put("documentationOffice", "DS");
    context.put("error", false);

    String testCaseLawLdml = caseLawLdmlTemplateUtils.getXmlFromTemplate(context);
    caseLawBucket.save("BFRE000107055.xml", testCaseLawLdml);
  }

  @Test
  @DisplayName("Should return case law when using api endpoint with document number")
  void shouldReturnSingleCaselawJson() throws Exception {
    indexCaselawService.reindexAll(Instant.now().toString());

    assert caseLawSynthesizedRepository.findById("BFRE000107055").isPresent();

    mockMvc
        .perform(
            get(ApiConfig.Paths.CASELAW + "/BFRE000107055").contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.documentNumber", Matchers.is("BFRE000107055")),
            jsonPath("$.courtType", Matchers.is("FG")),
            jsonPath("$.location", Matchers.is("Berlin")),
            jsonPath("$.documentType", Matchers.is("Urteil")),
            jsonPath("$.decisionDate", Matchers.is("2023-01-02")),
            jsonPath("$.guidingPrinciple", Matchers.is("Das ist der Leitsatz")),
            jsonPath("$.otherLongText", Matchers.is("Sonstiger Langtext")),
            jsonPath("$.otherHeadnote", Matchers.is("Sonstiger Orientierungssatz")),
            jsonPath("$.caseFacts", Matchers.is("Tatbestand")),
            jsonPath("$.otherLongText", Matchers.is("Sonstiger Langtext")),
            jsonPath("$.publicationStatus").doesNotExist(),
            jsonPath("$.error").doesNotExist(),
            jsonPath("$.documentationOffice").doesNotExist(),
            jsonPath(
                "$.encoding[*]['@id']",
                containsInAnyOrder(
                    "/v1/case-law/BFRE000107055/html", "/v1/case-law/BFRE000107055/xml")),
            jsonPath(
                "$.encoding[*].contentUrl",
                containsInAnyOrder(
                    "/v1/case-law/BFRE000107055.html", "/v1/case-law/BFRE000107055.xml")));
  }

  @Test
  @DisplayName("Should return not found when using document number is not found")
  void shouldReturnNotFound() throws Exception {

    mockMvc
        .perform(get(ApiConfig.Paths.CASELAW + "/TEST").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return XML version of a decision")
  void textLegislationXMLEndpoint() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.CASELAW + "/BFRE000107055.xml")
                .contentType(MediaType.APPLICATION_XML))
        .andExpectAll(
            status().isOk(),
            content().string(startsWithIgnoringCase("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")),
            content().contentType("application/xml"));
  }

  @Test
  @DisplayName("Should return case law as html when using api endpoint with document number")
  void shouldReturnSingleCaselawHtml() throws Exception {

    String responseContent =
        mockMvc
            .perform(
                get(ApiConfig.Paths.CASELAW + "/BFRE000107055.html")
                    .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    assertThat(responseContent, containsString("Das ist der Leitsatz"));
    assertThat(responseContent, containsString("Sonstiger Orientierungssatz"));
    assertThat(responseContent, containsString("Tatbestand"));
  }

  @Test
  @DisplayName("Should return 404 when using document number is not found")
  void shouldReturn404() throws Exception {

    mockMvc
        .perform(get(ApiConfig.Paths.CASELAW + "/test.html").contentType(MediaType.TEXT_HTML))
        .andExpect(status().isNotFound());
  }
}
