package de.bund.digitalservice.ris.search.caselawhandover.shared;

import de.bund.digitalservice.ris.search.exception.FileTransformationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mapping.MappingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** Utility class for XML processing, including XSLT transformations and NodeList manipulations. */
public class XmlUtils {
  private XmlUtils() {}

  private static final Logger logger = LogManager.getLogger(XmlUtils.class);
  private static final TransformerFactory transformerFactory =
      new net.sf.saxon.TransformerFactoryImpl();
  private static final DocumentBuilderFactory documentBuilderFactory = getDocumentBuilderFactory();
  private static final String HTML_TRANSFORMATION_ERROR = "Xml transformation error.";

  /**
   * Loads XSLT templates from a file in the classpath.
   *
   * @param filePath The path to the XSLT file in the classpath.
   * @return The compiled XSLT templates.
   * @throws FileTransformationException if an error occurs during loading or compilation.
   */
  public static Templates getTemplates(String filePath) {
    try {
      ClassPathResource xsltResource = new ClassPathResource(filePath);
      String fileContent = IOUtils.toString(xsltResource.getInputStream(), StandardCharsets.UTF_8);
      return transformerFactory.newTemplates(new StreamSource(new StringReader(fileContent)));
    } catch (TransformerConfigurationException | IOException e) {
      logger.error("XSLT initialization error.", e);
      throw new FileTransformationException(e.getMessage());
    }
  }

  public static String applyXsltToNodeList(Templates templates, NodeList nodeList) {
    return applyXsltToDomSources(templates, toList(nodeList).stream().map(DOMSource::new).toList());
  }

  private static String applyXsltToDomSources(Templates templates, List<DOMSource> domSources) {
    try {
      Transformer transformer = templates.newTransformer();
      StringWriter writer = new StringWriter();
      StreamResult result = new StreamResult(writer);
      for (DOMSource source : domSources) {
        transformer.transform(source, result);
      }
      return writer.toString().trim();
    } catch (TransformerException e) {
      logger.error("Transformation error.", e);
      throw new MappingException(e.getMessage());
    }
  }

  /**
   * Converts a list of mixed Nodes and Strings to a NodeList.
   *
   * @param inputs The list containing Nodes and Strings.
   * @return A NodeList representing the combined content.
   * @throws MappingException if an unexpected type is encountered.
   */
  public static NodeList jaxbParseToNodeList(List<Object> inputs) {
    try {
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      Element root = document.createElement("root");
      for (Object part : inputs) {
        switch (part) {
          case Node n -> {
            document.adoptNode(n);
            root.appendChild(n);
          }
          case String s -> root.appendChild(document.createTextNode(s));
          default -> {
            logger.error(
                "Transformation error. Unexpected node type: {}", part.getClass().getName());
            throw new MappingException(HTML_TRANSFORMATION_ERROR);
          }
        }
      }
      return root.getChildNodes();
    } catch (ParserConfigurationException e) {
      logger.error("Transformation error.", e);
      throw new MappingException(e.getMessage());
    }
  }

  /**
   * Converts an HTML string to a NodeList.
   *
   * @param html The HTML string to convert.
   * @return A NodeList representing the HTML content.
   * @throws MappingException if an error occurs during parsing.
   */
  public static NodeList htmlStringToNodeList(String html) {
    try {
      String wrapped = "<wrapper>" + html + "</wrapper>";
      DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
      Document doc = builder.parse(new InputSource(new StringReader(wrapped)));
      return doc.getDocumentElement().getChildNodes();
    } catch (ParserConfigurationException | IOException | SAXException e) {
      logger.error(HTML_TRANSFORMATION_ERROR, e);
      throw new MappingException(e.getMessage());
    }
  }

  /**
   * Converts a NodeList to a List of Nodes.
   *
   * @param nodeList The NodeList to convert.
   * @return A List containing the Nodes from the NodeList.
   */
  public static List<Node> toList(NodeList nodeList) {
    // Needed because NodeList doesn't implement Iterable
    List<Node> nodes = new ArrayList<>();
    for (int i = 0; i < nodeList.getLength(); i++) {
      nodes.add(nodeList.item(i));
    }
    return nodes;
  }

  /**
   * Applies the given XSLT templates to the provided XML content string.
   *
   * @param templates The XSLT templates to apply.
   * @param content The XML content as a string.
   * @return The transformed XML content as a string.
   * @throws MappingException if an error occurs during transformation.
   */
  public static String xsltTransform(Templates templates, String content) {
    try {
      StringWriter xsltOutput = new StringWriter();
      Transformer transformer = templates.newTransformer();
      transformer.transform(
          new StreamSource(new StringReader(content.strip())), new StreamResult(xsltOutput));
      return xsltOutput.toString();
    } catch (TransformerException e) {
      logger.error("Xslt transformation error.", e);
      throw new MappingException(e.getMessage());
    }
  }

  private static DocumentBuilderFactory getDocumentBuilderFactory() {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setAttribute(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
    return factory;
  }
}
