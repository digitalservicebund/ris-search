package de.bund.digitalservice.ris.search.unit.service.xslt;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.service.xslt.AdministrativeDirectiveXsltTransformerService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

  @ParameterizedTest(name = "{2}")
  @CsvSource(
      value = {
        "KSNR0000.akn.xml| placeholder.html| transform",
      },
      delimiter = '|')
  void testTransformLiteratureXmlDocuments(
      String inputFileName, String expectedFileName, String testName) throws IOException {
    byte[] bytes = Files.readAllBytes(Path.of(resourcesBasePath, inputFileName));

    var result = service.transform(bytes);

    var actualDocument = Jsoup.parse(result);
    assertThat(actualDocument).isNotNull();
  }
}
