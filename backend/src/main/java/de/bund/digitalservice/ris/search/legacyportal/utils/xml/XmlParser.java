package de.bund.digitalservice.ris.search.legacyportal.utils.xml;

import java.io.IOException;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlParser {

  private static final Logger logger = LoggerFactory.getLogger(XmlParser.class);
  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  XPath xpath = XPathFactory.newInstance().newXPath();

  private Document document;

  public XmlParser(byte[] xmlData) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilder builder = factory.newDocumentBuilder();
    this.document = builder.parse(new java.io.ByteArrayInputStream(xmlData));
  }

  public Optional<NodeList> getNodeList(String xpathExpression) {
    try {
      XPathExpression expr = xpath.compile(xpathExpression);
      NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

      if (nodeList != null && nodeList.getLength() > 0) {
        return Optional.of(nodeList);
      }
    } catch (XPathExpressionException e) {
      logger.error("Error to getNodeList", e);
    }

    return Optional.empty();
  }

  public String getValueItem(String xpathExpression, Node nodeItem) {
    try {
      return xpath.evaluate(xpathExpression, nodeItem);
    } catch (XPathExpressionException e) {
      logger.error("Error to getValueItem", e);
    }
    return StringUtils.EMPTY;
  }
}
