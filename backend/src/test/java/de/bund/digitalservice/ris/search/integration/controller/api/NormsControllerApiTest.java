package de.bund.digitalservice.ris.search.integration.controller.api;

import static de.bund.digitalservice.ris.ZipTestUtils.readZipStream;
import static de.bund.digitalservice.ris.search.utils.JsonLdUtils.writeJsonLdString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.models.opensearch.TableOfContentsItem;
import de.bund.digitalservice.ris.search.schema.TableOfContentsSchema;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class NormsControllerApiTest extends ContainersIntegrationBase {

  // there should be a corresponding XML file in resources/data/LDML/norm
  static final String MANIFESTATION_URL_PREFIX =
      ApiConfig.Paths.LEGISLATION_SINGLE
          + "/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/regelungstext-1";
  static final String MANIFESTATION_URL_HTML = MANIFESTATION_URL_PREFIX + ".html";
  static final String MANIFESTATION_URL_XML = MANIFESTATION_URL_PREFIX + ".xml";
  static final String MANIFESTATION_PREFIX_URL_ZIP =
      ApiConfig.Paths.LEGISLATION_SINGLE + "/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01.zip";

  @Autowired private IndexNormsService indexNormsService;
  @Autowired private MockMvc mockMvc;

  @BeforeEach
  void setUpSearchControllerApiTest() {
    clearRepositoryData();
    normsRepository.saveAll(NormsTestData.allDocuments);
  }

  @Test
  @DisplayName("Json Endpoint Should return json when requesting a single expression")
  void shouldReturnJsonWhenRequestingNormAsJson() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_SINGLE + "/bund/bgbl-1/1000/test/2000-10-06/2/deu")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.@type", is("Legislation")),
            jsonPath("$.name", is("Test Gesetz")),
            jsonPath("$.legislationIdentifier", is("eli/bund/bgbl-1/1000/test")),
            jsonPath("$.alternateName", is("TestG1")),
            jsonPath("$.abbreviation", is("TeG")),
            jsonPath("$.legislationDate", is("2024-01-02")),
            jsonPath("$.datePublished", is("2024-01-03")),
            jsonPath("$.workExample.hasPart", hasSize(2)),
            jsonPath("$.workExample.hasPart[0].@type", is("Legislation")),
            jsonPath("$.workExample.hasPart[0].eId", is("eid1")),
            jsonPath("$.workExample.hasPart[0].guid", is("guid1")),
            jsonPath(
                "$.workExample.hasPart[0].@id",
                is("/v1/legislation/eli/bund/bgbl-1/1000/test/2000-10-06/2/deu#eid1")),
            jsonPath("$.workExample.hasPart[0].name", is("§ 1 Example article")),
            jsonPath("$.workExample.hasPart[0].isActive", is(true)),
            jsonPath("$.workExample.hasPart[0].entryIntoForceDate", is("2023-12-31")),
            jsonPath("$.workExample.hasPart[0].expiryDate", is("3000-01-02")));
  }

  @Test
  @DisplayName("Json Endpoint Should return table of contents")
  void jsonEndpointShouldReturnTableOfContents() throws Exception {
    String expectedToc =
        writeJsonLdString(
            NormsTestData.nestedToC.stream().map(this::mapToTableOfContentsSchema).toList());
    var result =
        mockMvc
            .perform(
                get(ApiConfig.Paths.LEGISLATION_SINGLE + "/bund/bgbl-1/1000/test/2000-10-06/2/deu")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    assertThat(result).contains(expectedToc);
  }

  private TableOfContentsSchema mapToTableOfContentsSchema(TableOfContentsItem item) {
    return new TableOfContentsSchema(
        item.id(),
        item.marker(),
        item.heading(),
        item.children().stream().map(this::mapToTableOfContentsSchema).toList());
  }

  @Test
  @DisplayName("Html endpoint should return HTML when requesting a single norm")
  void shouldReturnHtmlWhenRequestingNormAsHtml() throws Exception {
    var response =
        mockMvc
            .perform(get(MANIFESTATION_URL_HTML).contentType(MediaType.TEXT_HTML))
            .andExpectAll(status().isOk(), content().contentType("text/html;charset=UTF-8"))
            .andReturn();

    var document = Jsoup.parse(response.getResponse().getContentAsString());
    assertThat(document.head().getElementsByTag("title").text())
        .isEqualTo("Formatting Test Document (MFT)");

    Element h1Element =
        Objects.requireNonNull(
            document.body().getElementById("einleitung-n1_doktitel-n1_text-n1_doctitel-n1"));
    assertThat(h1Element.outerHtml())
        .isEqualTo(
            "<h1 class=\"titel\" id=\"einleitung-n1_doktitel-n1_text-n1_doctitel-n1\">Formatting Test Document</h1>");
  }

  public static Stream<Arguments> fileTestArguments() {
    String baseUrl = MANIFESTATION_URL_PREFIX.replace("regelungstext-1", "");
    return Stream.of(
        Arguments.of(
            "return file when it exists and extension supported", 200, baseUrl + "dokument.pdf"),
        Arguments.of("not return file when extension not supported", 404, baseUrl + "bild_1.png"),
        Arguments.of("not return file when does not exist", 404, baseUrl + "nonexistent.jpg"),
        Arguments.of(
            "not return files if relative paths are used",
            400,
            baseUrl + "../1991-02-01/bild_1.jpg"));
  }

  @ParameterizedTest
  @MethodSource("fileTestArguments")
  @DisplayName("File endpoint should {0}")
  void shouldReturnFilesWhenRequestedAndIfExtensionIsSupported(
      String ignoredTestDescription, int status, String path) throws Exception {
    mockMvc.perform(get(path)).andExpect(status().is(status));
  }

  @Test
  @DisplayName("Html endpoint should adapt img src paths")
  void shouldReturnHtmlWithAdaptedImgSrcAttributes() throws Exception {
    final MockHttpServletRequestBuilder requestBuilder =
        get(MANIFESTATION_URL_HTML).contentType(MediaType.TEXT_HTML);

    var response =
        mockMvc
            .perform(requestBuilder)
            .andExpectAll(status().isOk(), content().contentType("text/html;charset=UTF-8"))
            .andReturn();

    var document = Jsoup.parse(response.getResponse().getContentAsString());

    Element image =
        Objects.requireNonNull(
            document.body().getElementById("art-z5_abs-z1_inhalt-n1_text-n1_bild-n1"));

    final String srcInLDML = "eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/bild_1.jpg";
    String expectedSrc = "/v1/legislation/" + srcInLDML;
    assertThat(image.attr("src")).isEqualTo(expectedSrc);
  }

  @Test
  @DisplayName("Serves images via the API with correct contentType")
  void shouldReturnReferencedImageWithContentType() throws Exception {
    String url =
        ApiConfig.Paths.LEGISLATION_SINGLE
            + "/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/bild_1.jpg";
    mockMvc
        .perform(get(url))
        .andExpectAll(status().isOk(), content().contentType(MediaType.IMAGE_JPEG));
  }

  @Test
  @DisplayName("XML Endpoint Should return XML when requesting a single norm")
  void textLegislationXMLEndpoint() throws Exception {
    mockMvc
        .perform(get(MANIFESTATION_URL_XML).contentType(MediaType.APPLICATION_XML))
        .andExpectAll(
            status().isOk(),
            content().string(startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")),
            content().contentType("application/xml"));
  }

  @Test
  @DisplayName("ZIP endpoint should return a ZIP all relevant files")
  void zipEndpointWithRelevantFiles() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get(MANIFESTATION_PREFIX_URL_ZIP))
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpectAll(status().isOk(), content().contentType("application/zip"))
            .andReturn();

    byte[] zipBytes = result.getResponse().getContentAsByteArray();
    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(zipBytes);
    final Map<String, byte[]> files = readZipStream(byteInputStream);

    String resourceDirectoryPath =
        getClass()
            .getResource("/data/LDML/norm/eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/")
            .getPath();
    assertThat(files)
        .containsOnly(
            Map.entry(
                "regelungstext-1.xml",
                Files.readAllBytes(Path.of(resourceDirectoryPath, "regelungstext-1.xml"))),
            Map.entry(
                "anlage-regelungstext-1.xml",
                Files.readAllBytes(Path.of(resourceDirectoryPath, "anlage-regelungstext-1.xml"))),
            Map.entry(
                "bild_1.jpg", Files.readAllBytes(Path.of(resourceDirectoryPath, "bild_1.jpg"))),
            Map.entry(
                "bild_1.png", Files.readAllBytes(Path.of(resourceDirectoryPath, "bild_1.png"))),
            Map.entry(
                "dokument.pdf",
                Files.readAllBytes(Path.of(resourceDirectoryPath, "dokument.pdf"))));
  }

  @Test
  @DisplayName("ZIP endpoint should return Not Found if there are no matching files")
  void zipEndpointNotFound() throws Exception {
    mockMvc
        .perform(get(MANIFESTATION_PREFIX_URL_ZIP.replace("bund/bgbl-1/", "bund/bgbl-10000/")))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Html Endpoint Should return html when requesting a single norm article")
  void shouldReturnHtmlWhenRequestingNormArticleAsHtml() throws Exception {

    var response =
        mockMvc
            .perform(
                get(MANIFESTATION_URL_HTML.replace(".html", "/art-z1.html"))
                    .contentType(MediaType.TEXT_HTML))
            .andExpectAll(status().isOk(), content().contentType("text/html;charset=UTF-8"))
            .andReturn();
    var content = response.getResponse().getContentAsString();
    Document parsed = Jsoup.parse(content);

    final Elements articles = parsed.body().selectXpath("//article");
    assertThat(articles).hasSize(1);

    final String articleId = Objects.requireNonNull(articles.getFirst().attribute("id")).getValue();
    assertThat(articleId).isEqualTo("art-z1");
  }

  @Test
  @DisplayName(
      "Html Endpoint Should return error html when requesting a single norm article not existing")
  void shouldReturnErrorMessageWhenRequestedNormArticleNotExisting() throws Exception {

    mockMvc
        .perform(
            get(MANIFESTATION_URL_HTML.replace(".html", "/art-z10.html"))
                .contentType(MediaType.TEXT_HTML))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Html Endpoint Should return error html when requesting a single norm not in bucket")
  void shouldReturnErrorMessageWhenRequestedNormNotInBucket() throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_SINGLE
                    + "/bund/bgbl-1/1000/s999/1000-01-01/1/epo/1000-01-02/reguliga teksto-1.html")
                .contentType(MediaType.TEXT_HTML))
        .andDo(print())
        .andExpect(content().string(containsString("<div>LegalDocML file not found</div>")))
        .andExpect(content().contentType("text/html;charset=UTF-8"))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return list of norms")
  void shouldReturnListOfNorms() throws Exception {
    int expectedSize = NormsTestData.allDocuments.size();
    mockMvc
        .perform(get(ApiConfig.Paths.LEGISLATION).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.member", hasSize(expectedSize)))
        .andExpect(jsonPath("$.@type", is("hydra:Collection")))
        .andExpect(jsonPath("$.totalItems", is(expectedSize)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(strings = {"TeG", "teg"})
  @DisplayName("Should allow searching norms by abbreviation")
  void shouldSupportAbbreviation(String spelling) throws Exception {
    final String uri = ApiConfig.Paths.LEGISLATION + "?searchTerm=%s".formatted(spelling);
    mockMvc
        .perform(get(uri).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.member", hasSize(3)),
            jsonPath("$.member[0]['item'].abbreviation", is("TeG")));
  }

  @Test
  @DisplayName("Should compute temporalCoverage and legislationLegalForce correctly")
  void shouldHaveCorrectTemporalCoverage() throws Exception {
    DocumentContext json =
        JsonPath.parse(
            mockMvc
                .perform(
                    get(ApiConfig.Paths.LEGISLATION + "?eli=eli/bund/bgbl-1/1000/test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString());
    assertThat(json.read("$.member.length()", Integer.class)).isEqualTo(1);
    assertThat(json.read("$.member[0].item.abbreviation", String.class)).isEqualTo("TeG");
    assertThat(json.read("$.member[0].item.workExample.temporalCoverage", String.class))
        .isEqualTo("2025-11-01/..");
    assertThat(json.read("$.member[0].item.workExample.legislationLegalForce", String.class))
        .isEqualTo("InForce");

    json =
        JsonPath.parse(
            mockMvc
                .perform(
                    get(ApiConfig.Paths.LEGISLATION + "?eli=eli/2024/teg/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString());
    assertThat(json.read("$.member.length()", Integer.class)).isEqualTo(1);
    assertThat(json.read("$.member[0].item.abbreviation", String.class)).isEqualTo("TeG3");
    assertThat(json.read("$.member[0].item.workExample.temporalCoverage", String.class))
        .isEqualTo("2025-11-03/2025-11-03");
    assertThat(json.read("$.member[0].item.workExample.legislationLegalForce", String.class))
        .isEqualTo("NotInForce");
  }

  @Test
  @DisplayName("Filtered search should return one match")
  void filteredSearchShouldReturnOneMatch() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION
                    + "?searchTerm=Gesetz&dateFrom=2025-11-01&dateTo=2025-11-01")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(
            jsonPath(
                "$.member[0].item.workExample.legislationIdentifier",
                is("eli/bund/bgbl-1/1000/test/2000-10-06/2/deu")))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should find a norm when searching for its expression ELI explicitly")
  void shouldFindNormByExpressionEli() throws Exception {
    final String eli = NormsTestData.allDocuments.getFirst().getExpressionEli();
    final String uri = ApiConfig.Paths.LEGISLATION + "?searchTerm=\"%s\"".formatted(eli);
    mockMvc
        .perform(get(uri).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.member", hasSize(1)),
            jsonPath("$.member[0]['item'].workExample.legislationIdentifier", is(eli)));
  }

  @Test
  @DisplayName("dateTo is inclusive and dateFrom being null means unbounded")
  void dateToIsInclusiveAndDateFromBeingNullMeansUnbounded() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION + "?searchTerm=Gesetz&dateTo=2025-11-01")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("dateFrom is inclusive and dateTo being null means unbounded")
  void dateFromIsInclusiveAndDateToBeingNullMeansUnbounded() throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION + "?searchTerm=Gesetz&dateFrom=2025-11-03")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should return ok and empty page for no search results")
  void noSearchResultsShouldReturnOk() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION + "?searchTerm=foobar")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should return highlights matching the norm search query")
  void shouldReturnHighlightsMatchingTheNormSearchQuery() throws Exception {
    String highlightedMatch = "<mark>Test</mark> Gesetz";
    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION + "?searchTerm=test")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member[*].articles", hasSize(0)))
        .andExpect(jsonPath("$.member[*].textMatches").value(hasSize(3)))
        .andExpect(jsonPath("$.member[0].textMatches[0].@type").value(equalTo("SearchResultMatch")))
        .andExpect(
            jsonPath("$.member[0].textMatches[*].name")
                .value(containsInAnyOrder("name", "§ 1 Example article", "§ 2 Example article")))
        .andExpect(
            jsonPath("$.member[0].textMatches[*].location")
                .value(containsInAnyOrder(null, "eid1", "eid2")))
        .andExpect(
            jsonPath("$.member[0].textMatches[*].text")
                .value(containsInAnyOrder(highlightedMatch, "example text 1", "example text 2")));
  }

  @Test
  @DisplayName("Should return highlights in article names")
  void shouldHighlightArticleNames() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION + "?searchTerm=test example article")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.member[0].textMatches[*]", hasSize(3)),
            jsonPath("$.member[0].textMatches[0].@type", equalTo("SearchResultMatch")),
            jsonPath(
                "$.member[0].textMatches[*].name",
                containsInAnyOrder(
                    "name",
                    "§ 1 <mark>Example</mark> <mark>article</mark>",
                    "§ 2 <mark>Example</mark> <mark>article</mark>")),
            jsonPath(
                "$.member[0].textMatches[*].location", containsInAnyOrder(null, "eid1", "eid2")),
            jsonPath(
                "$.member[0].textMatches[*].text",
                containsInAnyOrder(
                    "<mark>Test</mark> Gesetz",
                    "<mark>example</mark> text 1",
                    "<mark>example</mark> text 2")));
  }

  @ParameterizedTest
  @CsvSource(
      value = {
        // sorting by legislationIdentifier sorts by the expression eli
        "legislationIdentifier, 3, eli/2024/teg/2;eli/2024/teg/3;eli/bund/bgbl-1/1000/test",
        // reverse sort reverses the sort
        "-legislationIdentifier, 3, eli/bund/bgbl-1/1000/test;eli/2024/teg/3;eli/2024/teg/2"
      })
  @DisplayName("norms sort by id correctly")
  void normsSortByIdCorrectly(String sortParam, Integer expectedSize, String expectedIds)
      throws Exception {

    DocumentContext json =
        JsonPath.parse(
            mockMvc
                .perform(
                    get(ApiConfig.Paths.LEGISLATION
                            + String.format("?searchTerm=%s&sort=%s", "test", sortParam))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString());

    assertThat(json.read("$.member.length()", Integer.class)).isEqualTo(expectedSize);
    String ids = String.join(";", json.read("$.member[*].item.legislationIdentifier", List.class));
    assertThat(ids).isEqualTo(expectedIds);
  }

  @ParameterizedTest
  @CsvSource(
      value = {
        // default sort is by relevance and therefore dates returned are not sorted
        "'', 3, 2025-11-01/..;2025-11-03/2025-11-03;2025-11-02/2025-11-03",
        // sorting by date sorts by in force (api uses temporalCoverage for in force date)
        "DATUM, 3, 2025-11-01/..;2025-11-02/2025-11-03;2025-11-03/2025-11-03",
        // reverse sort reverses the sort
        "-DATUM, 3, 2025-11-03/2025-11-03;2025-11-02/2025-11-03;2025-11-01/.."
      })
  @DisplayName("norms sort by date correctly")
  void normsSortByDateCorrectly(String sortParam, Integer expectedSize, String expectedDates)
      throws Exception {

    DocumentContext json =
        JsonPath.parse(
            mockMvc
                .perform(
                    get(ApiConfig.Paths.LEGISLATION
                            + String.format("?searchTerm=%s&sort=%s", "test", sortParam))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString());

    assertThat(json.read("$.member.length()", Integer.class)).isEqualTo(expectedSize);
    List<String> actualDates =
        json.read("$.member[*].item.workExample.temporalCoverage", List.class);
    assertThat(String.join(";", actualDates)).isEqualTo(expectedDates);
  }

  @Test
  void itSortsByTemporalCoverageFrom() throws Exception {

    normsRepository.deleteAll();

    var normTestOne =
        Norm.builder()
            .id("n1")
            .officialTitle("title1")
            .entryIntoForceDate(LocalDate.of(2026, 1, 1))
            .build();

    var normTestTwo =
        Norm.builder()
            .id("id2")
            .officialTitle("title2")
            .entryIntoForceDate(LocalDate.of(2025, 1, 1))
            .build();

    normsRepository.saveAll(List.of(normTestOne, normTestTwo));

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION + String.format("?sort=%s", "temporalCoverageFrom"))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member[*].item.name", is(List.of("title2", "title1"))));

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION + String.format("?sort=%s", "-temporalCoverageFrom"))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member[*].item.name", is(List.of("title1", "title2"))));
  }

  @Test
  @DisplayName("Should return most relevant expression for a day")
  void shouldReturnMostRelevantExpressionForADay() throws Exception {
    addNormXmlFiles(NormsTestData.s102WorkExpressions);
    indexNormsService.reindexAll(Instant.now().toString());

    // A very old date should return the oldest expression
    DocumentContext json =
        JsonPath.parse(
            mockMvc
                .perform(
                    get(ApiConfig.Paths.LEGISLATION
                            + "?eli="
                            + NormsTestData.S_102_WORK_ELI
                            + "&mostRelevantOn=1900-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString());
    assertThat(json.read("$.member.length()", Integer.class)).isEqualTo(1);
    assertThat(json.read("$.member[0].item.workExample.legislationIdentifier", String.class))
        .isEqualTo("eli/bund/bgbl-1/1991/s102/1991-01-01/1/deu");

    // A date where 1 expression was in force return that expression
    json =
        JsonPath.parse(
            mockMvc
                .perform(
                    get(ApiConfig.Paths.LEGISLATION
                            + "?eli="
                            + NormsTestData.S_102_WORK_ELI
                            + "&mostRelevantOn=1991-06-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString());
    assertThat(json.read("$.member.length()", Integer.class)).isEqualTo(1);
    assertThat(json.read("$.member[0].item.workExample.legislationIdentifier", String.class))
        .isEqualTo("eli/bund/bgbl-1/1991/s102/1991-01-01/1/deu");

    // A date where no expressions were in force should return the next to be in force
    json =
        JsonPath.parse(
            mockMvc
                .perform(
                    get(ApiConfig.Paths.LEGISLATION
                            + "?eli="
                            + NormsTestData.S_102_WORK_ELI
                            + "&mostRelevantOn=1996-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString());
    assertThat(json.read("$.member.length()", Integer.class)).isEqualTo(1);
    assertThat(json.read("$.member[0].item.workExample.legislationIdentifier", String.class))
        .isEqualTo("eli/bund/bgbl-1/1991/s102/2020-01-01/1/deu");

    // A date far in the future will return the last expression (ausserkraft undefined)
    json =
        JsonPath.parse(
            mockMvc
                .perform(
                    get(ApiConfig.Paths.LEGISLATION
                            + "?eli="
                            + NormsTestData.S_102_WORK_ELI
                            + "&mostRelevantOn=5000-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString());
    assertThat(json.read("$.member.length()", Integer.class)).isEqualTo(1);
    assertThat(json.read("$.member[0].item.workExample.legislationIdentifier", String.class))
        .isEqualTo("eli/bund/bgbl-1/1991/s102/2050-01-01/1/deu");
  }
}
