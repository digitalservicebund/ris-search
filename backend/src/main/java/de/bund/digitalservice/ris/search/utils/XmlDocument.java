package de.bund.digitalservice.ris.search.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.AbstractList;
import java.util.List;
import java.util.Optional;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class facilitates the processing and manipulation of XML documents using the DOM (Document
 * Object Model) and XPath expressions.
 *
 * <p>It provides methods to parse, query, modify, and extract data from XML content, ensuring
 * secure processing by disabling potentially risky features such as external entities. Namespace
 * contexts are supported to handle XML namespaces.
 */
public class XmlDocument {
  private final Document document;
  private final XPath xpathInstance = XPathFactory.newInstance().newXPath();
  private final Logger logger = LogManager.getLogger(XmlDocument.class);

  /**
   * Constructs an XmlDocument object by parsing a byte array containing XML content. Configures a
   * namespace-aware and secure XML parser, and initializes the document field with the parsed XML
   * content.
   *
   * @param content the byte array containing the XML document content to be parsed
   * @throws ParserConfigurationException if a DocumentBuilder cannot be created which satisfies the
   *     configuration requested
   * @throws IOException if an input or output exception occurs during the parsing process
   * @throws SAXException if any SAX errors occur while parsing the XML content
   */
  public XmlDocument(byte[] content)
      throws ParserConfigurationException, IOException, SAXException {
    xpathInstance.setNamespaceContext(new LegalDocMLDeContext());
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(content));
  }

  /**
   * Constructs an XmlDocument object by creating a new Document from the given Node. Configures
   * secure XML processing and namespace support during the document creation.
   *
   * @param node the Node to be imported and used to create a new DOM Document
   * @throws ParserConfigurationException if a DocumentBuilder cannot be created with the required
   *     configuration
   */
  public XmlDocument(Node node) throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    factory.setNamespaceAware(true);
    xpathInstance.setNamespaceContext(new LegalDocMLDeContext());
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document newDocument = builder.newDocument();
    Node importedNode = newDocument.importNode(node, true);
    newDocument.appendChild(importedNode);
    document = newDocument;
  }

  /**
   * Retrieves the text content of the first XML element that matches the given XPath expression. If
   * the XPath expression does not match any nodes, the method returns {@code null}.
   *
   * @param xpath the XPath expression to evaluate against the XML document
   * @return the text content of the matching XML element, or {@code null} if no match is found
   * @throws XPathExpressionException if the given XPath expression cannot be evaluated
   */
  public String getSimpleElementByXpath(String xpath) throws XPathExpressionException {
    Node node = (Node) xpathInstance.evaluate(xpath, document, XPathConstants.NODE);
    return node != null ? node.getTextContent() : null;
  }

  /**
   * Evaluates the given XPath expression against the XML document and determines whether any nodes
   * match the expression.
   *
   * @param xpath the XPath expression to be evaluated
   * @return {@code true} if the XPath expression matches one or more nodes in the document,
   *     otherwise {@code false}
   * @throws XPathExpressionException if the given XPath expression cannot be evaluated
   */
  public boolean getElementExistByXpath(String xpath) throws XPathExpressionException {
    return (boolean) xpathInstance.evaluate(xpath, document, XPathConstants.BOOLEAN);
  }

  /**
   * Evaluates the given XPath expression against the XML document and returns a NodeList containing
   * all nodes that match the expression.
   *
   * @param xpath the XPath expression to be evaluated
   * @return a NodeList containing all nodes that match the given XPath expression
   * @throws XPathExpressionException if the given XPath expression cannot be evaluated
   */
  public NodeList getNodesByXpath(String xpath) throws XPathExpressionException {
    return (NodeList) xpathInstance.evaluate(xpath, document, XPathConstants.NODESET);
  }

  /**
   * Retrieves the first XML node that matches the given XPath expression. If no matching node is
   * found, an empty {@code Optional} is returned.
   *
   * @param xpath the XPath expression used to search for a matching node
   * @return an {@code Optional} containing the first matching node, or empty if no match is found
   */
  public Optional<Node> getFirstMatchedNodeByXpath(String xpath) {
    return getFirstMatchedNodeByXpath(xpath, document);
  }

  /**
   * Retrieves the first XML node that matches the given XPath expression within the context of the
   * provided Node. If no matching node is found, an empty {@code Optional} is returned.
   *
   * @param xpath the XPath expression used to search for a matching node
   * @param item the context Node against which the XPath expression will be evaluated
   * @return an {@code Optional} containing the first matching node, or empty if no match is found
   */
  public Optional<Node> getFirstMatchedNodeByXpath(String xpath, Node item) {
    try {
      return Optional.ofNullable((Node) xpathInstance.evaluate(xpath, item, XPathConstants.NODE));
    } catch (XPathExpressionException e) {
      return Optional.empty();
    }
  }

  /**
   * Converts a NodeList into a List of Node objects, providing an AbstractList implementation for
   * easy iteration and access.
   *
   * @param nodes the NodeList to be converted into a List
   * @return a List of Node objects corresponding to the provided NodeList
   */
  public static List<Node> asList(NodeList nodes) {
    return new AbstractList<>() {

      @Override
      public int size() {
        return nodes.getLength();
      }

      @Override
      public Node get(int index) {
        return nodes.item(index);
      }
    };
  }

  /**
   * Retrieves the text content of the first XML element that matches the given XPath expression. If
   * the XPath expression cannot be evaluated or does not match any nodes, the method logs a warning
   * and returns {@code null}.
   *
   * @param xpath the XPath expression to evaluate against the XML document
   * @return the text content of the matching XML element, or {@code null} if no match is found or
   *     an error occurs
   */
  public String getElementByXpath(String xpath) {
    try {
      return getSimpleElementByXpath(xpath);
    } catch (XPathExpressionException exception) {
      logger.warn(String.format("Error finding element by xpath: %s", xpath), exception);
      return null;
    }
  }

  /**
   * Returns the text value of a node, excluding text in any child nodes.
   *
   * @param node the node to extract text from
   * @return the text value of the node, excluding text in any child nodes
   */
  public static String extractDirectChildText(Node node) {
    StringBuilder textContent = new StringBuilder();
    NodeList childNodes = node.getChildNodes();

    for (int i = 0; i < childNodes.getLength(); i++) {
      Node n = childNodes.item(i);
      if (n.getNodeType() == Node.TEXT_NODE) {
        textContent.append(n.getNodeValue());
        textContent.append(" ");
      }
    }
    return textContent.toString();
  }

  /**
   * Extracts and cleans the concatenated text content of all XML nodes that match the given XPath
   * expression. The resulting text is space-normalized, replacing multiple consecutive whitespace
   * characters with a single space, and trims leading or trailing spaces.
   *
   * @param xPath the XPath expression used to select the nodes from which text content is extracted
   * @return a single string containing the normalized and concatenated text content of the matching
   *     nodes
   * @throws XPathExpressionException if the given XPath expression cannot be evaluated
   */
  public String extractCleanedText(String xPath) throws XPathExpressionException {
    NodeList nodes = (NodeList) xpathInstance.evaluate(xPath, document, XPathConstants.NODESET);
    final List<String> textItems =
        XmlDocument.asList(nodes).stream().map(Node::getTextContent).toList();
    return String.join(" ", textItems).replaceAll("\\s+", " ").trim();
  }

  /**
   * Removes all XML nodes from the document that match the specified XPath expression. This method
   * evaluates the provided XPath expression and deletes each matching node found within the
   * document, including its children.
   *
   * <p>If the XPath expression is invalid or cannot be evaluated, a warning is logged and no nodes
   * are removed.
   *
   * @param xPath the XPath expression used to locate the nodes to be removed
   */
  public void removeNodesByXpath(String xPath) {
    try {
      NodeList nodes = getNodesByXpath(xPath);
      for (int i = 0; i < nodes.getLength(); i++) {
        Node node = nodes.item(i);
        node.getParentNode().removeChild(node);
      }
    } catch (XPathExpressionException exception) {
      logger.warn(String.format("Error removing nodes by xpath: %s", xPath), exception);
    }
  }

  /**
   * Replaces all XML nodes that match the specified XPath expression with a text node containing
   * the provided replacement string.
   *
   * <p>This method evaluates the provided XPath expression to select the nodes to be replaced. Each
   * matching node is removed, and a new text node is inserted in its place. If the XPath expression
   * is invalid or cannot be evaluated, a warning is logged.
   *
   * @param xPath the XPath expression used to locate the nodes to be replaced
   * @param replacement the string content to be used as the replacement for the matched nodes
   */
  public void replaceNodesByXpathWithString(String xPath, String replacement) {
    try {
      NodeList nodes = getNodesByXpath(xPath);
      for (int i = 0; i < nodes.getLength(); i++) {
        Node node = nodes.item(i);
        node.getParentNode().replaceChild(document.createTextNode(replacement), node);
      }
    } catch (XPathExpressionException exception) {
      logger.warn(String.format("Error removing nodes by xpath: %s", xPath), exception);
    }
  }
}
