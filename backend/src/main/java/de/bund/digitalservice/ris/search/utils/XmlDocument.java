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

public class XmlDocument {
  private Document document;
  private final XPath xpathInstance = XPathFactory.newInstance().newXPath();
  private final Logger logger = LogManager.getLogger(XmlDocument.class);

  public XmlDocument(byte[] content)
      throws ParserConfigurationException, IOException, SAXException {
    xpathInstance.setNamespaceContext(new LegalDocMLDeContext());
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(content));
  }

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

  public String getSimpleElementByXpath(String xpath) throws XPathExpressionException {
    Node node = (Node) xpathInstance.evaluate(xpath, document, XPathConstants.NODE);
    return node != null ? node.getTextContent() : null;
  }

  public NodeList getNodesByXpath(String xpath) throws XPathExpressionException {
    return (NodeList) xpathInstance.evaluate(xpath, document, XPathConstants.NODESET);
  }

  public Optional<Node> getFirstMatchedNodeByXpath(String xpath) {
    return getFirstMatchedNodeByXpath(xpath, document);
  }

  public Optional<Node> getFirstMatchedNodeByXpath(String xpath, Node item) {
    try {
      return Optional.ofNullable((Node) xpathInstance.evaluate(xpath, item, XPathConstants.NODE));
    } catch (XPathExpressionException e) {
      return Optional.empty();
    }
  }

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

  public String getElementByXpath(String xpath) {
    try {
      return getSimpleElementByXpath(xpath);
    } catch (XPathExpressionException exception) {
      logger.warn(String.format("Error finding element by xpath: %s", xpath), exception);
      return null;
    }
  }

  /** Returns the text value of a node, excluding text in any child nodes. */
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

  public String extractCleanedText(String xPath) throws XPathExpressionException {
    NodeList nodes = (NodeList) xpathInstance.evaluate(xPath, document, XPathConstants.NODESET);
    final List<String> textItems =
        XmlDocument.asList(nodes).stream().map(Node::getTextContent).toList();
    return String.join(" ", textItems).replaceAll("\\s+", " ").trim();
  }

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
