package de.bund.digitalservice.ris.search.controller.api;

import static de.bund.digitalservice.ris.ZipTestUtils.readZipStream;
import static de.bund.digitalservice.ris.utils.JsonldResultMatchers.isJsonLdCompliant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.controller.api.testData.NormsTestData;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SuppressWarnings("unchecked")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class NormsControllerApiIntegrationTest extends ContainersIntegrationBase {

  // there should be a corresponding XML file in resources/data/LDML/norm
  static final String MANIFESTATION_URL_PREFIX =
      ApiConfig.Paths.LEGISLATION_SINGLE
          + "/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/regelungstext-1";
  static final String MANIFESTATION_URL_XML = MANIFESTATION_URL_PREFIX + ".xml";
  static final String MANIFESTATION_PREFIX_URL_ZIP =
      ApiConfig.Paths.LEGISLATION_SINGLE + "/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01.zip";

  @BeforeAll
  public void loadDefaults() {
    loadDefaultData();
  }

  @Autowired private MockMvc mockMvc;

  private byte[] readResourceBytes(String resourcePath) throws IOException {
    try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
      return Objects.requireNonNull(in, "resource not found: " + resourcePath).readAllBytes();
    }
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
            jsonPath("$['@type']", is("Legislation")),
            jsonPath("$.name", is("Test Gesetz")),
            jsonPath("$.legislationIdentifier", is("eli/bund/bgbl-1/1000/test/2000-10-06/2/deu")),
            jsonPath("$.alternateName", is("TestG1")),
            jsonPath("$.abbreviation", is("TeG")),
            jsonPath("$.exampleOfWork.legislationDate", is("2024-01-02")),
            jsonPath("$.exampleOfWork.datePublished", is("2024-01-03")),
            jsonPath("$.hasPart", hasSize(3)),
            jsonPath("$.hasPart[0]['@type']", is("Legislation")),
            jsonPath("$.hasPart[0].eId", is("art-z1")),
            jsonPath(
                "$.hasPart[0]['@id']",
                is("/v1/legislation/eli/bund/bgbl-1/1000/test/2000-10-06/2/deu#art-z1")),
            jsonPath("$.hasPart[0].name", is("1")),
            jsonPath("$.hasPart[0].temporalCoverage", is("2023-12-31/3000-01-02")))
        .andExpect(isJsonLdCompliant());
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
    MvcResult asyncResult =
        mockMvc
            .perform(get(MANIFESTATION_PREFIX_URL_ZIP).contentType("application/zip"))
            .andExpect(request().asyncStarted())
            .andReturn();

    MvcResult result =
        mockMvc
            .perform(asyncDispatch(asyncResult))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/zip"))
            .andReturn();

    byte[] zipBytes = result.getResponse().getContentAsByteArray();
    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(zipBytes);
    final Map<String, byte[]> files = readZipStream(byteInputStream);

    String resourceDirectoryPath =
        "/data/LDML/norm/eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/";
    assertThat(files)
        .containsOnly(
            Map.entry(
                "eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/regelungstext-1.xml",
                readResourceBytes(resourceDirectoryPath + "regelungstext-1.xml")),
            Map.entry(
                "eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/anlage-regelungstext-1.xml",
                readResourceBytes(resourceDirectoryPath + "anlage-regelungstext-1.xml")),
            Map.entry(
                "eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/bild_1.jpg",
                readResourceBytes(resourceDirectoryPath + "bild_1.jpg")),
            Map.entry(
                "eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/bild_1.png",
                readResourceBytes(resourceDirectoryPath + "bild_1.png")),
            Map.entry(
                "eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/dokument.pdf",
                readResourceBytes(resourceDirectoryPath + "dokument.pdf")));
  }

  @Test
  @DisplayName("ZIP endpoint should return Not Found if there are no matching files")
  void zipEndpointNotFound() throws Exception {
    mockMvc
        .perform(get(MANIFESTATION_PREFIX_URL_ZIP.replace("bund/bgbl-1/", "bund/bgbl-10000/")))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return list of norms")
  void shouldReturnListOfNorms() throws Exception {
    int expectedSize = NormsTestData.allNorms.size();
    mockMvc
        .perform(get(ApiConfig.Paths.LEGISLATION).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.member", hasSize(expectedSize)))
        .andExpect(jsonPath("$['@type']", is("hydra:Collection")))
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
            jsonPath("$.member[0]['item'].abbreviation", is("TeG")))
        .andExpect(isJsonLdCompliant());
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
    assertThat(json.read("$.member[0].item.alternateName", String.class)).isEqualTo("TestG1");
    assertThat(json.read("$.member[0].item.temporalCoverage", String.class))
        .isEqualTo("2025-11-01/..");
    assertThat(json.read("$.member[0].item.legislationLegalForce", String.class))
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
    assertThat(json.read("$.member[0].item.alternateName", String.class)).isEqualTo("TestG3");
    assertThat(json.read("$.member[0].item.temporalCoverage", String.class))
        .isEqualTo("2025-11-03/2025-11-03");
    assertThat(json.read("$.member[0].item.legislationLegalForce", String.class))
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
                "$.member[0].item.legislationIdentifier",
                is("eli/bund/bgbl-1/1000/test/2000-10-06/2/deu")))
        .andExpect(status().isOk())
        .andExpect(isJsonLdCompliant());
  }

  @Test
  @DisplayName("Should allow filtering norms by abbreviation")
  void shouldReturnNormsFilteringByAbbreviation() throws Exception {
    final String uri = ApiConfig.Paths.LEGISLATION + "?abbreviation=Teg";
    mockMvc
        .perform(get(uri).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.member", hasSize(3)),
            jsonPath("$.member[0]['item'].abbreviation", is("TeG")),
            jsonPath("$.member[1]['item'].abbreviation", is("TeG")),
            jsonPath("$.member[2]['item'].abbreviation", is("TeG")));
  }

  @Test
  @DisplayName("Should allow filtering norms by ris-abbreviation")
  void shouldReturnNormsFilteringByRisAbbreviation() throws Exception {
    final String uri = ApiConfig.Paths.LEGISLATION + "?risAbbreviation=Teg";
    mockMvc
        .perform(get(uri).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.member", hasSize(1)),
            jsonPath("$.member[0]['item'].alternateName", is("TestG1")));
  }

  @Test
  @DisplayName("Should find a norm when searching for its expression ELI explicitly")
  void shouldFindNormByExpressionEli() throws Exception {
    final String eli = NormsTestData.allNorms.getFirst().getExpressionEli();
    final String uri = ApiConfig.Paths.LEGISLATION + "?searchTerm=\"%s\"".formatted(eli);
    mockMvc
        .perform(get(uri).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.member", hasSize(1)),
            jsonPath("$.member[0]['item'].legislationIdentifier", is(eli)));
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
        .andExpect(
            jsonPath("$.member[0].textMatches[0]['@type']").value(equalTo("SearchResultMatch")))
        .andExpect(jsonPath("$.member[0].textMatches[*].text").value(contains(highlightedMatch)))
        .andExpect(isJsonLdCompliant());
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
            jsonPath("$.member[0].textMatches[0]['@type']", equalTo("SearchResultMatch")),
            jsonPath(
                "$.member[0].textMatches[*].name",
                containsInAnyOrder(
                    "name",
                    "§ 1 <mark>Example</mark> <mark>article</mark>",
                    "§ 2 <mark>Example</mark> <mark>article</mark>")),
            jsonPath(
                "$.member[0].textMatches[*].location",
                containsInAnyOrder(null, "art-z1", "art-z2")),
            jsonPath(
                "$.member[0].textMatches[*].text",
                containsInAnyOrder(
                    "<mark>Test</mark> Gesetz",
                    "<mark>example</mark> text 1",
                    "<mark>example</mark> text 2")))
        .andExpect(isJsonLdCompliant());
  }

  @ParameterizedTest
  @CsvSource(
      value = {
        // sorting by legislationIdentifier sorts by the expression eli
        "legislationIdentifier, 3, eli/2024/teg/2/exp;eli/2024/teg/3/exp;eli/bund/bgbl-1/1000/test/2000-10-06/2/deu",
        // reverse sort reverses the sort
        "-legislationIdentifier, 3, eli/bund/bgbl-1/1000/test/2000-10-06/2/deu;eli/2024/teg/3/exp;eli/2024/teg/2/exp"
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
        // default sort is by relevance then date. Since the searchTerm is "test", results are not
        // sorted by date
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
    List<String> actualDates = json.read("$.member[*].item.temporalCoverage", List.class);
    assertThat(String.join(";", actualDates)).isEqualTo(expectedDates);
  }

  @Test
  @DisplayName("It returns all workExamples of a given expressionEli")
  void itReturnsTheWorkExmapleOfAGivenWorkEli() throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_WORK_EXAMPLE + "/bund/bgbl-1/1000/test")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$['@type']", equalTo("hydra:Collection")))
        .andExpect(
            jsonPath("$['@id']", equalTo("/v1/legislation/work-example/eli?pageIndex=0&size=100")))
        .andExpect(jsonPath("$.member[0]['@type']", equalTo("Legislation")))
        .andExpect(
            jsonPath(
                "$.member[0].['@id']",
                equalTo("/v1/legislation/eli/bund/bgbl-1/1000/test/2000-10-06/2/deu")))
        .andExpect(
            jsonPath(
                "$.member[0].legislationIdentifier",
                equalTo("eli/bund/bgbl-1/1000/test/2000-10-06/2/deu")))
        .andExpect(jsonPath("$.member[0].temporalCoverage", equalTo("2025-11-01/..")))
        .andExpect(jsonPath("$.member[0].legislationLegalForce", equalTo("InForce")));
  }
}
