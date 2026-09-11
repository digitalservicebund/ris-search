package de.bund.digitalservice.ris.search.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import java.net.URI;
import java.util.Objects;
import java.util.stream.Stream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class NormsControllerHtmlApiIntegrationTest extends ContainersIntegrationBase {

  // there should be a corresponding XML file in resources/data/LDML/norm
  static final String MANIFESTATION_URL_PREFIX =
      ApiConfig.Paths.LEGISLATION_SINGLE
          + "/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/regelungstext-1";
  static final String MANIFESTATION_URL_HTML = MANIFESTATION_URL_PREFIX + ".html";

  @BeforeAll
  public void loadDefaults() {
    loadDefaultData();
  }

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("Html endpoint should return HTML when requesting a single norm")
  void shouldReturnHtmlWhenRequestingNormAsHtml() throws Exception {
    System.err.println("Norm files: " + normsBucket.getAllKeys());
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

  @Test
  @DisplayName("Html endpoint should adapt img src paths")
  void shouldReturnHtmlWithAdaptedImgSrcAttributes() throws Exception {
    System.err.println("Norm files: " + normsBucket.getAllKeys());
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
  @DisplayName("Html Endpoint Should return html when requesting a single norm article")
  void shouldReturnHtmlWhenRequestingNormArticleAsHtml() throws Exception {
    System.err.println("Norm files: " + normsBucket.getAllKeys());

    var response =
        mockMvc
            .perform(
                get(MANIFESTATION_URL_HTML.replace(".html", "/art-z1.html"))
                    .contentType(MediaType.TEXT_HTML))
            .andExpectAll(status().isOk(), content().contentType("text/html;charset=UTF-8"))
            .andReturn();
    var content = response.getResponse().getContentAsString();
    Document parsed = Jsoup.parse(content);

    final var article = parsed.body().getElementById("art-z1");
    assertThat(article).isNotNull();
  }

  @Test
  @DisplayName("The article html endpoint should work with a special character eid")
  void articleHtmlEndpointWorksWithSpecialCharacterEid() throws Exception {
    System.err.println("Norm files: " + normsBucket.getAllKeys());

    var response =
        mockMvc
            .perform(
                get(MANIFESTATION_URL_HTML.replace(".html", "/art-z§§ 4 bis 14.html"))
                    .contentType(MediaType.TEXT_HTML))
            .andExpectAll(status().isOk(), content().contentType("text/html;charset=UTF-8"))
            .andReturn();
    var content = response.getResponse().getContentAsString();
    Document parsed = Jsoup.parse(content);

    final var article = parsed.body().getElementById("art-z%c2%a7%c2%a7%204%20bis%2014");
    assertThat(article).isNotNull();
  }

  @Test
  @DisplayName(
      "Html Endpoint Should return error html when requesting a single norm article not existing")
  void shouldReturnErrorMessageWhenRequestedNormArticleNotExisting() throws Exception {
    System.err.println("Norm files: " + normsBucket.getAllKeys());

    mockMvc
        .perform(
            get(MANIFESTATION_URL_HTML.replace(".html", "/art-z10.html"))
                .contentType(MediaType.TEXT_HTML))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Html Endpoint Should return error html when requesting a single norm not in bucket")
  void shouldReturnErrorMessageWhenRequestedNormNotInBucket() throws Exception {
    System.err.println("Norm files: " + normsBucket.getAllKeys());

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

  @ParameterizedTest(name = "HTML Endpoint should resolve article with eId={0}")
  @MethodSource("articleEidProvider")
  @DisplayName(
      "Html Endpoint should resolve article html with different encodings and UTF-8 variants of eId")
  void shouldResolveArticleWithVariousEidFormats(String articleEid, boolean isEncoded)
      throws Exception {
    System.err.println("Norm files: " + normsBucket.getAllKeys());

    String url = MANIFESTATION_URL_HTML.replace(".html", "/" + articleEid + ".html");
    var request = isEncoded ? get(URI.create(url)) : get(url);
    var response =
        mockMvc
            .perform(request.contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andReturn();

    Document parsed = Jsoup.parse(response.getResponse().getContentAsString());
    var article = parsed.body().getElementById("art-z%c2%a7%c2%a7%204%20bis%2014");
    assertThat(article).isNotNull();
  }

  static Stream<Arguments> articleEidProvider() {
    return Stream.of(
        Arguments.of("art-z§§ 4 bis 14", false),
        Arguments.of("art-z%C2%A7%C2%A7%204%20bis%2014", true),
        Arguments.of("art-z%c2%a7%c2%a7%204%20bis%2014", true));
  }

  @Test
  @DisplayName("Html Endpoint Should return 404 for encoded article eId not present in XML")
  void shouldReturn404ForNonexistingEncodedArticleEid() throws Exception {
    System.err.println("Norm files: " + normsBucket.getAllKeys());

    String encodedMissing = "art-z%c2%a7%c2%a7%20999%20bis%201234";
    URI uri = URI.create(MANIFESTATION_URL_HTML.replace(".html", "/" + encodedMissing + ".html"));
    mockMvc.perform(get(uri).contentType(MediaType.TEXT_HTML)).andExpect(status().isNotFound());
  }
}
