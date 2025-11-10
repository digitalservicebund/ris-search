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
  @CsvSource(
      value = {
        "uli/example1/literature.xml| uli/example1/literature.html| Transforms literature with mainTitle, alternativeHeadline, outline and mainBody",
        "uli/example2/literature.xml| uli/example2/literature.html| Transforms literature with only alternativeHeadline and mainBody",
        "uli/example3/literature.xml| uli/example3/literature.html| Transforms literature with only mainTitle and outline",
        "uli/example4/literature.xml| uli/example4/literature.html| Transforms literature with mainTitle, alternativeHeadline and mainTitleAdditions",
        "uli/example5/literature.xml| uli/example5/literature.html| Transforms literature with alternativeHeadline and mainTitleAdditions",
        "uli/example6/literature.xml| uli/example6/literature.html| Transforms literature with mainTitleAdditions",
        "uli/example7/literature.xml| uli/example7/literature.html| Transforms literature with mainTitle and mainTitleAdditions",
        "uli/example8/literature.xml| uli/example8/literature.html| Transforms literature citations",
      },
      delimiter = '|')
  void testTransformLiteratureXmlDocuments(
      String inputFileName, String expectedFileName, String testName) throws IOException {
    byte[] bytes = Files.readAllBytes(Path.of(resourcesBasePath, inputFileName));

    var result = service.transformLiterature(bytes);

    var expectedHtml = Files.readString(Path.of(resourcesBasePath, expectedFileName));
    var expectedDocument = Jsoup.parse(expectedHtml);
    var actualDocument = Jsoup.parse(result);
    assertThat(actualDocument.html()).isEqualTo(expectedDocument.html());
  }
}
