package de.bund.digitalservice.ris.search.unit.service.xslt;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.service.xslt.LiteratureXsltTransformerService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.jsoup.Jsoup;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LiteratureXsltTransformerServiceTest {
  private final LiteratureXsltTransformerService service = new LiteratureXsltTransformerService();

  private final String resourcesBasePath =
      Objects.requireNonNull(getClass().getResource("/data/xslt/literature")).getPath();

  LiteratureXsltTransformerServiceTest() throws IOException {}

  @ParameterizedTest(name = "{2}")
  @CsvSource({
    "uli/example1/literature.xml, uli/example1/literature.html, Should transform literature with mainTitel, documentaryTitle, outline and mainBody",
    "uli/example2/literature.xml, uli/example2/literature.html, Should transform literature with only documentaryTitle and mainBody",
    "uli/example3/literature.xml, uli/example3/literature.html, Should transform literature with only mainTitel and outline",
  })
  void testTransformLiteratureXmlDocuments(
      String inputFileName, String expectedFileName, String testName) throws IOException {
    byte[] bytes = Files.readAllBytes(Path.of(resourcesBasePath, inputFileName));

    var result = service.transformLiterature(bytes);

    var expectedHtml = Files.readString(Path.of(resourcesBasePath, expectedFileName));
    var expectedDocument = Jsoup.parse(expectedHtml);
    var actualDocument = Jsoup.parse(result);
    assertThat(actualDocument.body().html()).isEqualTo(expectedDocument.body().html());
  }
}
