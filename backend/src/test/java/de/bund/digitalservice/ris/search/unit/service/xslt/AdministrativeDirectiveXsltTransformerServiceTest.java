package de.bund.digitalservice.ris.search.unit.service.xslt;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.service.xslt.AdministrativeDirectiveXsltTransformerService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.jsoup.Jsoup;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AdministrativeDirectiveXsltTransformerServiceTest {
  private final AdministrativeDirectiveXsltTransformerService service =
      new AdministrativeDirectiveXsltTransformerService();

  private final String resourcesBasePath =
      Objects.requireNonNull(getClass().getResource("/data/xslt/administrative_directive"))
          .getPath();

  AdministrativeDirectiveXsltTransformerServiceTest() throws IOException {}

  @ParameterizedTest(name = "{1}")
  @CsvSource(
      value = {
        "example1 | transforms title, shortReport and content",
        "example2 | transforms directive without title",
        "example3 | transforms directive without shortReport",
        "example4 | transforms directive without content",
        "example5 | transforms directive with active references",
        "example6 | transforms directive with caselaw references",
        "example7 | transforms directive with active references and caselaw references",
        "example8 | transforms directive with footnotes",
        "example9 | transforms directive with references and footnotes",
      },
      delimiter = '|')
  void testTransformLiteratureXmlDocuments(String testfilesDir, String testName)
      throws IOException {
    Path inputFilePath = Paths.get(resourcesBasePath, testfilesDir, "directive.xml");
    Path expectedFilePath = Paths.get(resourcesBasePath, testfilesDir, "directive.html");

    byte[] bytes = Files.readAllBytes(inputFilePath);
    var result = service.transform(bytes);

    var expectedHtml = Files.readString(expectedFilePath);
    var expectedDocument = Jsoup.parse(expectedHtml);
    var actualDocument = Jsoup.parse(result);
    assertThat(actualDocument.html()).isEqualTo(expectedDocument.html());
  }
}
