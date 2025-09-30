package de.bund.digitalservice.ris.search.unit.service.xslt;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.service.xslt.LiteratureXsltTransformerService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

class LiteratureXsltTransformerServiceTest {
  private final LiteratureXsltTransformerService service = new LiteratureXsltTransformerService();

  private final String resourcesBasePath =
      Objects.requireNonNull(getClass().getResource("/data/xslt/literature")).getPath();
  private final byte[] literatureExample1 =
      Files.readAllBytes(Path.of(resourcesBasePath + "/literature-example-1.xml"));
  private final byte[] literatureExample2 =
      Files.readAllBytes(Path.of(resourcesBasePath + "/literature-example-2.xml"));
  private final byte[] literatureExample3 =
      Files.readAllBytes(Path.of(resourcesBasePath + "/literature-example-3.xml"));

  LiteratureXsltTransformerServiceTest() throws IOException {}

  @Test
  void testTransformsMainTitleAsH1() {
    var result = service.transformLiterature(literatureExample1);

    Document doc = Jsoup.parse(result);

    Elements h1Elements = doc.select("html > body > h1");

    assertThat(h1Elements).hasSize(1);
    assertThat(Objects.requireNonNull(h1Elements.first()).text())
        .isEqualTo("Erstes Test-Dokument ULI");
  }

  @Test
  void testTransformsDocumentaryTitleAsH3IfMainTitleExists() {
    var result = service.transformLiterature(literatureExample1);

    Document doc = Jsoup.parse(result);

    Elements h3Elements = doc.select("html > body > h3");

    assertThat(h3Elements).hasSize(1);
    assertThat(Objects.requireNonNull(h3Elements.first()).text())
        .isEqualTo("Dokumentarischer Test-Titel");
  }

  @Test
  void testTransformsDocumentaryTitleAsH1IfMainTitleDoesNotExist() {
    var result = service.transformLiterature(literatureExample2);

    Document doc = Jsoup.parse(result);

    Elements h1Elements = doc.select("html > body > h1");

    assertThat(h1Elements).hasSize(1);
    assertThat(Objects.requireNonNull(h1Elements.first()).text())
        .isEqualTo("Dokumentarischer Test-Titel");

    Elements h3Elements = doc.select("html > body > h3");
    assertThat(h3Elements).isEmpty();
  }

  @Test
  void testTransformsOutline() {
    var result = service.transformLiterature(literatureExample1);

    Document doc = Jsoup.parse(result);

    Elements h2Elements = doc.select("html > body > h2");
    assertThat(Objects.requireNonNull(h2Elements.first()).text()).isEqualTo("Gliederung");

    Elements outlineItems = doc.select("li");
    assertThat(outlineItems).hasSize(3);
    assertThat(outlineItems.stream().map(Element::text))
        .containsExactly("I. Problemstellung.", "II. Lösung.", "III. Zusammenfassung.");
  }

  @Test
  void testDoesNotAddOutlineSectionIfOutlineDoesNotExist() {
    var result = service.transformLiterature(literatureExample2);

    Document doc = Jsoup.parse(result);

    Elements h2Elements = doc.select("html > body > h2");
    assertThat(h2Elements.stream().map(Element::text)).isNotEmpty().doesNotContain("Gliederung");
  }

  @Test
  void testTransformsShortReport() {
    var result = service.transformLiterature(literatureExample1);

    Document doc = Jsoup.parse(result);

    Elements h2Elements = doc.select("html > body > h2");
    assertThat(Objects.requireNonNull(h2Elements.get(1)).text()).isEqualTo("Kurzrefarat");

    Elements divs = doc.select("div");
    assertThat(divs).hasSize(1);
    Element div = divs.getFirst();
    String divText = div.html().stripIndent();
    assertThat(divText)
        .isEqualTo(
            """
            <p>A <a href="http://www.foo.de">foo</a> is <span>bar</span>. <br>
              Bar <sub>baz</sub> or <sup>bas</sup>.</p> Bar.
            <p>1. <em>EM</em> 2. hlj 3. noindex 4. <strong>strong</strong></p>"""
                .stripIndent());
  }

  @Test
  void testDoesNotAddShortReportSectionIfShortReportDoesNotExist() {
    var result = service.transformLiterature(literatureExample3);

    Document doc = Jsoup.parse(result);

    Elements h2Elements = doc.select("html > body > h2");
    assertThat(h2Elements.stream().map(Element::text)).isNotEmpty().doesNotContain("Kurzrefarat");
  }
}
