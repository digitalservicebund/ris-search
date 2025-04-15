package de.bund.digitalservice.ris.search.mapper;

import static de.bund.digitalservice.ris.search.utils.MappingUtils.cleanText;

import de.bund.digitalservice.ris.search.utils.XmlDocument;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;

public class NormParagraphToTextMapper {
  private static final String X_PATH_ARTICLE_PARAGRAPHS = ".//*[local-name()='paragraph']";
  private static final String X_PATH_PARAGRAPH_NUM = ".//*[local-name()='num']";
  private static final String X_PATH_PARAGRAPH_SENTENCE_MARKER =
      ".//*[local-name()='marker' and @refersTo='satzende']";

  private NormParagraphToTextMapper() {}

  private static String extractParagraphNumber(XmlDocument paragraphXml) {
    return paragraphXml
        .getFirstMatchedNodeByXpath(X_PATH_PARAGRAPH_NUM)
        .map(Node::getTextContent)
        .orElse("");
  }

  private static String extractParagraphTextContent(XmlDocument paragraphXml) {
    paragraphXml.removeNodesByXpath(X_PATH_PARAGRAPH_NUM);
    paragraphXml.replaceNodesByXpathWithString(X_PATH_PARAGRAPH_SENTENCE_MARKER, " ");
    return paragraphXml
        .getFirstMatchedNodeByXpath(X_PATH_ARTICLE_PARAGRAPHS)
        .map(Node::getTextContent)
        .orElse("");
  }

  public static String extractTextFromParagraph(Node normParagraph)
      throws ParserConfigurationException {

    XmlDocument paragraphXml = new XmlDocument(normParagraph);

    String paragraphNumber = extractParagraphNumber(paragraphXml);
    String paragraphTextContent = extractParagraphTextContent(paragraphXml);

    if (paragraphNumber.isEmpty()) {
      return cleanText(paragraphTextContent);
    }

    return String.format("%s %s", paragraphNumber, cleanText(paragraphTextContent));
  }
}
