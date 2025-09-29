package de.bund.digitalservice.ris.search.unit.service.xslt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.shaded.org.apache.commons.lang3.StringUtils.deleteWhitespace;

import de.bund.digitalservice.ris.search.service.xslt.LiteratureXsltTransformerService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LiteratureXsltTransformerServiceTest {
  private final LiteratureXsltTransformerService service = new LiteratureXsltTransformerService();
  private final String resourcesBasePath = getClass().getResource("/data").getPath();

  private byte[] getExampleListeratureAsBytes() {}

  @Test
  void testTransformsCaselawHeaderCorrectly() throws IOException {
    var actualXml = caseLawLdmlTemplateUtils.getXmlFromTemplate(null);
    var actualHtml =
        service.transformCaseLaw(actualXml.getBytes(StandardCharsets.UTF_8), "api/v1/");
    var expectedHeader =
        """
        <h1 id="title">
          <p>Title</p>
        </h1>
        """;
    assertTrue(StringUtils.deleteWhitespace(actualHtml).contains(deleteWhitespace(expectedHeader)));
  }

  @Test
  void testTransformsCaselawBorderNumberCorrectly() throws IOException {
    var actualXml = caseLawLdmlTemplateUtils.getXmlFromTemplate(null);
    var actualHtml =
        service.transformCaseLaw(actualXml.getBytes(StandardCharsets.UTF_8), "api/v1/");
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
    var actualHtml =
        service.transformCaseLaw(actualXml.getBytes(StandardCharsets.UTF_8), "api/v1/");
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
    var actualHtml =
        service.transformCaseLaw(actualXml.getBytes(StandardCharsets.UTF_8), "api/v1/");
    String expectedSubstring = "<%s>Das ist der Leitsatz</%s>".formatted(expected, expected);
    assertThat(actualHtml).contains(expectedSubstring);
  }

  @Test
  void testReturnsSourceInImageTagWhenPresent() throws IOException {
    String image =
        """
            <akn:img src="bild1.jpg" alt="Abbildung" title="bild1.jpg"/>
            """;
    Map<String, Object> context = new HashMap<>();
    context.put("outline", image);

    var actualXml = caseLawLdmlTemplateUtils.getXmlFromTemplate(context);
    var actualHtml =
        service.transformCaseLaw(actualXml.getBytes(StandardCharsets.UTF_8), "api/v1/");
    var expectedImage =
        """
            <img src="api/v1/bild1.jpg" alt="Abbildung" title="bild1.jpg">
            """;
    assertTrue(StringUtils.deleteWhitespace(actualHtml).contains(deleteWhitespace(expectedImage)));
  }
}
