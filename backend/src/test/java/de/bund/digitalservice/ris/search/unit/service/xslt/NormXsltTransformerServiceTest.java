package de.bund.digitalservice.ris.search.unit.service.xslt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.FileTransformationException;
import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.xslt.NormXsltTransformerService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

class NormXsltTransformerServiceTest {
  public static final String RESOURCES_BASE_PATH = "res://";

  private final NormsBucket normsBucketMock = mock(NormsBucket.class);
  private final NormXsltTransformerService service =
      new NormXsltTransformerService(normsBucketMock);

  private final String resourcesPath =
      getClass().getResource("/data/XsltTransformerServiceTest/").getPath();

  final Function<String, ResponseInputStream<GetObjectResponse>> makeInputStream =
      filename -> {
        try {
          final var allBytes = Files.readAllBytes(Path.of(resourcesPath, filename));
          final var stream = new ByteArrayInputStream(allBytes);
          return new ResponseInputStream<>(mock(GetObjectResponse.class), stream);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      };

  @ParameterizedTest(name = "{2}")
  @CsvSource({
    "main,         twoParagraphArticle.xml, twoParagraphArticle.html,                               Should transform an article with two paragraphs",
    "main,         list.xml, list.html,                                                             Should transform list to HTML",
    "main,         fullDepthArticle.xml, fullDepthArticle.html,                                     Should transform full depth article to HTML",
    "main,         nestedArticles.xml, nestedArticles.html,                                         Should transform articles at every level to HTML",
    "main,         formatting.xml, formatting.html,                                                 Should transform an article with formatting to HTML",
    "main,         heading.xml, heading.html,                                                       Should transform a heading with marker inside",
    "main,         authorialNote.xml, authorialNote.html,                                           Should transform authorialNotes to HTML",
    "main,         authorialNoteWithPlacementBase.xml, authorialNoteWithPlacementBase.html,         Should transform authorialNotes with placementBase",
    "main,         authorialNoteInDocTitle.xml, authorialNoteInDocTitle.html,                       Should transform authorialNotes in title",
    "attachment,   authorialNoteInDocParent.xml, authorialNoteInDoc.html,                           Should transform authorialNotes in attachment body",
    "main,         notes.xml, notes.html,                                                           Should transform notes",
    "main,         image.xml, image.html,                                                           Should transform img tag",
    "main,         pdf.xml, pdf.html,                                                               Should transform links with pdf file",
    "main,         noPdfLinks.xml, noPdfLinks.html,                                                 Should not apply any special transformation to non-pdf links",
    "main,         container.xml, container.html,                                                   Should transform container elements in the preface",
    "main,         preambleFormula.xml, preambleFormula.html,                                       Should transform a preamble formula",
    "main,         preambleFormulaNotes.xml, preambleFormulaNotes.html,                             Should output notes of a preamble formula",
    "main,         preambleFormulaAndTocNotes.xml, preambleFormulaAndTocNotes.html,                 Should output notes of a preamble formula and of the official toc in their own containers",
    "main,         conclusionsFormula.xml, conclusionsFormula.html,                                 Should transform a conclusions formula",
    "main,         conclusionsFormulaNotes.xml, conclusionsFormulaNotes.html,                       Should output notes of a conclusions formula",
    "main,         blockList.xml, blockList.html,                                                   Should transform a blockList",
    "main,         preformatted.xml, preformatted.html,                                             Should transform preformatted paragraphs",
    "main,         proprietary.xml, proprietary.html,                                               Should transform proprietary metadata"
  })
  void testTransformNormLegalDocMlFull(
      String type, String inputFileName, String expectedFileName, String testName)
      throws IOException, NoSuchKeyException {
    byte[] bytes = Files.readAllBytes(Path.of(resourcesPath, inputFileName));

    when(normsBucketMock.getStream(
            "eli/bund/bgbl-1/0000/s1000/2000-01-01/1/deu/2000-01-01/authorialNoteInDocAttachment.xml"))
        .thenReturn(makeInputStream.apply("authorialNoteInDocAttachment.xml"));

    var result =
        type.equals("attachment")
            ? service.transformArticle(bytes, "anlagen-n1_anlage-n1", RESOURCES_BASE_PATH)
            : service.transformNorm(bytes, "deu", RESOURCES_BASE_PATH, "regelungstext-1");

    var outputSettings =
        new Document.OutputSettings()
            // Needed so not closing tags in html like <br> are transformed to self-closing tags
            // e.g. </br>
            .syntax(Document.OutputSettings.Syntax.xml)
            .prettyPrint(true);

    var expectedHtml =
        Jsoup.parse(Files.readString(Path.of(resourcesPath, expectedFileName)))
            .outputSettings(outputSettings)
            .body()
            .html();
    var actualHtml = Jsoup.parse(result).outputSettings(outputSettings).body().html();

    // Use xml Diffbuilder to be able to do a structural comparison not affected by whitespaces
    // around nodes
    Diff diff =
        DiffBuilder.compare(expectedHtml)
            .withTest(actualHtml)
            .ignoreWhitespace()
            .checkForIdentical()
            .build();
    assertThat(diff.hasDifferences()).describedAs(diff::toString).isFalse();
  }

  @Test
  void testTransformNormWithAttachments() throws IOException, NoSuchKeyException {
    byte[] bytes = Files.readAllBytes(Path.of(resourcesPath, "attachments.xml"));

    when(normsBucketMock.getStream(
            "eli/bund/bgbl-1/0000/s1000/2000-01-01/1/deu/2000-01-01/anlage-regelungstext-1.xml"))
        .thenReturn(makeInputStream.apply("anlage-regelungstext-1.xml"));
    when(normsBucketMock.getStream(
            "eli/bund/bgbl-1/0000/s1000/2000-01-01/1/deu/2000-01-01/anlage-regelungstext-2.xml"))
        .thenReturn(makeInputStream.apply("anlage-regelungstext-2.xml"));

    var actualHtml =
        service.transformNorm(bytes, "subtype", RESOURCES_BASE_PATH, "regelungstext-1");

    var expectedHtml = Files.readString(Path.of(resourcesPath, "attachments.html"));
    var expectedDocument = Jsoup.parse(expectedHtml);
    var actualDocument = Jsoup.parse(actualHtml);
    assertThat(actualDocument.body().html()).isEqualTo(expectedDocument.body().html());
  }

  @Test
  void testHandlesMissingAttachment() throws IOException {
    Mockito.reset(normsBucketMock);
    // same file as in testTransformNormWithAttachments, but normsBucketMock isn't set up to serve
    // included files
    byte[] bytes = Files.readAllBytes(Path.of(resourcesPath, "attachments.xml"));

    FileTransformationException exception =
        Assertions.assertThrows(
            FileTransformationException.class,
            () -> service.transformNorm(bytes, "subtype", RESOURCES_BASE_PATH, "regelungstext-1"));
    assertThat(exception.getMessage()).endsWith("anlage-regelungstext-1.xml");
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
  @DisplayName("Outputs the same Eingangsformel footnotes in the norm and the single article view")
  void testPreambleFormulaNotesInBothViews() throws IOException, NoSuchKeyException {
    byte[] bytes = Files.readAllBytes(Path.of(resourcesPath, "preambleFormulaNotes.xml"));

    var normView =
        Jsoup.parse(service.transformNorm(bytes, "deu", RESOURCES_BASE_PATH, "regelungstext-1"));
    var articleView =
        Jsoup.parse(service.transformArticle(bytes, "präambel-n1_formel-n1", RESOURCES_BASE_PATH));

    for (Document view : List.of(normView, articleView)) {
      assertThat(view.select("ul.nichtamtliche-fussnoten > li.fussnote").text())
          .isEqualTo("Eingangsformel: Note attached to the Eingangsformel");
      assertThat(view.select("ol.fussnoten > li.fussnote").text())
          .startsWith("* Authorial note in the Eingangsformel");
    }

    // both lists belong to the Eingangsformel, not to the enclosing preamble
    assertThat(normView.select("#präambel-n1_formel-n1 > ul.nichtamtliche-fussnoten")).hasSize(1);
    assertThat(normView.select("#präambel-n1_formel-n1 > ol.fussnoten")).hasSize(1);
  }

  @Test
  @DisplayName("In can parse special characters in ids")
  void testSpecialCharacterInArticleEId() throws IOException {
    byte[] bytes =
        Files.readAllBytes(Path.of(resourcesPath, "articleWithSpecialCharactersInId.xml"));
    var result = service.transformArticle(bytes, "art-z§§ 1 bis 3", RESOURCES_BASE_PATH);

    var actualHtml = Jsoup.parse(result).body().html();

    var expectedHtml =
        Jsoup.parse(
                Files.readString(Path.of(resourcesPath, "articleWithSpecialCharactersInId.html")))
            .body()
            .html();

    assertThat(actualHtml).isEqualTo(expectedHtml);
  }

  @Test
  @DisplayName("In can parse umlaute in article ids")
  void testUmlautInArticleEId() throws IOException {
    byte[] bytes = Files.readAllBytes(Path.of(resourcesPath, "preambleFormulaWithUmlaut.xml"));
    var result = service.transformArticle(bytes, "präambel-1_formel-1", RESOURCES_BASE_PATH);

    var actualHtml = Jsoup.parse(result).body().html();

    var expectedHtml =
        Jsoup.parse(Files.readString(Path.of(resourcesPath, "preambleFormulaWithUmlaut.html")))
            .body()
            .html();

    assertThat(actualHtml).isEqualTo(expectedHtml);
  }

  @Test
  void testTransformsNormTocCorrectly() throws IOException {
    var xml =
        Files.readAllBytes(
            Path.of(
                "src/test/resources/data/LDML/norm/eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/regelungstext-1.xml"));
    var html = service.transformNorm(xml, "subtype", RESOURCES_BASE_PATH, "regelungstext-1");
    var expectedToc =
        """
        <div class="level-1">
         <span class="akn-span" id="präambel-n1_blockcontainer-n1_inhuebs-n1_eintrag-n1_span-n1">Abschnitt 1</span>
        </div>
        <div class="level-2">
         <span class="akn-span" id="präambel-n1_blockcontainer-n1_inhuebs-n1_eintrag-n2_span-n1">Allgemeine Bestimmungen</span>
        </div>
        <div class="level-5">
         <span class="akn-span" id="präambel-n1_blockcontainer-n1_inhuebs-n1_eintrag-n3_span-n1">Art 1</span> <span class="akn-span" id="präambel-n1_blockcontainer-n1_inhuebs-n1_eintrag-n3_span-n2">Test Title</span>
        </div>
        <div class="level-1">
         <span class="akn-span" id="präambel-n1_blockcontainer-n1_inhuebs-n1_eintrag-n4_span-n1">Art 2</span> <span class="akn-span" id="präambel-n1_blockcontainer-n1_inhuebs-n1_eintrag-n4_span-n2">Another Test Title</span>
        </div>""";
    final Document parsed = Jsoup.parse(html);

    String actualToc = parsed.selectXpath("//div[@class='official-toc']").html();
    assertThat(actualToc).isEqualTo(expectedToc);
    String tocHeader = parsed.selectXpath("//div[@class='official-toc']/../span").html();
    assertThat(tocHeader).isEqualTo("Inhaltsübersicht");
  }

  @Test
  void testTransformsNormArticleCorrectly() throws IOException {
    var xml =
        Files.readAllBytes(
            Path.of(
                "src/test/resources/data/LDML/norm/eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/regelungstext-1.xml"));
    var html = service.transformArticle(xml, "art-z1", RESOURCES_BASE_PATH);
    var expectedArticle =
        """
         <div id="art-z1" data-period="#meta-n1_geltzeiten-n1_geltungszeitgr-n1">
            <h2 class="einzelvorschrift">
              <span class="akn-num" id="art-z1_bezeichnung-n1">§ 1</span>
              <span class="akn-heading" id="art-z1_überschrift-n1">
                Basic HTML Elements
              </span>
            </h2>
            <section class="akn-paragraph" id="art-z1_abs-z1">
               <span class="akn-num" id="art-z1_abs-z1_bezeichnung-n1">(1)</span>
               <div class="akn-content" id="art-z1_abs-z1_inhalt-n1">
                  <p class="akn-p" id="art-z1_abs-z1_inhalt-n1_text-n1">
                     <b id="art-z1_abs-z1_inhalt-n1_text-n1_fettschrift-n1">Bold</b> text. <i id="art-z1_abs-z1_inhalt-n1_text-n1_kursiv-n1">Italic</i> text. <u id="art-z1_abs-z1_inhalt-n1_text-n1_u-n1">Underlined</u> text. This contains <sub id="art-z1_abs-z1_inhalt-n1_text-n1_sub-n1">subscript</sub> text. This contains <sup id="art-z1_abs-z1_inhalt-n1_text-n1_sup-n1">superscript</sup> text. This text contains a <a href="#" id="art-z1_abs-z1_inhalt-n1_text-n1_a-n1">link</a>. <span class="akn-span" id="art-z1_abs-z1_inhalt-n1_text-n1_span-n1">Inline container</span>. <br id="art-z1_abs-z1_inhalt-n1_text-n1_br-n1"><br id="art-z1_abs-z1_inhalt-n1_text-n1_br-n2">This text has two preceding line breaks.</p>
                  </div>
               </section>
             </div>
        """;
    var expectedDocument = Jsoup.parse(expectedArticle);
    var actualDocument = Jsoup.parse(html);
    assertThat(actualDocument.body().html()).isEqualTo(expectedDocument.body().html());
  }
}
