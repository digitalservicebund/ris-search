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
  private final byte[] exampleLiteratureBytes;

  LiteratureXsltTransformerServiceTest() throws IOException {
    String resourcesBasePath = Objects.requireNonNull(getClass().getResource("/data")).getPath();
    exampleLiteratureBytes =
        Files.readAllBytes(Path.of(resourcesBasePath + "/literature-example.xml"));
  }

  @Test
  void testTransformsMainTitleCorrectly() {
    var result = service.transformLiterature(exampleLiteratureBytes);

    Document doc = Jsoup.parse(result);

    Elements h1Elements = doc.select("html > body > h1");

    assertThat(h1Elements).hasSize(1);
    assertThat(Objects.requireNonNull(h1Elements.first()).text())
        .isEqualTo("Erstes Test-Dokument ULI");
  }

  @Test
  void testTransformsOutlineCorrectly() {
    var result = service.transformLiterature(exampleLiteratureBytes);

    Document doc = Jsoup.parse(result);

    Elements h2Elements = doc.select("html > body > h2");
    assertThat(Objects.requireNonNull(h2Elements.first()).text()).isEqualTo("Gliederung");

    Elements outlineItems = doc.select("li");
    assertThat(outlineItems).hasSize(3);
    assertThat(outlineItems.stream().map(Element::text))
        .containsExactly("I. Problemstellung.", "II. Lösung.", "III. Zusammenfassung.");
  }

  @Test
  void testTransformsShortReportCorrectly() {
    var result = service.transformLiterature(exampleLiteratureBytes);

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
}
