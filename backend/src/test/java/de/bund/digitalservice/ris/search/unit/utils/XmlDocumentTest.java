package de.bund.digitalservice.ris.search.unit.utils;

import de.bund.digitalservice.ris.search.utils.XmlDocument;
import java.io.IOException;
import java.util.Optional;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

class XmlDocumentTest {

  @Test
  void testRemoveNodesByXpath() throws ParserConfigurationException, IOException, SAXException {
    String xml = "<xml><test><text>Test</text></test></xml>";
    XmlDocument xmlDocument = new XmlDocument(xml.getBytes());

    Optional<Node> node1 = xmlDocument.getFirstMatchedNodeByXpath("/xml/test/text");
    Assertions.assertTrue(node1.isPresent());
    String text1 =
        xmlDocument.getFirstMatchedNodeByXpath("/xml/test").orElseThrow().getTextContent();
    Assertions.assertEquals("Test", text1);

    xmlDocument.removeNodesByXpath("/xml/test/text");
    Optional<Node> node2 = xmlDocument.getFirstMatchedNodeByXpath("/xml/test/text");
    Assertions.assertEquals(Optional.empty(), node2);
    String text2 =
        xmlDocument.getFirstMatchedNodeByXpath("/xml/test").orElseThrow().getTextContent();

    Assertions.assertEquals("", text2);
  }

  @Test
  void testFirstMatchedNode() throws ParserConfigurationException, IOException, SAXException {
    String xml = "<xml><test><text>Test</text></test></xml>";
    XmlDocument xmlDocument = new XmlDocument(xml.getBytes());
    Optional<Node> actual = xmlDocument.getFirstMatchedNodeByXpath(".//*[local-name()='text']");

    Assertions.assertEquals("Test", actual.orElseThrow().getTextContent());
  }

  @Test
  void testFirstMatchedNodeNested() throws ParserConfigurationException, IOException, SAXException {
    String xml = "<xml><test><text>Test</text></test></xml>";
    XmlDocument xmlDocument = new XmlDocument(xml.getBytes());
    Optional<Node> parent = xmlDocument.getFirstMatchedNodeByXpath("/xml/test");
    Optional<Node> nested =
        xmlDocument.getFirstMatchedNodeByXpath(".//*[local-name()='text']", parent.orElseThrow());
    Assertions.assertEquals("Test", nested.orElseThrow().getTextContent());
  }

  @Test
  void testFirstMatchedNodeNullpointerReturnsEmptyOption()
      throws ParserConfigurationException, IOException, SAXException {
    String xml = "<xml><test><text>Test</text></test></xml>";
    XmlDocument xmlDocument = new XmlDocument(xml.getBytes());
    Optional<Node> actual = xmlDocument.getFirstMatchedNodeByXpath(".//*[local-name()='notFound']");

    Assertions.assertEquals(Optional.empty(), actual);
  }

  @Test
  void testFirstMatchedNodeXPathExpressionExceptionReturnsEmptyOption()
      throws ParserConfigurationException, IOException, SAXException {
    String xml = "<xml><test><text>Test</text></test></xml>";
    XmlDocument xmlDocument = new XmlDocument(xml.getBytes());
    Optional<Node> actual = xmlDocument.getFirstMatchedNodeByXpath("<>");

    Assertions.assertEquals(Optional.empty(), actual);
  }

  @Test
  void testReplaceNodesByXpathWithString()
      throws ParserConfigurationException, IOException, SAXException {
    String xml = "<xml><test><text>Test</text></test></xml>";
    XmlDocument xmlDocument = new XmlDocument(xml.getBytes());

    String text1 =
        xmlDocument.getFirstMatchedNodeByXpath("/xml/test").orElseThrow().getTextContent();
    Assertions.assertEquals("Test", text1);

    xmlDocument.replaceNodesByXpathWithString("/xml/test/text", "New Text");
    String text2 =
        xmlDocument.getFirstMatchedNodeByXpath("/xml/test").orElseThrow().getTextContent();

    Assertions.assertEquals("New Text", text2);
  }
}
