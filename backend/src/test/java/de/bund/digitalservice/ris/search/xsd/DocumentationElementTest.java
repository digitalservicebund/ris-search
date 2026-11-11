package de.bund.digitalservice.ris.search.xsd;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
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

    return (Element) parseSchema(xml).getElementsByTagName("xs:documentation").item(0);
  }

  private Document parseSchema(String xml) throws Exception {
    // Namespace-aware, like the factory in XSDDescriptionParser.
    var factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    return factory
        .newDocumentBuilder()
        .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
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

    var documentationElement = new DocumentationElement(element);

    assertThat(documentationElement.getDocumentation()).isEqualTo("Intro text\n\n* Eins\n* Zwei");
  }

  @Test
  void documentationNestedInAnAttributeIsNotAttributedToTheEnclosingType() throws Exception {
    // Reproduces a real XSD shape: an xs:attribute's own documentation sits inside the same
    // complexType as the type's own top-level documentation. Only the latter should resolve to
    // the type's name; the former describes the attribute, not the type.
    var document =
        parseSchema(
            """
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
              <xs:complexType name="aktenzeichen">
                <xs:annotation>
                  <xs:documentation>Aktenzeichen der Entscheidung</xs:documentation>
                </xs:annotation>
                <xs:attribute name="refersTo">
                  <xs:annotation>
                    <xs:documentation>Das primäre Aktenzeichen ist hierdurch gekennzeichnet.</xs:documentation>
                  </xs:annotation>
                </xs:attribute>
              </xs:complexType>
            </xs:schema>
            """);

    var documentationElements = document.getElementsByTagName("xs:documentation");
    var typeLevelDocumentation = (Element) documentationElements.item(0);
    var attributeLevelDocumentation = (Element) documentationElements.item(1);

    assertThat(new DocumentationElement(typeLevelDocumentation).getTypeName())
        .isEqualTo("aktenzeichen");
    assertThat(new DocumentationElement(attributeLevelDocumentation).getTypeName()).isNull();
  }

  @Test
  void documentationOfAnEnumerationValueKnowsItsValueAndType() throws Exception {
    var document =
        parseSchema(
            """
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
              <xs:simpleType name="referenzRichtungValue">
                <xs:annotation>
                  <xs:documentation>Richtung der Referenz</xs:documentation>
                </xs:annotation>
                <xs:restriction base="xs:string">
                  <xs:enumeration value="aktiv">
                    <xs:annotation>
                      <xs:documentation>Dieses Dokument zitiert.</xs:documentation>
                    </xs:annotation>
                  </xs:enumeration>
                </xs:restriction>
              </xs:simpleType>
            </xs:schema>
            """);

    var documentationElements = document.getElementsByTagName("xs:documentation");
    var typeDocumentation = new DocumentationElement((Element) documentationElements.item(0));
    var valueDocumentation = new DocumentationElement((Element) documentationElements.item(1));

    assertThat(typeDocumentation.getTypeName()).isEqualTo("referenzRichtungValue");
    assertThat(typeDocumentation.getEnumerationValue()).isNull();
    assertThat(valueDocumentation.getTypeName()).isEqualTo("referenzRichtungValue");
    assertThat(valueDocumentation.getEnumerationValue()).isEqualTo("aktiv");
  }
}
