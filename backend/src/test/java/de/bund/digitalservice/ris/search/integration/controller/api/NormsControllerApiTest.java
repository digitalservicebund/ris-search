package de.bund.digitalservice.ris.search.integration.controller.api;

import static de.bund.digitalservice.ris.search.utils.JsonLdUtils.writeJsonLdString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import de.bund.digitalservice.ris.search.api.schema.TableOfContentsSchema;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.models.opensearch.TableOfContentsItem;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.ResultMatcher;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
@WithJwt("jwtTokens/ValidAccessToken.json")
class NormsControllerApiTest extends ContainersIntegrationBase {

  // there should be a corresponding XML file in resources/data/LDML/norm
  static final String MANIFESTATION_URL_PREFIX =
      ApiConfig.Paths.LEGISLATION_SINGLE
          + "/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/regelungstext-1";
  static final String MANIFESTATION_URL_HTML = MANIFESTATION_URL_PREFIX + ".html";
  static final String MANIFESTATION_URL_XML = MANIFESTATION_URL_PREFIX + ".xml";
  static final String MANIFESTATION_PREFIX_URL_ZIP =
      ApiConfig.Paths.LEGISLATION_SINGLE + "/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01.zip";

  @Autowired private NormsRepository normsRepository;
  @Autowired private MockMvc mockMvc;

  Boolean initialized = false;

  @BeforeEach
  void setUpSearchControllerApiTest() throws IOException {
    if (initialized) return; // replacement for @BeforeAll setup, which causes errors
    initialized = true;

    assertTrue(openSearchContainer.isRunning());

    super.recreateIndex();
    super.updateMapping();

    normsRepository.saveAll(NormsTestData.allDocuments);
  }

  @Test
  @DisplayName("Json Endpoint Should return json when requesting a single expression")
  void shouldReturnJsonWhenRequestingNormAsJson() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_SINGLE
                    + "/bund/bgbl-1/1000/test/2000-10-06/2/deu/regelungstext-1")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.@type", Matchers.is("Legislation")),
            jsonPath("$.name", Matchers.is("Test Gesetz")),
            jsonPath(
                "$.legislationIdentifier",
                Matchers.is("eli/bund/bgbl-1/1000/test/regelungstext-1")),
            jsonPath("$.alternateName", Matchers.is("TestG1")),
            jsonPath("$.abbreviation", Matchers.is("TeG")),
            jsonPath("$.legislationDate", Matchers.is("2024-01-02")),
            jsonPath("$.datePublished", Matchers.is("2024-01-03")),
            jsonPath("$.workExample.hasPart", Matchers.hasSize(2)),
            jsonPath("$.workExample.hasPart[0].@type", Matchers.is("Legislation")),
            jsonPath("$.workExample.hasPart[0].eId", Matchers.is("eid1")),
            jsonPath("$.workExample.hasPart[0].guid", Matchers.is("guid1")),
            jsonPath(
                "$.workExample.hasPart[0].@id",
                Matchers.is(
                    "/v1/legislation/eli/bund/bgbl-1/1000/test/2000-10-06/2/deu/regelungstext-1#eid1")),
            jsonPath("$.workExample.hasPart[0].name", Matchers.is("§ 1 Example article")),
            jsonPath("$.workExample.hasPart[0].isActive", Matchers.is(true)),
            jsonPath("$.workExample.hasPart[0].entryIntoForceDate", Matchers.is("2023-12-31")),
            jsonPath("$.workExample.hasPart[0].expiryDate", Matchers.is("3000-01-02")));
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
                get(ApiConfig.Paths.LEGISLATION_SINGLE
                        + "/bund/bgbl-1/1000/test/2000-10-06/2/deu/regelungstext-1")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    assertThat(result).contains(expectedToc);
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
            document.body().getElementById("einleitung-1_doktitel-1_text-1_doctitel-1"));
    assertThat(h1Element.outerHtml())
        .isEqualTo(
            "<h1 class=\"titel\" id=\"einleitung-1_doktitel-1_text-1_doctitel-1\">Formatting Test Document</h1>");
  }

  @Test
  @DisplayName("XML Endpoint Should return XML when requesting a single norm")
  void textLegislationXMLEndpoint() throws Exception {
    mockMvc
        .perform(get(MANIFESTATION_URL_XML).contentType(MediaType.APPLICATION_XML))
        .andExpectAll(
            status().isOk(),
            content().string(Matchers.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")),
            content().contentType("application/xml"));
  }

  @Test
  @DisplayName("ZIP endpoint should return a ZIP with two specific XML files")
  void zipEndpointWithTwoXmlFiles() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get(MANIFESTATION_PREFIX_URL_ZIP))
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpectAll(status().isOk(), content().contentType("application/zip"))
            .andReturn();

    byte[] zipBytes = result.getResponse().getContentAsByteArray();
    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(zipBytes);
    ZipInputStream zipInputStream = new ZipInputStream(byteInputStream);

    Map<String, byte[]> files = new HashMap<>();

    ZipEntry entry;
    while ((entry = zipInputStream.getNextEntry()) != null) {
      files.put(entry.getName(), zipInputStream.readAllBytes());
      zipInputStream.closeEntry();
    }
    zipInputStream.close();

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
                "offenestruktur-0.xml",
                Files.readAllBytes(Path.of(resourceDirectoryPath, "offenestruktur-0.xml"))));
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
                get(MANIFESTATION_URL_HTML.replace(".html", "/hauptteil-1_para-1.html"))
                    .contentType(MediaType.TEXT_HTML))
            .andExpectAll(status().isOk(), content().contentType("text/html;charset=UTF-8"))
            .andReturn();
    var content = response.getResponse().getContentAsString();
    Document parsed = Jsoup.parse(content);

    final Elements articles = parsed.body().selectXpath("//article");
    assertThat(articles).hasSize(1);
    final String articleId = Objects.requireNonNull(articles.get(0).attribute("id")).getValue();
    assertThat(articleId).isEqualTo("hauptteil-1_para-1");
  }

  @Test
  @DisplayName(
      "Html Endpoint Should return error html when requesting a single norm article not existing")
  void shouldReturnErrorMessageWhenRequestedNormArticleNotExisting() throws Exception {

    mockMvc
        .perform(
            get(MANIFESTATION_URL_HTML.replace(".html", "/hauptteil-1_para-10.html"))
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
        .andExpect(
            content().string(Matchers.containsString("<div>LegalDocML file not found</div>")))
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
        .andExpect(jsonPath("$.member", Matchers.hasSize(expectedSize)))
        .andExpect(jsonPath("$.member[0]['item'].abbreviation", Matchers.is("TeG")))
        .andExpect(jsonPath("$.@type", Matchers.is("hydra:Collection")))
        .andExpect(jsonPath("$.totalItems", Matchers.is(expectedSize)))
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
            jsonPath("$.member", Matchers.hasSize(1)),
            jsonPath("$.member[0]['item'].abbreviation", Matchers.is("TeG")));
  }

  @Test
  @DisplayName("Should compute temporalCoverage and legislationLegalForce correctly")
  void shouldHaveCorrectTemporalCoverage() throws Exception {
    var yesterday = LocalDate.now().minusDays(1);
    var tomorrow = LocalDate.now().plusDays(1);

    mockMvc
        .perform(get(ApiConfig.Paths.LEGISLATION).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpectAll(
            jsonPath(
                "$.member[0].item.workExample.temporalCoverage",
                Matchers.is(DateUtils.toDateIntervalString(yesterday, null))),
            jsonPath("$.member[0].item.workExample.legislationLegalForce", Matchers.is("InForce")),
            jsonPath(
                "$.member[1].item.workExample.temporalCoverage",
                Matchers.is(DateUtils.toDateIntervalString(yesterday, tomorrow))),
            jsonPath("$.member[1].item.workExample.legislationLegalForce", Matchers.is("InForce")),
            jsonPath(
                "$.member[2].item.workExample.temporalCoverage",
                Matchers.is(DateUtils.toDateIntervalString(null, yesterday))),
            jsonPath(
                "$.member[2].item.workExample.legislationLegalForce", Matchers.is("NotInForce")));
  }

  @Test
  @DisplayName("Filtered search should return one match")
  void filteredSearchShouldReturnOneMatch() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION
                    + "?searchTerm=Gesetz&dateFrom=2023-01-02&dateTo=2023-01-02")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", Matchers.hasSize(1)))
        .andExpect(
            jsonPath(
                "$.member[0]['item'].@id",
                Matchers.is("/v1/legislation/eli/2024/teg/2/regelungstext-1")))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should find a norm when searching for its expression ELI explicitly")
  void shouldFindNormByExpressionEli() throws Exception {
    final String eli = NormsTestData.allDocuments.get(0).getExpressionEli();
    final String uri = ApiConfig.Paths.LEGISLATION + "?searchTerm=\"%s\"".formatted(eli);
    mockMvc
        .perform(get(uri).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.member", Matchers.hasSize(1)),
            jsonPath("$.member[0]['item'].workExample.legislationIdentifier", Matchers.is(eli)));
  }

  @Test
  @DisplayName("From is inclusive and null to means unbounded")
  void fromIsInclusiveAndNullToMeansUnbounded() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION + "?searchTerm=Gesetz&dateTo=2023-01-02")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", Matchers.hasSize(1)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Null from means unbounded and to is inclusive")
  void nullFromMeansUnboundedAndToIsInclusive() throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION + "?searchTerm=Gesetz&normsDateFrom=2023-01-02")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.member", Matchers.hasSize(3)))
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
        .andExpect(jsonPath("$.member[*].articles", Matchers.hasSize(0)))
        .andExpect(jsonPath("$.member[*].textMatches").value(Matchers.hasSize(3)))
        .andExpect(
            jsonPath("$.member[0].textMatches[0].@type")
                .value(Matchers.equalTo("SearchResultMatch")))
        .andExpect(
            jsonPath("$.member[0].textMatches[*].name")
                .value(
                    Matchers.containsInAnyOrder(
                        "name", "§ 1 Example article", "§ 2 Example article")))
        .andExpect(
            jsonPath("$.member[0].textMatches[*].location")
                .value(Matchers.containsInAnyOrder(null, "eid1", "eid2")))
        .andExpect(
            jsonPath("$.member[0].textMatches[*].text")
                .value(
                    Matchers.containsInAnyOrder(
                        highlightedMatch, "example text 1", "example text 2")));
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
            jsonPath("$.member[0].textMatches[*]", Matchers.hasSize(3)),
            jsonPath("$.member[0].textMatches[0].@type", Matchers.equalTo("SearchResultMatch")),
            jsonPath(
                "$.member[0].textMatches[*].name",
                Matchers.containsInAnyOrder(
                    "name",
                    "§ 1 <mark>Example</mark> <mark>article</mark>",
                    "§ 2 <mark>Example</mark> <mark>article</mark>")),
            jsonPath(
                "$.member[0].textMatches[*].location",
                Matchers.containsInAnyOrder(null, "eid1", "eid2")),
            jsonPath(
                "$.member[0].textMatches[*].text",
                Matchers.containsInAnyOrder(
                    "<mark>Test</mark> Gesetz",
                    "<mark>example</mark> text 1",
                    "<mark>example</mark> text 2")));
  }

  public static Stream<Arguments> provideSortingTestArguments() {
    Stream.Builder<Arguments> stream = Stream.builder();

    stream.add(Arguments.of("", null));

    List<String> sortedLegislationIdentifiers =
        NormsTestData.allDocuments.stream().map(Norm::getWorkEli).sorted().toList();
    stream.add(
        Arguments.of(
            "legislationIdentifier",
            jsonPath(
                "$.member[*].item.legislationIdentifier",
                Matchers.is(sortedLegislationIdentifiers))));

    List<String> inverseSortedLegislationIdentifiers =
        new ArrayList<>(sortedLegislationIdentifiers);
    Collections.reverse(inverseSortedLegislationIdentifiers);
    stream.add(
        Arguments.of(
            "-legislationIdentifier",
            jsonPath(
                "$.member[*].item.legislationIdentifier",
                Matchers.is(inverseSortedLegislationIdentifiers))));

    List<String> dates =
        new ArrayList<>(
            NormsTestData.allDocuments.stream()
                .map(d -> d.getNormsDate().toString())
                .sorted()
                .toList());

    stream.add(
        Arguments.of("date", jsonPath("$.member[*].item.legislationDate", Matchers.is(dates))));

    List<String> invertedDates = new ArrayList<>(dates);
    Collections.reverse(invertedDates);

    stream.add(
        Arguments.of(
            "-date", jsonPath("$.member[*].item.legislationDate", Matchers.is(invertedDates))));

    return stream.build();
  }

  @ParameterizedTest
  @MethodSource("provideSortingTestArguments")
  @DisplayName("sorts results correctly")
  void shouldReturnCorrectSort(String sortParam, ResultMatcher matcher) throws Exception {

    var perform =
        mockMvc
            .perform(
                get(ApiConfig.Paths.LEGISLATION
                        + String.format("?searchTerm=%s&sort=%s", "test", sortParam))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.member", Matchers.hasSize(NormsTestData.allDocuments.size())));

    if (matcher != null) {
      perform.andExpect(matcher);
    }
  }

  private TableOfContentsSchema mapToTableOfContentsSchema(TableOfContentsItem item) {
    return new TableOfContentsSchema(
        item.id(),
        item.marker(),
        item.heading(),
        item.children().stream().map(this::mapToTableOfContentsSchema).toList());
  }
}
