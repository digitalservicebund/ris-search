package de.bund.digitalservice.ris.search.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.shaded.org.apache.commons.lang3.StringUtils.deleteWhitespace;

import de.bund.digitalservice.ris.search.exception.FileTransformationException;
import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.XsltTransformerService;
import de.bund.digitalservice.ris.search.utils.CaseLawLdmlTemplateUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

class XsltTransformerServiceTest {

  public static final String RESOURCES_BASE_PATH = "res://";
  private final NormsBucket normsBucketMock = Mockito.mock(NormsBucket.class);
  private final XsltTransformerService service = new XsltTransformerService(normsBucketMock);

  private final CaseLawLdmlTemplateUtils caseLawLdmlTemplateUtils = new CaseLawLdmlTemplateUtils();

  String resourcesPath = getClass().getResource("/data/XsltTransformerServiceTest/").getPath();

  @ParameterizedTest(name = "{2}")
  @CsvSource({
    "twoParagraphArticle.xml, twoParagraphArticle.html, Should transform an article with two paragraphs",
    "list.xml, list.html, Should transform list to HTML",
    "fullDepthArticle.xml, fullDepthArticle.html, Should transform full depth article to HTML",
    "nestedArticles.xml, nestedArticles.html, Should transform articles at every level to HTML",
    "formatting.xml, formatting.html, Should transform an article with formatting to HTML",
    "heading.xml, heading.html, Should transform a heading with marker inside",
    "authorialNote.xml, authorialNote.html, Should transform authorialNotes to HTML",
    "authorialNoteWithPlacementBase.xml, authorialNoteWithPlacementBase.html, Should transform authorialNotes with placementBase",
    "authorialNoteInDocTitle.xml, authorialNoteInDocTitle.html, Should transform authorialNotes in title",
    "authorialNoteInDoc.xml, authorialNoteInDoc.html, Should transform authorialNotes in attachment body",
    "notes.xml, notes.html, Should transform notes",
    "image.xml, image.html, Should transform img tag",
    "container.xml, container.html, Should transform container elements in the preface",
    "preambleFormula.xml, preambleFormula.html, Should transform a preamble formula",
    "conclusionsFormula.xml, conclusionsFormula.html, Should transform a conclusions formula",
    "blockList.xml, blockList.html, Should transform a blockList",
    "preformatted.xml, preformatted.html, Should transform preformatted paragraphs",
    "proprietary.xml, proprietary.html, Should transform proprietary metadata",
    "offenestruktur-without-title.xml, offenestruktur-without-title.html, Should transform an attachment without title",
  })
  void testTransformNormLegalDocMlFull(
      String inputFileName, String expectedFileName, String testName) throws IOException {
    byte[] bytes = Files.readAllBytes(Path.of(resourcesPath, inputFileName));

    var actualHtml = service.transformNorm(bytes, "subtype", RESOURCES_BASE_PATH);

    var expectedHtml = Files.readString(Path.of(resourcesPath, expectedFileName));
    var expectedDocument = Jsoup.parse(expectedHtml);
    var actualDocument = Jsoup.parse(actualHtml);
    assertThat(actualDocument.body().html()).isEqualTo(expectedDocument.body().html());
  }

  @Test
  void testTransformNormWithAttachments() throws IOException, NoSuchKeyException {
    byte[] bytes = Files.readAllBytes(Path.of(resourcesPath, "attachments.xml"));

    final Function<String, ResponseInputStream<GetObjectResponse>> makeInputStream =
        filename -> {
          try {
            final var allBytes = Files.readAllBytes(Path.of(resourcesPath, filename));
            final var stream = new ByteArrayInputStream(allBytes);
            return new ResponseInputStream<>(Mockito.mock(GetObjectResponse.class), stream);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        };

    Mockito.when(
            normsBucketMock.getStream(
                "eli/bund/bgbl-1/0000/s1000/2000-01-01/1/deu/2000-01-01/offenestruktur-1.xml"))
        .thenReturn(makeInputStream.apply("offenestruktur-1.xml"));
    Mockito.when(
            normsBucketMock.getStream(
                "eli/bund/bgbl-1/0000/s1000/2000-01-01/1/deu/2000-01-01/offenestruktur-2.xml"))
        .thenReturn(makeInputStream.apply("offenestruktur-2.xml"));

    var actualHtml = service.transformNorm(bytes, "subtype", RESOURCES_BASE_PATH);

    var expectedHtml = Files.readString(Path.of(resourcesPath, "attachments.html"));
    var expectedDocument = Jsoup.parse(expectedHtml);
    var actualDocument = Jsoup.parse(actualHtml);
    assertThat(actualDocument.body().html()).isEqualTo(expectedDocument.body().html());
  }

  @Test
  void testHandlesMissingAttachment() throws IOException {
    Mockito.reset(normsBucketMock);
    byte[] bytes = Files.readAllBytes(Path.of(resourcesPath, "attachments.xml"));

    FileTransformationException exception =
        Assertions.assertThrows(
            FileTransformationException.class,
            () -> service.transformNorm(bytes, "subtype", RESOURCES_BASE_PATH));
    assertThat(exception.getMessage()).endsWith("offenestruktur-1.xml");
  }

  @Test
  @DisplayName("Includes a link to authorialNotes in the heading when in article mode")
  void testArticleHeadingWithAuthorialNote() throws IOException {
    byte[] bytes = Files.readAllBytes(Path.of(resourcesPath, "authorialNote.xml"));
    var actualHtml = service.transformArticle(bytes, "c", RESOURCES_BASE_PATH);
    var actualDocument = Jsoup.parse(actualHtml);
    var heading = actualDocument.select("h2").html();
    assertThat(heading)
        .isEqualTo(
            """
            <span class="akn-num" id="c_num-1">§ 1</span> <span class="akn-heading" id="c_heading-1">Article title <a href="#c_heading-1_amtlfnote-1"><sup>1</sup></a></span>""");
  }

  @Test
  void testTransformsNormTocCorrectly() throws IOException {
    var xml =
        Files.readAllBytes(
            Path.of(
                "src/test/resources/data/LDML/norm/eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/regelungstext-1.xml"));
    var html = service.transformNorm(xml, "subtype", RESOURCES_BASE_PATH);
    var expectedToc =
        """
        <div class="level-1">
         <span class="akn-span" id="preambel-1_blockcontainer-1_inhuebs-1_eintrag-1_span-1">Abschnitt 1</span>
        </div>
        <div class="level-2">
         <span class="akn-span" id="preambel-1_blockcontainer-1_inhuebs-1_eintrag-2_span-1">Allgemeine Bestimmungen</span>
        </div>
        <div class="level-5">
         <span class="akn-span" id="preambel-1_blockcontainer-1_inhuebs-1_eintrag-3_span-1">Art 1</span> <span class="akn-span" id="preambel-1_blockcontainer-1_inhuebs-1_eintrag-3_span-2">Test Title</span>
        </div>
        <div class="level-1">
         <span class="akn-span" id="preambel-1_blockcontainer-1_inhuebs-1_eintrag-4_span-1">Art 2</span> <span class="akn-span" id="preambel-1_blockcontainer-1_inhuebs-1_eintrag-4_span-2">Another Test Title</span>
        </div>""";
    final Document parsed = Jsoup.parse(html);

    String actualToc = parsed.selectXpath("//div[@class='official-toc']").html();
    assertThat(actualToc).isEqualTo(expectedToc);
    String tocHeader = parsed.selectXpath("//div[@class='official-toc']/../span").html();
    assertThat(tocHeader).isEqualTo("Inhaltsübersicht");
  }

  @Test
  void testTransformsCaselawHeaderCorrectly() throws IOException {
    var actualXml = caseLawLdmlTemplateUtils.getXmlFromTemplate(null);
    var actualHtml = service.transformCaseLaw(actualXml.getBytes(StandardCharsets.UTF_8));
    var expectedHeader =
        """
        <h1 id="title">
          <p>Title</p>
        </h1>
        """;
    assertTrue(StringUtils.deleteWhitespace(actualHtml).contains(deleteWhitespace(expectedHeader)));
  }

  @Test
  void testTransformsNormArticleCorrectly() throws IOException {
    var xml =
        Files.readAllBytes(
            Path.of(
                "src/test/resources/data/LDML/norm/eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/regelungstext-1.xml"));
    var html = service.transformArticle(xml, "hauptteil-1_para-1", RESOURCES_BASE_PATH);
    var expectedArticle =
        """
         <article id="hauptteil-1_para-1" data-period="#meta-1_geltzeiten-1_geltungszeitgr-1">
            <h2 class="einzelvorschrift">
              <span class="akn-num" id="hauptteil-1_para-1_bezeichnung-1" data-marker="1">
                § 1
              </span>
              <span class="akn-heading" id="hauptteil-1_para-1_ueberschrift-1">
                Basic HTML Elements
              </span>
            </h2>
            <section class="akn-paragraph" id="hauptteil-1_para-1_abs-1">
               <span class="akn-num" id="hauptteil-1_para-1_abs-1_bezeichnung-1" data-marker="1"></span>
               <div class="akn-content" id="hauptteil-1_para-1_abs-1_inhalt-1">
                  <p class="akn-p" id="hauptteil-1_para-1_abs-1_inhalt-1_text-1">
                     <b id="hauptteil-1_para-1_abs-1_inhalt-1_text-1_fettschrift-1">Bold</b> text. <i id="hauptteil-1_para-1_abs-1_inhalt-1_text-1_kursiv-1">Italic</i> text. <u id="hauptteil-1_para-1_abs-1_inhalt-1_text-1_u-1">Underlined</u> text. This contains <sub id="hauptteil-1_para-1_abs-1_inhalt-1_text-1_sub-1">subscript</sub> text. This contains <sup id="hauptteil-1_para-1_abs-1_inhalt-1_text-1_sup-1">superscript</sup> text. This text contains a <a href="#" id="hauptteil-1_para-1_abs-1_inhalt-1_text-1_a-1">link</a>. <span class="akn-span" id="hauptteil-1_para-1_abs-1_inhalt-1_text-1_span-1">Inline container</span>. <br id="hauptteil-1_para-1_abs-1_inhalt-1_text-1_br-1"><br id="hauptteil-1_para-1_abs-1_inhalt-1_text-1_br-2">This text has two preceding line breaks.</p>
                  </div>
               </section>
            </article>
        """;
    var expectedDocument = Jsoup.parse(expectedArticle);
    var actualDocument = Jsoup.parse(html);
    assertThat(actualDocument.body().html()).isEqualTo(expectedDocument.body().html());
  }

  @Test
  void testTransformsCaselawBorderNumberCorrectly() throws IOException {
    var actualXml = caseLawLdmlTemplateUtils.getXmlFromTemplate(null);
    var actualHtml = service.transformCaseLaw(actualXml.getBytes(StandardCharsets.UTF_8));
    var expectedBorderNumber =
        """
            <dl class="border-number">
              <dt class="number" id="border-number-link-1">1</dt>
              <dd class="content"><p>Example Tatbestand/CaseFacts. More background</p></dd>
            </dl>
            """;
    String actual = StringUtils.deleteWhitespace(actualHtml);
    assertThat(actual).contains(deleteWhitespace(expectedBorderNumber));

    var otherBorderNumber =
        """
            <dl class="border-number">
               <dt class="number" id="border-number-link-2">2</dt>
               <dd class="content">
                  <p>even more background</p>
               </dd>
            </dl>
            """;
    assertThat(actual).contains(deleteWhitespace(otherBorderNumber));
  }

  @Test
  void testTransformsCaselawTableCorrectlyWithStyles() throws IOException {
    var actualXml = caseLawLdmlTemplateUtils.getXmlFromTemplate(null);
    var actualHtml = service.transformCaseLaw(actualXml.getBytes(StandardCharsets.UTF_8));
    var expectedTable =
        """
            <table cellpadding="2" cellspacing="0">
              <tbody>
                <tr>
                  <td colspan="2" rowspan="1">
                    <p style="text-align:center">
                      <strong>Spalte 1</strong>
                    </p>
                  </td>
                  <td colspan="1" rowspan="1">
                    <p style="text-align:center">
                      <strong>Spalte 2</strong>
                    </p>
                  </td>
                    <td colspan="1" rowspan="1">
                      <p style="text-align:center">
                       <strong>Spalte 3</strong>
                      </p>
                  </td>
                    <td colspan="1" rowspan="1">
                      <p style="text-align:center">
                        <strong>Spalte 4</strong>
                      </p>
                    </td>
                    <td colspan="1" rowspan="1">
                      <p style="text-align:center">
                        <strong>Spalte <br>5- <br>text</strong>
                      </p>
                    </td>
                  </tr>
                </tbody>
              </table>
            """;
    assertThat(StringUtils.deleteWhitespace(actualHtml)).contains(deleteWhitespace(expectedTable));
  }

  @ParameterizedTest
  @CsvSource({"akn:p,p", "akn:div,div", "akn:span,span", "akn:sub,sub", "akn:sup,sup"})
  @DisplayName("Test if AKN tags are transformed to HTML tags correctly")
  void testAknTagsToHtmlTransformation(String input, String expected) throws IOException {
    String text = "<%s>Das ist der Leitsatz</%s>".formatted(input, input);
    Map<String, Object> context = new HashMap<>();
    context.put("outline", text);
    var actualXml = caseLawLdmlTemplateUtils.getXmlFromTemplate(context);
    var actualHtml = service.transformCaseLaw(actualXml.getBytes(StandardCharsets.UTF_8));
    String expectedSubstring = "<%s>Das ist der Leitsatz</%s>".formatted(expected, expected);
    assertThat(actualHtml).contains(expectedSubstring);
  }

  @Test
  void testReturnsPlaceholderSourceInImageTag() throws IOException {
    String image =
        """
            <akn:img src="original/path/to/image" alt="Abbildung" title="bild1.jpg"/>
            """;
    Map<String, Object> context = new HashMap<>();
    context.put("outline", image);

    var actualXml = caseLawLdmlTemplateUtils.getXmlFromTemplate(context);
    var actualHtml = service.transformCaseLaw(actualXml.getBytes(StandardCharsets.UTF_8));
    var expectedImage =
        """
            <img src="/placeholder.png" alt="Platzhalterbild. Das Bild wird bald hinzugefügt.">
            """;
    assertTrue(StringUtils.deleteWhitespace(actualHtml).contains(deleteWhitespace(expectedImage)));
  }
}
