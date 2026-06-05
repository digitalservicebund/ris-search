package de.bund.digitalservice.ris.search.integration.controller.api;

import static de.bund.digitalservice.ris.ZipTestUtils.readZipStream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWithIgnoringCase;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.SharedTestConstants;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.PublicationStatus;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.utils.CaseLawLdmlTemplateUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.opensearch.core.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class CaseLawControllerApiTest extends ContainersIntegrationBase {
  @Autowired private IndexCaselawService indexCaselawService;
  @Autowired private MockMvc mockMvc;
  @Autowired private CaseLawBucket bucket;
  private final CaseLawLdmlTemplateUtils caseLawLdmlTemplateUtils = new CaseLawLdmlTemplateUtils();
  private final String documentNumber = "BFRE000107055";

  private String createTestCaseLawLdml() throws IOException {
    LocalDate decisionDate = LocalDate.of(2023, Month.JANUARY, 2);

    Map<String, Object> context = new HashMap<>();
    context.put("documentNumber", this.documentNumber);
    context.put("courtType", "FG");
    context.put("location", "Berlin");
    context.put("documentType", "Urteil");
    context.put("decisionDate", decisionDate.toString());
    context.put("leitsatz", "Das ist der Leitsatz");
    context.put("otherLongText", "Sonstiger Langtext");
    context.put("otherHeadnote", "Sonstiger Orientierungssatz");
    context.put("publicationStatus", PublicationStatus.UNPUBLISHED.toString());
    context.put(
        "background",
        """
                <akn:hcontainer ris:domainTerm="Randnummer" eId="randnummer-1" name="Randnummer">
                    <akn:num>1</akn:num>
                    <akn:content>
                        <akn:p>Example Tatbestand/CaseFacts. More background</akn:p>
                    </akn:content>
                </akn:hcontainer>
                <akn:p style="text-align:center">
                    <akn:img src="Attachment.png" alt="Abbildung"/>
                </akn:p>
    """);

    return caseLawLdmlTemplateUtils.getXmlFromTemplate(context);
  }

  private String getResourcePath(String fileName, String extension) {
    return ApiConfig.Paths.CASELAW + "/" + fileName + "." + extension;
  }

  private String getResourcePath(String extension) {
    return getResourcePath(this.documentNumber, extension);
  }

  @BeforeEach
  void setUpSearchControllerApiTest() throws IOException {
    clearRepositoryData();
    String testCaseLawLdml = createTestCaseLawLdml();
    caseLawBucket.save(this.documentNumber + "/" + this.documentNumber + ".xml", testCaseLawLdml);
    caseLawBucket.save(this.documentNumber + "/Attachment.png", "picture");
  }

  @Test
  @DisplayName("Should return case law when using api endpoint with document number")
  void shouldReturnSingleCaselawJson() throws Exception {
    indexCaselawService.reindexAll(SharedTestConstants.TIMESTAMP_2024_01_01_AS_STRING);

    mockMvc
        .perform(
            get(ApiConfig.Paths.CASELAW + "/" + this.documentNumber)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.documentNumber", Matchers.is(this.documentNumber)),
            jsonPath("$.courtType", Matchers.is("FG")),
            jsonPath("$.location", Matchers.is("Berlin")),
            jsonPath("$.documentType", Matchers.is("Urteil")),
            jsonPath("$.decisionDate", Matchers.is("2023-01-02")),
            jsonPath("$.guidingPrinciple", Matchers.is("Das ist der Leitsatz")),
            jsonPath("$.otherLongText", Matchers.is("Sonstiger Langtext")),
            jsonPath("$.otherHeadnote", Matchers.is("Sonstiger Orientierungssatz")),
            jsonPath("$.caseFacts", Matchers.is("Example Tatbestand/CaseFacts. More background")),
            jsonPath("$.otherLongText", Matchers.is("Sonstiger Langtext")),
            jsonPath("$.publicationStatus").doesNotExist(),
            jsonPath("$.error").doesNotExist(),
            jsonPath("$.documentationOffice").doesNotExist(),
            jsonPath(
                "$.encoding[*]['@id']",
                containsInAnyOrder(
                    "/v1/case-law/" + this.documentNumber + "/html",
                    "/v1/case-law/" + this.documentNumber + "/xml",
                    "/v1/case-law/" + this.documentNumber + "/zip")),
            jsonPath(
                "$.encoding[*].contentUrl",
                containsInAnyOrder(
                    "/v1/case-law/" + this.documentNumber + ".html",
                    "/v1/case-law/" + this.documentNumber + ".xml",
                    "/v1/case-law/" + this.documentNumber + ".zip")));
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
        .perform(get(getResourcePath("xml")).contentType(MediaType.APPLICATION_XML))
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
            .perform(get(getResourcePath("html")).contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    assertThat(responseContent, containsString("Das ist der Leitsatz"));
    assertThat(responseContent, containsString("Sonstiger Orientierungssatz"));
    assertThat(responseContent, containsString("Tatbestand"));
  }

  @Test
  @DisplayName("Should return case law and attachment in zip when using api endpoint for zip")
  void shouldReturnCaselawZip() throws Exception {

    MvcResult result =
        mockMvc
            .perform(get(getResourcePath("zip")).contentType(MediaType.valueOf("application/zip")))
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpectAll(status().isOk(), content().contentType("application/zip"))
            .andReturn();

    byte[] zipBytes = result.getResponse().getContentAsByteArray();
    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(zipBytes);
    final Map<String, byte[]> files = readZipStream(byteInputStream);

    Assertions.assertThat(files)
        .containsOnly(
            Map.entry(
                this.documentNumber + "/" + this.documentNumber + ".xml",
                createTestCaseLawLdml().getBytes()),
            Map.entry(this.documentNumber + "/Attachment.png", "picture".getBytes()));
  }

  @Test
  @DisplayName("Should return 404 when using document number is not found")
  void shouldReturn404() throws Exception {

    mockMvc
        .perform(get(getResourcePath("test", "html")).contentType(MediaType.TEXT_HTML))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @CsvSource({",/v1/case-law/"})
  @DisplayName("Html endpoint should adapt img src paths")
  void shouldReturnHtmlWithAdaptedImgSrcAttributes(String header, String expectedPrefix)
      throws Exception {
    final MockHttpServletRequestBuilder requestBuilder =
        get(getResourcePath("html")).contentType(MediaType.TEXT_HTML);

    if (!Strings.isEmpty(header)) {
      requestBuilder.header("get-resources-via", header);
    }

    var response =
        mockMvc
            .perform(requestBuilder)
            .andExpectAll(status().isOk(), content().contentType("text/html;charset=UTF-8"))
            .andReturn();

    var document = Jsoup.parse(response.getResponse().getContentAsString());

    Element image = Objects.requireNonNull(document.body().getElementsByTag("img").first());

    final String srcInLDML = this.documentNumber + "/Attachment.png";
    String expectedSrc = expectedPrefix + srcInLDML;
    Assertions.assertThat(image.attr("src")).isEqualTo(expectedSrc);
  }

  @Test
  @DisplayName("Serves images via the API with correct contentType")
  void shouldReturnReferencedImageWithContentType() throws Exception {
    mockMvc
        .perform(get(getResourcePath(this.documentNumber + "/Attachment", "png")))
        .andExpectAll(status().isOk(), content().contentType(MediaType.IMAGE_PNG));
  }

  @Test
  @DisplayName("Returns 404 for disallowed image extensions")
  void shouldReturn404ForDisallowedImageExtensions() throws Exception {
    String[] disallowed = {"exe", "svg", "txt", "pdf"};
    for (String ext : disallowed) {
      mockMvc
          .perform(get(getResourcePath(this.documentNumber + "/Attachment", ext)))
          .andExpect(status().isNotFound());
    }
  }

  @Test
  @DisplayName("Returns placeholder.png if requested image does not exist")
  void shouldReturnPlaceholderIfImageMissing() throws Exception {
    byte[] expected = new ClassPathResource("placeholder.png").getInputStream().readAllBytes();

    mockMvc
        .perform(get(getResourcePath(this.documentNumber + "/NonExistent", "png")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_PNG))
        .andExpect(content().bytes(expected))
        .andReturn()
        .getResponse()
        .getContentAsByteArray();
  }

  @Test
  @DisplayName("Returns 500 if placeholder.png is missing")
  void shouldReturn500IfPlaceholderMissing() throws Exception {
    try (MockedConstruction<ClassPathResource> ignored =
        Mockito.mockConstruction(
            ClassPathResource.class,
            (mock, context) ->
                when(mock.getInputStream()).thenThrow(new IOException("not found")))) {
      mockMvc
          .perform(get(getResourcePath(this.documentNumber + "/NonExistent", "png")))
          .andExpect(status().isInternalServerError());
    }
  }

  @Test
  void itReturnsFileChangesBetweenTimestamps() throws Exception {
    Changelog changelog =
        new Changelog(
            new HashSet<>(List.of("file1/file1.xml")),
            new HashSet<>(List.of("file2/file2.xml")),
            false);
    String changelogContent = new ObjectMapper().writeValueAsString(changelog);

    bucket.save(
        ChangelogService.CHANGELOGS_PREFIX + "2026-07-03T12:00:00.276525407Z", changelogContent);

    String from = "2026-07-03T12:00:00Z";
    String to = "2026-07-04T12:00:00Z";

    mockMvc
        .perform(
            get(ApiConfig.Paths.CASELAW_CHANGELOGS)
                .params(MultiValueMap.fromSingleValue(Map.of("from", from, "to", to))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.changed[0].['@id']").value("/v1/case-law/file1/zip"))
        .andExpect(jsonPath("$.changed[0].['@type']").value("MediaObject"))
        .andExpect(jsonPath("$.deleted[0].['@id']").value("/v1/case-law/file2"))
        .andExpect(jsonPath("$.deleted[0].['@type']").value("Decision"));
  }
}
