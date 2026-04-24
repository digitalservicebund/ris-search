package de.bund.digitalservice.ris.search.unit.utils;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.models.ldml.caselaw.JaxbHtml;
import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class JaxbHtmlTest {

  @Test
  @DisplayName("build() should return null for empty or null inputs")
  void testBuildWithEmptyInputs() {
    assertThat(JaxbHtml.build(null)).isNull();
    assertThat(JaxbHtml.build(Collections.emptyList())).isNull();
    assertThat(JaxbHtml.build(Collections.singletonList(null))).isNull();
  }

  @Test
  @DisplayName("build() should return a JaxbHtml instance when valid list provided")
  void testBuildWithValidInput() {
    List<Object> content = List.of("<span>Test</span>");
    JaxbHtml result = JaxbHtml.build(content);

    assertThat(result).isNotNull();
    assertThat(result.getHtml()).containsExactly("<span>Test</span>");
  }

  @Test
  void testToHtmlStringConvertsContentAndRemovesNamespaces() throws Exception {
    String xmlSnippet = "<div xmlns:ns=\"http://example.com\">Content</div>";
    Document doc =
        DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(new ByteArrayInputStream(xmlSnippet.getBytes()));
    Element element = doc.getDocumentElement();

    JaxbHtml jaxbHtml = new JaxbHtml(List.of(element));

    String result = jaxbHtml.toHtmlString();

    assertThat(result).contains("<div>Content</div>").doesNotContain("xmlns:ns");
  }
}
