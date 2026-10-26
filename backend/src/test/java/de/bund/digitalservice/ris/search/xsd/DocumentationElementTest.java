package de.bund.digitalservice.ris.search.xsd;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

class DocumentationElementTest {

  private Element parseDocumentationElement(String documentationInnerXml) throws Exception {
    String xml =
        """
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
          <xs:complexType name="someType">
            <xs:annotation>
              <xs:documentation>%s</xs:documentation>
            </xs:annotation>
          </xs:complexType>
        </xs:schema>
        """
            .formatted(documentationInnerXml);

    var document =
        DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

    return (Element) document.getElementsByTagName("xs:documentation").item(0);
  }

  @Test
  void stripsSourceIndentationFromEachLineButKeepsLineBreaksAndBulletMarkers() throws Exception {
    var element =
        parseDocumentationElement(
            """

                Intro text

                * Eins
                * Zwei
            """);

    var documentationElement = new DocumentationElement(element, "");

    assertThat(documentationElement.getDocumentation()).isEqualTo("Intro text\n\n* Eins\n* Zwei");
  }
}
