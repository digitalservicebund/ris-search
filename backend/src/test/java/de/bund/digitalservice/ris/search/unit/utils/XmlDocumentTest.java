package de.bund.digitalservice.ris.search.unit.utils;

import de.bund.digitalservice.ris.search.utils.XmlDocument;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Node;

class XmlDocumentTest {

  private static Stream<Function<byte[], XmlDocument>> xmlDocumentFromBytesSupplier() {
    return Stream.of(
        inputBytes -> {
          try {
            return XmlDocument.fromNormBytes(inputBytes);
          } catch (Exception e) {
            return null;
          }
        },
        inputBytes -> {
          try {
            return XmlDocument.fromLiteratureBytes(inputBytes);
          } catch (Exception e) {
            return null;
          }
        });
  }

  @ParameterizedTest
  @MethodSource("xmlDocumentFromBytesSupplier")
  void testRemoveNodesByXpath(Function<byte[], XmlDocument> constructor) {
    String xml = "<xml><test><text>Test</text></test></xml>";
    XmlDocument xmlDocument = constructor.apply(xml.getBytes());

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

  @ParameterizedTest
  @MethodSource("xmlDocumentFromBytesSupplier")
  void testFirstMatchedNode(Function<byte[], XmlDocument> constructor) {
    String xml = "<xml><test><text>Test</text></test></xml>";
    XmlDocument xmlDocument = constructor.apply(xml.getBytes());
    Optional<Node> actual = xmlDocument.getFirstMatchedNodeByXpath(".//*[local-name()='text']");

    Assertions.assertEquals("Test", actual.orElseThrow().getTextContent());
  }

  @ParameterizedTest
  @MethodSource("xmlDocumentFromBytesSupplier")
  void testFirstMatchedNodeNested(Function<byte[], XmlDocument> constructor) {
    String xml = "<xml><test><text>Test</text></test></xml>";
    XmlDocument xmlDocument = constructor.apply(xml.getBytes());
    Optional<Node> parent = xmlDocument.getFirstMatchedNodeByXpath("/xml/test");
    Optional<Node> nested =
        xmlDocument.getFirstMatchedNodeByXpath(".//*[local-name()='text']", parent.orElseThrow());
    Assertions.assertEquals("Test", nested.orElseThrow().getTextContent());
  }

  @ParameterizedTest
  @MethodSource("xmlDocumentFromBytesSupplier")
  void testFirstMatchedNodeNullpointerReturnsEmptyOption(
      Function<byte[], XmlDocument> constructor) {
    String xml = "<xml><test><text>Test</text></test></xml>";
    XmlDocument xmlDocument = constructor.apply(xml.getBytes());
    Optional<Node> actual = xmlDocument.getFirstMatchedNodeByXpath(".//*[local-name()='notFound']");

    Assertions.assertEquals(Optional.empty(), actual);
  }

  @ParameterizedTest
  @MethodSource("xmlDocumentFromBytesSupplier")
  void testFirstMatchedNodeXPathExpressionExceptionReturnsEmptyOption(
      Function<byte[], XmlDocument> constructor) {
    String xml = "<xml><test><text>Test</text></test></xml>";
    XmlDocument xmlDocument = constructor.apply(xml.getBytes());
    Optional<Node> actual = xmlDocument.getFirstMatchedNodeByXpath("<>");

    Assertions.assertEquals(Optional.empty(), actual);
  }

  @ParameterizedTest
  @MethodSource("xmlDocumentFromBytesSupplier")
  void testReplaceNodesByXpathWithString(Function<byte[], XmlDocument> constructor) {
    String xml = "<xml><test><text>Test</text></test></xml>";
    XmlDocument xmlDocument = constructor.apply(xml.getBytes());

    String text1 =
        xmlDocument.getFirstMatchedNodeByXpath("/xml/test").orElseThrow().getTextContent();
    Assertions.assertEquals("Test", text1);

    xmlDocument.replaceNodesByXpathWithString("/xml/test/text", "New Text");
    String text2 =
        xmlDocument.getFirstMatchedNodeByXpath("/xml/test").orElseThrow().getTextContent();

    Assertions.assertEquals("New Text", text2);
  }
}
