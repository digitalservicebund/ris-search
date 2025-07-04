package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.Attachment;
import de.bund.digitalservice.ris.search.utils.XmlDocument;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class NormAttachmentMapper {
  private NormAttachmentMapper() {}

  private static final Logger logger = LogManager.getLogger(NormAttachmentMapper.class);

  /**
   * Extracts the document title and eId from attachments.
   *
   * @param mainDocument The XML document containing zero or more references to attachments.
   * @param attachmentFiles A Map of filenames to file contents for the references in mainDocument.
   * @return A list of Attachment objects.
   */
  public static List<Attachment> parseAttachments(
      XmlDocument mainDocument, Map<String, String> attachmentFiles) {

    List<Node> references = getAttachmentReferences(mainDocument);

    return references.stream()
        .map(
            reference -> {
              final String eId = reference.getAttributes().getNamedItem("eId").getTextContent();
              Node attachmentRef =
                  ((Element) reference).getElementsByTagName("akn:documentRef").item(0);
              try {
                var href = attachmentRef.getAttributes().getNamedItem("href").getNodeValue();
                var attachmentFileString = attachmentFiles.get(href);
                if (attachmentFileString == null) {
                  return Optional.<Attachment>empty();
                }
                XmlDocument attachmentDocument =
                    new XmlDocument(attachmentFileString.getBytes(StandardCharsets.UTF_8));
                Optional<Node> docTitleNode =
                    attachmentDocument.getFirstMatchedNodeByXpath(
                        "/akn:akomaNtoso/akn:doc/akn:preface/akn:block/akn:docTitle");

                Optional<Node> numNode =
                    attachmentDocument.getFirstMatchedNodeByXpath(
                        "./akn:inline[@refersTo='anlageregelungstext-num']", docTitleNode.get());
                Optional<Node> referenceNode =
                    attachmentDocument.getFirstMatchedNodeByXpath(
                        "./akn:inline[@refersTo='anlageregelungstext-bezug']", docTitleNode.get());

                String text =
                    attachmentDocument.extractCleanedText(
                        "/akn:akomaNtoso/akn:doc/akn:mainBody//text()");

                var attachment =
                    Attachment.builder()
                        .marker(numNode.map(Node::getTextContent).orElse(null))
                        .docTitle(referenceNode.map(Node::getTextContent).orElse(null))
                        .eId(eId)
                        .textContent(text)
                        .manifestationEli(href)
                        .build();
                return Optional.of(attachment);
              } catch (ParserConfigurationException
                  | SAXException
                  | IOException
                  | RuntimeException
                  | XPathExpressionException e) {
                logger.error("Error parsing attachments", e);
                return Optional.<Attachment>empty();
              }
            })
        .flatMap(Optional::stream)
        .toList();
  }

  private static List<Node> getAttachmentReferences(XmlDocument mainDocument) {
    final String X_PATH_ATTACHMENT = "/akn:akomaNtoso/akn:act/akn:attachments/akn:attachment";
    try {
      NodeList attachmentNodes = mainDocument.getNodesByXpath(X_PATH_ATTACHMENT);
      return XmlDocument.asList(attachmentNodes);
    } catch (XPathExpressionException e) {
      logger.error("Error finding attachment references", e);
      return List.of();
    }
  }
}
